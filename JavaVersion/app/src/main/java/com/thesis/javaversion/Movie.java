package com.thesis.javaversion;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "movie_table")
public class Movie implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int ID; // Unique

    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "plot")
    private String plot;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo(name = "genre")
    private ArrayList<String> genre;
    @ColumnInfo(name = "age_rated")
    private String ageRated;
    @ColumnInfo(name = "score")
    private int score;
    @ColumnInfo(name = "img_url")
    private String imgUrl;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public String getAgeRated() {
        return ageRated;
    }

    public void setAgeRated(String ageRated) {
        this.ageRated = ageRated;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
