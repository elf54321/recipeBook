package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.elte.recipebook.viewModel.AddRecipeViewModel
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.DeepText
import androidx.core.net.toUri
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AddRecipeDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    val context = LocalContext.current
    // same parentEntry, same VM instance
    val parentEntry = remember { navController.getBackStackEntry("add") }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)
    val name by remember { derivedStateOf { viewModel.name } }
    val description by remember { derivedStateOf { viewModel.description } }
    val portionText by remember { derivedStateOf { viewModel.portionText } }
    val selectedType by remember { derivedStateOf { viewModel.selectedType } }
    val selectedEquipment by remember { derivedStateOf { viewModel.selectedEquipment } }
    val selectedPrice by remember { derivedStateOf { viewModel.selectedPriceCategory } }
    val imageUriString by remember { derivedStateOf { viewModel.imageUriString } }
    val imageUri = imageUriString?.toUri()

    val types = viewModel.availableTypes
    val equipmentOptions = viewModel.availableEquipment
    val prices = viewModel.availablePriceCategories

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.onImageSelected(uri) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4EF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🍽️ Recipe Details",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = portionText,
                onValueChange = viewModel::onPortionChange,
                label = { Text("Portion (number)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown for TypeOfMeal
            ExposedDropdownMenuBox(
                expanded = viewModel.isTypeMenuExpanded,
                onExpandedChange = { viewModel.toggleTypeMenu() }
            ) {
                OutlinedTextField(
                    value = selectedType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type of Meal") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isTypeMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = viewModel.isTypeMenuExpanded,
                    onDismissRequest = { viewModel.toggleTypeMenu() }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                viewModel.onTypeSelected(type)
                                viewModel.toggleTypeMenu()
                            }
                        )
                    }
                }
            }

            // Equipment multi-select
            Text("Equipment needed:")
            FlowRow(
                //mainAxisSpacing = 8.dp,
                //crossAxisSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                equipmentOptions.forEach { eq ->
                    val selected = selectedEquipment.contains(eq)
                    Surface(
                        tonalElevation = if (selected) 4.dp else 0.dp,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .toggleable(
                                value = selected,
                                onValueChange = { viewModel.toggleEquipment(eq) }
                            )
                    ) {
                        Text(
                            text = eq.name,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Dropdown for PriceCategory
            ExposedDropdownMenuBox(
                expanded = viewModel.isPriceMenuExpanded,
                onExpandedChange = { viewModel.togglePriceMenu() }
            ) {
                OutlinedTextField(
                    value = selectedPrice.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Price Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isPriceMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = viewModel.isPriceMenuExpanded,
                    onDismissRequest = { viewModel.togglePriceMenu() }
                ) {
                    prices.forEach { pc ->
                        DropdownMenuItem(
                            text = { Text(pc.name) },
                            onClick = {
                                viewModel.onPriceCategorySelected(pc)
                                viewModel.togglePriceMenu()
                            }
                        )
                    }
                }
            }

            // Nutritional info inputs
            Text("Nutrition (per portion):")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = "Test",
                    onValueChange = { /* implement viewModel.onNutritionChange */ },
                    label = { Text("Energy (kcal)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                // Repeat for fat, protein, carbs, sugar, salt, fiber
            }


            // Save action
            Button(
                onClick = {
                    // validate required fields
                    viewModel.insertRecipe {
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Recipe")
            }
        }
    }

    Button(onClick = {
        viewModel.insertRecipe {
            navController.popBackStack("home", inclusive = false)
        }
    }) {
        Text("Save Recipe")
    }
}
