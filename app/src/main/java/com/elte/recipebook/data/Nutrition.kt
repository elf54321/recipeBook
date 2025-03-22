package com.elte.recipebook.data

data class Nutrition (
    val iD: Int,
    var energy: Double,
    var fat: Double,
    var protein: Double,
    var carbohydrate: Double,
    var sugar: Double,
    var salt: Double,
    var fiber: Double
)