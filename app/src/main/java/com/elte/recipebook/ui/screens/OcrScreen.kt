package com.elte.recipebook.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.elte.recipebook.ui.theme.DeepText
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.SunnyYellow
import com.elte.recipebook.viewModel.AddRecipeViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun OcrScreen(
    modifier: Modifier = Modifier,
    navigateToStep: () -> Unit,
    navController: NavHostController
) {
    val parentEntry = remember(navController.getBackStackEntry("ocr")) {
        navController.getBackStackEntry("ocr")
    }
    val viewModel: AddRecipeViewModel = hiltViewModel(parentEntry)
    LaunchedEffect(viewModel.description) {
        Log.d("AddRecipeScreen", "Description updated: $viewModel.description")
    }

        val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    val imageCropper = rememberImageCropper()
    var croppedBitmapInstructions by remember { mutableStateOf<ImageBitmap?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = imageCropper.crop(uri, context)
                when (result) {
                    is CropResult.Success -> {
                        croppedBitmapInstructions = result.bitmap
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

    fun runTextRecognition(
        bitmap: Bitmap,
        viewModel: AddRecipeViewModel,
        onNavigate: () -> Unit)
    {
        isProcessing = true
        try {
            val safeBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val inputImage = InputImage.fromBitmap(safeBitmap, 0)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val extractedText = visionText.text
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(100)
                    }
                    if (extractedText.isNotBlank()) {
                        viewModel.onDescriptionChange(extractedText)
                    }
                    else{
                        viewModel.onDescriptionChange("Text could not be extracted")
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(100)
                    }
                    onNavigate()
                    isProcessing = false
                }
                .addOnFailureListener {
                    Log.e("OcrScreen", "OCR failed: ${it.message}")
                    viewModel.onDescriptionChange("Text could not be extracted")
                    onNavigate()
                    isProcessing = false
                }
        }
        catch (e: Exception) {
            Log.e("OCR", "Internal OCR error", e)
            viewModel.onDescriptionChange("Text could not be extracted")
            onNavigate()
            isProcessing = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SoftBackground, Color(0xFFFFECB3))
                )
            )
    ) {
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
                    text = "\uD83C\uDF73 Add a New Recipe with OCR",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isProcessing
                ) {
                    Text("Pick Photo for Instructions", color = Color.Black)
                }

                croppedBitmapInstructions?.let { bitmap ->
                    Text("Instructions:", color = DeepText)
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                    )
                }

                Button(
                    onClick = {
                       croppedBitmapInstructions?.let { runTextRecognition(
                           bitmap = it.asAndroidBitmap(),
                           viewModel = viewModel,
                           onNavigate = {
                               navController.navigate("add")
                           }
                       ) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isProcessing
                ) {
                    Text("Extract", color = Color.Black)
                }

                Button(
                    onClick = {
                        navController.navigate("add")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9)),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isProcessing
                ) {
                    Text("Skip", color = Color.Black)
                }
            }
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
    }

    imageCropper.cropState?.let { cropState ->
        ImageCropperDialog(state = cropState)
    }
}