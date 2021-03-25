package com.csci448.trentdouglas.lab_9.fragments

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.csci448.trentdouglas.lab_9.MainActivity
import com.csci448.trentdouglas.lab_9.R
import com.csci448.trentdouglas.lab_9.databinding.FragmentLocatrBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import kotlin.text.StringBuilder

class LocatrFragment: SupportMapFragment() {

    companion object{
        public const val REQUEST_LOC_ON = 0
        private var locationUpdateState = false
    }
    private lateinit var locationRequest: LocationRequest
//    private var _binding:FragmentLocatrBinding? = null
//    private val binding get() = _binding!!
    //private lateinit var locationPermissionCallback: ActivityResultCallback<Boolean>
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var googleMap: GoogleMap
    private lateinit var lastLocation: Location

    private val LOG_TAG = "locatrFragment: "


    private fun updateUI() {
        // make sure we have a map and a location
        if( !::googleMap.isInitialized || !::lastLocation.isInitialized ) {
            return }
        // create a point for the corresponding lat/long
        val myLocationPoint = LatLng(lastLocation.latitude, lastLocation.longitude)
        // Step 3 will go here
        // create the marker
        val myMarker = MarkerOptions().position(myLocationPoint).title( getAddress(lastLocation) )
        // clear any prior markers on the map
        googleMap.clear()
        // add the new markers
        googleMap.addMarker(myMarker)
        // include all points that should be within the bounds of the zoom
        // convex hull
        val bounds = LatLngBounds.Builder().include(myLocationPoint).build()
        // add a margin
        val margin = resources.getDimensionPixelSize(R.dimen.map_inset_margin)
        // create a camera to smoothly move the map view
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, margin)
        // move our camera!
        googleMap.animateCamera(cameraUpdate)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        locationRequest = LocationRequest.create()
        locationRequest.interval=0
        locationRequest.numUpdates=1
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lastLocation = locationResult.lastLocation
                super.onLocationResult(locationResult)
                Log.d(LOG_TAG, "Got a location: ${locationResult.lastLocation}")
 //               binding.locationTextView.text = "(${locationResult.lastLocation.latitude},${locationResult.lastLocation.longitude})"
 //               binding.addressTextView.text = getAddress(locationResult.lastLocation)
                updateUI()
            }
        }

        getMapAsync { map ->
            googleMap = map
            requireActivity().invalidateOptionsMenu()
        }



        var locationPermissionCallback = ActivityResultCallback { result: Boolean ->
            if(result) {
                // permission is granted
                Log.d(LOG_TAG, "permission granted!")

                // make sure button is enabled and launch the pickContact intent
                checkIfLocationCanBeRetrieved()

            } else {
                // permission denied, explain to the user
                Log.d(LOG_TAG, "permission denied, please grant permission")

            }
        }
        locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), locationPermissionCallback)


        super.onCreate(savedInstanceState)
    }

    private fun getAddress(location: Location): String {
        val geocoder = Geocoder(requireActivity())
        val addressTextBuilder = StringBuilder()
        try {
            val addresses = geocoder.getFromLocation(location.latitude,
                    location.longitude,
                    1)
            if(addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                for(i in 0..address.maxAddressLineIndex) {
                    if(i > 0) {
                        addressTextBuilder.append( "\n" )
                    }
                    addressTextBuilder.append( address.getAddressLine(i) )
                }
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error getting address ${e.localizedMessage}")
            //binding.addressTextView.text = "error getting location"
            return "error getting address :("
        }
        return addressTextBuilder.toString()
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
            addOnFailureListener {
                    exc ->
                locationUpdateState = false
                requireActivity().invalidateOptionsMenu()
                if(exc is ResolvableApiException) {
                    try {
                        exc.startResolutionForResult(requireActivity(), REQUEST_LOC_ON)
                    } catch (e: IntentSender.SendIntentException) {
                        // do nothing, they cancelled so ignore error
                    }
                } }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data:
    Intent?) {
        if(resultCode != Activity.RESULT_OK) {
            return
        }
        if(requestCode == REQUEST_LOC_ON) {
            locationUpdateState = true
            requireActivity().invalidateOptionsMenu()
        }
    }

    fun checkPermissionAndGetLocation(){
        if (ContextCompat.checkSelfPermission( requireContext(), ACCESS_FINE_LOCATION )!= PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), ACCESS_FINE_LOCATION)) {
                // user already said no, don't ask again
                Toast.makeText(requireContext(), "We must access your location to plot where you are", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.d(LOG_TAG, "requesting permission...")
                locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }

        else{
            // permission has been granted, do what we want
            Log.d(LOG_TAG, "thanks!")
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")
        val mapView = super.onCreateView(inflater, container, savedInstanceState)
        return mapView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(LOG_TAG, "onCreateOptionsMenu() called")
        inflater.inflate(R.menu.fragment_locatr, menu)
        val locationItem = menu.findItem(R.id.get_location_menu_item)
        //locationItem.isEnabled = locationUpdateState
        locationItem.isEnabled = (locationUpdateState && ::googleMap.isInitialized)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.get_location_menu_item) {
            Log.d(LOG_TAG, "checking permissions")
            checkPermissionAndGetLocation()
            return true
        }
        Log.d(LOG_TAG, "called location button!")
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        checkIfLocationCanBeRetrieved()

    }

    override fun onStop() {
        Log.d(LOG_TAG, "onStop called")
        fusedLocationProviderClient.removeLocationUpdates( locationCallback )
        super.onStop()
    }


}