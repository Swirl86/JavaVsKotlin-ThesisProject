package com.thesis.kotlinversion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thesis.kotlinversion.database.RoomDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConstraintLayout emptyView;
    private List<Movie> originalMovieList;
    private List<Movie> movieList;
    private MovieAdapter adapter;
    private MovieAdapter.RecyclerViewClickListener listener;
    private RoomDB database;

    private Button btnByTitle, btnByYear, btnByScore, btnByAll;

    private FloatingActionButton movieFormBtn, scrollUp, deleteBtn;

    boolean titleFilterClick = false;
    boolean yearFilterClick = false;
    boolean scoreFilterClick = false;

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
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

        scrollUp.setOnClickListener(view -> {
            recyclerView.smoothScrollToPosition(0);
            scrollUp.setVisibility(View.GONE);
        });

        movieFormBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MovieFormActivity.class);
            startActivity(intent);
        });

        deleteBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delAllDb)
                    .setMessage(R.string.delAllDbSure)

                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        database.movieDao().deleteAll(movieList);
                        movieList.clear();
                        adapter.notifyDataSetChanged();
                        emptyView.setVisibility(View.VISIBLE);
                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        // Sort buttons
        btnByTitle.setOnClickListener(view -> {
            filterList("title", titleFilterClick, btnByTitle);
            titleFilterClick = !titleFilterClick;
        });
        btnByYear.setOnClickListener(view -> {
            filterList("year", yearFilterClick, btnByYear);
            yearFilterClick = !yearFilterClick;
        });
        btnByScore.setOnClickListener(view -> {
            filterList("score", scoreFilterClick, btnByScore);
            scoreFilterClick = !scoreFilterClick;
        });
        btnByAll.setOnClickListener(view -> filterList("all", false, btnByAll));
    }

    public void init() {
        setOnClickListener();
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);

        Context context = getApplicationContext();
        database = RoomDB.getInstance(context);
        movieList = new ArrayList<>();

        originalMovieList = database.movieDao().getAll();
        movieList = new ArrayList<>(originalMovieList);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new MovieAdapter(MainActivity.this, movieList, listener, database);
        recyclerView.setAdapter(adapter);

        scrollUp = findViewById(R.id.scroll_up_main);
        movieFormBtn = findViewById(R.id.go_to_form_btn);
        deleteBtn = findViewById(R.id.delete_all_btn);

        // Filter buttons
        btnByTitle = findViewById(R.id.filterByTitle);
        btnByYear = findViewById(R.id.filterByYear);
        btnByScore = findViewById(R.id.filterByScore);
        btnByAll = findViewById(R.id.filterByAll);
    }

    private void setOnClickListener() {
        listener = (v, position) -> {
            Intent intent = new Intent(getApplicationContext(), MovieAdapter.class);
            intent.putExtra("movies", movieList.get(position));
            startActivity(intent);
        };
    }

    // Search filter handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
    private void filterList(String filterOption, boolean prevClicked, Button button) {

        List<Movie> filteredMovies;

        // Sort by...
        switch (filterOption) {
            case "title":
                filteredMovies = movieList.stream()
                        .sorted(Comparator.comparing(Movie::getTitle))
                        .collect(Collectors.toList());
                break;
            case "year":
                filteredMovies = movieList.stream()
                        .sorted(Comparator.comparing(Movie::getReleaseDate))
                        .collect(Collectors.toList());
                break;
            case "score":
                filteredMovies = movieList.stream()
                        .sorted(Comparator.comparing(Movie::getScore).reversed())
                        .collect(Collectors.toList());
                break;
            default:
                filteredMovies = originalMovieList;
                break;
        }

        if(prevClicked && !filterOption.equals("all")) {
            Collections.reverse(filteredMovies);
            button.setBackgroundColor(Color.parseColor("#b4b4b4"));
        } else if(!filterOption.equals("all")) {
            button.setBackgroundColor(Color.parseColor("#8941ff"));
        }

        movieList.clear();
        movieList.addAll(filteredMovies);

        adapter.notifyDataSetChanged();
    }

}