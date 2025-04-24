package com.elte.recipebook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elte.recipebook.data.entities.Recipe
import com.elte.recipebook.viewModel.ShowAllRecipeViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
fun SavedScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Weekly Meal Planner",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )
        WeeklyMealPlannerView ()
    }
}

@Composable
fun WeeklyMealPlannerView() {
    val showAllRecipeViewModel: ShowAllRecipeViewModel = viewModel()
    val allRecipes by showAllRecipeViewModel.allRecipes.observeAsState(initial = emptyList())
    val currentDate = remember { LocalDate.now() }
    var currentWeekStartDate by remember { mutableStateOf(currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    val mealsForDay = remember { mutableStateMapOf<LocalDate, MutableList<Recipe>>() }
    var showRecipeSelectionDialog by remember { mutableStateOf(false) }
    var dayForNewMeal by remember { mutableStateOf<LocalDate?>(null) }

    // Show RecipeSelectionDialog when required
    if (showRecipeSelectionDialog && dayForNewMeal != null) {
        RecipeSelectionDialog(
            recipes = allRecipes,
            selectedDay = dayForNewMeal!!, // Pass selectedDay
            showAllRecipeViewModel = showAllRecipeViewModel, // Pass recipeViewModel
            onRecipeSelected = { selectedRecipes ->
                // Handle selected recipes and save them to the meal plan
                showAllRecipeViewModel.addMealPlanForDate(dayForNewMeal!!.toDate(), selectedRecipes)
                showRecipeSelectionDialog = false // Close dialog
                dayForNewMeal = null // Clear the day for the new meal
            },
            onDismiss = {
                showRecipeSelectionDialog = false
                dayForNewMeal = null
            }
        )
    }

    // Check if a day is selected
    if (selectedDay != null) {
        val mealsForDay by showAllRecipeViewModel.getMealPlanForDate(selectedDay!!.toDate()).observeAsState(emptyList())
        DayMealPlanScreen(
            selectedDay = selectedDay!!,
            meals = mealsForDay.flatMap { it.recipes }, // Ensure meals are in a non-null list
            onAddMealClick = {
                dayForNewMeal = selectedDay // Assign the selected day for the new meal
                showRecipeSelectionDialog = true // Show the recipe selection dialog
            },
            onBack = { selectedDay = null },
            showAllRecipeViewModel = showAllRecipeViewModel // Pass recipeViewModel here
        )
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            WeekNavigation(
                currentWeekStartDate = currentWeekStartDate,
                onPreviousWeek = { currentWeekStartDate = currentWeekStartDate.minusWeeks(1) },
                onNextWeek = { currentWeekStartDate = currentWeekStartDate.plusWeeks(1) }
            )
            WeeklyDaySelector(
                currentWeekStartDate = currentWeekStartDate,
                onDayClick = { selectedDay = it }
            )
        }
    }
}

@Composable
fun WeekNavigation(
    currentWeekStartDate: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val weekEndDate = currentWeekStartDate.plusDays(6)
    val formatter = DateTimeFormatter.ofPattern("MMM d")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(onClick = onPreviousWeek) {
            Text("<")
        }
        Text(
            text = "${formatter.format(currentWeekStartDate)} - ${formatter.format(weekEndDate)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Button(onClick = onNextWeek) {
            Text(">")
        }
    }
}

@Composable
fun WeeklyDaySelector(
    currentWeekStartDate: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val daysOfWeek = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
        daysOfWeek.forEach { dayOfWeek ->
            val currentDate = currentWeekStartDate.plusDays(dayOfWeek.value.toLong() - 1)
            DaySelectorItem(
                day = currentDate,
                onDayClick = onDayClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DaySelectorItem(day: LocalDate, onDayClick: (LocalDate) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
            .clickable { onDayClick(day) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = DateTimeFormatter.ofPattern("EEE").format(day),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
fun DayMealPlanScreen(
    selectedDay: LocalDate,
    meals: List<Recipe>,
    onAddMealClick: () -> Unit,
    onBack: () -> Unit,
    showAllRecipeViewModel: ShowAllRecipeViewModel
) {
    val context = LocalContext.current
    val currentDate = selectedDay.toDate() // Convert LocalDate to Date
    var showRecipeSelectionDialog by remember { mutableStateOf(false) }
    var selectedRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }

    // Show Recipe Selection Dialog if required
    if (showRecipeSelectionDialog) {
        showAllRecipeViewModel.allRecipes.value?.let {
            RecipeSelectionDialog(
                recipes = it, // Get all recipes
                selectedDay = selectedDay, // Pass selectedDay here
                showAllRecipeViewModel = showAllRecipeViewModel, // Pass recipeViewModel
                onRecipeSelected = { recipe -> selectedRecipes = selectedRecipes + recipe },
                onDismiss = { showRecipeSelectionDialog = false }
            )
        }
    }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = DateTimeFormatter.ofPattern("EEEE, MMM d").format(selectedDay),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show meals if available
        if (meals.isEmpty()) {
            Text("No meals planned for this day.", color = MaterialTheme.colorScheme.onBackground)
        } else {
            Column {
                Text("Meal Plan:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                meals.forEach { recipe ->
                    MealItem(recipe = recipe, onRemoveClick = {
                        // Handle meal removal logic
                        showAllRecipeViewModel.removeMealFromPlan(selectedDay.toDate(), recipe)
                    })
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Button to add meals
        Button(
            onClick = {
                // Show recipe selection dialog when button is clicked
                showRecipeSelectionDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add meal")
            Text("Add Meal")
        }
    }
}


fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

@Composable
fun MealItem(recipe: Recipe, onRemoveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = recipe.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f)) // Push the remove button to the right
        IconButton(onClick = onRemoveClick) {
            Text(
                text = "✕",
                color = Color.Red,
                fontSize = 20.sp, // Adjust size as needed
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun RecipeSelectionDialog(
    recipes: List<Recipe>,
    onRecipeSelected: (List<Recipe>) -> Unit,
    onDismiss: () -> Unit,
    selectedDay: LocalDate,
    showAllRecipeViewModel: ShowAllRecipeViewModel
) {
    // Track selected recipes
    val selectedRecipes = remember { mutableStateListOf<Recipe>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Recipes") },
        text = {
            if (recipes.isEmpty()) {
                Text("No recipes available.")
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    recipes.forEach { recipe ->
                        val isChecked = selectedRecipes.contains(recipe)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isChecked) {
                                        selectedRecipes.remove(recipe)
                                    } else {
                                        selectedRecipes.add(recipe)
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    if (it) {
                                        selectedRecipes.add(recipe)
                                    } else {
                                        selectedRecipes.remove(recipe)
                                    }
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Immediately save the selected recipes to the meal plan for the selected day
                    showAllRecipeViewModel.addMealPlanForDate(selectedDay.toDate(), selectedRecipes)
                    onRecipeSelected(selectedRecipes) // Optional: Pass the selected recipes back if needed
                    onDismiss()  // Dismiss the dialog
                }
            ) {
                Text("Add Meals")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}