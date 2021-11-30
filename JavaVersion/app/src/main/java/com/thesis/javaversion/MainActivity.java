package com.thesis.javaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    private FloatingActionButton movieFormBtn;
    private FloatingActionButton scrollUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (movieList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int x, int y) {
                if (y < 0) {
                    scrollUp.setVisibility(View.GONE);
                } else if (y > 0) {
                    scrollUp.setVisibility(View.VISIBLE);
                }
            }
        });

        scrollUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
                scrollUp.setVisibility(View.GONE);
            }
        });

        movieFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MovieFormActivity.class);
                startActivity(intent);
            }
        });
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

        scrollUp = findViewById(R.id.scroll_up_main);
        movieFormBtn = findViewById(R.id.go_to_form_btn);
    }

    public Movie dummyValue() {
        Movie dummy = new Movie();
        dummy.setTitle("Luca");
        dummy.setPlot("On the Italian Riviera, an unlikely but strong friendship grows between a human being and a sea monster disguised as a human.");
        dummy.setReleaseDate("2021");
        dummy.setAgeRated("G – General Audiences");
        ArrayList<String> genre =  new ArrayList<String>(){{
            add("Animation"); add("Comedy"); add("Adventure");
        }};
        dummy.setGenre(genre);
        dummy.setScore(9);
        dummy.setImgUrl("https://prod-bb-images.akamaized.net/book-covers/coverimage-9788726815375-publizon-2021-06-10.jpg?w=200&format=jpg&quality=85");

        return dummy;
    }
}