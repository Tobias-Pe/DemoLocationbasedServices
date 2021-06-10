package edu.hm.cs.ma.demolocationawareapp.ui.geofence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.hm.cs.ma.demolocationawareapp.databinding.FragmentGeofenceBinding

class GeofenceFragment : Fragment() {

    private lateinit var geofenceViewModel: GeofenceViewModel
    private var _binding: FragmentGeofenceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        geofenceViewModel =
            ViewModelProvider(this).get(GeofenceViewModel::class.java)

        _binding = FragmentGeofenceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        geofenceViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}