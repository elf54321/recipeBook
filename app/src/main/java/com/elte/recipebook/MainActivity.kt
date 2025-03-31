package com.elte.recipebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.Black) },
                    label = { Text("Home", color = Color.Black) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Black) },
                    label = { Text("Saved", color = Color.Black) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black) },
                    label = { Text("Add", color = Color.Black) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black) },
                    label = { Text("Grocery", color = Color.Black) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black) },
                    label = { Text("Profile", color = Color.Black) },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> HomeScreen(Modifier.padding(padding))
            else -> Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Coming Soon", color = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSection(title: String, options: List<String>) {
    val selectedOptions = remember { mutableStateListOf<String>() }

    Column {
        Text(
            title,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option in selectedOptions
                OutlinedButton(
                    onClick = {
                        if (isSelected) selectedOptions.remove(option)
                        else selectedOptions.add(option)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) Color(0xFFFFC107) else Color.White,
                        contentColor = if (isSelected) Color.Black else Color(0xFFFFC107)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFFC107)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val filters = listOf(
        "🍽 All", "🥗 Vegetarian", "🌱 Vegan", "⏱ Quick", "🔥 Popular",
        "🆕 New", "💪 Healthy", "🍰 Dessert", "🥖 Low-Carb", "🚫 Gluten-Free"
    )
    var selectedFilter by remember { mutableStateOf("🍽 All") }
    val recipes = listOf(
        "🍝 Quick Pasta Delight",
        "🥗 High-Protein Salad",
        "🍲 15-min Vegan Stew",
        "🍛 Low-Carb Curry",
        "🥘 Grandma’s Goulash",
        "🍜 Dorm-Friendly Ramen",
        "🌮 Budget Taco Bowl",
        "🍳 Protein-Packed Breakfast",
        "🍰 Easy Healthy Dessert",
        "🍕 Minimal-Ingredient Pizza"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3))
                )
            )
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
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF0F0F0),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    )
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFFC107), shape = CircleShape)
                        .clickable { showFilterDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = Color.Black)
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyRow {
                items(filters) { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                if (isSelected) Color(0xFFFFC107) else Color.White,
                                CircleShape
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            filter,
                            color = if (isSelected) Color.Black else Color.DarkGray
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(recipes) { recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(Color.LightGray)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(recipe, style = MaterialTheme.typography.titleMedium, color = Color.Black)
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
                            FilterSection("🍽 Meal Type", listOf("Breakfast", "Lunch", "Dinner", "Dessert", "Snack"))
                            Spacer(Modifier.height(12.dp))
                            FilterSection("🥗 Diet", listOf("Vegetarian", "Vegan", "Gluten-Free", "Dairy-Free", "Low-Carb", "High-Protein"))
                            Spacer(Modifier.height(12.dp))
                            FilterSection("⏱ Time & Difficulty", listOf("Under 15 min", "30 min", "Easy", "Intermediate", "Advanced"))
                            Spacer(Modifier.height(12.dp))
                            FilterSection("💸 Budget", listOf("Cheap", "Moderate", "Fancy"))
                            Spacer(Modifier.height(12.dp))
                            FilterSection("🧰 Equipment", listOf("No Oven", "No Blender", "One Pot Only"))
                            Spacer(Modifier.height(12.dp))
                            FilterSection("💪 Nutrition Goals", listOf("Weight Loss", "Muscle Gain", "Balanced Diet"))
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { showFilterDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                                modifier = Modifier.align(Alignment.End)
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