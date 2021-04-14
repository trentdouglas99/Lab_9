package com.csci448.trentdouglas.lab_9.util

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.csci448.trentdouglas.lab_9.data.jsonStuff.ApiData
import com.csci448.trentdouglas.lab_9.fragments.LocatrFragment
import com.google.gson.Gson
import java.net.URL


class WeatherWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    companion object {
        private const val WEATHER_API_KEY = "5d089dadf2c84967e8d55dbbb5716e06"
        private var lat:Double = LocatrFragment.INSTANCE.getLat()
        private var long:Double = LocatrFragment.INSTANCE.getLong()
        private var WEATHER_DATA_API = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&appid=${WEATHER_API_KEY}"
        private const val LOG_TAG = "448.WeatherWorker"
        fun getApiData(outputData: Data) = outputData.getString(WEATHER_API_KEY)
    }

    override fun doWork(): Result {
        // do the actual work
        Log.d("WeatherWorker", "doWork Called")
        var urlStringResult:String = URL(WEATHER_DATA_API).readText()
        urlStringResult = urlStringResult.replace("[", "")
        urlStringResult = urlStringResult.replace("]", "")

        Log.d("WeatherWorker", urlStringResult)


        val apiData = Gson().fromJson(urlStringResult, ApiData::class.java)
        Log.d(LOG_TAG, "${apiData.main.temp}")
        LocatrFragment.INSTANCE.setWeather(apiData.main.temp, apiData.weather.description)


        val outputData = workDataOf(WEATHER_API_KEY to urlStringResult)
        return Result.success(outputData)
    }
}