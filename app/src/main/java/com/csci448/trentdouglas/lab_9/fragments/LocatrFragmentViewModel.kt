package com.csci448.trentdouglas.lab_9.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.data.repo.MarkerDataRepository

class LocatrFragmentViewModel (private val markerDataRepository: MarkerDataRepository) : ViewModel() {
    val markerListLiveData = markerDataRepository.getMarkers()

    fun addMarker(markerData: MarkerData) {
        markerDataRepository.addMarker(markerData)
    }

}