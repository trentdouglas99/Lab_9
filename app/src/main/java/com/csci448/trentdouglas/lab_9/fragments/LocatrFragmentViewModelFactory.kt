package com.csci448.trentdouglas.lab_9.fragments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.csci448.trentdouglas.lab_9.data.repo.MarkerDataRepository

class LocatrFragmentViewModelFactory (private val context: Context) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MarkerDataRepository::class.java).newInstance(
            MarkerDataRepository.getInstance(context))
    }

}