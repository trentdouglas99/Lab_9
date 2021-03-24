package com.csci448.trentdouglas.lab_9.fragments

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.csci448.trentdouglas.lab_9.R
import com.csci448.trentdouglas.lab_9.databinding.FragmentLocatrBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

class LocatrFragment: Fragment() {

    companion object{
        public const val REQUEST_LOC_ON = 0
        private var locationUpdateState = false
    }
    private lateinit var locationRequest: LocationRequest
    private var _binding:FragmentLocatrBinding? = null
    private val binding get() = _binding!!

    private val LOG_TAG = "locatrFragment: "
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        locationRequest = LocationRequest.create()
        locationRequest.interval=0
        locationRequest.numUpdates=1
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY

        super.onCreate(savedInstanceState)
    }

    private fun checkIfLocationCanBeRetrieved() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build()).apply {
            addOnSuccessListener {
                locationUpdateState = true
                requireActivity().invalidateOptionsMenu()
            }
            addOnFailureListener {exc ->
                locationUpdateState = false
                requireActivity().invalidateOptionsMenu()
                if(exc is ResolvableApiException) {
                    try {
                        exc.startResolutionForResult(requireActivity(), REQUEST_LOC_ON)
                    } catch (e: IntentSender.SendIntentException) {
                        // do nothing, they cancelled so ignore error
                    }
                } }
        } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")
        _binding = FragmentLocatrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(LOG_TAG, "onCreateOptionsMenu() called")
        inflater.inflate(R.menu.fragment_locatr, menu)
        val locationItem = menu.findItem(R.id.get_location_menu_item)
        locationItem.isEnabled = locationUpdateState
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(LOG_TAG, "called location button!")
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        checkIfLocationCanBeRetrieved()

    }


}