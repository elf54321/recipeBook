package com.elte.recipebook.nav

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.elte.recipebook.ui.screens.*
import com.elte.recipebook.viewModel.AddRecipeViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun RecipeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateToRoute: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home")    { HomeScreen(navigateToRoute, modifier) }
        composable("saved")   { SavedScreen(modifier) }
        composable("ocr")      {
            // Navigation change: now the add process starts from ocr
            OcrScreen(modifier = modifier,
                      navigateToStep = { navigateToRoute("add") },
                      navController  = navController)
        }
        composable("add")     {
            AddRecipeScreen(
                modifier       = modifier,
                navigateToStep = { navigateToRoute("add/details") },
                navController  = navController,
            )
        }
        composable("add/details") {
            // when user clicks “Select ingredients” inside this screen,
            // call `navController.navigate("select_ingredients")`
            AddRecipeDetailScreen(
                modifier      = modifier,
                navController = navController
            )
        }

        // ←— NEW: Select from existing ingredients, or jump to create new
        composable("add/selectIngredients") {
            SelectIngredientsScreen(
                modifier            = modifier,
                navController       = navController,
                onAddNewIngredient  = {
                    // when the user taps “Add New Ingredient”
                    navController.navigate("add/createIngredient")
                },
                onDone              = {
                    // when they’re finished picking ingredients
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }

        composable("add/createIngredient") {
            CreateNewIngredientsScreen(
                modifier            = modifier,
                navController       = navController,
                parentEntity        = "add",
                onIngredientCreated = {
                    // after we’ve saved the new ingredient, pop back
                    navController.popBackStack(
                        route     = "add/selectIngredients",
                        inclusive = false
                    )
                }
            )
        }

        composable("grocery")  { GroceryScreen(modifier) }
        composable("recipe/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments
                ?.getString("recipeId")
                ?.toIntOrNull()
            if (recipeId != null) {
                OneRecipeScreen(
                    recipeId,
                    navigateToRoute,
                    navController,
                    modifier)
            }
        }
    }
}
