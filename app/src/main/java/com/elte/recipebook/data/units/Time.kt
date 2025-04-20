package com.elte.recipebook.data.units

enum class Time(override val symbol: String) : UnitInterface {
    SECOND("s"),
    MINUTE("min"),
    HOUR("h"),
    DAY("d")
}