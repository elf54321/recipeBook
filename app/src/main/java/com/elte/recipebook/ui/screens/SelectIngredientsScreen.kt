package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.elte.recipebook.viewModel.AddRecipeViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun SelectIngredientsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onAddNewIngredient: () -> Unit,
    onDone:            () -> Unit
) {
    // Obtain the shared AddRecipeViewModel scoped to the "add" flow
    val parentEntry = remember { navController.getBackStackEntry("add") }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Select Ingredients Screen")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onAddNewIngredient) {
            Text(text = "Add New Ingredient")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onDone) {
            Text(text = "Done")
        }
    }
}
