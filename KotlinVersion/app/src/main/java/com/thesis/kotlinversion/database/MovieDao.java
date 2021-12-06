package com.thesis.kotlinversion.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thesis.kotlinversion.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    // Insert query
    @Insert(onConflict = REPLACE)
    void insert(Movie movie);

    // Delete all query
    @Delete
    void deleteAll(List<Movie> movie);

   // Delete query
    @Delete
    void delete(Movie movie);

    // Get all query
    @Query("SELECT * FROM movie_table")
    List<Movie> getAll();
}
