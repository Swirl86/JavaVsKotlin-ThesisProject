package com.thesis.javaversion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Filter;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thesis.javaversion.database.RoomDB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConstraintLayout emptyView;
    private List<Movie> movieList;
    private RoomDB database;
    private MovieAdapter adapter;
    private Context context;
    private MovieAdapter.RecyclerViewClickListener listener;

    private Button btnByTitle, btnByYear, btnByScore;

    private FloatingActionButton movieFormBtn;
    private FloatingActionButton scrollUp;

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        // Sort buttons
        btnByTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterList("title");
            }
        });
        btnByYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterList("year");
            }
        });
        btnByScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterList("score");
            }
        });
    }

    public void init() {
        recyclerView = findViewById(R.id.recycler_view);
        setOnClickListener();
        emptyView = findViewById(R.id.empty_view);
        movieList = new ArrayList<>();

        context = getApplicationContext();
        database = RoomDB.getInstance(context);

        movieList = database.movieDao().getAll();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new MovieAdapter(MainActivity.this, movieList, listener);
        recyclerView.setAdapter(adapter);

        scrollUp = findViewById(R.id.scroll_up_main);
        movieFormBtn = findViewById(R.id.go_to_form_btn);

        // Filter buttons
        btnByTitle = findViewById(R.id.filterByTitle);
        btnByYear = findViewById(R.id.filterByYear);
        btnByScore = findViewById(R.id.filterByScore);

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

    @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0
    private void filterList(String filterOption) {

        List<Movie> filteredMovies = new ArrayList<>();

        // Sort by...
        switch (filterOption) {
            case "year":
                filteredMovies = movieList.stream().sorted(Comparator.comparing(Movie::getReleaseDate)).collect(Collectors.toList());
                break;
            case "score":
                filteredMovies = movieList.stream().sorted(Comparator.comparing(Movie::getScore).reversed()).collect(Collectors.toList());
                break;
            default:
                filteredMovies = movieList.stream().sorted(Comparator.comparing(Movie::getTitle)).collect(Collectors.toList());
                break;
        }
        movieList.clear();
        movieList.addAll(filteredMovies);

        adapter.notifyDataSetChanged();

    }

}