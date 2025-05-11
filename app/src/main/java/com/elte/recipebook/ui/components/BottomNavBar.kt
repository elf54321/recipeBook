package com.elte.recipebook.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Default.Home, "Home"),
    BottomNavItem("saved", Icons.Default.Favorite, "Saved"),
    // add recipe starts from ocr
    BottomNavItem("ocr", Icons.Default.Add, "Add"),
    BottomNavItem("grocery", Icons.Default.ShoppingCart, "Grocery"),
    BottomNavItem("recipe/{recipeId}", Icons.Default.Receipt, "Recipe")
)


@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String?) {

    NavigationBar(containerColor = Color.White) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null, tint = Color.Black) },
                label = { Text(item.label, color = Color.Black) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
