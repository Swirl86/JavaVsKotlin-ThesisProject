package com.thesis.kotlinversion.database

import androidx.room.*
import com.thesis.kotlinversion.Movie

@Dao
interface MovieDao {
    // Insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie?)

    // Delete all query
    @Delete
    fun deleteAll(movie: List<Movie?>?)

    // Delete query
    @Delete
    fun delete(movie: Movie?)

    // Get all query
    @get:Query("SELECT * FROM movie_table")
    val all: MutableList<Movie?>?
}