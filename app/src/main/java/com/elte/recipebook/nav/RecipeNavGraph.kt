package com.elte.recipebook.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.elte.recipebook.ui.screens.*

@Composable
fun RecipeNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(modifier) }
        composable("saved") { SavedScreen(modifier) }
        composable("add") { AddRecipeScreen(modifier) }
        composable("grocery") { GroceryScreen(modifier) }
        composable("profile") { ProfileScreen(modifier) }
    }
}
