package com.elte.recipebook.data.entities

// Stores a foreign key (nutritionId) referencing Nutrition
// The enum field typeOfMeal will be handled with a type converter.

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageUri: String? = null
)

//data class Recipe (
//    val iD: Int,
//    var ingredients: List<IngredientInformation>,
//    var name: String,
//    var source: String,
//    var instructions: String,
//    var portion: Double,
//    var typeOfMeal: Enum<TypeOfMeal>,
//    var priceCategory: String,
//    var nutrition: Nutrition
//)