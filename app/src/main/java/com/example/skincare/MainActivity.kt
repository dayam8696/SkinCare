package com.example.skincare

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.skincare.ml.SkinDiseaseModel
import com.example.skincare.ui.theme.SkinCareTheme
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            if (!isGranted) {
                Toast.makeText(this, "$permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions for both camera and image access
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        }

        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            requestPermissionLauncher.launch(permissions)
        }

        setContent {
            SkinCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SkinDiseaseScreen()
                }
            }
        }
    }
}

@Composable
fun SkinDiseaseScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val diseases = listOf(
        "Actinic keratosis", "Atopic Dermatitis", "Benign keratosis", "Dermatofibroma",
        "Melanocytic nevus", "Melanoma", "Squamous cell carcinoma", "Tinea Ringworm Candidiasis", "Vascular lesion"
    )
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var prediction by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isButtonPressed by remember { mutableStateOf(false) }

    // Launcher for picking an image
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            bitmap = loadBitmap(context, it)
        }
    }

    // Launcher for capturing photo
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { capturedBitmap: Bitmap? ->
        bitmap = capturedBitmap?.let {
            Bitmap.createScaledBitmap(it, 240, 240, true)
        }
    }

    // Animation for button press
    val buttonScale by animateFloatAsState(
        targetValue = if (isButtonPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFBBDEFB), // Light Blue
                        Color(0xFFE3F2FD), // Very Light Blue
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Text(
                text = "Skin Disease Classifier",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1565C0),
                modifier = Modifier.padding(top = 16.dp)
            )

            // Image preview card
            Card(
                modifier = Modifier
                    .size(280.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image Selected",
                        color = Color(0xFF616161),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Image action buttons (Upload and Camera)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        isButtonPressed = true
                        imagePickerLauncher.launch("image/*")
                        isButtonPressed = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                        .alpha(if (isLoading) 0.6f else 1f)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Upload Image",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        isButtonPressed = true
                        cameraLauncher.launch()
                        isButtonPressed = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                        .alpha(if (isLoading) 0.6f else 1f)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Camera",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Capture Photo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Predict button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        isButtonPressed = true
                        isLoading = true
                        bitmap?.let { bmp ->
                            prediction = predictDisease(context, bmp, diseases)
                        } ?: run {
                            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        }
                        isLoading = false
                        isButtonPressed = false
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                        .alpha(if (isLoading || bitmap == null) 0.6f else 1f)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    ),
                    enabled = bitmap != null && !isLoading
                ) {
                    Text(
                        text = "Predict Disease",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF1976D2),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Prediction result
            prediction?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Prediction",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// Load and preprocess bitmap from URI
fun loadBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        // Resize to 240x240 and ensure RGB format
        Bitmap.createScaledBitmap(bitmap, 240, 240, true)
    } catch (e: Exception) {
        Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        null
    }
}

// Run TFLite model inference
fun predictDisease(context: Context, bitmap: Bitmap, diseases: List<String>): String {
    try {
        // Initialize TFLite model
        val model = SkinDiseaseModel.newInstance(context)

        // Preprocess image to ByteBuffer
        val byteBuffer = preprocessImage(bitmap)
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 240, 240, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Run inference
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Get probabilities and find the class with highest probability
        val probabilities = outputFeature0.floatArray
        if (probabilities.size != 9) {
            return "Error: Model output size (${probabilities.size}) does not match expected 9 classes"
        }
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val confidence = probabilities[maxIndex]

        // Close model
        model.close()

        // Return formatted result
        return "${diseases[maxIndex]} (Confidence: ${String.format("%.2f", confidence * 100)}%)"
    } catch (e: Exception) {
        Toast.makeText(context, "Error running model: ${e.message}", Toast.LENGTH_LONG).show()
        return "Error: ${e.message}"
    }
}

// Convert bitmap to ByteBuffer for TFLite model
fun preprocessImage(bitmap: Bitmap): ByteBuffer {
    // Convert hardware bitmap to software bitmap if necessary
    val softwareBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
        bitmap.copy(Bitmap.Config.ARGB_8888, true)
    } else {
        bitmap
    }

    val byteBuffer = ByteBuffer.allocateDirect(4 * 1 * 240 * 240 * 3)
    byteBuffer.order(ByteOrder.nativeOrder())

    val pixels = IntArray(240 * 240)
    softwareBitmap.getPixels(pixels, 0, 240, 0, 0, 240, 240)

    for (pixel in pixels) {
        // Normalize pixel values to [0,1] as per your notebook (img/255.)
        byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f) // R
        byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)  // G
        byteBuffer.putFloat((pixel and 0xFF) / 255.0f)         // B
    }

    // Recycle the software bitmap if it was copied
    if (softwareBitmap != bitmap) {
        softwareBitmap.recycle()
    }

    return byteBuffer
}

// Extension function for scale animation
fun Modifier.scale(scale: Float) = this.then(
    Modifier.graphicsLayer(
        scaleX = scale,
        scaleY = scale
    )
)