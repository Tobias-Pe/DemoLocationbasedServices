package edu.hm.cs.ma.demolocationawareapp.ui.location

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import edu.hm.cs.ma.demolocationawareapp.databinding.FragmentLocationBinding

class LocationFragment : Fragment() {

    private lateinit var locationRequest: LocationRequest
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    private var isLocationClientConfigured: Boolean = false

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var binding: FragmentLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        locationViewModel =
            ViewModelProvider(this).get(LocationViewModel::class.java)

        binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.locationText
        locationViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        binding.button.setOnClickListener {
            handleButtonClicked()
        }

        initPermissionLauncher()

        initLocationProvider()


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationViewModel.setText("Lat: ${locationResult.lastLocation.latitude}\nLon: ${locationResult.lastLocation.longitude}\nAccuracy:${locationResult.lastLocation.accuracy}")
            }
        }

        isLocationClientConfigured = false

        return root
    }

    private fun initPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {
                val isGranted: Boolean = it.values.all { isGranted -> isGranted == true }
                // Check permission status after requesting from the user
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.i("Permission", "Granted")
                    getLastLocation()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.i("Permission", "Not Granted")
                    locationViewModel.setText("This feature will be unavailable to you until the permission to access your smartphones location is allowed.")
                }
            }
    }

    private fun initLocationProvider() {
        // Settings for fusedLocationClient
        locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 100
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Init the LocationProvider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        checkSettings()
    }

    private fun checkSettings() {
        val builder = LocationSettingsRequest.Builder()
        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        exception.statusCode
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun handleButtonClicked() {
        requestPermission()

        getLastLocation()
    }

    private fun getLastLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            if (!isLocationClientConfigured) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback, Looper.getMainLooper()
                )
                isLocationClientConfigured = true
            }
        }
    }

    private fun requestPermission() {
        val permissions: ArrayList<String> = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val permissionsToRequest = ArrayList<String>()
        // Check if permissions are already granted
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not yet granted
                permissionsToRequest.add(permission)
            }
        }
        // Request permissions now
        val size = permissionsToRequest.size
        if (size > 0) {
            // NOTE: possibility here to check for shouldShowRequestPermissionRationale
            requestPermissionLauncher.launch(permissions.toTypedArray())
            Log.i("Permission", "requested permissions")
        }
    }

    override fun onResume() {
        super.onResume()
        isLocationClientConfigured = false
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}