package com.csci448.trentdouglas.lab_9.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.csci448.trentdouglas.lab_9.data.MarkerData
import java.util.*

@Dao
interface MarkerDataDao {
    @Query("SELECT * FROM markerData")
    fun getMarkers(): LiveData<List<MarkerData>>

    @Query("SELECT * FROM markerData WHERE id=(:id)")
    fun getMarker(id: UUID): LiveData<MarkerData?>

    @Update
    fun updateMarker(markerData: MarkerData)

    @Insert
    fun addMarker(markerData: MarkerData)

    @Query("DELETE FROM markerData")
    fun clearData()
}