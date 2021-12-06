package com.thesis.kotlinversion.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thesis.kotlinversion.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(
    GenreTypeConverter::class
)
abstract class RoomDB : RoomDatabase() {
    // Create Dao - Data Access Objects
    abstract fun movieDao(): MovieDao?

    companion object {
        private var database: RoomDB? = null
        private const val DATABASE_NAME = "movie_database"
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): RoomDB? {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext, RoomDB::class.java, DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database
        }
    }
}