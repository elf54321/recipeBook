package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elte.recipebook.data.entities.Nutrition
import com.elte.recipebook.viewModel.AddRecipeViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun CreateNewIngredientsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onIngredientCreated: () -> Unit
) {
    // Obtain the shared VM from the "add" nav-entry
    val parentEntry = remember { navController.getBackStackEntry("add") }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency") },
                modifier = Modifier.weight(1f)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = "Nutrition (per unit)",
            style = MaterialTheme.typography.titleMedium
        )

        // Nutrition fields
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = energy,
                onValueChange = { energy = it },
                label = { Text("Energy (kcal)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat (g)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein (g)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = carbohydrate,
                onValueChange = { carbohydrate = it },
                label = { Text("Carbs (g)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sugar,
                onValueChange = { sugar = it },
                label = { Text("Sugar (g)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = salt,
                onValueChange = { salt = it },
                label = { Text("Salt (g)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fiber,
                onValueChange = { fiber = it },
                label = { Text("Fiber (g)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))
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
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Save Ingredient")
        }
    }
}
