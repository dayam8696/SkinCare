package com.example.skincare.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ResultScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Use the prediction directly from SkinDiseaseResult
    val prediction = SkinDiseaseResult.prediction ?: "Unknown"

    // Extract the disease name from the prediction string (e.g., "Melanoma (Confidence: 92.34%)")
    val diseaseName = prediction.split(" (Confidence:")[0].trim()

    // Map of diseases to their descriptions with "what to do" and "what not to do" in bullet points
    val diseaseDescriptions = mapOf(
        "Actinic keratosis" to """
            Identified as Actinic keratosis with high confidence. This condition often appears as rough, scaly patches on sun-exposed areas like the face, scalp, or hands, caused by prolonged UV exposure, and may progress to skin cancer if untreated.

            **Recommended Actions:**
            - Schedule an immediate consultation with a dermatologist within 48 hours for a thorough evaluation.
            - Protect the area by applying a broad-spectrum sunscreen daily with at least SPF 30.
            - Wear protective clothing, such as hats or long sleeves, when outdoors.

            **Precautions:**
            - Avoid further sun exposure to the affected area to prevent worsening.
            - Do not attempt to scratch or peel off the scaly patches, as this may lead to irritation or infection.
            - Refrain from using unverified topical treatments without professional advice.
        """.trimIndent(),
        "Atopic Dermatitis" to """
            Detected as Atopic Dermatitis, commonly known as eczema. This condition presents as red, inflamed, and itchy skin, often in areas like the elbows, knees, or neck, and may be triggered by allergens or stress.

            **Recommended Actions:**
            - Consult a dermatologist within 72 hours to develop a tailored treatment plan.
            - Keep the skin moisturized by applying a fragrance-free cream or ointment multiple times daily.
            - Use lukewarm water for bathing to soothe the skin and reduce irritation.

            **Precautions:**
            - Avoid using harsh soaps, detergents, or scented products that can exacerbate irritation.
            - Do not scratch the affected areas, as this can lead to infection or worsening of the condition.
            - Refrain from exposing the skin to extreme temperatures, such as very hot or cold environments.
        """.trimIndent(),
        "Benign keratosis" to """
            Recognized as Benign keratosis, likely a seborrheic keratosis, which appears as warty, waxy growths on the skin, often on the face, chest, or back. These are non-cancerous but can be irritated or cosmetically bothersome.

            **Recommended Actions:**
            - Schedule a dermatologist visit within one week for confirmation and to discuss removal options if desired.
            - Apply a gentle moisturizer to the area to reduce dryness and irritation.
            - Monitor the growth for any changes in appearance over time.

            **Precautions:**
            - Avoid scratching or picking at the growth, as this can cause irritation or bleeding.
            - Do not attempt to remove the growth yourself, as improper removal can lead to infection.
            - Refrain from applying harsh chemicals or exfoliants to the affected area.
        """.trimIndent(),
        "Dermatofibroma" to """
            Identified as Dermatofibroma, a benign fibrous growth often appearing as a small, firm bump on the legs or arms, typically brown or reddish, possibly resulting from minor trauma like an insect bite.

            **Recommended Actions:**
            - Arrange a dermatologist evaluation within one week to rule out other conditions.
            - Monitor the bump for changes in size, color, or texture over time.
            - Keep the area clean and covered if it’s prone to rubbing against clothing.

            **Precautions:**
            - Avoid trauma to the area, such as bumping or rubbing, which can cause irritation.
            - Do not attempt to cut or remove the bump yourself, as this can lead to scarring or infection.
            - Refrain from applying unverified creams or treatments without professional guidance.
        """.trimIndent(),
        "Melanocytic nevus" to """
            Detected as Melanocytic nevus, a common mole that can appear anywhere on the body, often brown or black. Most are benign, but changes in size, shape, or color may indicate a risk of melanoma.

            **Recommended Actions:**
            - Schedule a dermatologist appointment within one week to monitor the mole and assess any risks.
            - Protect the area from sun exposure by applying sunscreen or covering with clothing.
            - Take note of any changes in the mole’s appearance and report them to your doctor.

            **Precautions:**
            - Avoid picking at or scratching the mole, as this can cause irritation or bleeding.
            - Do not expose the mole to excessive sun or tanning beds, which can increase melanoma risk.
            - Refrain from attempting to remove the mole yourself, as this can lead to complications.
        """.trimIndent(),
        "Melanoma" to """
            Identified as Melanoma, a serious form of skin cancer that can appear as an asymmetrical lesion with irregular borders and multiple colors (brown, black, red, or blue). Immediate action is critical.

            **Recommended Actions:**
            - Consult a dermatologist within 24 hours for a biopsy and further evaluation.
            - Protect the area by covering it and avoiding any further irritation.
            - Prepare for a detailed examination by noting any recent changes in the lesion.

            **Precautions:**
            - Avoid sun exposure entirely to prevent further damage to the affected area.
            - Do not apply any topical treatments, creams, or home remedies until professionally assessed.
            - Refrain from scratching or touching the lesion, as this can interfere with diagnosis.
        """.trimIndent(),
        "Squamous cell carcinoma" to """
            Recognized as Squamous cell carcinoma, a type of skin cancer presenting as a scaly, red patch or a raised growth with a central depression, often on sun-exposed areas. This condition requires urgent attention.

            **Recommended Actions:**
            - See a dermatologist within 48 hours for a biopsy and to discuss a treatment plan.
            - Protect the area from UV exposure by using sunscreen and wearing protective clothing.
            - Keep the lesion clean and monitor for signs of infection or growth.

            **Precautions:**
            - Avoid scratching or irritating the lesion, as this can worsen the condition or cause bleeding.
            - Do not expose the area to further sun or UV light, which can accelerate progression.
            - Refrain from using unverified treatments or creams without professional advice.
        """.trimIndent(),
        "Tinea Ringworm Candidiasis" to """
            Detected as Tinea Ringworm Candidiasis, a fungal infection that may present as red, scaly, ring-shaped patches, often itchy, on the skin, scalp, or nails. It’s contagious and requires prompt treatment.

            **Recommended Actions:**
            - Consult a dermatologist within 72 hours to start antifungal therapy.
            - Keep the affected area clean and dry to prevent the spread of the fungus.
            - Wash hands frequently and launder clothing or towels that come into contact with the area.

            **Precautions:**
            - Avoid sharing personal items like towels, combs, or clothing, as this can spread the infection.
            - Do not scratch the affected area, as this can lead to further spread or secondary infections.
            - Refrain from using non-prescribed antifungal creams without a confirmed diagnosis.
        """.trimIndent(),
        "Vascular lesion" to """
            Identified as a Vascular lesion, which may appear as a red, purple, or blue mark caused by abnormal blood vessels, such as a hemangioma or port-wine stain. While often benign, some may require treatment.

            **Recommended Actions:**
            - Schedule a dermatologist visit within one week for evaluation and to discuss treatment options.
            - Monitor the lesion for growth, changes in color, or signs of bleeding.
            - Keep the area clean and protected from irritation or injury.

            **Precautions:**
            - Avoid trauma to the area, such as bumping or rubbing, which can cause bleeding or discomfort.
            - Do not attempt to treat or remove the lesion yourself, as this can lead to complications.
            - Refrain from applying heat or harsh products to the lesion without professional guidance.
        """.trimIndent()
    )

    // Get the description for the detected disease, or a default message if not found
    val description = diseaseDescriptions[diseaseName] ?: """
        The detected condition is not recognized in our database.

        **Recommended Actions:**
        - Consult a dermatologist for a thorough evaluation within 72 hours to ensure proper diagnosis.
        - Keep the area clean and monitor for any changes in appearance or symptoms.
        - Protect the skin by avoiding irritants and applying a gentle moisturizer if needed.

        **Precautions:**
        - Avoid sun exposure or irritation to the affected area until professionally assessed.
        - Do not apply unverified treatments or home remedies without medical advice.
        - Refrain from scratching or picking at the area to prevent infection.
    """.trimIndent()

    // Split the description into parts: intro, recommended actions, and precautions
    val parts = description.split("**Recommended Actions:**", "**Precautions:**")
    val introText = parts[0].trim()
    val recommendedActionsText = if (parts.size > 1) parts[1].trim() else ""
    val precautionsText = if (parts.size > 2) parts[2].trim() else ""

    // Button press animation state
    var isButtonPressed by remember { mutableStateOf(false) }
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
                        Color(0xFFE6F0FA), // Light blue
                        Color(0xFFBBDEFB)  // Slightly darker blue
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: "Scan Results: [Prediction]"
            Text(
                text = "Scan Results: $prediction",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF6B6B), // Coral red color
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Subheader: "Personalized Recommendation"
            Text(
                text = "Personalized Recommendation:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            // Divider line for visual separation
            Divider(
                color = Color(0xFF4285F4).copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Disease name in bold and larger font
            Text(
                text = diseaseName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4), // Blue to make it stand out
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description in a Card for better presentation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Introductory paragraph
                    Text(
                        text = introText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recommended Actions heading and content
                    Text(
                        text = "Recommended Actions:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = recommendedActionsText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp) // Indent bullet points
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Precautions heading and content
                    Text(
                        text = "Precautions:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = precautionsText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp) // Indent bullet points
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button: "Check Again"
            Button(
                onClick = {
                    isButtonPressed = true
                    navController.navigate("PredictionScreen")
                    isButtonPressed = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(buttonScale)
                    .shadow(4.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4), // Blue button color
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Check Again",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}