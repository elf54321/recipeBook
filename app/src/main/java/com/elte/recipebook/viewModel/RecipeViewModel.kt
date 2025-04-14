package com.elte.recipebook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elte.recipebook.data.database.AppDatabase
import com.elte.recipebook.data.entities.Recipe
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()

    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.insert(recipe)
        }
    }
}