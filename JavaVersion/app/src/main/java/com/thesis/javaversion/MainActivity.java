package com.thesis.javaversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.thesis.javaversion.database.RoomDB;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConstraintLayout emptyView;
    private List<Movie> movieList;
    private RoomDB database;
    private MovieAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        /* TODO changes to database reload to show updates
        *   also to hide/show emptyView correctly */
        if (movieList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void init() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        movieList = new ArrayList<>();

        context = getApplicationContext();
        database = RoomDB.getInstance(context);

        //database.movieDao().insert(dummyValue());

        movieList = database.movieDao().getAll();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new MovieAdapter(MainActivity.this, movieList);
        recyclerView.setAdapter(adapter);
    }

    public Movie dummyValue() {
        Movie dummy = new Movie();
        dummy.setTitle("Luca");
        dummy.setPlot("LOVELY MOVIE!");
        dummy.setReleaseDate("2021");
        dummy.setAgeRated("PG – Parental Guidance Suggested");
        ArrayList<String> genre =  new ArrayList<String>(){{
            add("Animation"); add("Comedy"); add("Adventure");
        }};
        dummy.setGenre(genre);
        dummy.setScore(9);
        dummy.setImgUrl("https://prod-bb-images.akamaized.net/book-covers/coverimage-9788726815375-publizon-2021-06-10.jpg?w=200&format=jpg&quality=85");

        return dummy;
    }
}