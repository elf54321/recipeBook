package com.elte.recipebook.data.entities

import androidx.room.Entity

@Entity(
    tableName = "recipe_ingredient_cross_ref",
    primaryKeys = ["recipeId", "ingredientId"]
)
// Warning, this data class(table) has no enforced FK constraints!
data class RecipeIngredientCrossRef(
    val recipeId: Int,       // → Recipe.id Foreign Key
    val ingredientId: Int    // → Ingredient.id Foreign Key
)