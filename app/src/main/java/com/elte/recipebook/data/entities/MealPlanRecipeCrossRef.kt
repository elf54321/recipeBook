package com.elte.recipebook.data.entities

import androidx.room.Entity

// The junction entity that links MealPlan and Recipe. Uses Composite key.

@Entity(primaryKeys = ["mealPlanId", "recipeId"])
data class MealPlanRecipeCrossRef(
    val mealPlanId: Int,
    val recipeId: Int
)