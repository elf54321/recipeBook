package com.elte.recipebook.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.elte.recipebook.data.database.AppDatabase
import com.elte.recipebook.data.entities.Recipe

class OneRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()

    fun getRecipeById(id: Int): LiveData<Recipe> {
        return recipeDao.getRecipeById(id).asLiveData()
    }

    suspend fun deleteRecipeById(id: Int) {
        recipeDao.deleteRecipeById(id)
    }
}
