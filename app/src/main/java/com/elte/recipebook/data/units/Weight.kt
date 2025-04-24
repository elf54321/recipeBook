package com.elte.recipebook.data.units

enum class Weight(override val symbol: String) : UnitInterface {
    MILLIGRAM("mg"),
    GRAM("g"),
    DECAGRAM("dg"),
    KILOGRAM("kg")
}