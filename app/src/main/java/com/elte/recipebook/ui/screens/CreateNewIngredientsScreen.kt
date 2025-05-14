package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elte.recipebook.data.entities.Nutrition
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.viewModel.AddRecipeViewModel
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import com.elte.recipebook.ui.theme.SunnyYellow


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun CreateNewIngredientsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onIngredientCreated: () -> Unit,
    parentEntity : String
) {
    // Shared VM from the "add" nav-entry
    val parentEntry = remember { navController.getBackStackEntry(parentEntity) }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)

    // Scroll state
    val scrollState = rememberScrollState()

    // Local UI state for ingredient properties
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }

    // Local UI state for nutrition properties
    var energy by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbohydrate by remember { mutableStateOf("") }
    var sugar by remember { mutableStateOf("") }
    var salt by remember { mutableStateOf("") }
    var fiber by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoftBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create New Ingredient",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Ingredient fields
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor   = SunnyYellow
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = "Nutrition (per unit)",
            style = MaterialTheme.typography.titleMedium
        )

        // Nutrition fields
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = energy,
                onValueChange = { energy = it },
                label = { Text("Energy (kcal)") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat (g)") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein (g)") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = carbohydrate,
                onValueChange = { carbohydrate = it },
                label = { Text("Carbs (g)") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sugar,
                onValueChange = { sugar = it },
                label = { Text("Sugar (g)") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = salt,
                onValueChange = { salt = it },
                label = { Text("Salt (g)") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fiber,
                onValueChange = { fiber = it },
                label = { Text("Fiber (g)") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = SunnyYellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Parse inputs and create data objects
                val nutritionObj = Nutrition(
                    energy = energy.toIntOrNull() ?: 0,
                    fat = fat.toDoubleOrNull() ?: 0.0,
                    protein = protein.toDoubleOrNull() ?: 0.0,
                    carbohydrate = carbohydrate.toDoubleOrNull() ?: 0.0,
                    sugar = sugar.toDoubleOrNull() ?: 0.0,
                    salt = salt.toDoubleOrNull() ?: 0.0,
                    fiber = fiber.toDoubleOrNull() ?: 0.0
                )
                viewModel.createIngredient(
                    name = name,
                    price = price.toDoubleOrNull() ?: 0.0,
                    currency = currency,
                    quantity = quantity.toDoubleOrNull() ?: 0.0,
                    unit = unit,
                    nutrition = nutritionObj
                )
                onIngredientCreated()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow)
        ) {
            Text(text = "Save Ingredient",color = Color.Black)
        }
    }
}
