package com.elte.recipebook.data.units

enum class Length(override val symbol: String) : UnitInterface {
    MILLIMETER("mm"),
    CENTIMETER("cm"),
    DECIMETRE("dm"),
    METER("m")
}