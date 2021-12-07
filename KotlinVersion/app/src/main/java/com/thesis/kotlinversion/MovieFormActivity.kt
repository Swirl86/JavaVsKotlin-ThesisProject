package com.thesis.kotlinversion

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.thesis.kotlinversion.Utils.isValidUrl
import com.thesis.kotlinversion.database.RoomDB
import com.thesis.kotlinversion.database.RoomDB.Companion.getInstance
import java.util.*

class MovieFormActivity : AppCompatActivity() {
    private var genreView: TextView? = null
    private var ageRatedSpinner: Spinner? = null
    private var backBtn: Button? = null
    private var submitBtn: Button? = null
    private var database: RoomDB? = null
    private lateinit var selectedGenres: BooleanArray
    private var selectedGenreList: MutableList<Int>? = null
    private lateinit var genreArray: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_form)
        init()
        submitBtn!!.setOnClickListener(submitMovieFormListener)
        backBtn!!.setOnClickListener { view: View? ->
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        genreView!!.setOnClickListener { v: View? -> showGenreDialog() }
    }

    private fun init() {
        val context = applicationContext
        database = getInstance(context)
        backBtn = findViewById(R.id.back_btn)
        submitBtn = findViewById(R.id.submit_btn)

        // Spinner age rate
        ageRatedSpinner = findViewById<View>(R.id.spinner_age_rated) as Spinner
        val ageRateAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.age_rate)
        )
        ageRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageRatedSpinner!!.adapter = ageRateAdapter

        // Checkbox Genre
        genreArray = resources.getStringArray(R.array.genre)
        selectedGenres = BooleanArray(genreArray.size)
        selectedGenreList = ArrayList()
        genreView = findViewById(R.id.input_genre)
    }

    private fun showGenreDialog() {
        val genreBuilder = AlertDialog.Builder(this)
        genreBuilder.setTitle("Choose Genre(s)")
        genreBuilder.setMultiChoiceItems(genreArray, selectedGenres)
        { dialogInterface: DialogInterface?, i: Int, checked: Boolean ->
            if (checked) {
                selectedGenreList!!.add(i)
                selectedGenreList!!.sort()
            } else selectedGenreList!!.removeAt(i)

        }
        genreBuilder.setPositiveButton("OK") { dialogInterface: DialogInterface?, i: Int ->
            val stringBuilder = StringBuilder()
            for (j in selectedGenreList!!.indices) {
                stringBuilder.append(genreArray[selectedGenreList!![j]])

                if (j != selectedGenreList!!.size - 1) stringBuilder.append(", ")
            }
            genreView!!.text = stringBuilder.toString()
        }
        genreBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
        genreBuilder.setNeutralButton("Clear all") { dialogInterface: DialogInterface?, i: Int ->

            for (j in selectedGenreList!!.indices) selectedGenres[j] = false

            selectedGenreList!!.clear()
            genreView!!.text = ""
        }
        val dialog = genreBuilder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private val submitMovieFormListener = View.OnClickListener {
        val newMovie = Movie()
        val isValid = ArrayList<Boolean>()
        var promp = findViewById<TextView>(R.id.promp_input_title)
        var input = findViewById<EditText>(R.id.input_title)
        var text = input.text.toString()
        isValid.add(checkInput(promp, text, "textInput"))
        newMovie.title = text
        promp = findViewById(R.id.promp_input_date)
        input = findViewById(R.id.input_release_date)
        text = input.text.toString()
        isValid.add(checkInput(promp, text, "yearInput"))
        newMovie.releaseDate = text
        promp = findViewById(R.id.promp_input_age_rated)
        text = ageRatedSpinner!!.selectedItem.toString()
        isValid.add(checkInput(promp, text, "ageInput"))
        newMovie.ageRated = text
        promp = findViewById(R.id.promp_input_genre)
        text = genreView!!.text.toString()
        isValid.add(checkInput(promp, text, "textInput"))
        newMovie.genre = ArrayList(Arrays.asList(*text.split(", ").toTypedArray()))
        promp = findViewById(R.id.promp_input_score)
        input = findViewById(R.id.input_score)
        text = input.text.toString()
        isValid.add(checkInput(promp, text, newMovie))
        input = findViewById(R.id.input_img_url)
        text = input.text.toString()
        if (isValidUrl(text)) newMovie.imgUrl = text else newMovie.imgUrl =
            R.drawable.no_img.toString()

        promp = findViewById(R.id.promp_input_plot)
        input = findViewById(R.id.input_plot)
        text = input.text.toString()
        isValid.add(checkInput(promp, text, "textInput"))
        newMovie.plot = text
        if (!isValid.contains(false)) {
            database!!.movieDao()!!.insert(newMovie)
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkInput(promp: TextView, text: String, movie: Movie): Boolean {
        return try {
            val score = text.toInt()
            if (score in 1..10) {
                promp.visibility = View.GONE
                movie.score = score
                return true
            }
            promp.visibility = View.VISIBLE
            false
        } catch (nfe: NumberFormatException) {
            promp.visibility = View.VISIBLE
            false
        }
    }

    private fun checkInput(promp: TextView, text: String, type: String): Boolean {
        when (type) {
            "textInput" -> {
                return if (text == "") {
                    promp.visibility = View.VISIBLE
                    false
                } else {
                    promp.visibility = View.GONE
                    true
                }
            }
            "yearInput" -> {
                return try {
                    val movieCreatedYear = text.toInt()
                    if (movieCreatedYear in 1871..2050) {
                        promp.visibility = View.GONE
                        return true
                    }
                    promp.visibility = View.VISIBLE
                    false
                } catch (nfe: NumberFormatException) {
                    promp.visibility = View.VISIBLE
                    false
                }
            }
            "ageInput" -> {
                return if (text == "Age rate *") {
                    promp.visibility = View.VISIBLE
                    false
                } else {
                    promp.visibility = View.GONE
                    true
                }
            }
            else -> return false
        }
    }


}