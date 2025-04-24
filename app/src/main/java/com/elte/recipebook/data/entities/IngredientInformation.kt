package com.elte.recipebook.data.entities

// This entity joins a Recipe with an Ingredient and a UnitOfMeasure.
// The provided iD field is used as its primary key, and additional columns store foreign key references.
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.elte.recipebook.data.units.UnitInterface

@Entity(
    tableName = "ingredient_information",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["iD"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId"), Index("ingredientId")]
)
data class IngredientInformation (
    @PrimaryKey
    val id: Int,
    val recipeId: Int,      // references Recipe.iD
    val ingredientId: Int,  // references Ingredient.iD
    var quantity: Double,
    val unit: String // The conversion from uni to string happens in the View Model.
)