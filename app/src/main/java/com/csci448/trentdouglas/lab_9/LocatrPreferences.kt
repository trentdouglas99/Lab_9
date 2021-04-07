package com.csci448.trentdouglas.lab_9

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.core.content.edit

class LocatrPreferences(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val PREFS_POLLING_KEY = "polling_key"
    }

    var isPollingOn: Boolean
        get() = prefs.getBoolean(PREFS_POLLING_KEY, false)
        set(value) = prefs.edit {
            putBoolean(PREFS_POLLING_KEY, value)
            commit()
        }

}