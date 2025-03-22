package com.elte.recipebook.data

data class Recipe (
    val iD: Int,
    var ingredients: List<IngredientInformation>,
    var name: String,
    var source: String,
    var instructions: String,
    var portion: Double,
    var typeOfMeal: Enum<TypeOfMeal>,
    var priceCategory: String,
    var nutrition: Nutrition
)