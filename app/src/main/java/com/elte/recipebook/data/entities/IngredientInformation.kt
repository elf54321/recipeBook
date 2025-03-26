package com.elte.recipebook.data.entities

// This entity joins a Recipe with an Ingredient and a UnitOfMeasure.
// The provided iD field is used as its primary key, and additional columns store foreign key references.
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredient_information",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["iD"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["iD"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UnitOfMeasure::class,
            parentColumns = ["iD"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId"), Index("ingredientId"), Index("unitId")]
)
data class IngredientInformation (
    @PrimaryKey
    val iD: Int,
    val recipeId: Int,      // references Recipe.iD
    val ingredientId: Int,  // references Ingredient.iD
    var quantity: Double,
    val unitId: Int         // references UnitOfMeasure.iD
)