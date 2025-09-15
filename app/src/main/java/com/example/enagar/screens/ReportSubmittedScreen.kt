package com.example.enagar.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.OvershootInterpolator
import androidx.navigation.NavController
import com.example.enagar.R
import com.example.enagar.navigation.Screen
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSubmittedScreen(navController: NavController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Generate a report ID
    val reportId = remember { "REP-${UUID.randomUUID().toString().take(8).uppercase()}" }
    // Animation for checkmark
    val scaleAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = OvershootInterpolator(4f)::getInterpolation)
        )
    }

    // Animation for illustration fade-in + slide-up
    var illustrationAlpha by remember { mutableStateOf(0f) }
    var illustrationOffsetY by remember { mutableStateOf(50f) }
    LaunchedEffect(Unit) {
        val duration = 800
        val steps = 60
        for (i in 0..steps) {
            illustrationAlpha = i / steps.toFloat()
            illustrationOffsetY = 50 * (1 - i / steps.toFloat())
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF81C784))
                    )
                )
            )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated illustration
            Image(
                painter = painterResource(id = R.drawable.success_illustration),
                contentDescription = "Success Illustration",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(150.dp)
                    .offset(y = illustrationOffsetY.dp)
                    .alpha(illustrationAlpha)
                    .padding(bottom = 16.dp)
            )

            // Animated checkmark
            Text(
                text = "✅",
                fontSize = 48.sp,
                modifier = Modifier.scale(scaleAnim.value)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Thank you for reporting!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your report has been successfully submitted. Our team will review it and take necessary action soon.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))
            // Show Report ID with copy button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Report ID: $reportId",
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Report ID", reportId)
                    clipboard.setPrimaryClip(clip)
                }) {
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Go to Home")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate(Screen.MyReports.route) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("View My Reports")
            }
        }
    }
}
