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
            parentColumns = ["id"],
            childColumns = ["nutritionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("nutritionId"),
        Index(value = ["name"], unique = true)
    ]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nutritionId: Int,    // → Nutrition.id Foreign Key

    val name: String,
    val price: Double,
    val priceCurrency: String,
    val quantity: Double,
    val unit: String
)