package com.elte.recipebook.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.elte.recipebook.ui.screens.*

@Composable
fun RecipeNavGraph(navController: NavHostController, modifier: Modifier = Modifier, navigateToRoute: (String) -> Unit) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navigateToRoute, modifier) }
        composable("saved") { SavedScreen(modifier) }
        composable("add") { AddRecipeScreen(modifier) }
        composable("grocery") { GroceryScreen(modifier) }
        composable("recipe/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toIntOrNull()
            if (recipeId != null) {
                OneRecipeScreen(recipeId = recipeId, navigateToRoute, modifier)
            }
        }
    }
}
