package com.elte.recipebook.data.units

enum class Volume(override val symbol: String) : UnitInterface {
    MILLILITER("mL"),
    LITER("L"),
    TEASPOON("tsp"),
    TABLESPOON("tbsp"),
    CUP("cup")
}