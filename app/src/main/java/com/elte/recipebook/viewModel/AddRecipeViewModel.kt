package com.elte.recipebook.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Data classes
import com.elte.recipebook.data.dao.RecipeDao
import com.elte.recipebook.data.entities.Recipe
import com.elte.recipebook.data.TypeOfMeal
import com.elte.recipebook.data.Equipment
import com.elte.recipebook.data.PriceCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao
) : ViewModel() {
    // --------------------------------------------------------------------------//
    //                          Nutrition Part                                   //
    // --------------------------------------------------------------------------//




    // --------------------------------------------------------------------------//

    // --------------------------------------------------------------------------//
    //                          Recipe Part                                      //
    // --------------------------------------------------------------------------//

        // Event channel
        private val _eventFlow = MutableSharedFlow<UIEvent>()
        val eventFlow = _eventFlow.asSharedFlow()

        sealed class UIEvent {
            data class ShowSnackbar(val message: String): UIEvent()
        }

        ////////// AddRecipeScreen(1st) //////////

            // — UI state, backed by Compose’s mutableStateOf
            var name by mutableStateOf("")
                private set

            var description by mutableStateOf("")
                private set

            var portionText by mutableStateOf("")    // collect as String, convert later
                private set

            // image URI string
            private var imageUri by mutableStateOf<String?>(null)
                val imageUriString: String? get() = imageUri

            // — UI event handlers
            fun onNameChange(new: String)         { name = new }
            fun onDescriptionChange(new: String) { description = new }
            fun onImageSelected(uri: Uri?) { imageUri = uri?.toString() }

            private fun resetForm() {
                name         = ""
                description = ""
                imageUri     = null
            }


        ////////// AddRecipeDetailScreen(2nd) //////////
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
                fun onPortionChange(new: String)      { portionText = new }

                // Handlers to update Dropdown states
                fun onTypeSelected(type: TypeOfMeal) {
                    _selectedType = type
                }
                fun onPriceCategorySelected(pc: PriceCategory) {
                    _selectedPriceCategory = pc
                }

                fun toggleEquipment(eq: Equipment) {
                    if (_selectedEquipment.contains(eq)) {
                        _selectedEquipment.remove(eq)
                    } else if (_selectedEquipment.size < 12) {
                        _selectedEquipment.add(eq)
                        viewModelScope.launch {
                            _eventFlow.emit(UIEvent.ShowSnackbar("Maximum 12 Equipment is allowed!"))
                        }
                    }
                }
                var isTypeMenuExpanded by mutableStateOf(false)
                    private set
                fun toggleTypeMenu() { isTypeMenuExpanded = !isTypeMenuExpanded }

                var isPriceMenuExpanded by mutableStateOf(false)
                    private set
                fun togglePriceMenu() { isPriceMenuExpanded = !isPriceMenuExpanded }
    // --------------------------------------------------------------------------//

    // Creating a new recipe in the database
    fun insertRecipe(onSuccess: () -> Unit = {}) {
        // Validation
        if (name.isBlank()) return

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
}