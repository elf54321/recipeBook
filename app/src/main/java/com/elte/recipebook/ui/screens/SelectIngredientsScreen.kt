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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elte.recipebook.ui.theme.DeepText
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
) {    // Obtain the shared ViewModel from the "ocr" nav entry
    val parentEntry = remember { navController.getBackStackEntry("ocr") }
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
                        containerColor = if (isSelected) SunnyYellow else SoftBackground,
                        contentColor   = DeepText           // <— drives the text color
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
                        verticalAlignment   = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ingredient.name,
                            style = MaterialTheme.typography.bodyLarge
                            // no explicit color: uses DeepText via contentColor
                        )
                        Text(
                            text = "${ingredient.quantity} ${ingredient.unit}",
                            style = MaterialTheme.typography.bodyMedium
                            // same here
                        )
                    }
                }
            }

            // “+ Add new Ingredient” card stays the same as before
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SoftBackground,
                        contentColor   = DeepText
                    ),
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
                        text = "+ Add new Ingredient",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Button(
            onClick = {
                // Tell the VM to insert the recipe (and its cross-refs)
                viewModel.insertRecipe {
                    // If successful, onDone callback being called
                    onDone()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow)
        ) {
            Text("Create Recipe",color = Color.Black)
        }
    }
}
