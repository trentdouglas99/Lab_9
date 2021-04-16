package com.csci448.trentdouglas.lab_9.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.csci448.trentdouglas.lab_9.R
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.databinding.ListItemMarkerBinding
import com.csci448.trentdouglas.lab_9.fragments.HistoryFragment.Companion.markerListViewModel

class MarkerListAdapter (private val markerListViewModel: MarkerListViewModel, private val clickListner: (MarkerData) -> Unit ) : PagedListAdapter<MarkerData, MarkerHolder>(DIFF_UTIL),
        SwipeToDeleteHelper.ItemTouchHelperAdapter {

    private var LOG_TAG = "adapter"
    private lateinit var attachedRecyclerView: RecyclerView


    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<MarkerData>() {
            override fun areContentsTheSame(oldItem: MarkerData, newItem: MarkerData): Boolean = oldItem == newItem

            override fun areItemsTheSame(oldItem: MarkerData, newItem: MarkerData): Boolean = oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerHolder {
        val binding = ListItemMarkerBinding.inflate( LayoutInflater.from(parent.context), parent, false )
        return MarkerHolder(binding)

    }

    override fun onBindViewHolder(holder: MarkerHolder, position: Int) {
        val character = getItem(position)
        if(character != null) {
            holder.bind(character, clickListner)
        }
        //        else {
//            holder.clear()
//        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }

    override fun onItemDismiss(position: Int) {
        val context = attachedRecyclerView.context
        val marker = getItem(position)
        if(marker != null) {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.confirm_delete)
                setMessage(context.resources.getString(R.string.confirm_delete_message, marker.time))
                setIcon(R.drawable.ic_menu_delete_character_light)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    markerListViewModel.deleteMarker(marker)
                }
                setNegativeButton(android.R.string.cancel) { _, _ ->
                    notifyItemChanged(position)
                }
                show()
            }
        }
    }

}