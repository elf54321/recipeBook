package com.elte.recipebook.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.elte.recipebook.viewModel.OneRecipeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.elte.recipebook.ui.theme.SoftBackground
import kotlinx.coroutines.launch

@Composable
fun OneRecipeScreen(
    recipeId: Int,
    navigateToRoute: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: OneRecipeViewModel = viewModel(key = "recipe_$recipeId")
) {
    val recipe by viewModel.recipe.observeAsState()
    val ingredients by viewModel.ingredients.observeAsState(emptyList())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        viewModel.getRecipeDetails(recipeId)
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showIngredientPopup by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }


    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .background(SoftBackground)
    ) {
        recipe?.let {
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = { isEditing = !isEditing },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Save" else "Edit",
                        tint = Color.Black
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Recipe",
                        tint = Color.Black
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 56.dp)
                ) {
                    if (it.imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(it.imageUri.toUri()),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.LightGray, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.LightGray, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image", color = Color.DarkGray)
                        }
                    }
                    if (isEditing) {
                        Button(onClick = { /* TODO: Image picker */ }) {
                            Text("Change Image")
                        }
                    }

                    if (isEditing) {
                        OutlinedTextField(
                            value = "22",//editedTitle,
                            onValueChange = { },//editedTitle = it },
                            label = { Text("Title") }
                        )
                        OutlinedTextField(
                            value = "22",//editedDescription,
                            onValueChange = {},// editedDescription = it },
                            label = { Text("Description") }
                        )
                    } else {
                        Text(it.name, style = MaterialTheme.typography.headlineSmall)
                        Text(it.description, style = MaterialTheme.typography.bodyMedium)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        if (isEditing) {
                            ingredients.forEach { (ingredient, nutrition) ->
                                //ingredients.forEachIndexed { index, (ingredient, _) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = ingredient.quantity.toString(),
                                        onValueChange = { /* update */ },
                                        label = { Text("Qty") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = ingredient.unit,
                                        onValueChange = { /* update */ },
                                        label = { Text("Unit") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = ingredient.name,
                                        onValueChange = { /* update */ },
                                        label = { Text("Name") },
                                        modifier = Modifier.weight(2f)
                                    )
                                    IconButton(onClick = { {} }) {}//removeIngredient(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }

                            Button(
                                onClick = { showIngredientPopup = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Ingredient")
                            }
                        } else {
                            ingredients.forEach { (ingredient, nutrition) ->
                                var showInfo by remember { mutableStateOf(false) }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        ingredient.quantity.toString(),
                                        modifier = Modifier.weight(1f)
                                    ) // Replace with actual quantity
                                    Text(
                                        ingredient.unit,
                                        modifier = Modifier.weight(1f)
                                    ) // Replace with unit
                                    Text(ingredient.name, modifier = Modifier.weight(2f))
                                    IconButton(onClick = { showInfo = true }) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = "Nutritional Info"
                                        )
                                    }
                                }

                                if (showInfo) {
                                    AlertDialog(
                                        onDismissRequest = { showInfo = false },
                                        title = { Text("Nutrition Info") },
                                        text = {
                                            Text(
                                                "${nutrition.energy} kcal, ${nutrition.fat}g fat, ${nutrition.protein}g protein, " +
                                                        "${nutrition.carbohydrate}g carbs, ${nutrition.sugar}g sugar, ${nutrition.salt}g salt, ${nutrition.fiber}g fiber"
                                            )
                                        },
                                        confirmButton = {
                                            TextButton(onClick = { showInfo = false }) {
                                                Text("OK")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Recipe") },
                text = { Text("Are you sure you want to delete this recipe?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            coroutineScope.launch {
                                viewModel.deleteRecipeById(recipeId)
                                navigateToRoute("home")
                                Toast.makeText(context, "Recipe deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }
        if (showIngredientPopup) {
            Dialog(onDismissRequest = { showIngredientPopup = false }) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp
                ) {
                    CreateNewIngredientsScreen(
                        navController = navController,
                        parentEntity = "recipe/{recipeId}",
                        onIngredientCreated = {
                            navController.getBackStackEntry("recipe/{recipeId}")
                            showIngredientPopup = false
                        }
//                            viewModel.refreshIngredients()
                    )
                }
            }
        }

    }
}
