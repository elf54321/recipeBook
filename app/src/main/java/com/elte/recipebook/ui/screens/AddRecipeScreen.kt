package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.filled.ArrowBack

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AddRecipeScreen(
    modifier: Modifier = Modifier,
    navigateToStep: () -> Unit,
    navController: NavHostController,
) {
    // scope ViewModel to the "ocr" entry
    val parentEntry = remember { navController.getBackStackEntry("ocr") }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)
    val context = LocalContext.current

    // Observe state from ViewModel
    val name = viewModel.name
    val description = viewModel.description
    val imageUriString = viewModel.imageUriString
    val imageUri = imageUriString?.toUri()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SoftBackground)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\uD83C\uDF73 Add a New Recipe",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Recipe Name") },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Instructions and Description") },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Pick Photo", color = Color.Black)
                }
                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                   Button(
                         onClick = {
                               if (name.isNotBlank() && description.isNotBlank()) {
                                    navController.navigate("add/details")
                                   }
                                    else {
                                         Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                       }
                                   },
                         colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow),
                         modifier = Modifier.fillMaxWidth()) { Text("Next", color = Color.Black) }
            }
        }
    }
}