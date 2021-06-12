package edu.hm.cs.ma.demolocationawareapp.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import edu.hm.cs.ma.demolocationawareapp.databinding.FragmentLocationBinding
import edu.hm.cs.ma.demolocationawareapp.util.Permission

class LocationFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return root
    }

    @SuppressLint("MissingPermission")
    private fun handleButtonClicked() {
        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionChecker = Permission(requireContext(), requireActivity())
        permissionChecker.requestPermissionsIfNecessary(permissions)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            locationViewModel.setText("Lat: ${location.latitude}\nLon: ${location.longitude}\nAccuracy:${location.accuracy}")
        }
    }
}