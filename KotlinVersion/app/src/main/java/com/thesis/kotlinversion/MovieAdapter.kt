package com.thesis.kotlinversion

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.thesis.kotlinversion.Utils.isNumeric
import com.thesis.kotlinversion.database.RoomDB
import java.util.*
import java.util.stream.Collectors

class MovieAdapter(
    context: Activity,
    movieList: MutableList<Movie?>,
    listener: RecyclerViewClickListener,
    database: RoomDB
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>(), Filterable {
    private var movieList: MutableList<Movie?>?
    private var filteredMovieList: MutableList<Movie?>? = ArrayList(movieList)
    private val context: Activity
    private val listener: RecyclerViewClickListener
    private val database: RoomDB
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.movie_row, parent, false)
        return ViewHolder(view)
    }

    init {
        this.movieList = movieList
        this.listener = listener
        this.context = context
        this.database = database
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movieList!![position]
        val movieUrl = movie!!.imgUrl
        if (isNumeric(movieUrl)) { // Drawable no_img object number
            holder.img.setImageResource(R.drawable.no_img)
        } else {
            Picasso.with(context).load(movie.imgUrl).into(holder.img, object : Callback {
                override fun onSuccess() {}
                override fun onError() {
                    // If no valid image add no_img from drawable
                    holder.img.setImageResource(R.drawable.no_img)
                }
            })
        }
        val title = movie.title + " (" + movie.releaseDate + ")"
        holder.title.text = title
        holder.plot.text = movie.plot
        val score = "Score : " + getStars(movie.score) + " " + movie.score + "/10"
        holder.score.text = score
        var chip: Chip
        holder.genresLayout.removeAllViews()
        for (genre in movie.genre!!) {
            chip = Chip(holder.genresLayout.context)
            chip.text = genre
            chip.chipBackgroundColor = ColorStateList.valueOf(randomColor)
            chip.setOnClickListener { view: View ->
                val chosenGenre = (view as Chip).text.toString()
                val moviesMatchingGenre = filteredMovieList!!.stream()
                    .filter { x: Movie? -> x!!.genre!!.contains(chosenGenre) }
                    .collect(Collectors.toList())
                movieList!!.clear()
                movieList!!.addAll(moviesMatchingGenre)
                notifyDataSetChanged()
            }
            holder.genresLayout.addView(chip)
        }
        holder.deleteBtn.setOnClickListener { view: View? ->
            AlertDialog.Builder(context)
                .setTitle(R.string.delSpecDb)
                .setMessage(R.string.delSpecDbSure)
                .setPositiveButton("yes") { dialog: DialogInterface?, which: Int ->
                    database.movieDao()!!.delete(movieList!![position])
                    movieList = database.movieDao()!!.all
                    notifyDataSetChanged()
                    context.recreate() // Reset start state
                }
                .setNegativeButton("no", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    private fun getStars(score: Int): String {
        val stars = StringBuilder()
        for (i in 0 until score) {
            stars.append("\u2b50 ")
        }
        return stars.toString().trim { it <= ' ' }
    }

    // minimumBrightnessRatio between 0 and 1. 1 = all white, 0 = any possible color
    private val randomColor: Int
        get() {
            val random = Random()
            val minimumBrightnessRatio = 0.5f
            val minimumColorValue = (255f * minimumBrightnessRatio).toInt()
            val variableColorValue = 255 - minimumColorValue
            val r = minimumColorValue + random.nextInt(variableColorValue)
            val g = minimumColorValue + random.nextInt(variableColorValue)
            val b = minimumColorValue + random.nextInt(variableColorValue)
            return Color.argb(255, r, g, b)
        }

    override fun getItemCount(): Int {
        return movieList!!.size
    }

    override fun getFilter(): Filter {
        return movieFilter
    }

    private val movieFilter: Filter = object : Filter() {
        @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            var filteredList: MutableList<Movie>? = ArrayList()
                if (charSequence.isEmpty()) {
                    filteredList?.addAll(filteredMovieList as Collection<Movie>)
            } else {
                val filterPattern = charSequence.toString().trim { it <= ' ' }.lowercase()

                    filteredList = filteredMovieList?.filter { x: Movie? ->
                        (x!!.title!!.lowercase().trim { it <= ' ' }
                            .contains(filterPattern)
                                || x.releaseDate == filterPattern || x.plot!!.lowercase()
                            .trim { it <= ' ' }
                            .contains(filterPattern))
                    } as MutableList<Movie>?

            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            movieList.clear()
            movieList.addAll(filterResults.values as Collection<Movie?>)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.rv_img_view)
        var title: TextView = itemView.findViewById(R.id.rv_title_view)
        var plot: TextView = itemView.findViewById(R.id.rv_plot_view)
        var score: TextView = itemView.findViewById(R.id.rv_score_view)
        var genresLayout: ChipGroup = itemView.findViewById<View>(R.id.chip_group) as ChipGroup
        var deleteBtn: ImageButton = itemView.findViewById(R.id.rv_delete_btn)

    }

    interface RecyclerViewClickListener {
        fun onClick(v: View?, position: Int)
    }
}