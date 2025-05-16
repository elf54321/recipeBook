package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.viewModel.AddRecipeViewModel
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun CreateNewIngredientsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onIngredientCreated: () -> Unit,
    parentEntity: String
) {
    // Shared VM
    val parentEntry = remember { navController.getBackStackEntry(parentEntity) }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)

    val scrollState = rememberScrollState()

    // form state
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var energy by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbohydrate by remember { mutableStateOf("") }
    var sugar by remember { mutableStateOf("") }
    var salt by remember { mutableStateOf("") }
    var fiber by remember { mutableStateOf("") }

    val nutritionFields: List<Pair<String, Pair<String, (String) -> Unit>>> = listOf(
        "Energy (kcal)" to (energy     to { newValue: String -> energy     = newValue }),
        "Fat (g)"       to (fat        to { newValue: String -> fat        = newValue }),
        "Protein (g)"   to (protein    to { newValue: String -> protein    = newValue }),
        "Carbs (g)"     to (carbohydrate to { newValue: String -> carbohydrate = newValue }),
        "Sugar (g)"     to (sugar      to { newValue: String -> sugar      = newValue }),
        "Salt (g)"      to (salt       to { newValue: String -> salt       = newValue }),
        "Fiber (g)"     to (fiber      to { newValue: String -> fiber      = newValue })
    )
    fun filterNumeric(input: String): String {
        return input.filter { char -> char.isDigit() || char == '.' }
    }

    // validation flags
    val nameError     = name.length !in 1..30
    val unitError     = unit.isBlank()
    val qtyError      = quantity.toDoubleOrNull() == null
    val priceError    = price.toDoubleOrNull() == null
    val currencyError = currency.isBlank()

    // overall form validity
    val formValid = !nameError && !unitError && !qtyError && !priceError && !currencyError

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoftBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Create New Ingredient",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // NAME
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 30) name = it },
            label = { Text("Name*") },
            isError = nameError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (nameError) {
            Text(
                "Name must be 1–30 characters",
                color = Color.Red
            )
        }

        // QUANTITY & UNIT
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = filterNumeric(it) },
                label = { Text("Quantity*") },
                isError = qtyError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit*") },
                isError = unitError,
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // PRICE & CURRENCY
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = filterNumeric(it) },
                label = { Text("Price*") },
                isError = priceError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency*") },
                isError = currencyError,
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }


        Text("Nutrition (per unit)")

        // NUTRITION FIELDS (numeric only)
        nutritionFields
            .chunked(2)
            .forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { (label, valueAndSetter) ->
                        val (currentValue, setter) = valueAndSetter
                        OutlinedTextField(
                            value = currentValue,
                            onValueChange = { text: String ->
                                // first filter out non-numeric chars, then call the setter
                                val cleaned = filterNumeric(text)
                                setter(cleaned)
                            },
                            label = { Text(label) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onSaveIngredient(
                    name.trim(),
                    quantity,
                    unit.trim(),
                    price,
                    currency.trim(),
                    energy,
                    fat,
                    protein,
                    carbohydrate,
                    sugar,
                    salt,
                    fiber
                )
                onIngredientCreated()
            },
            enabled = formValid,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow)
        ) {
            Text("Save Ingredient", color = Color.Black)
        }
    }
}
