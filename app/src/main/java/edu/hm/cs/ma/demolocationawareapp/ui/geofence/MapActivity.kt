package edu.hm.cs.ma.demolocationawareapp.ui.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import edu.hm.cs.ma.demolocationawareapp.R
import edu.hm.cs.ma.demolocationawareapp.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var locationManager: LocationManager

    // Define center of geofences
    private val centerOfGeofenceHochschule = LatLng(48.1549, 11.5557)
    private val centerOfGeofenceBurger = LatLng(48.1535, 11.5605)
    private val centerOfGeofencePerlach = LatLng(48.0857, 11.6723)

    // Geofence Client to connect with Location API
    private lateinit var geofencingClient: GeofencingClient
    // List of geofence objects
    private var geofenceList: ArrayList<Geofence> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Geofence Demo"
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /////////////////////////create and add geofences to list////////////////////////////////
        val requestIdOfFirstGeofence = "Hochschule München"
        val radiusInMetersOfFirstGeofence = 100F
        // First geofence
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(requestIdOfFirstGeofence)
                .setCircularRegion(centerOfGeofenceHochschule.latitude, centerOfGeofenceHochschule.longitude, radiusInMetersOfFirstGeofence)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        )

        // Second geofence
        val requestIdOfSecondGeofence = "Burger"
        val radiusInMetersOfSecondGeofence = 150F
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(requestIdOfSecondGeofence)
                .setCircularRegion(centerOfGeofenceBurger.latitude, centerOfGeofenceBurger.longitude, radiusInMetersOfSecondGeofence)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(10000) // send dwell transition after 10 seconds in geofence
                .build()
        )

        // Third geofence
        val requestIdOfThirdGeofence = "LiveTestingArea51"
        val radiusInMetersOfThirdGeofence = 75F
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(requestIdOfThirdGeofence)
                .setCircularRegion(centerOfGeofencePerlach.latitude, centerOfGeofencePerlach.longitude, radiusInMetersOfThirdGeofence)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        )

        // init geofencingClient
        geofencingClient = LocationServices.getGeofencingClient(this)
        // add geofences to client
        addGeofences()
    }


    // specify the geofences which should be monitored
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            //triggered if the device is already inside the geofence
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofences(geofenceList)
        }.build()
    }

    // specify an Pending intent
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /////////////////////////add geofences to client////////////////////////////////////////
    @SuppressLint("MissingPermission")
    private fun addGeofences() {
        // use the geofencing request and the created intent
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                Toast.makeText(applicationContext, "Adding geofences successful", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(applicationContext, "Adding geofences failed", Toast.LENGTH_SHORT)
                    .show()
                Log.i("MainActivity", it.toString())
            }
        }
    }

    // remove geofences
    override fun onDestroy() {
        super.onDestroy()
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                Log.i("MapActivity", "Geofences were destroyed!")
            }
            addOnFailureListener {
                Log.i("MapActivity", "Could not destroy geofences!")
            }
        }


    }

    /**
     * Not relevant for basic usage of geofences. Only for illustrative purposes.
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        val strokeColor = "#33FF0000"
        val circleColor = "#330000ff"
        // create circle around first geofence
        mMap.addCircle(CircleOptions()
            .center(centerOfGeofenceHochschule)
            .radius(100.0)
            .strokeColor(Color.parseColor(strokeColor))
            .fillColor(Color.parseColor(circleColor)))

        // create circle around second geofence
        mMap.addCircle(CircleOptions()
            .center(centerOfGeofenceBurger)
            .radius(150.0)
            .strokeColor(Color.parseColor(strokeColor))
            .fillColor(Color.parseColor(circleColor)))

        // create circle around second geofence
        mMap.addCircle(CircleOptions()
            .center(centerOfGeofencePerlach)
            .radius(75.0)
            .strokeColor(Color.parseColor(strokeColor))
            .fillColor(Color.parseColor(circleColor)))

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        // Add a marker at the current position and move the camera
        val currentPosition = LatLng(location!!.latitude, location.longitude)
        val zoomLevel = 16.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, zoomLevel))
    }

    // Necessary to implement for LocationListener. Not necessary for geofences.
    override fun onLocationChanged(location: Location) {
        // do nothing
    }


}