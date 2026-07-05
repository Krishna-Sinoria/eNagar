package com.example.enagar.presentation.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.enagar.presentation.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun ReportSubmittedScreen(
    navController: NavController
) {
    // ── Original state — UNTOUCHED ────────────────────────────────────────────
    val context  = LocalContext.current
    val reportId = navController.currentBackStackEntry
        ?.arguments
        ?.getString("reportId") ?: ""

    // ── Animation states ──────────────────────────────────────────────────────
    var iconVisible  by remember { mutableStateOf(false) }
    var cardVisible  by remember { mutableStateOf(false) }
    var btnsVisible  by remember { mutableStateOf(false) }
    var copyToast    by remember { mutableStateOf(false) }

    // Pulsing ring animation on success icon
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue   = 1f,
        targetValue    = 1.12f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        delay(100); iconVisible = true
        delay(350); cardVisible = true
        delay(200); btnsVisible = true
    }

    // Auto-hide copy toast
    LaunchedEffect(copyToast) {
        if (copyToast) { delay(1800); copyToast = false }
    }

    val colorScheme = MaterialTheme.colorScheme

    // ── Root ──────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.primary,
                        colorScheme.primary.copy(alpha = 0.7f),
                        colorScheme.background,
                        colorScheme.background
                    ),
                    startY = 0f,
                    endY   = 1200f
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(56.dp))

            // ── Animated success icon ─────────────────────────────────────────
            AnimatedVisibility(
                visible = iconVisible,
                enter   = fadeIn(tween(500)) + scaleIn(tween(500, easing = EaseOutBack))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Outer pulse ring
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        )
                        // Inner solid circle
                        Box(
                            modifier         = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint               = colorScheme.primary,
                                modifier           = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text       = "Report Submitted!",
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        textAlign  = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text      = "Thank you for helping improve your city.\nOur team will review your report shortly.",
                        fontSize  = 14.sp,
                        color     = Color.White.copy(alpha = 0.82f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Tracking card ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = cardVisible,
                enter   = fadeIn(tween(450)) + slideInVertically(tween(450)) { 50 }
            ) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(24.dp),
                    colors    = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {

                        // Card header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier         = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint               = colorScheme.primary,
                                    modifier           = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Tracking Details",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color      = colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(
                            modifier  = Modifier.padding(vertical = 16.dp),
                            color     = colorScheme.outline.copy(alpha = 0.3f)
                        )

                        // Report ID row
                        Text(
                            "Report ID",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorScheme.surfaceVariant)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = reportId.ifBlank { "Not Available" },
                                style      = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color      = colorScheme.onSurface,
                                modifier   = Modifier.weight(1f)
                            )
                            // Copy button — ORIGINAL LOGIC UNTOUCHED
                            IconButton(
                                onClick  = {
                                    if (reportId.isNotBlank()) {
                                        val clipboard = context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager
                                        clipboard.setPrimaryClip(
                                            ClipData.newPlainText("Report ID", reportId)
                                        )
                                        copyToast = true
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (copyToast)
                                        Icons.Default.Check
                                    else
                                        Icons.Default.ContentCopy,
                                    contentDescription = "Copy Report ID",
                                    tint               = if (copyToast)
                                        Color(0xFF2E7D32)
                                    else
                                        colorScheme.primary,
                                    modifier           = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Inline copy feedback
                        AnimatedVisibility(visible = copyToast) {
                            Text(
                                "✓ Report ID copied to clipboard",
                                style    = MaterialTheme.typography.labelSmall,
                                color    = Color(0xFF2E7D32),
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Status + Priority chips row
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TrackingChip(
                                modifier = Modifier.weight(1f),
                                icon     = Icons.Default.HourglassBottom,
                                label    = "Status",
                                value    = "Pending",
                                color    = Color(0xFFE65100)
                            )
                            TrackingChip(
                                modifier = Modifier.weight(1f),
                                icon     = Icons.Default.Flag,
                                label    = "Priority",
                                value    = "Low",
                                color    = Color(0xFF1565C0)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // What happens next
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.primaryContainer
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "What happens next?",
                                    style      = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                NextStep("1", "Municipal team reviews your report",       colorScheme.primary)
                                NextStep("2", "Field worker gets assigned to the issue",  colorScheme.primary)
                                NextStep("3", "Work begins — status updates to In Progress", colorScheme.primary)
                                NextStep("4", "Issue resolved — you'll be notified",      colorScheme.primary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Action buttons — ORIGINAL NAVIGATION UNTOUCHED ────────────────
            AnimatedVisibility(
                visible = btnsVisible,
                enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { 40 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // View Reports
                    Button(
                        onClick   = { navController.navigate(Screen.MyReports.route) },
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape     = RoundedCornerShape(16.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor   = colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Default.List, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "View My Reports",
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }

                    // Back to Home
                    OutlinedButton(
                        onClick  = { navController.navigate(Screen.Home.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.primary
                        ),
                        border   = androidx.compose.foundation.BorderStroke(
                            1.5.dp, colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Home, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Back to Home",
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Tracking chip ─────────────────────────────────────────────────────────────
@Composable
private fun TrackingChip(
    modifier: Modifier = Modifier,
    icon:     ImageVector,
    label:    String,
    value:    String,
    color:    Color
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                value,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color      = color
            )
        }
    }
}

// ── Next step row ─────────────────────────────────────────────────────────────
@Composable
private fun NextStep(step: String, text: String, color: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier          = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier         = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                step,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Bold,
                color      = color
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text,
            style    = MaterialTheme.typography.bodySmall,
            color    = colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}