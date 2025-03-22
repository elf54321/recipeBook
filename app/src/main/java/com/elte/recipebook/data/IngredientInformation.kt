package com.elte.recipebook.data

data class IngredientInformation (
    val iD: Int,
    var ingredient: Ingredient,
    var quantity: Double,
    var unit: UnitOfMeasure
)