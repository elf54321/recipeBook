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
        Index(value = ["name"], unique = true)  // <- enforcing unique ingredients
    ]
)
//Example:
// (Quantity x Unit) Name costs Price PriceCurrency.
// 2 l milk costs 800 Ft
data class Ingredient (
    @PrimaryKey(autoGenerate = true)
    val iD: Int =0,
    val nutritionId: Int,  // references Nutrition.iD
    var name: String,
    var price: Double,
    var priceCurrency: String,
    var quantity: Double,
    val unit: String
)