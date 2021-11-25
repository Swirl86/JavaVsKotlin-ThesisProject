package com.thesis.javaversion;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> movieList;
    private Activity context;

    public MovieAdapter(Activity context, List<Movie> movieList) {
        this.movieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.movie_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        // TODO check if image exist/works if not use drawable no_img
        Picasso.with(context).load(movie.getImgUrl()).into(holder.img);

        holder.title.setText(movie.getTitle());
        holder.plot.setText(movie.getPlot());
        String score = "Score : " + getStars( movie.getScore()) + " " +  movie.getScore() + "/10";
        holder.score.setText(score);
    }

    public  String getStars(int score) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < score; i++) {
            stars.append("\u2b50 ");
        }
        return stars.toString().trim();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, plot, score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.rv_img_view);
            title = itemView.findViewById(R.id.rv_title_view);
            plot = itemView.findViewById(R.id.rv_plot_view);
            score = itemView.findViewById(R.id.rv_score_view);
        }
    }
}
