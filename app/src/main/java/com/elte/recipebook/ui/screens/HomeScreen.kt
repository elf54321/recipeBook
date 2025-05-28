package com.elte.recipebook.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.elte.recipebook.data.PriceCategory
import com.elte.recipebook.data.TypeOfMeal
import com.elte.recipebook.data.entities.Recipe
import com.elte.recipebook.ui.components.FilterSection
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.viewModel.ShowAllRecipeViewModel

private val typeOfMealEmojis = mapOf(
    TypeOfMeal.BREAKFAST to "🍳",
    TypeOfMeal.LUNCH to "🍛",
    TypeOfMeal.DINNER to "🍽",
    TypeOfMeal.DESSERT to "🍰",
    TypeOfMeal.SNACK to "🥪",
    TypeOfMeal.DRINK to "🥤"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowAllRecipeViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showFilterDialog by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf<TypeOfMeal?>(null) }
    var selectedPriceFilter by remember { mutableStateOf<PriceCategory?>(null) }

    var tempTypeFilter by remember { mutableStateOf<TypeOfMeal?>(null) }
    var tempPriceFilter by remember { mutableStateOf<PriceCategory?>(null) }

    LaunchedEffect(showFilterDialog) {
        if (showFilterDialog) {
            tempTypeFilter = selectedFilter
            tempPriceFilter = selectedPriceFilter
        }
    }

    val recipes: List<Recipe> by viewModel.allRecipes.observeAsState(emptyList())
    LaunchedEffect(Unit) { viewModel.getAllRecipe() }

    val filteredRecipes = remember(recipes, searchQuery, selectedFilter, selectedPriceFilter) {
        recipes.filter { recipe ->
            val matchesSearch = searchQuery.text.isBlank() ||
                    recipe.name.contains(searchQuery.text, ignoreCase = true) ||
                    recipe.description.contains(searchQuery.text, ignoreCase = true)
            val matchesType = selectedFilter == null || recipe.typeOfMeal == selectedFilter
            val matchesPrice = selectedPriceFilter == null || recipe.priceCategory == selectedPriceFilter

            matchesSearch && matchesType && matchesPrice
        }
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(SoftBackground, Color(0xFFFFECB3))))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search recipes...") },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        disabledContainerColor = Color(0xFFF0F0F0),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SunnyYellow, shape = CircleShape)
                        .clickable { showFilterDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = Color.Black)
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyRow {
                items(TypeOfMeal.values()) { type ->
                    val emoji = typeOfMealEmojis[type] ?: ""
                    val isSelected = selectedFilter == type
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                if (isSelected) SunnyYellow else Color.White,
                                CircleShape
                            )
                            .clickable { selectedFilter = type }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "$emoji ${type.displayText}",
                            color = if (isSelected) Color.Black else Color.DarkGray
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(filteredRecipes) { recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { navigateToRoute("recipe/${recipe.id}") },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            recipe.imageUri?.let { uriString ->
                                Image(
                                    painter = rememberAsyncImagePainter(uriString),
                                    contentDescription = "Recipe Image",
                                    modifier = Modifier.fillMaxWidth().height(150.dp)
                                )
                            } ?: run {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .background(Color.LightGray)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(recipe.name, style = MaterialTheme.typography.titleMedium, color = Color.Black)
                            Text(recipe.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }
                }
            }

            if (showFilterDialog) {
                Dialog(
                    onDismissRequest = { showFilterDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        tonalElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // ── Meal Type Picker ─────────────────────
                            Text("🍽 Meal Type", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(8.dp))
                            LazyRow {
                                items(TypeOfMeal.values()) { type ->
                                    val emoji = typeOfMealEmojis[type] ?: ""
                                    val isSelected = tempTypeFilter == type
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .background(
                                                if (isSelected) SunnyYellow else Color.White,
                                                CircleShape
                                            )
                                            .clickable {
                                                tempTypeFilter = if (isSelected) null else type
                                            }
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            "$emoji ${type.displayText}",
                                            color = if (isSelected) Color.Black else Color.DarkGray
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // ── Price Category Picker ─────────────────
                            Text("💰 Price Category", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(8.dp))
                            LazyRow {
                                items(PriceCategory.values()) { price ->
                                    val isSelected = tempPriceFilter == price
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .background(
                                                if (isSelected) SunnyYellow else Color.White,
                                                CircleShape
                                            )
                                            .clickable {
                                                tempPriceFilter = if (isSelected) null else price
                                            }
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            price.name,
                                            color = if (isSelected) Color.Black else Color.DarkGray
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // ── Actions ──────────────────────────────
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        // reset the dialog picks
                                        tempTypeFilter = null
                                        tempPriceFilter = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                                ) {
                                    Text("Clean", color = Color.Black)
                                }
                                Button(
                                    onClick = {
                                        // commit to the real filters
                                        selectedFilter = tempTypeFilter
                                        selectedPriceFilter = tempPriceFilter
                                        showFilterDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow)
                                ) {
                                    Text("Apply", color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }


        }
    }
}
