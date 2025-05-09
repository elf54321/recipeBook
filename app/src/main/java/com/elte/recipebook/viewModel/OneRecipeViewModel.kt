package com.elte.recipebook.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.elte.recipebook.data.database.AppDatabase
import com.elte.recipebook.data.entities.Recipe
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elte.recipebook.data.entities.IngredientWithNutrition
import kotlinx.coroutines.launch

class OneRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()

    private val _recipe = MutableLiveData<Recipe?>()
    val recipe: LiveData<Recipe?> = _recipe

    private val _ingredients = MutableLiveData<List<IngredientWithNutrition>>()
    val ingredients: LiveData<List<IngredientWithNutrition>> = _ingredients
/*
    fun getRecipeDetails(recipeId: Int) {
        viewModelScope.launch {
            val recipe = recipeDao.getRecipeById(recipeId)
            _recipe.postValue(recipe)

            val ingredients = recipeDao.getIngredientsByRecipeId(recipeId)
            val fullData = ingredients.map {
                val nutrition = recipeDao.getNutritionById(it.nutritionId)
                IngredientWithNutrition(it, nutrition)
            }
            _ingredients.postValue(fullData)
        }
    }
 */
    fun getRecipeById(recipeId: Int) {
        viewModelScope.launch {
            val fetchedRecipe = recipeDao.getRecipeById(recipeId)
            _recipe.postValue(fetchedRecipe)
        }
    }

    suspend fun deleteRecipeById(id: Int) {
        recipeDao.deleteRecipeById(id)
        _recipe.postValue(null)
    }
}

