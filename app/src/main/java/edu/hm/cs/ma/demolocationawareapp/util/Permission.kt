package edu.hm.cs.ma.demolocationawareapp.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

class Permission(private val context: Context, private val activity: Activity) {

    fun requestPermissionsIfNecessary(permissions: ArrayList<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        // Check if permissions are already granted
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not yet granted
                permissionsToRequest.add(permission)
            }
        }
        // Request permissions now
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }


}