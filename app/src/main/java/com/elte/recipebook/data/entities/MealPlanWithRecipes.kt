package com.elte.recipebook.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Relation class to load a MealPlan along with its recipes.
data class MealPlanWithRecipes(
    @Embedded val mealPlan: MealPlan,
    @Relation(
        parentColumn = "iD",
        entityColumn = "id",
        associateBy = Junction(
            value = MealPlanRecipeCrossRef::class,
            parentColumn = "mealPlanId",
            entityColumn = "recipeId"
        )
    )
    val recipes: List<Recipe>
)