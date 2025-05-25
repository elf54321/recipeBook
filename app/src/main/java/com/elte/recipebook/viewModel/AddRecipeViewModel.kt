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
    //Entities
    import com.elte.recipebook.data.entities.RecipeIngredientCrossRef
    import com.elte.recipebook.data.entities.Recipe
    import com.elte.recipebook.data.entities.Ingredient
    import com.elte.recipebook.data.entities.Nutrition

    // Daos, DataAccessObject
    import com.elte.recipebook.data.dao.RecipeDao
    import com.elte.recipebook.data.dao.IngredientDao
    import com.elte.recipebook.data.dao.NutritionDao

import com.elte.recipebook.data.TypeOfMeal
import com.elte.recipebook.data.Equipment
import com.elte.recipebook.data.PriceCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val nutritionDao: NutritionDao,
    val shoppingListManager: ShoppingListManager
) : ViewModel() {
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

        ////////// SelectIngredientsScreen(3rd) //////////
    // --------------------------------------------------------------------------//
        // Backing StateFlow for all ingredients in DB
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

        /* Toggle whether an ingredient is in the selected list */
        fun toggleIngredientSelection(ing: Ingredient) {
            if (_selectedIngredients.contains(ing)) {
                _selectedIngredients.remove(ing)
            } else {
                _selectedIngredients.add(ing)
            }
        }
    // Creating a new recipe in the database
    fun insertRecipe(onSuccess: () -> Unit = {}) {
        // 1) Basic validation
        if (name.isBlank()) return

        viewModelScope.launch {
            // 2) Parse the portion
            val portionValue = portionText.toDoubleOrNull() ?: 1.0

            // 3) Recipe entity
            val newRecipe = Recipe(
                name          = name,
                description   = description,
                imageUri      = imageUriString,
                source        = "AddedByUser",
                portion       = portionValue,
                typeOfMeal    = selectedType,
                priceCategory = selectedPriceCategory,
                equipment     = selectedEquipment.toTypedArray()
                // convert List→Array
                // nutrition id is not part of the db currently
            )

            // 4) Insert and grab the generated ID
            val recipeId = recipeDao.insert(newRecipe).toInt()

            // 5) Populate the junction table
            selectedIngredients.forEach { ing ->
                recipeDao.insertRecipeIngredientCrossRef(
                    RecipeIngredientCrossRef(
                        recipeId       = recipeId,
                        ingredientId   = ing.id        // your Ingredient primary key
                    )
                )
            }

            // 6) Reset form & call success callback
            resetForm()
            shoppingListManager.addIngredients(selectedIngredients.toList())
            onSuccess()
        }
    }
    private fun resetForm() {
        name         = ""
        description  = ""
        imageUri     = null
        _selectedIngredients.clear()
    }
    // --------------------------------------------------------------------------//
    //                        Ingredient - Nutrition Part                        //
    // --------------------------------------------------------------------------//
    fun onSaveIngredient(
        name: String,
        quantityStr: String,
        unit: String,
        priceStr: String,
        currency: String,
        energyStr: String,
        fatStr: String,
        proteinStr: String,
        carbStr: String,
        sugarStr: String,
        saltStr: String,
        fiberStr: String
    ) {
        // Basic required checks
        if (name.length !in 1..30 ||
            unit.isBlank() ||
            quantityStr.toDoubleOrNull() == null ||
            priceStr.toDoubleOrNull() == null ||
            currency.isBlank()
        ) {
            return  // bail out if any required field is bad
        }

        // Build Nutrition object (missing values default to zero)
        val nutrition = Nutrition(
            energy       = energyStr.toIntOrNull()   ?: 0,
            fat          = fatStr.toDoubleOrNull()   ?: 0.0,
            protein      = proteinStr.toDoubleOrNull() ?: 0.0,
            carbohydrate = carbStr.toDoubleOrNull()  ?: 0.0,
            sugar        = sugarStr.toDoubleOrNull() ?: 0.0,
            salt         = saltStr.toDoubleOrNull()  ?: 0.0,
            fiber        = fiberStr.toDoubleOrNull() ?: 0.0
        )

        // Delegate to your DAO-backed saver
        createIngredient(
            name     = name.trim(),
            price    = priceStr.toDouble(),
            currency = currency.trim(),
            quantity = quantityStr.toDouble(),
            unit     = unit.trim(),
            nutrition = nutrition
        )
    }

    /**
     * Inserts Nutrition, then Ingredient, and finally
     * updates the in-memory selected list.
     */
    private fun createIngredient(
        name: String,
        price: Double,
        currency: String,
        quantity: Double,
        unit: String,
        nutrition: Nutrition
    ) {
        viewModelScope.launch {
            // 1) insert Nutrition
            val nutritionId = nutritionDao
                .insertNutrition(nutrition)
                .toInt()

            // 2) build Ingredient (still id = 0)
            val ingredient = Ingredient(
                nutritionId   = nutritionId,
                name          = name,
                price         = price,
                priceCurrency = currency,
                quantity      = quantity,
                unit          = unit
            )

            // 3) insert & grab the generated id
            val newId = ingredientDao.insert(ingredient).toInt()

            // 4) update the instance with its real id
            val savedIngredient = ingredient.copy(id = newId)

            // 5) add to your in-memory list
            _selectedIngredients.add(savedIngredient)
        }
    }

}