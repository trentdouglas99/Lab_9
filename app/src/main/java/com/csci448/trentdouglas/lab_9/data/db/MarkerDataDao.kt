package com.csci448.trentdouglas.lab_9.data.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.google.android.gms.maps.model.Marker
import java.util.*

@Dao
interface MarkerDataDao {
    @Query("SELECT * FROM markerData")
    fun getMarkers(): LiveData<List<MarkerData>>

    @Query("SELECT * FROM markerdata")
    fun getMarkersPaged(): DataSource.Factory<Int, MarkerData>

    @Query("SELECT * FROM markerData WHERE id=(:id)")
    fun getMarker(id: UUID): LiveData<MarkerData?>

    @Update
    fun updateMarker(markerData: MarkerData)

    @Insert
    fun addMarker(markerData: MarkerData)

    @Query("DELETE FROM markerData")
    fun clearData()

    @Delete
    fun deleteMarker(marker: MarkerData)
}