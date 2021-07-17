package com.example.newdo.database

import androidx.room.TypeConverter
import com.example.newdo.database.model.Source

class Converters {

    @TypeConverter
    fun convertFromSource(source: Source) : String {
        return source.name
    }

    @TypeConverter
    fun convertToSource(name: String) : Source {
        return Source(name, name)
    }
}