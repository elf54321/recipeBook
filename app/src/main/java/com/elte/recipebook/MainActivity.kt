package com.elte.recipebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.elte.recipebook.nav.RecipeNavGraph
import com.elte.recipebook.ui.components.BottomNavBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val currentBackStack = navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStack.value?.destination?.route

            val navigateToRoute: (String) -> Unit = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }
            Scaffold(
                bottomBar = { BottomNavBar(navController, currentRoute) }
            ) { padding ->
                RecipeNavGraph(navController = navController, modifier = Modifier.padding(padding), navigateToRoute)
            }
        }
    }
}
