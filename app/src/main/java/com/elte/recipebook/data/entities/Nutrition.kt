package com.elte.recipebook.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition")
data class Nutrition (
    @PrimaryKey
    val iD: Int,
    var energy: Double,
    var fat: Double,
    var protein: Double,
    var carbohydrate: Double,
    var sugar: Double,
    var salt: Double,
    var fiber: Double
)