package com.csci448.trentdouglas.lab_9

import android.app.Application

class LocatrAppliction : Application() {
    companion object {
        lateinit var locatrSharedPreferences: LocatrPreferences
    }

    override fun onCreate() {
        super.onCreate()
        locatrSharedPreferences = LocatrPreferences(applicationContext)
    }
}