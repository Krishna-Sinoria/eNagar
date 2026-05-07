package com.example.enagar.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.CitizenViewModel
import kotlinx.coroutines.delay

@Composable
fun ReportSubmittedScreen(
    navController: NavController
) {



    val context = LocalContext.current

    val reportId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getString("reportId")
            ?: ""

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(150)

        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5ECE3),
                        Color(0xFFFFFBF7)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ SUCCESS ICON
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn()
            ) {

                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(
                            elevation = 14.dp,
                            shape = CircleShape
                        )
                        .background(
                            color = Color(0xFF6D4C41),
                            shape = CircleShape
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // ✅ TITLE
            Text(
                text = "Report Submitted Successfully",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ✅ DESCRIPTION
            Text(
                text = "Thank you for helping improve your city. Our municipal team will review your report shortly.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // ✅ TRACKING CARD
            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFBF7)
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    Text(
                        text = "Tracking Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D4C41)
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = "Report ID",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    // ✅ REPORT ID ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            text =
                                reportId.ifBlank { "Report ID Not Available" },
                            fontSize = 16.sp,

                            fontWeight = FontWeight.Bold,

                            color = Color(0xFF6D4C41)
                        )

                        IconButton(
                            onClick = {

                                if (reportId.isNotBlank()) {

                                    val clipboard =
                                        context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager

                                    val clip =
                                        ClipData.newPlainText(
                                            "Report ID",
                                            reportId
                                        )

                                    clipboard.setPrimaryClip(
                                        clip
                                    )
                                }
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.ContentCopy,

                                contentDescription = null,

                                tint = Color(0xFF6D4C41)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    // ✅ STATUS CHIPS
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        StatusChip(
                            title = "Status",
                            value = "Pending"
                        )

                        StatusChip(
                            title = "Priority",
                            value = "Low"
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            // ✅ VIEW REPORTS BUTTON
            Button(

                onClick = {
                    navController.navigate(
                        Screen.MyReports.route
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(18.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41)
                )

            ) {

                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "View My Reports",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ✅ HOME BUTTON
            OutlinedButton(

                onClick = {
                    navController.navigate(
                        Screen.Home.route
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(18.dp),

                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6D4C41)
                )

            ) {

                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "Back To Home",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatusChip(
    title: String,
    value: String
) {

    Column {

        Text(
            text = title,
            color = Color.Gray,
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Surface(
            shape = RoundedCornerShape(50),

            color = Color(0xFFEADFD8)
        ) {

            Text(
                text = value,

                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                ),

                color = Color(0xFF6D4C41),

                fontWeight = FontWeight.Bold
            )
        }
    }
}