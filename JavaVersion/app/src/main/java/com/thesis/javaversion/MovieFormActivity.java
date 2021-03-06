package com.thesis.javaversion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.thesis.javaversion.database.RoomDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MovieFormActivity extends AppCompatActivity {

    private TextView genreView;
    private Spinner ageRatedSpinner;
    private Button backBtn, submitBtn;
    private RoomDB database;

    private boolean[] selectedGenres;
    private ArrayList<Integer> selectedGenreList;
    private String[] genreArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_form);

        init();

        submitBtn.setOnClickListener(submitMovieFormListener);

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        genreView.setOnClickListener(v -> showGenreDialog());
    }

    public void init() {
        Context context = getApplicationContext();
        database = RoomDB.getInstance(context);

        backBtn = findViewById(R.id.back_btn);
        submitBtn = findViewById(R.id.submit_btn);

        // Spinner age rate
        ageRatedSpinner = (Spinner) findViewById(R.id.spinner_age_rated);
        ArrayAdapter<String> ageRateAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.age_rate));
        ageRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageRatedSpinner.setAdapter(ageRateAdapter);

        // Checkbox Genre
        genreArray = getResources().getStringArray(R.array.genre);
        selectedGenres = new boolean[genreArray.length];
        selectedGenreList = new ArrayList<>();
        genreView = findViewById(R.id.input_genre);
    }

    private void showGenreDialog() {
        AlertDialog.Builder genreBuilder = new AlertDialog.Builder(this);
        genreBuilder.setTitle("Choose Genre(s)");

        genreBuilder.setMultiChoiceItems(genreArray, selectedGenres, (dialogInterface, i, checked) -> {
            if (checked) {
                selectedGenreList.add(i);
                Collections.sort(selectedGenreList);
            } else {
                selectedGenreList.remove(i);
            }
        });

        genreBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < selectedGenreList.size(); j++) {

                stringBuilder.append(genreArray[selectedGenreList.get(j)]);

                if (j != selectedGenreList.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            genreView.setText(stringBuilder.toString());
        });

        genreBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        genreBuilder.setNeutralButton("Clear all", (dialogInterface, i) -> {
            for (int j = 0; j < selectedGenreList.size(); j++) {
                selectedGenres[j] = false;
            }
            selectedGenreList.clear();
            genreView.setText("");
        });

        AlertDialog dialog = genreBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private View.OnClickListener submitMovieFormListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Movie newMovie = new Movie();
            ArrayList<Boolean> isValid = new ArrayList<>();

            TextView promp = findViewById(R.id.promp_input_title);
            EditText input = findViewById(R.id.input_title);
            String text = input.getText().toString();
            isValid.add(checkInput(promp, text, "textInput"));
            newMovie.setTitle(text);

            promp = findViewById(R.id.promp_input_date);
            input = findViewById(R.id.input_release_date);
            text = input.getText().toString();
            isValid.add(checkInput(promp, text, "yearInput"));
            newMovie.setReleaseDate(text);

            promp = findViewById(R.id.promp_input_age_rated);
            text = ageRatedSpinner.getSelectedItem().toString();
            isValid.add(checkInput(promp, text, "ageInput"));
            newMovie.setAgeRated(text);

            promp = findViewById(R.id.promp_input_genre);
            text = genreView.getText().toString();
            isValid.add(checkInput(promp, text, "textInput"));
            newMovie.setGenre(new ArrayList<>(Arrays.asList(text.split(", "))));

            promp = findViewById(R.id.promp_input_score);
            input = findViewById(R.id.input_score);
            text = input.getText().toString();
            isValid.add(checkInput(promp, text, newMovie));

            input = findViewById(R.id.input_img_url);
            text = input.getText().toString();
            if (Utils.IsValidUrl(text)) {
                newMovie.setImgUrl(text);
            } else {
                newMovie.setImgUrl(String.valueOf(R.drawable.no_img));
            }

            promp = findViewById(R.id.promp_input_plot);
            input = findViewById(R.id.input_plot);
            text = input.getText().toString();
            isValid.add(checkInput(promp, text, "textInput"));
            newMovie.setPlot(text);

            if (!isValid.contains(false)) {
                database.movieDao().insert(newMovie);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
    };

    private boolean checkInput(TextView promp, String text, Movie movie) {
        try {
            int score = Integer.parseInt(text);

            if (score > 0 && score <= 10) {
                promp.setVisibility(View.GONE);
                movie.setScore(score);
                return true;
            }

            promp.setVisibility(View.VISIBLE);
            return false;
        } catch (NumberFormatException nfe) {
            promp.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean checkInput(TextView promp, String text, String type) {

        switch (type) {
            case "textInput":
                if (text.equals("")) {
                    promp.setVisibility(View.VISIBLE);
                    return false;
                } else {
                    promp.setVisibility(View.GONE);
                    return true;
                }
            case "yearInput":
                try {
                    int movieCreatedYear = Integer.parseInt(text);

                    if (movieCreatedYear > 1870 && movieCreatedYear <= 2050) {
                        promp.setVisibility(View.GONE);
                        return true;
                    }

                    promp.setVisibility(View.VISIBLE);
                    return false;
                } catch (NumberFormatException nfe) {
                    promp.setVisibility(View.VISIBLE);
                    return false;
                }
            case "ageInput":
                if (text.equals("Age rate *")) {
                    promp.setVisibility(View.VISIBLE);
                    return false;
                } else {
                    promp.setVisibility(View.GONE);
                    return true;
                }
            default:
                return false;
        }

    }

}