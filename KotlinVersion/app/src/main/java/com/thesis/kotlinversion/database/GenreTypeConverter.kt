package com.thesis.kotlinversion.database

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.util.ArrayList

class GenreTypeConverter {
    @TypeConverter
    fun fromString(value: String?): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<String?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}