package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.material3.TextFieldDefaults
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.SunnyYellow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.elte.recipebook.ui.theme.DeepText
import com.elte.recipebook.viewModel.AddRecipeViewModel

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AddRecipeDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    val context = LocalContext.current
    val parentEntry = remember { navController.getBackStackEntry("ocr") }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)

    val portionText by remember { derivedStateOf { viewModel.portionText } }
    val selectedType by remember { derivedStateOf { viewModel.selectedType } }
    // observe snapshotStateList directly
    val selectedEquipment = viewModel.selectedEquipment
    val selectedPrice by remember { derivedStateOf { viewModel.selectedPriceCategory } }

    var showEquipmentDialog by rememberSaveable { mutableStateOf(false) }

    val types = viewModel.availableTypes
    val equipmentOptions = viewModel.availableEquipment
    val prices = viewModel.availablePriceCategories

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            if (event is AddRecipeViewModel.UIEvent.ShowSnackbar) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SoftBackground)
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
                    color = DeepText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = portionText,
                    onValueChange = viewModel::onPortionChange,
                    label = { Text("Portion (number)") },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor  = Color.White,
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor  = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown for Type of Meal
                ExposedDropdownMenuBox(
                    expanded = viewModel.isTypeMenuExpanded,
                    onExpandedChange = { viewModel.toggleTypeMenu() }
                ) {
                    OutlinedTextField(
                        value = selectedType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type of Meal", color = DeepText) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = viewModel.isTypeMenuExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            cursorColor          = DeepText,
                            // Label / placeholder
                            focusedLabelColor    = SoftBackground,
                            unfocusedLabelColor  = SoftBackground,
                            // Container
                            focusedContainerColor    = SoftBackground,
                            unfocusedContainerColor  = SoftBackground,
                            disabledContainerColor   = SoftBackground,
                            // Indicator (outline)
                            focusedIndicatorColor    = SunnyYellow,

                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = viewModel.isTypeMenuExpanded,
                        onDismissRequest = { viewModel.toggleTypeMenu() },
                        modifier = Modifier
                            .background(SoftBackground)              // paint the menu itself
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name, color = DeepText) }, // menu text
                                onClick = {
                                    viewModel.onTypeSelected(type)
                                    viewModel.toggleTypeMenu()
                                },
                                modifier = Modifier.background(SoftBackground)     // each item bg
                            )
                        }
                    }
                }

                // Dropdown for Price Category
                ExposedDropdownMenuBox(
                    expanded = viewModel.isPriceMenuExpanded,
                    onExpandedChange = { viewModel.togglePriceMenu() }
                ) {
                    OutlinedTextField(
                        value = selectedPrice.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Price Category", color = DeepText) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = viewModel.isPriceMenuExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            cursorColor             = DeepText,
                            // Label / placeholder
                            focusedLabelColor       = SoftBackground,
                            unfocusedLabelColor     = SoftBackground,
                            // Container
                            focusedContainerColor   = SoftBackground,
                            unfocusedContainerColor = SoftBackground,
                            disabledContainerColor  = SoftBackground,
                            // Indicator (outline)
                            focusedIndicatorColor   = SunnyYellow
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = viewModel.isPriceMenuExpanded,
                        onDismissRequest = { viewModel.togglePriceMenu() },
                        modifier = Modifier
                            .background(SoftBackground)  // paint the menu itself
                    ) {
                        prices.forEach { pc ->
                            DropdownMenuItem(
                                text = { Text(pc.name, color = DeepText) }, // item text color
                                onClick = {
                                    viewModel.onPriceCategorySelected(pc)
                                    viewModel.togglePriceMenu()
                                },
                                modifier = Modifier.background(SoftBackground) // each item bg
                            )
                        }
                    }
                }

                // ─── Equipment selector ──────────────────────────────────────────────
                Text("Equipment needed:")
                ExposedDropdownMenuBox(
                    expanded = showEquipmentDialog,
                    onExpandedChange = { showEquipmentDialog = !showEquipmentDialog }
                ) {
                    // Anchor: FlowRow of chips, wrapping lines
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()             // attach menu under this
                            .clickable { showEquipmentDialog = true }
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        if (selectedEquipment.isEmpty()) {
                            Text("None", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            selectedEquipment.forEach { eq ->
                                AssistChip(
                                    onClick = { /* no-op or show details */ },
                                    label = { Text(eq.name) },
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 0.dp)
                                        .pointerInput(eq) {
                                            detectTapGestures(
                                                onPress = {
                                                    val removed = try {
                                                        withTimeout(2000L) {
                                                            awaitRelease()   // user must hold 2s
                                                            false
                                                        }
                                                    } catch (_: TimeoutCancellationException) {
                                                        true
                                                    }
                                                    if (removed) viewModel.toggleEquipment(eq)
                                                }
                                            )
                                        }
                                )
                            }
                        }
                        // Dropdown arrow
                        Icon(
                            imageVector = if (showEquipmentDialog)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    // The pop-up menu with checkboxes
                    ExposedDropdownMenu(
                        expanded = showEquipmentDialog,
                        onDismissRequest = { showEquipmentDialog = false },
                        modifier = Modifier
                            .background(SoftBackground)
                    ) {
                        equipmentOptions.forEach { eq ->
                            val isSelected = selectedEquipment.contains(eq)
                            val canSelectMore = selectedEquipment.size < 12
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.toggleEquipment(eq)
                                },
                                enabled = isSelected || canSelectMore,
                                modifier = Modifier.background(SoftBackground),
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = isSelected,
                                            onCheckedChange = null,
                                            colors = CheckboxDefaults.colors(
                                                // box fill when checked
                                                checkedColor     = SoftBackground,
                                                // checkmark stroke
                                                checkmarkColor   = SunnyYellow,
                                                // box border when unchecked
                                                uncheckedColor   = DeepText.copy(alpha = 0.6f)
                                        ))
                                        Spacer(Modifier.width(8.dp))
                                        Text(eq.name, color = DeepText)
                                    }
                                }
                            )
                        }
                    }
                }
                // ────────────────────────────────────────────────────────────────────
                // Save action
                Button(
                    onClick = {
                        // validate required fields
                        navController.navigate("add/selectIngredients")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow)
                ) {
                    Text("Next", color = Color.Black)
                }
            }
        }
    }
}
