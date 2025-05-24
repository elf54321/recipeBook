package com.elte.recipebook.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.elte.recipebook.viewModel.OneRecipeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elte.recipebook.data.entities.Ingredient
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.viewModel.ShoppingListViewModel
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

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
    val allIngredients by viewModel.allIngredients.collectAsState()
    val selectedIngredients = viewModel.selectedIngredients
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val editedIngredients = remember { mutableStateListOf<Ingredient>() }


    var editedTitle by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }
    var editedImageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(ingredients) {
        editedIngredients.clear()
        editedIngredients.addAll(ingredients.map { it.ingredient.copy() })
    }
    LaunchedEffect(recipeId) {
        viewModel.getRecipeDetails(recipeId)
    }
    LaunchedEffect(recipe) {
        recipe?.let {
            editedTitle = it.name
            editedDescription = it.description
            editedImageUri = it.imageUri
        }
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showIngredientPopup by remember { mutableStateOf(false) }
    var showAddExistingIngredientPopup by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val imageCropper = rememberImageCropper()
    var isProcessing by remember { mutableStateOf(false) }

    fun saveBitmapToCacheFile(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "cropped_image_${System.currentTimeMillis()}.png")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = imageCropper.crop(uri, context)
                when (result) {
                    is CropResult.Success -> {
                        val croppedBitmap = result.bitmap.asAndroidBitmap()
                        val imageUri = saveBitmapToCacheFile(context, croppedBitmap)
                        editedImageUri = imageUri.toString()
                    }
                    is CropResult.Cancelled -> {
                        Log.d("OcrScreen", "Cropping cancelled")
                    }
                    is CropError -> {
                        Log.e("OcrScreen", "Cropping error: ${result.name}")
                    }
                }
            }
        }
    }


    val sharedViewModel: ShoppingListViewModel = hiltViewModel()
    val shoppingListManager = sharedViewModel.shoppingListManager

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SoftBackground)

    ) {
        recipe?.let {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                // Top row: edit (±cancel) | add to grocery | delete
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row {
                            IconButton(onClick = {
                                if (isEditing) {
                                    viewModel.updateRecipeDetails(
                                        recipeId,
                                        editedTitle,
                                        editedDescription,
                                        editedImageUri,
                                        editedIngredients.toList()
                                    )
                                }
                                isEditing = !isEditing
                            }) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                    contentDescription = if (isEditing) "Save" else "Edit",
                                    tint = Color.Black
                                )
                            }
                            if (isEditing) {
                                IconButton(onClick = {
                                    // reset fields
                                    editedTitle = it.name
                                    editedDescription = it.description
                                    editedImageUri = it.imageUri
                                    editedIngredients.clear()
                                    editedIngredients.addAll(ingredients.map { ing -> ing.ingredient.copy() })
                                    isEditing = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancel Edit",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            shoppingListManager.addIngredients(ingredients.map { it.ingredient })
                            navController.navigate("grocery")
                        },
                        modifier = Modifier.wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SunnyYellow,
                            contentColor = Color.Black
                        ),
                    ) {
                        Text("Add to Grocery List")
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Recipe",
                                tint = Color.Black
                            )
                        }
                    }
                }



                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp)
                ) {
                    val imageToDisplay = if (isEditing) editedImageUri else it.imageUri

                    if (!imageToDisplay.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageToDisplay.toUri()),
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
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SunnyYellow,
                                contentColor = Color.Black
                            ),
                            enabled = !isProcessing
                        ) {
                            Text("Change Image")
                        }
                    }

                    if (isEditing) {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Title") }
                        )
                        OutlinedTextField(
                            value = editedDescription,
                            onValueChange = { editedDescription = it },
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
                            editedIngredients.forEachIndexed { index, ingredient ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    var quantityText by remember(ingredient.quantity) {
                                        mutableStateOf(
                                            if (ingredient.quantity == 0.0) ""
                                            else if (ingredient.quantity % 1 == 0.0) ingredient.quantity.toInt().toString()
                                            else ingredient.quantity.toString()
                                        )
                                    }
                                    OutlinedTextField(
                                        value = quantityText,
                                        onValueChange = { newValue ->
                                            if (newValue.matches(Regex("^\\d*\\.?\\d*\$")) &&
                                                newValue.count { it == '.' } <= 1) {
                                                quantityText = newValue
                                                val quantity = when {
                                                    newValue.isEmpty() -> 0.0
                                                    else -> newValue.toDoubleOrNull() ?: ingredient.quantity
                                                }
                                                editedIngredients[index] = ingredient.copy(quantity = quantity)
                                            }
                                        },
                                        label = { Text("Qty") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = ingredient.unit,
                                        onValueChange = {
                                            editedIngredients[index] = ingredient.copy(unit = it)
                                        },
                                        label = { Text("Unit") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = ingredient.name,
                                        onValueChange = {
                                            editedIngredients[index] = ingredient.copy(name = it)
                                        },
                                        label = { Text("Name") },
                                        modifier = Modifier.weight(2f)
                                    )
                                }
                            }


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showIngredientPopup = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SunnyYellow,
                                        contentColor = Color.Black
                                    ),
                                ) {
                                    Text("New Ingredient")
                                }
                                Button(
                                    onClick = { showAddExistingIngredientPopup = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SunnyYellow,
                                        contentColor = Color.Black
                                    ),
                                ) {
                                    Text("Edit Existing")
                                }
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
                                        if (ingredient.quantity == 0.0) ""
                                        else if (ingredient.quantity % 1 == 0.0) ingredient.quantity.toInt().toString()
                                        else ingredient.quantity.toString(),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        ingredient.unit,
                                        modifier = Modifier.weight(1f)
                                    )
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
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        imageCropper.cropState?.let { cropState ->
            ImageCropperDialog(state = cropState)
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
                            coroutineScope.launch {
                                delay(500) //TODO NOT OPTIMAL
                                viewModel.refreshIngredients()
                            }
                        }
                    )
                }
            }
        }
        if (showAddExistingIngredientPopup) {
            Dialog(onDismissRequest = { showAddExistingIngredientPopup = false }) {
                Surface(
                    color = SoftBackground,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Select Ingredient", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allIngredients) { ingredient ->
                                val isSelected = selectedIngredients.contains(ingredient)
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) SunnyYellow else MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleIngredientSelection(ingredient) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = ingredient.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "${ingredient.quantity} ${ingredient.unit}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { showAddExistingIngredientPopup = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            showAddExistingIngredientPopup = false
                            editedIngredients.clear()
                            editedIngredients.addAll(selectedIngredients)
                        }) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
}
