package com.csci448.trentdouglas.lab_9.data.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.data.db.MarkerDataDao
import com.csci448.trentdouglas.lab_9.data.db.MarkerDataDatabase
import java.util.*
import java.util.concurrent.Executors

class MarkerDataRepository private constructor (private val markerDataDao: MarkerDataDao) {
    fun getMarkers(): LiveData<List<MarkerData>> = markerDataDao.getMarkers()
    fun getMarkers(id: UUID): LiveData<MarkerData?> = markerDataDao.getMarker(id)
    private val executor = Executors.newSingleThreadExecutor()
    companion object {
        private var INSTANCE: MarkerDataRepository? = null
        fun getInstance(context: Context): MarkerDataRepository {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    val database = MarkerDataDatabase.getInstance(context)
                    instance = MarkerDataRepository(database.MarkerDataDao)
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

    fun getMarkersPaged(): LiveData<PagedList<MarkerData>> =
            LivePagedListBuilder(
                    markerDataDao.getMarkersPaged(),
                    PagedList.Config.Builder().setPageSize(100).build()
            ).build()

    fun addMarker(markerData: MarkerData){
        executor.execute{
            markerDataDao.addMarker(markerData)
        }
    }
//    fun updateMarker(markerData: MarkerData){
//        executor.execute{
//            markerData.updateMarker(markerData)
//        }
//    }
    fun clearData(){
        executor.execute{
            markerDataDao.clearData()
        }
    }
    fun deleteMarker(marker: MarkerData) {
        executor.execute {
            markerDataDao.deleteMarker(marker)
        }
    }


}