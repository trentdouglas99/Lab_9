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
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.csci448.trentdouglas.lab_9.R
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.util.NetworkConnectionUtil.isNetworkAvailableAndConnected
import com.csci448.trentdouglas.lab_9.util.WeatherWorker
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LocatrFragment: SupportMapFragment(), GoogleMap.OnMarkerClickListener {



    companion object{
        public const val REQUEST_LOC_ON = 0
        private var locationUpdateState = false
        public lateinit var INSTANCE:LocatrFragment
    }
    private lateinit var mapView : View

    private var markerData:MarkerData = MarkerData()
    public fun getLong():Double{
        return markerData.longitude
    }
    public fun getLat():Double{
        return markerData.lattitude
    }

    public fun setWeather(temp:Double, conditions:String){
        markerData.temperature = ((temp - 273.15) * 9/5 + 32).toInt()

        markerData.conditions = conditions
    }

    private lateinit var mySnackbar: Snackbar
    private lateinit var locationRequest: LocationRequest

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var googleMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var workManager: WorkManager

    private val LOG_TAG = "locatrFragment: "

    fun getWeatherData(){
        val workRequest = OneTimeWorkRequest.Builder(WeatherWorker::class.java).build()
        workManager.getWorkInfoByIdLiveData(workRequest.id).observe(
                viewLifecycleOwner,
                { workInfo ->
                    when( workInfo.state ) {
                        WorkInfo.State.SUCCEEDED -> {
                            val apiData = WeatherWorker.getApiData(workInfo.outputData)
                            if(apiData != null) {
                                Log.d(LOG_TAG, apiData.toString())
                                Toast.makeText(requireContext(), "got data!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        WorkInfo.State.CANCELLED, WorkInfo.State.FAILED -> {
                            Toast.makeText(requireContext(), "Network request could not be fulfilled :(", Toast.LENGTH_SHORT).show()

                        } }
                } )



        workManager.enqueue(workRequest)
    }

    private fun updateUI() {
        // make sure we have a map and a location
        if( !::googleMap.isInitialized || !::lastLocation.isInitialized ) {
            return }
        // create a point for the corresponding lat/long
        val myLocationPoint = LatLng(lastLocation.latitude, lastLocation.longitude)
 
        val myMarker = MarkerOptions()
                .position(myLocationPoint)
                .title("Longitude: ${lastLocation.longitude}, Lattidtude: ${lastLocation.latitude}")


        lateinit var marker: Marker
        marker = googleMap.addMarker(
                myMarker
        )
        marker.tag = markerData
        marker.showInfoWindow()

        //googleMap.clear()

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

    override fun onResume() {
        super.onResume()
        if(!isNetworkAvailableAndConnected(requireActivity())){
            Toast.makeText(requireContext(), R.string.internet_reason, Toast.LENGTH_SHORT).show()

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        workManager = WorkManager.getInstance(requireContext())
        INSTANCE = this
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
                markerData.lattitude = locationResult.lastLocation.latitude
                markerData.longitude = locationResult.lastLocation.longitude
                val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss MM/dd/yyyy")
                val cal: Calendar = Calendar.getInstance()
                markerData.time = dateFormat.format(cal.getTime())
                getWeatherData()



                //               binding.locationTextView.text = "(${locationResult.lastLocation.latitude},${locationResult.lastLocation.longitude})"
 //               binding.addressTextView.text = getAddress(locationResult.lastLocation)
                updateUI()
            }
        }



        getMapAsync { map ->
            googleMap = map
            googleMap.setOnMarkerClickListener(this)
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
                        addressTextBuilder.append("\n")
                    }
                    addressTextBuilder.append(address.getAddressLine(i))
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
            addOnFailureListener { exc ->
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
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)!= PERMISSION_GRANTED){

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
        mapView = super.onCreateView(inflater, container, savedInstanceState)!!

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
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        p0!!.showInfoWindow()

        var view: View? = getActivity()?.findViewById(R.id.fragment_container)
        view = view!!
        mySnackbar = Snackbar.make(view, "Time: ${(p0.tag as MarkerData).time} \nWeather: ${(p0.tag as MarkerData).conditions} (${(p0.tag as MarkerData).temperature}\u2109)", LENGTH_LONG)
        mySnackbar.show()
        return true
    }


}