package com.csci448.trentdouglas.lab_9.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class MarkerData (@PrimaryKey val id: UUID = UUID.randomUUID(),
                       var time: String = "",
                       var lattitude: Double = 0.0,
                       var longitude: Double = 0.0,
                       var temperature: Int = 0,
                       var conditions: String = "",)

