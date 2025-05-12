package com.elte.recipebook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elte.recipebook.ui.theme.DeepText
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.viewModel.ShoppingListViewModel

@Composable
fun GroceryScreen(modifier: Modifier = Modifier) {
    val sharedViewModel: ShoppingListViewModel = hiltViewModel()
    val shoppingListManager = sharedViewModel.shoppingListManager
    val ingredients = shoppingListManager.currentIngredients

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SoftBackground, Color(0xFFFFECB3))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Grocery List",
                style = MaterialTheme.typography.headlineSmall,
                color = DeepText,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            LazyColumn {
                items(ingredients) { ingredient ->
                    Text("${ingredient.quantity} ${ingredient.unit} of ${ingredient.name}")
                }
            }
            Button(
                onClick = {
                    shoppingListManager.clear()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SunnyYellow,
                    contentColor = Color.Black
                ),
            ) {
                Text("Clear list", color = Color.Black)
            }
        }
    }

}