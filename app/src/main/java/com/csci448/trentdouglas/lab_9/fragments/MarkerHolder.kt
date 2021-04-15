package com.csci448.trentdouglas.lab_9.fragments

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.databinding.ListItemMarkerBinding

class MarkerHolder (val binding: ListItemMarkerBinding) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var markerData: MarkerData

    fun bind(markerData: MarkerData, clickListener: (MarkerData) -> Unit ) {
        this.markerData = markerData
        //itemView.setOnClickListener { clickListener(this.crime) }


        binding.conditions.text = this.markerData.conditions
        binding.temperature.text = this.markerData.temperature.toString()
        binding.longitude.text = this.markerData.longitude.toString()
        binding.latitude.text = this.markerData.lattitude.toString()
        binding.time.text = this.markerData.time
//        binding.crimeSolvedImageView.visibility = if(this.crime.isSolved) {
//            View.VISIBLE
//        } else {
//            View.GONE
//        }
    }

}