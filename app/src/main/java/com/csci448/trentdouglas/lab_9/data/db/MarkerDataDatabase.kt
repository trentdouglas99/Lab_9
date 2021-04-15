package com.csci448.trentdouglas.lab_9.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.google.android.gms.maps.model.Marker

@Database(entities = [ MarkerData::class ], version = 2)
@TypeConverters(MarkerDataTypeConverters::class)
abstract class MarkerDataDatabase : RoomDatabase() {
    abstract val MarkerDataDao: MarkerDataDao
    companion object {
        private const val DATABASE_NAME = "markerData-database"
        private var INSTANCE: MarkerDataDatabase? = null

//        private val migration_1_2 = object : Migration(1,2) {
//            override fun migrate(database: SupportSQLiteDatabase) { database.execSQL( "ALTER TABLE crime ADD COLUMN suspect TEXT DEFAULT NULL")
//                database.execSQL( "ALTER TABLE crime ADD COLUMN suspectNumber TEXT DEFAULT NULL")
//            }
//        }


        fun getInstance(context: Context): MarkerDataDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        MarkerDataDatabase::class.java,
                        DATABASE_NAME)
                        //.addMigrations(migration_1_2)
                        .build()
                }
                return instance
            }
        }
    }
}