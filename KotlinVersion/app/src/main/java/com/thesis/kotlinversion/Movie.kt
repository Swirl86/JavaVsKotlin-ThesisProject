package com.thesis.kotlinversion

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable
import java.util.ArrayList

@Entity(tableName = "movie_table")
class Movie : Serializable {
    @PrimaryKey(autoGenerate = true)
    var iD = 0

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "plot")
    var plot: String? = null

    @ColumnInfo(name = "release_date")
    var releaseDate: String? = null

    @ColumnInfo(name = "genre")
    var genre: ArrayList<String>? = null

    @ColumnInfo(name = "age_rated")
    var ageRated: String? = null

    @ColumnInfo(name = "score")
    var score = 0

    @ColumnInfo(name = "img_url")
    var imgUrl: String? = null
}