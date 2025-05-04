package com.elte.recipebook.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
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
import com.elte.recipebook.ui.theme.DeepText
import com.elte.recipebook.ui.theme.SoftBackground
import com.elte.recipebook.ui.theme.SunnyYellow
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.launch

@Composable
fun OcrScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    val imageCropper = rememberImageCropper()
    var croppedBitmapIngredients by remember { mutableStateOf<ImageBitmap?>(null) }
    var croppedBitmapInstructions by remember { mutableStateOf<ImageBitmap?>(null) }

    var extractedIngredients by remember { mutableStateOf<String?>(null) }
    var extractedInstructions by remember { mutableStateOf<String?>(null) }

    var currentPicker by remember { mutableStateOf(1) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = imageCropper.crop(uri, context)
                when (result) {
                    is CropResult.Success -> {
                        if (currentPicker == 1) {
                            croppedBitmapIngredients = result.bitmap
                        } else {
                            croppedBitmapInstructions = result.bitmap
                        }
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

    fun runTextRecognition(bitmap: Bitmap, onTextExtracted: (String) -> Unit) {
        isProcessing = true
        try {
            val safeBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val inputImage = InputImage.fromBitmap(safeBitmap, 0)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    onTextExtracted(visionText.text)
                    isProcessing = false
                }
                .addOnFailureListener {
                    Log.e("OcrScreen", "OCR failed: ${it.message}")
                    onTextExtracted("Failed to extract text.")
                    isProcessing = false
                }
        }
        catch (e: Exception) {
            Log.e("OCR", "Internal OCR error", e)
            onTextExtracted("Internal error: ${e.localizedMessage}")
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
                        currentPicker = 1
                        imagePickerLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isProcessing
                ) {
                    Text("Pick Photo for Ingredients", color = Color.Black)
                }

                croppedBitmapIngredients?.let { bitmap ->
                    Text("Ingredients:", color = DeepText)
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
                        currentPicker = 2
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
                       croppedBitmapIngredients?.let { runTextRecognition(it.asAndroidBitmap()) { extractedIngredients = it } }
                       croppedBitmapInstructions?.let { runTextRecognition(it.asAndroidBitmap()) { extractedInstructions = it } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9)),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !isProcessing
                ) {
                    Text("Extract Text from Images", color = Color.Black)
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