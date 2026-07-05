package com.example.enagar.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.enagar.components.BottomNavBar
import com.example.enagar.presentation.navigation.Screen

// ── Data class — UNTOUCHED ────────────────────────────────────────────────────
data class UserProfile(
    val name:    String,
    val email:   String,
    val phone:   String,
    val address: String,
    val aadhar:  String,
    val role:    String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    // ── User data — UNTOUCHED ─────────────────────────────────────────────────
    val user = UserProfile(
        name    = "HackSmiths",
        email   = "hacksmiths@email.com",
        phone   = "+91 7753992292",
        address = "University of Lucknow, Lucknow",
        aadhar  = "789658 4521 5649",
        role    = "Citizen"
    )

    val colorScheme = MaterialTheme.colorScheme

    // ── Entrance animation ────────────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        color      = colorScheme.onPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: edit profile */ }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint               = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary
                )
            )
        },
        bottomBar      = { BottomNavBar(navController as NavHostController) },
        containerColor = colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Avatar hero banner ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.primary,
                                colorScheme.background
                            ),
                            startY = 0f,
                            endY   = 420f
                        )
                    )
                    .padding(top = 28.dp, bottom = 36.dp),
                contentAlignment = Alignment.Center
            ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(82.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = user.name.first().toString(),
                                    fontSize   = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text       = user.name,
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Role badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text     = user.role,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                                fontSize = 13.sp,
                                color    = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Stats row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(28.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            ProfileStat("24",  "Reports")
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )
                            ProfileStat("18", "Resolved")
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )
                            ProfileStat("6", "Active")
                        }
                    }

            }

            // ── Personal Info card ────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(500, 200)) + slideInVertically(tween(500, 200)) { 40 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Info card
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            // Card heading
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier         = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        null,
                                        tint     = colorScheme.primary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Personal Information",
                                    style      = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color      = colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            InfoRow(
                                icon  = Icons.Default.Email,
                                label = "Email",
                                value = user.email
                            )
                            InfoDivider()
                            InfoRow(
                                icon  = Icons.Default.Phone,
                                label = "Phone",
                                value = user.phone
                            )
                            InfoDivider()
                            InfoRow(
                                icon  = Icons.Default.LocationOn,
                                label = "Address",
                                value = user.address
                            )
                            InfoDivider()
                            InfoRow(
                                icon  = Icons.Default.Badge,
                                label = "Aadhaar Number",
                                value = user.aadhar
                            )
                        }
                    }

                    // Verify Aadhaar button — ORIGINAL NAVIGATION UNTOUCHED
                    Button(
                        onClick   = { navController.navigate(Screen.AadhaarVerification.route) },
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor   = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Verify Aadhaar",
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Settings / help / logout card
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            MenuRow(
                                icon    = Icons.Default.Settings,
                                label   = "Settings",
                                color   = colorScheme.primary,
                                onClick = { /* TODO */ }
                            )
                            MenuDivider()
                            MenuRow(
                                icon    = Icons.Default.HelpOutline,
                                label   = "Help & Support",
                                color   = colorScheme.primary,
                                onClick = { /* TODO */ }
                            )
                            MenuDivider()
                            MenuRow(
                                icon    = Icons.Default.PrivacyTip,
                                label   = "Privacy Policy",
                                color   = colorScheme.primary,
                                onClick = { /* TODO */ }
                            )
                            MenuDivider()
                            // Logout — ORIGINAL NAVIGATION UNTOUCHED
                            MenuRow(
                                icon    = Icons.AutoMirrored.Filled.Logout,
                                label   = "Logout",
                                color   = Color(0xFFB71C1C),
                                onClick = {
                                    navController.navigate(Screen.SignIn.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ── Stat item in hero ─────────────────────────────────────────────────────────
@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = Color.White
        )
        Text(
            label,
            fontSize = 11.sp,
            color    = Color.White.copy(alpha = 0.78f)
        )
    }
}

// ── Info row with icon ────────────────────────────────────────────────────────
@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = colorScheme.primary, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                label,
                fontSize = 11.sp,
                color    = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                value,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InfoDivider() {
    HorizontalDivider(
        color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}

// ── Menu row ──────────────────────────────────────────────────────────────────
@Composable
private fun MenuRow(
    icon:    ImageVector,
    label:   String,
    color:   Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            label,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color      = if (label == "Logout") color else colorScheme.onSurface,
            modifier   = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            null,
            tint     = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(horizontal = 16.dp),
        color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        thickness = 1.dp
    )
}