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

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    // TODO 1: Declare FusedLocationProviderClient

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

        return root
    }

    private fun initLocationProvider() {
        // TODO 2: init fusedLocationClient here
        TODO("Not yet implemented")
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

    private fun handleButtonClicked() {
        requestPermission()

        getLastLocation()
    }

    private fun getLastLocation() {
        // TODO 3: add onSuccessListener on lastLocation
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
}