package com.elte.recipebook.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elte.recipebook.data.entities.Ingredient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingListManager @Inject constructor() {
    var currentIngredients by mutableStateOf<List<Ingredient>>(emptyList())
        private set

    //    fun setIngredients(ingredients: List<Ingredient>) {
//        currentIngredients = ingredients
//    }
    fun addIngredients(ingredients: List<Ingredient>) {
        currentIngredients = currentIngredients + ingredients
    }

    fun clear() {
        currentIngredients = emptyList()
    }
}