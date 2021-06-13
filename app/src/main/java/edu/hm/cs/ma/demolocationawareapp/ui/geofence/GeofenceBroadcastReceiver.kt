package edu.hm.cs.ma.demolocationawareapp.ui.geofence

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import edu.hm.cs.ma.demolocationawareapp.R

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        // receive geofenceEvent from the intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)

        // check for errors
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("BroadcastReceiver", errorMessage)
            return
        }

        // Get the transition type. (Enter, Exit, Dwell)
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Get the geofences that were triggered. A single event can trigger multiple geofences.
        val triggeringGeofences = geofencingEvent.triggeringGeofences


        // set title of notifications
        var notificationTitle = ""
        when (geofenceTransition) {
            1 -> {
                notificationTitle = "Enter geofence: " + triggeringGeofences[0].requestId
            }
            2 -> {
                notificationTitle = "Exiting geofence: " + triggeringGeofences[0].requestId
            }
            4 -> {
                notificationTitle = "Dwell geofence: " + triggeringGeofences[0].requestId
            }
        }

        // set notification text depending on transition event and geofence id
        var notificationText = ""
        // First geofence
        if (geofenceTransition == 1 && triggeringGeofences[0].requestId == "Hochschule München") {
            notificationText = "Next lecture at 1:15 pm."
        } else if (geofenceTransition == 2 && triggeringGeofences[0].requestId == "Hochschule München") {
            notificationText = "There are no more lectures today."
        }

        // second geofence
        if (geofenceTransition == 1 && triggeringGeofences[0].requestId == "Burger") {
            notificationText = "20 percent discount on all burgers today."
        } else if (geofenceTransition == 2 && triggeringGeofences[0].requestId == "Burger") {
            notificationText = "Come back soon and get 1 burger for free."
        } else if (geofenceTransition == 4 && triggeringGeofences[0].requestId == "Burger") {
            notificationText = "You've walked for a long time. Hungry?"
        }

        // send notification and log info
        sendNotification(context, notificationTitle, notificationText)
        Log.i("BroadcastReceiver", "Geofence-Broadcast successful.")

    }

    // send push notification to user
    private fun sendNotification(
        context: Context?,
        notificationTitle: String,
        notificationText: String
    ) {
        // create intent to get back to mapactivity if user press the notification
        val mapIntent = Intent(context, MapActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, mapIntent, 0)

        val builder = NotificationCompat.Builder(context!!, "Geofence_Notifications")
            .setSmallIcon(R.drawable.ic_fence_24)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }


}