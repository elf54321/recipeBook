package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.viewModel.AddRecipeViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun SelectIngredientsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onAddNewIngredient: () -> Unit,
    onDone:            () -> Unit
) {    // Obtain the shared ViewModel from the "add" nav entry
    val parentEntry = remember { navController.getBackStackEntry("add") }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)

    // Observe all ingredients and the current selection
    val allIngredients by viewModel.allIngredients.collectAsState()
    val selectedIngredients = viewModel.selectedIngredients

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoftBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Select The Ingredients",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // List of existing ingredients + “create new” item
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allIngredients) { ingredient ->
                val isSelected = selectedIngredients.contains(ingredient)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SunnyYellow else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleIngredientSelection(ingredient) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Ingredient", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "${"ingredient.name"} kcal/${"ingredient.unit"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // “+ Create new Ingredient” card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onAddNewIngredient)
                ) {
                    Text(
                        text = "+ Create new Ingredient",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Bottom “Add Ingredients” button
        Button(
            onClick = onDone,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        ) {
            Text("Add Ingredients")
        }
    }
}
