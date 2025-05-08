package com.elte.recipebook.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/*
* The Ingredient entity uses its own iD as primary key and
* stores a foreign key (nutritionId) referencing Nutrition.
*/

@Entity(
    tableName = "ingredient",
    foreignKeys = [
        ForeignKey(
            entity = Nutrition::class,
            parentColumns = ["iD"],
            childColumns = ["nutritionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("nutritionId"),
       // Index(value = ["name"], unique = true)  // <- enforcing unique ingredients
    ]
)
// Info about a unique ingredient.
data class Ingredient (
    @PrimaryKey
    val iD: Int,
    var name: String,
    var price: Double, // How much does a unit cost from this Ingredient. (kg,piece,package...)
    var priceCurrency: String, // Auto-fill based on region/location data.
    val nutritionId: Int  // references Nutrition.iD
)