package com.elte.recipebook.data

enum class PriceCategory(val symbol: String) {
    BUDGET("€"),
    STANDARD("€€"),
    PREMIUM("€€€"),
    LUXURY("€€€€");

    override fun toString(): String = symbol
}