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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SkinCheckApp(navController: NavController) {
    val lightBlue = Color(0xFFE6F3FF)
    val darkBlue = Color(0xFF2D7CFF)
    val coral = Color(0xFFFF5A5F)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Status bar spacer
            Spacer(modifier = Modifier.height(24.dp))

            // Top title button
            Button(
                onClick = { /* Your action here */ },
                colors = ButtonDefaults.buttonColors(containerColor = coral),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .height(50.dp)
                    .width(240.dp)
            ) {
                Text(
                    text = "CHECK YOUR SKIN!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Steps flow
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                StepWithConnection(
                    number = 1,
                    title = "Take a photo of your skin problem",
                    showConnectionBelow = true,
                    darkBlue = darkBlue
                )

                StepWithConnection(
                    number = 2,
                    title = "AI instantly analyzes your photo",
                    showConnectionBelow = true,
                    darkBlue = darkBlue
                )

                StepWithConnection(
                    number = 3,
                    title = "Get a personalized PDF report",
                    showConnectionBelow = true,
                    darkBlue = darkBlue
                )

                StepWithConnection(
                    number = 4,
                    title = "AI consultant explains your result",
                    showConnectionBelow = false,
                    darkBlue = darkBlue
                )
            }

            // Bottom action button
            Button(
                onClick = {navController.navigate("PredictionScreen")},
                colors = ButtonDefaults.buttonColors(containerColor = coral),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .height(56.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "GET INSTANT RESULT",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bottom spacer for navigation
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StepWithConnection(
    number: Int,
    title: String,
    showConnectionBelow: Boolean,
    darkBlue: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circle with step number
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(darkBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Step $number",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (showConnectionBelow) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(darkBlue)
                )
            }
        }

        // Step text
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Text(
                text = "STEP $number",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

// Note: Using Icons.Filled.CameraAlt for all steps as specified

//@Preview(showBackground = true)
//@Composable
//fun SkinCheckAppPreview() {
//    MaterialTheme {
//        SkinCheckApp(na)
//    }
//}