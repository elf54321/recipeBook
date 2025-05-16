package com.elte.recipebook.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.elte.recipebook.data.database.AppDatabase
import com.elte.recipebook.data.entities.Recipe
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elte.recipebook.data.entities.Ingredient
import com.elte.recipebook.data.entities.IngredientWithNutrition
import com.elte.recipebook.data.entities.RecipeIngredientCrossRef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OneRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val recipeDao = db.recipeDao()
    private val nutritionDao = db.nutritionDao()
    private val ingredientDao = db.ingredientDao()

    private val _recipe = MutableLiveData<Recipe?>()
    val recipe: LiveData<Recipe?> = _recipe

    private val _ingredients = MutableLiveData<List<IngredientWithNutrition>>()
    val ingredients: LiveData<List<IngredientWithNutrition>> = _ingredients

    fun getRecipeDetails(recipeId: Int) {
        viewModelScope.launch {
            // 1) Recipe row
            val r = recipeDao.getRecipeById(recipeId)
            _recipe.postValue(r)

            // 2) Ingredients for that recipe
            val ings = recipeDao.getIngredientsByRecipeId(recipeId)

            // 3) For each, load its nutrition row
            val full = ings.map { ing ->
                // getNutritionById returns Nutrition? so handle null if needed
                val nut = nutritionDao.getNutritionById(ing.nutritionId)
                nut?.let { IngredientWithNutrition(ing, it) }
            }
            _ingredients.postValue(full as List<IngredientWithNutrition>?)
            _selectedIngredients.clear()
            _selectedIngredients.addAll(ings)
        }
    }

    fun updateRecipeDetails(
        recipeId: Int,
        title: String,
        description: String,
        imageUri: String?,
        updatedIngredients: List<Ingredient>
    ) {
        viewModelScope.launch {
            val recipe = recipeDao.getRecipeById(recipeId)

            val updatedRecipe = recipe.copy(
                name = title,
                description = description,
                imageUri = imageUri
            )

            recipeDao.update(updatedRecipe)

            recipeDao.deleteCrossRefsByRecipeId(recipeId)

            updatedIngredients.forEach { ingredient ->
                val existing = selectedIngredients.find { it.id == ingredient.id }
                if (existing != null)
                    ingredientDao.update(ingredient)
                else
                    ingredientDao.insert(ingredient)

                val crossRef = RecipeIngredientCrossRef(recipeId, ingredient.id)
                recipeDao.insertRecipeIngredientCrossRef(crossRef)
            }

            getRecipeDetails(recipeId)
        }
    }

    fun refreshIngredients() {
        viewModelScope.launch {
            ingredientDao.getAllIngredients()
                .collect { list ->
                    _allIngredients.value = list
                }
        }
    }

    fun getRecipeById(recipeId: Int) {
        viewModelScope.launch {
            val fetchedRecipe = recipeDao.getRecipeById(recipeId)
            _recipe.postValue(fetchedRecipe)
        }
    }

    private val _allIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val allIngredients: StateFlow<List<Ingredient>> = _allIngredients

    // SnapshotStateList for the ones the user has tapped
    private val _selectedIngredients = mutableStateListOf<Ingredient>()
    val selectedIngredients: List<Ingredient>
        get() = _selectedIngredients

    init {
        // Kick off collecting the ingredient list
        viewModelScope.launch {
            ingredientDao.getAllIngredients()
                .collect { list ->
                    _allIngredients.value = list
                }
        }
    }

    fun toggleIngredientSelection(ing: Ingredient) {
        if (_selectedIngredients.contains(ing)) {
            _selectedIngredients.remove(ing)
        } else {
            _selectedIngredients.add(ing)
        }
    }

    suspend fun deleteRecipeById(id: Int) {
        recipeDao.deleteRecipeById(id)
        recipeDao.deleteCrossRefsByRecipeId(id)
        _recipe.postValue(null)
    }
}

