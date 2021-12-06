package com.thesis.kotlinversion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thesis.kotlinversion.database.RoomDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements Filterable {

    private List<Movie> movieList;
    private List<Movie> filteredMovieList;
    private Activity context;
    private RecyclerViewClickListener listener;
    private RoomDB database;

    public MovieAdapter(Activity context, List<Movie> movieList, RecyclerViewClickListener listener, RoomDB database) {
        this.movieList = movieList;
        this.listener = listener;
        this.context = context;
        this.filteredMovieList = new ArrayList<>(movieList);
        this.database = database;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.movie_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        String movieUrl = movie.getImgUrl();
        if(Utils.isNumeric(movieUrl)) { // Drawable no_img object number
            holder.img.setImageResource(R.drawable.no_img);
        } else {
            Picasso.with(context).load(movie.getImgUrl()).into(holder.img, new Callback() {
                @Override
                public void onSuccess() { }

                @Override
                public void onError() {
                    // If no valid image add no_img from drawable
                    holder.img.setImageResource(R.drawable.no_img);
                }
            });
        }

        String title = movie.getTitle() + " (" + movie.getReleaseDate() + ")";
        holder.title.setText(title);
        holder.plot.setText(movie.getPlot());
        String score = "Score : " + getStars( movie.getScore()) + " " +  movie.getScore() + "/10";
        holder.score.setText(score);

        Chip chip;
        holder.genresLayout.removeAllViews();
        for (String genre:  movie.getGenre()) {
            chip = new Chip(holder.genresLayout.getContext());
            chip.setText(genre);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getRandomColor()));

            chip.setOnClickListener(view -> {
                String chosenGenre = ((Chip) view).getText().toString();
                List<Movie> moviesMatchingGenre = filteredMovieList.stream()
                        .filter(x -> x.getGenre().contains(chosenGenre))
                        .collect(Collectors.toList());

                movieList.clear();
                movieList.addAll( moviesMatchingGenre );
                notifyDataSetChanged();
            });

            holder.genresLayout.addView(chip);
        }

        holder.deleteBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delSpecDb)
                    .setMessage(R.string.delSpecDbSure)

                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        database.movieDao().delete(movieList.get(position));
                        movieList = database.movieDao().getAll();
                        notifyDataSetChanged();
                        ((Activity)context).recreate(); // Reset start state
                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

    }


    public  String getStars(int score) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < score; i++) {
            stars.append("\u2b50 ");
        }
        return stars.toString().trim();
    }

    // minimumBrightnessRatio between 0 and 1. 1 = all white, 0 = any possible color
    private int getRandomColor() {
        Random random = new Random();
        float minimumBrightnessRatio = 0.5F;
        int minimumColorValue = (int) (255f * minimumBrightnessRatio);
        int variableColorValue = 255 - minimumColorValue;

        int r = minimumColorValue + random.nextInt(variableColorValue);
        int g = minimumColorValue + random.nextInt(variableColorValue);
        int b = minimumColorValue + random.nextInt(variableColorValue);

        return Color.argb(255, r, g, b);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @Override
    public Filter getFilter() {

        return movieFilter;
    }

    private Filter movieFilter = new Filter() {

        @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Movie> filteredList;
            if(charSequence == null || charSequence.length() == 0) {
                filteredList = new ArrayList<>(filteredMovieList);
            } else {
                String filterPattern = charSequence.toString().trim().toLowerCase();

                filteredList = filteredMovieList.stream()
                        .filter(x ->
                                    x.getTitle().toLowerCase().trim().contains(filterPattern)
                                ||  x.getReleaseDate().equals(filterPattern)
                                ||  x.getPlot().toLowerCase().trim().contains(filterPattern)
                        )
                        .collect(Collectors.toList());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            movieList.clear();
            movieList.addAll( (List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, plot, score;
        ChipGroup genresLayout;
        ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.rv_img_view);
            title = itemView.findViewById(R.id.rv_title_view);
            plot = itemView.findViewById(R.id.rv_plot_view);
            score = itemView.findViewById(R.id.rv_score_view);
            genresLayout = (ChipGroup) itemView.findViewById(R.id.chip_group);
            deleteBtn = itemView.findViewById(R.id.rv_delete_btn);
        }
    }
    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

}
