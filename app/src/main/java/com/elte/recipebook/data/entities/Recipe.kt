package com.elte.recipebook.data.entities

// Stores a foreign key (nutritionId) referencing Nutrition
// The enum field typeOfMeal will be handled with a type converter.

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe",
    foreignKeys = [
        ForeignKey(
            entity = Nutrition::class,
            parentColumns = ["iD"],
            childColumns = ["nutritionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("nutritionId")]
)
data class Recipe (
    @PrimaryKey
    val iD: Int,
    var name: String,
    var source: String,
    var instructions: String,
    var portion: Double,
    var typeOfMeal: TypeOfMeal,  // Use a converter for this enum
    var priceCategory: String,
    val nutritionId: Int         // references Nutrition.iD
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