package com.elte.recipebook.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition")
data class Nutrition(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val energy: Int,// kcal
    val fat: Double ?= 0.0,
    val protein: Double ?= 0.0,
    val carbohydrate: Double ?= 0.0,
    val sugar: Double ?= 0.0,
    val salt: Double ?= 0.0,
    val fiber: Double ?= 0.0
)