package com.elte.recipebook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.elte.recipebook.data.database.AppDatabase
import com.elte.recipebook.data.entities.MealPlan
import com.elte.recipebook.data.entities.MealPlanRecipeCrossRef
import com.elte.recipebook.data.entities.MealPlanWithRecipes
import com.elte.recipebook.data.entities.Recipe
import kotlinx.coroutines.launch
import java.util.Date

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val mealPlanDao = AppDatabase.getDatabase(application).mealPlanDao()
    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()

    val allRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes().asLiveData()

    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.insert(recipe)
        }
    }

    fun addMealPlanForDate(date: Date, recipes: List<Recipe>, comment: String = "") {
        viewModelScope.launch {
            val mealPlan = MealPlan(iD = date.hashCode(), date = date, comment = comment)
            mealPlanDao.insert(mealPlan)

            recipes.forEach { recipe ->
                val mealPlanRecipeCrossRef = MealPlanRecipeCrossRef(
                    mealPlanId = mealPlan.iD,
                    recipeId = recipe.id
                )
                mealPlanDao.insertMealPlanRecipeCrossRef(mealPlanRecipeCrossRef)
            }
        }
    }

    fun getMealPlanForDate(date: Date): LiveData<List<MealPlanWithRecipes>> {
        return mealPlanDao.getMealPlanWithRecipesForDate(date).asLiveData()
    }
    fun removeMealFromPlan(day: Date, recipe: Recipe) {
        viewModelScope.launch {
            val mealPlanId = day.hashCode() // same logic used in addMealPlanForDate
            mealPlanDao.deleteMealFromMealPlan(mealPlanId, recipe.id)
        }
    }


}