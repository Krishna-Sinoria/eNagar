package com.example.enagar.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.enagar.R
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.CitizenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSubmittedScreen(navController: NavController) {
    val vm : CitizenViewModel = hiltViewModel()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    // Generate a report ID
    val reportId = vm.reportId.value
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
            delay((duration / steps).toLong())
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Report Submitted", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorScheme.primary),
            )
        },

        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
                    .offset(y = illustrationOffsetY.dp)
                    .alpha(illustrationAlpha),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            // Animated checkmark

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Thank you for reporting!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your report has been successfully submitted. Our team will review it and take necessary action soon.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))
            // Show Report ID with copy button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) Color(0xFF6D4C41).copy(alpha = 0.1f) else Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Report ID: $reportId",
                    color = Color(0xFF6D4C41),
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Report ID", reportId)
                    clipboard.setPrimaryClip(clip)
                }) {
                    Text("Copy", color = Color(0xFF6D4C41))
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41), contentColor = Color.White)
            ) {
                Text("Go Back")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate(Screen.MyReports.route) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6D4C41))
            ) {
                Text("View My Reports")
            }
        }
    }
}
