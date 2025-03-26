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
    indices = [Index("nutritionId")]
)
data class Ingredient (
    @PrimaryKey
    val iD: Int,
    var name: String,
    var price: Double,
    var priceCurrency: String,
    val nutritionId: Int  // references Nutrition.iD
)