package edu.hm.cs.ma.demolocationawareapp.ui.geofence

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import edu.hm.cs.ma.demolocationawareapp.R
import edu.hm.cs.ma.demolocationawareapp.databinding.FragmentGeofenceBinding

class GeofenceFragment: Fragment() {


    private lateinit var binding: FragmentGeofenceBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_geofence,
            null,
            false
        )

        // ask for permissions
        initPermissionLauncher()
        requestPermission()

        binding.startGeofenceActivity.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


    private fun requestPermission() {
        val permissions: ArrayList<String> = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
        val fineLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val backgroundLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.startGeofenceActivity.isEnabled = fineLocationPermission == PackageManager.PERMISSION_GRANTED
                    && backgroundLocationPermission == PackageManager.PERMISSION_GRANTED
        } else {
            binding.startGeofenceActivity.isEnabled = fineLocationPermission == PackageManager.PERMISSION_GRANTED
        }

        if (binding.startGeofenceActivity.isEnabled) {
            binding.fragmentGeofencesPermissionsGranted.visibility = View.GONE
        } else {
            binding.fragmentGeofencesPermissionsGranted.visibility = View.VISIBLE
        }
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
                    binding.startGeofenceActivity.isEnabled = true
                    binding.fragmentGeofencesPermissionsGranted.visibility = View.GONE
                    Log.i("Permission", "Granted")
                } else {
                    binding.fragmentGeofencesPermissionsGranted.visibility = View.VISIBLE
                    binding.startGeofenceActivity.isEnabled = false
                    Log.i("Permission", "Not Granted")
                }
            }
    }

}