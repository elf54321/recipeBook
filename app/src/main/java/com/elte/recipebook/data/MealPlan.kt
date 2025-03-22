package com.elte.recipebook.data

import java.util.Date

data class MealPlan (
    val iD: Int,
    var receipes: List<Recipe>,
    var date: Date,
    var comment: String
)