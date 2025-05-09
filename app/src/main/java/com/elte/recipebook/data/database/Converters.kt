package com.elte.recipebook.data.database

import androidx.room.TypeConverter
import com.elte.recipebook.data.Equipment
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

    // Array<Equipment> → String
    @TypeConverter
    fun fromEquipmentArray(equipment: Array<Equipment>): String {
        // join each enum’s name with commas
        return equipment.joinToString(separator = ",") { it.name }
    }

    // String → Array<Equipment>
    @TypeConverter
    fun toEquipmentArray(data: String): Array<Equipment> {
        // if the field was empty, return an empty array
        if (data.isBlank()) return emptyArray()
        // split on commas and map back to enum values
        return data.split(",")
            .map { Equipment.valueOf(it) }
            .toTypedArray()
    }
    /** List<Equipment> → String */
    @TypeConverter
    fun fromEquipmentList(equipment: List<Equipment>?): String? {
        // null or empty → null or ""
        return equipment
            ?.joinToString(",") { it.name }
            ?.takeIf { it.isNotBlank() }
    }

    /** String → List<Equipment> */
    @TypeConverter
    fun toEquipmentList(data: String?): List<Equipment> {
        if (data.isNullOrBlank()) return emptyList()
        return data
            .split(",")
            .map { Equipment.valueOf(it) }
    }
}