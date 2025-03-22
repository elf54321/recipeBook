package com.elte.recipebook.data

data class Ingredient (
    val iD: Int,
    var name: String,
    var price: Double,
    var priceCurrency: String,
    var nutrition: Nutrition
)