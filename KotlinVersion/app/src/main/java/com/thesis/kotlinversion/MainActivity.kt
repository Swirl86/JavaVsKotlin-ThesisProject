package com.thesis.kotlinversion

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thesis.kotlinversion.MovieAdapter.RecyclerViewClickListener
import com.thesis.kotlinversion.database.RoomDB
import com.thesis.kotlinversion.database.RoomDB.Companion.getInstance
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var emptyView: ConstraintLayout? = null
    private var originalMovieList: List<Movie?>? = null
    private var movieList: MutableList<Movie?>? = null
    private var adapter: MovieAdapter? = null
    private var listener: RecyclerViewClickListener? = null
    private var database: RoomDB? = null
    private var btnByTitle: Button? = null
    private var btnByYear: Button? = null
    private var btnByScore: Button? = null
    private var btnByAll: Button? = null
    private var movieFormBtn: FloatingActionButton? = null
    private var scrollUp: FloatingActionButton? = null
    private var deleteBtn: FloatingActionButton? = null
    private var titleFilterClick = false
    private var yearFilterClick = false
    private var scoreFilterClick = false

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        if (movieList!!.isEmpty()) {
            emptyView!!.visibility = View.VISIBLE
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, x: Int, y: Int) {

                if (y < 0) scrollUp!!.visibility = View.GONE
                 else if (y > 0) scrollUp!!.visibility = View.VISIBLE

            }
        })
        scrollUp!!.setOnClickListener { view: View? ->
            recyclerView.smoothScrollToPosition(0)
            scrollUp!!.visibility = View.GONE
        }
        movieFormBtn!!.setOnClickListener { view: View? ->
            val intent = Intent(applicationContext, MovieFormActivity::class.java)
            startActivity(intent)
        }
        deleteBtn!!.setOnClickListener { view: View? ->
            AlertDialog.Builder(this)
                .setTitle(R.string.delAllDb)
                .setMessage(R.string.delAllDbSure)
                .setPositiveButton("yes") { dialog: DialogInterface?, which: Int ->
                    database!!.movieDao()!!.deleteAll(movieList)
                    movieList!!.clear()
                    adapter!!.notifyDataSetChanged()
                    emptyView!!.visibility = View.VISIBLE
                }
                .setNegativeButton("no", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        // Sort buttons
        btnByTitle!!.setOnClickListener { view: View? ->
            filterList("title", titleFilterClick, btnByTitle)
            titleFilterClick = !titleFilterClick
        }
        btnByYear!!.setOnClickListener { view: View? ->
            filterList("year", yearFilterClick, btnByYear)
            yearFilterClick = !yearFilterClick
        }
        btnByScore!!.setOnClickListener { view: View? ->
            filterList("score", scoreFilterClick, btnByScore)
            scoreFilterClick = !scoreFilterClick
        }
        btnByAll!!.setOnClickListener { view: View? -> filterList("all", false, btnByAll) }
    }

    private fun init() {
        setOnClickListener()
        recyclerView = findViewById(R.id.recycler_view)
        emptyView = findViewById(R.id.empty_view)
        val context = applicationContext
        database = getInstance(context)

        addDummies()
        originalMovieList = database!!.movieDao()!!.all
        movieList = originalMovieList?.toMutableList()
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MovieAdapter(this@MainActivity, movieList as ArrayList<Movie?>, listener!!, database!!)
        recyclerView.adapter = adapter
        scrollUp = findViewById(R.id.scroll_up_main)
        movieFormBtn = findViewById(R.id.go_to_form_btn)
        deleteBtn = findViewById(R.id.delete_all_btn)

        // Filter buttons
        btnByTitle = findViewById(R.id.filterByTitle)
        btnByYear = findViewById(R.id.filterByYear)
        btnByScore = findViewById(R.id.filterByScore)
        btnByAll = findViewById(R.id.filterByAll)
    }

    private fun setOnClickListener() {
        listener = object : RecyclerViewClickListener {
            override fun onClick(v: View?, position: Int) {
                val intent = Intent(applicationContext, MovieAdapter::class.java)
                intent.putExtra("movies", movieList!![position])
                startActivity(intent)
            }
        }
    }

    // Search filter handling
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.searchbar, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.filter.filter(newText)
                return false
            }
        })
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N) //Req android 7.0 or higher
    private fun filterList(filterOption: String, prevClicked: Boolean, button: Button?) {
        var filteredMovies: MutableList<Movie?>?
        when (filterOption) {
            "title" -> movieList!!.sortedBy { it!!.title }
            "year" -> movieList!!.sortedBy { it!!.releaseDate }
            "score" -> movieList!!.sortedBy { it!!.score }.reversed()
            else -> originalMovieList
        }.also {
            filteredMovies = it as MutableList<Movie?>?

            if (prevClicked && filterOption != "all") {
                filteredMovies!!.reverse()
                button!!.setBackgroundColor(Color.parseColor("#b4b4b4"))
            } else if (filterOption != "all") {
                button!!.setBackgroundColor(Color.parseColor("#8941ff"))
            }

            movieList!!.clear()
            movieList!!.addAll(filteredMovies!!)
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun addDummies(){
        var movie: Movie = Movie()

        movie.title = "Gummi Bears"
        var myListGenre = arrayListOf<String>()
        myListGenre.add("Animation")
        movie.genre = myListGenre
        movie.releaseDate = "1989"
        movie.ageRated = "G – General Audiences"
        movie.score = 5
        movie.imgUrl = "123"
        movie.plot = "Some bears jumping around, making fun"

        var movie2: Movie = Movie()

        movie2.title = "Kindergarden cop"
        var myListGenre2 = arrayListOf<String>()
        myListGenre2.add("Comedy")
        movie2.genre = myListGenre2
        movie2.releaseDate = "1994"
        movie2.ageRated = "PG – Parental Guidance Suggested"
        movie2.score = 6
        movie2.imgUrl = "123"
        movie2.plot = "a funny cop enters your school"

        (0..50).forEach{
            database?.movieDao()?.insert(movie)
            database?.movieDao()?.insert(movie2)
        }


    }

}