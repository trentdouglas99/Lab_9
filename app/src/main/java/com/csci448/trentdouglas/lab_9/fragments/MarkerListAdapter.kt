package com.csci448.trentdouglas.lab_9.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.databinding.ListItemMarkerBinding

class MarkerListAdapter (private val crimes: List<MarkerData>, private val clickListener: (MarkerData) -> Unit ) : RecyclerView.Adapter<MarkerHolder>() {

    override fun getItemCount(): Int{
        return crimes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerHolder {
        val binding = ListItemMarkerBinding.inflate( LayoutInflater.from(parent.context), parent, false )
        return MarkerHolder(binding)

    }

    override fun onBindViewHolder(holder: MarkerHolder, position: Int) {
        val crime = crimes[position]
        holder.bind(crime, clickListener)
    }

}