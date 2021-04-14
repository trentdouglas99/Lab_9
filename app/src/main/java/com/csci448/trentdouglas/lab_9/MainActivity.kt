package com.csci448.trentdouglas.lab_9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.csci448.trentdouglas.lab_9.databinding.ActivityMainBinding
import com.csci448.trentdouglas.lab_9.fragments.LocatrFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.fab.setOnClickListener {
            //Toast.makeText(this, "Weee", Toast.LENGTH_SHORT).show()

            LocatrFragment.INSTANCE.checkPermissionAndGetLocation()
        }

        val navHostFragment = supportFragmentManager.findFragmentById((binding).navHostFragment.id) as NavHostFragment
        NavigationUI.setupActionBarWithNavController(this, navHostFragment.navController)
    }

    // on MainActivity.kt
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == LocatrFragment.REQUEST_LOC_ON) {
            val locatrFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as LocatrFragment
            locatrFragment.onActivityResult(requestCode, resultCode, data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController((binding as ActivityMainBinding).navHostFragment.id).navigateUp() || super.onSupportNavigateUp()
    }
}