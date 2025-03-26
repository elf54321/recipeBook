package com.elte.recipebook.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// MealPlan uses its own iD as primary key.
// Its many‑to‑many relationship with Recipe is handled by a separate junction entity.

@Entity(tableName = "meal_plan")
data class MealPlan (
    @PrimaryKey
    val iD: Int,
    var date: Date,
    var comment: String
)