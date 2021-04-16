package com.csci448.trentdouglas.lab_9.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.csci448.trentdouglas.lab_9.R
import com.csci448.trentdouglas.lab_9.databinding.FragmentAboutBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AboutFragment: Fragment() {
    private var _binding: FragmentAboutBinding? = null
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.visibility = View.GONE

        return binding.root
    }

}