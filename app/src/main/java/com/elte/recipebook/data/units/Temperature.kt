package com.elte.recipebook.data.units

enum class Temperature(override val symbol: String) : UnitInterface {
    CELSIUS("°C"),
    KELVIN("K")
}