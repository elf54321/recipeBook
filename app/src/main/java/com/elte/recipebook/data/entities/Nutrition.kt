package com.elte.recipebook.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition")
data class Nutrition(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val energy: Int,// kcal
    val fat: Double,
    val protein: Double,
    val carbohydrate: Double,
    val sugar: Double,
    val salt: Double,
    val fiber: Double
)