package com.csci448.trentdouglas.lab_9.fragments

import androidx.lifecycle.ViewModel
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.data.repo.MarkerDataRepository

class MarkerListViewModel (private val markerDataRepository: MarkerDataRepository) : ViewModel() {
    val crimeListLiveData = markerDataRepository.getMarkers()

    fun addCrime(markerData: MarkerData) {
        markerDataRepository.addMarker(markerData)
    }

}