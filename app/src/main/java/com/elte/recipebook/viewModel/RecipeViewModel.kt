package com.elte.recipebook.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data classes
import com.elte.recipebook.data.dao.RecipeDao
import com.elte.recipebook.data.entities.Recipe
import com.elte.recipebook.data.TypeOfMeal
import com.elte.recipebook.data.Equipment
import com.elte.recipebook.data.PriceCategory

class RecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao
) : ViewModel() {

    // — UI state, backed by Compose’s mutableStateOf
        var name by mutableStateOf("")
            private set

        var description by mutableStateOf("")
            private set

        var portionText by mutableStateOf("")    // collect as String, convert later
            private set

        // image URI string
        private var imageUri by mutableStateOf<String?>(null)

        // Backing state (private) + public getters for dropdowns
            private var _selectedType by mutableStateOf(TypeOfMeal.BREAKFAST)
            val selectedType: TypeOfMeal
                get() = _selectedType

            private val _selectedEquipment = mutableStateListOf<Equipment>()
            val selectedEquipment: List<Equipment>
                get() = _selectedEquipment

            private var _selectedPriceCategory by mutableStateOf(PriceCategory.BUDGET)
            val selectedPriceCategory: PriceCategory
                get() = _selectedPriceCategory

        // Options for dropdowns
            val availableTypes: List<TypeOfMeal>       = TypeOfMeal.entries
            val availableEquipment: List<Equipment>    = Equipment.entries
            val availablePriceCategories: List<PriceCategory> = PriceCategory.entries

    // — UI event handlers
        fun onNameChange(new: String)         { name = new }
        fun onDescriptionChange(new: String) { description = new }
        fun onPortionChange(new: String)      { portionText = new }
        fun onImageSelected(uri: Uri?) { imageUri = uri?.toString() }

        // Handlers to update Dropdown states
        fun onTypeSelected(type: TypeOfMeal) {
            _selectedType = type
        }

        fun toggleEquipment(eq: Equipment) {
            if (_selectedEquipment.contains(eq)) _selectedEquipment.remove(eq)
            else                                 _selectedEquipment.add(eq)
        }

        fun onPriceCategorySelected(pc: PriceCategory) {
            _selectedPriceCategory = pc
        }

    // — Insert to db
        fun insertRecipe(onSuccess: () -> Unit = {}) {
            // Validation
            if (name.isBlank()) return

            // val portion = portionText.toDoubleOrNull() ?: 1.0

            val newRecipe = Recipe(
                name         = name.trim(),
                description = description.trim(),
                imageUri     = imageUri,
            )

            viewModelScope.launch {
                recipeDao.insert(newRecipe)
                resetForm()
                onSuccess()
            }
        }

    /*
    For adding Nutrition, Ingredient etc

        fun onNutritionChange(updated: Nutrition) {
        }
        fun addIngredient(info: IngredientInformation) {
        }
        fun removeIngredient(info: IngredientInformation) {
        }

    */
    // — Reset the form (Can be added as a button)
        private fun resetForm() {
            name         = ""
            description = ""
            imageUri     = null
        }
}