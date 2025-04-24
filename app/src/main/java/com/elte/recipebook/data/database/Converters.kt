package com.elte.recipebook.data.database

import androidx.room.TypeConverter
import com.elte.recipebook.data.TypeOfMeal
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromTypeOfMeal(value: TypeOfMeal): String = value.name

    @TypeConverter
    fun toTypeOfMeal(value: String): TypeOfMeal = TypeOfMeal.valueOf(value)
}