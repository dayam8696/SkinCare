package com.example.skincare.ui

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skincare.R
import com.example.skincare.loadBitmap
import com.example.skincare.predictDisease

@Composable
fun SkinDiseaseDetectScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
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
            .background(Color(0xFFE6F0FA)) // Light blue background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Lets Start!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B6B), // Coral red color
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subheader
            Text(
                text = "You can upload photo from the device or take a photo with camera",
                fontSize = 16.sp,
                color = Color(0xFF333333),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Image preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFF4285F4))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Icon(
                        painter = painterResource(id = R.drawable.scan),
                        contentDescription = "Person outline",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(120.dp)
                    )

                    // If no custom icon resource, use a simple placeholder
                    if (bitmap == null) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw a dashed rectangle frame
                            val strokeWidth = 2.dp.toPx()
                            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                            drawRect(
                                color = Color(0xFF888888),
                                style = Stroke(
                                    width = strokeWidth,
                                    pathEffect = dashPathEffect
                                ),
                                size = Size(size.width * 0.7f, size.height * 0.7f),
                                topLeft = Offset(size.width * 0.15f, size.height * 0.15f)
                            )

                            // Draw simplified person icon
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val radius = size.width * 0.1f

                            // Head
                            drawCircle(
                                color = Color(0xFF888888),
                                radius = radius,
                                center = Offset(centerX, centerY - radius * 1.2f),
                                style = Stroke(width = strokeWidth)
                            )

                            // Body
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(centerX, centerY - radius * 0.2f),
                                end = Offset(centerX, centerY + radius * 1.8f),
                                strokeWidth = strokeWidth
                            )

                            // Draw corner brackets
                            val frameWidth = size.width * 0.7f
                            val frameHeight = size.height * 0.7f
                            val startX = size.width * 0.15f
                            val startY = size.height * 0.15f
                            val bracketLength = size.width * 0.15f

                            // Top-left bracket
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX, startY + bracketLength),
                                end = Offset(startX, startY),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX, startY),
                                end = Offset(startX + bracketLength, startY),
                                strokeWidth = strokeWidth
                            )

                            // Top-right bracket
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX + frameWidth - bracketLength, startY),
                                end = Offset(startX + frameWidth, startY),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX + frameWidth, startY),
                                end = Offset(startX + frameWidth, startY + bracketLength),
                                strokeWidth = strokeWidth
                            )

                            // Bottom-left bracket
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX, startY + frameHeight - bracketLength),
                                end = Offset(startX, startY + frameHeight),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX, startY + frameHeight),
                                end = Offset(startX + bracketLength, startY + frameHeight),
                                strokeWidth = strokeWidth
                            )

                            // Bottom-right bracket
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX + frameWidth - bracketLength, startY + frameHeight),
                                end = Offset(startX + frameWidth, startY + frameHeight),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color(0xFF888888),
                                start = Offset(startX + frameWidth, startY + frameHeight - bracketLength),
                                end = Offset(startX + frameWidth, startY + frameHeight),
                                strokeWidth = strokeWidth
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image action buttons (Upload and Camera)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        isButtonPressed = true
                        imagePickerLauncher.launch("image/*")
                        isButtonPressed = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4), // Blue button
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Upload Image",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
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
                        .height(48.dp)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4), // Blue button
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Take a Photo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tip text
            Text(
                text = "Tip: For more accurate results, upload 3 high-quality photos so the AI can thoroughly analyze the affected skin area.",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Predict button
            Button(
                onClick = {
                    isButtonPressed = true
                    isLoading = true
                    bitmap?.let { bmp ->
                        prediction = predictDisease(context, bmp, diseases)
                        prediction?.let { pred ->
                            SkinDiseaseResult.prediction = pred // Store prediction in the object
                            navController.navigate("ResultScreen")
                        }
                        Toast.makeText(context, "Predicted: $prediction", Toast.LENGTH_SHORT).show()
                    } ?: run {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                    isButtonPressed = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B), // Coral red button
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "PREDICT DISEASE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Prediction result (optional, can remove if only shown on ResultScreen)
            prediction?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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
                            color = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


object SkinDiseaseResult {
    var prediction: String? = null
}