package com.csci448.trentdouglas.lab_9.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.csci448.trentdouglas.lab_9.R
import com.csci448.trentdouglas.lab_9.data.repo.MarkerDataRepository
import com.csci448.trentdouglas.lab_9.databinding.FragmentListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsFragment: PreferenceFragmentCompat() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var clearData: Preference
    private val LOG_TAG = "testing"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.visibility = View.GONE
        val markerDataRepository: MarkerDataRepository = MarkerDataRepository.getInstance(requireContext())
        setPreferencesFromResource(R.xml.preferences, rootKey)
        Log.d(LOG_TAG, ".........................started...............................")

        preferenceManager.findPreference<Preference>("clear_data")!!.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {



                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(R.string.confirm_delete)
                        setMessage("Are you sure you want to delete the database?")
                        setIcon(R.drawable.ic_menu_delete_character_light)
                        setPositiveButton(android.R.string.ok) { _, _ ->
                            markerDataRepository.clearData()
                        }
                        setNegativeButton(android.R.string.cancel) { _, _ ->
                        }
                        show()
                    }







                    Log.d(LOG_TAG, ".........................cleared...............................")

                    true
                }

    }


}