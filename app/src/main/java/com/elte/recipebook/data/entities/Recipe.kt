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
/*
data class Recipe (
    // Added with Recipe screen
    val iD: Int,
    var name: String,
    val imageUri: String? = null,
    var instructions: String, //description
    var source: String? = null, // What is the recipe s origin.

    // Added with Recipe Detail screen
    var portion: Double, // This recipie is for this many person.
    var typeOfMeal: Enum<TypeOfMeal>,
    var priceCategory: String,
    var equipment: Enum<Equipment>,

    // Added with Select Ingredients
    var nutrition: Nutrition? = null, // ez lehet kimegy
    var ingredients: List<IngredientInformation>


)
*/
