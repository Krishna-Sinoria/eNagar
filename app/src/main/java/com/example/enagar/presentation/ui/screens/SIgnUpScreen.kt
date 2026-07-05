package com.example.enagar.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.enagar.R
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.AuthViewModel
import com.example.enagar.presentation.viewModel.WorkerViewModel

@Composable
fun SignUpScreen(
    navController: NavHostController
) {
    // ── Original ViewModels — UNTOUCHED ───────────────────────────────────────
    val authViewModel: AuthViewModel     = hiltViewModel()
    val workerViewModel: WorkerViewModel = hiltViewModel()

    // ── Original states — UNTOUCHED ───────────────────────────────────────────
    var name           by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var phone          by remember { mutableStateOf("") }
    var role           by remember { mutableStateOf("Citizen") }
    var departmentName by remember { mutableStateOf("") }
    var departmentId   by remember { mutableStateOf("") }

    // ── New UI-only state ─────────────────────────────────────────────────────
    var passwordVisible by remember { mutableStateOf(false) }
    var panelVisible    by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) { panelVisible = true }

    // ── Original success navigation — UNTOUCHED ───────────────────────────────
    LaunchedEffect(authViewModel.isRegisterSuccess.value) {
        if (authViewModel.isRegisterSuccess.value) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
        }
    }

    // ── Root layout ───────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        // ── Gradient top zone (matches SignIn) ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.primary,
                            colorScheme.primary.copy(alpha = 0.75f),
                            colorScheme.background
                        )
                    )
                )
        )

        // ── Scrollable content ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(52.dp))

            // ── Logo + Brand (same as SignIn) ─────────────────────────────────
            AnimatedVisibility(
                visible = panelVisible,
                enter   = fadeIn(tween(480)) + slideInVertically(tween(480)) { -44 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.loogo),
                        contentDescription = "eNagar Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text          = "eNagar",
                        fontSize      = 30.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text      = "Create your account",
                        fontSize  = 13.sp,
                        color     = Color.White.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ── Sign-up card ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = panelVisible,
                enter   = fadeIn(tween(540, 130)) + slideInVertically(tween(540, 130)) { 64 }
            ) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(28.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = colorScheme.surface,
                        contentColor   = colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Card heading
                        Text(
                            text       = "Join eNagar",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text      = "Fill in the details below to get started",
                            fontSize  = 13.sp,
                            color     = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // ── Role toggle strip (same style as SignIn) ───────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colorScheme.surfaceVariant)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Citizen", "FieldWorker").forEach { r ->
                                val isSelected = role == r
                                val label      = if (r == "FieldWorker") "Field Worker" else r
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(
                                            if (isSelected) colorScheme.primary
                                            else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(
                                        onClick  = { role = r },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                    ) {
                                        Text(
                                            text       = label,
                                            fontSize   = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold
                                            else FontWeight.Normal,
                                            color      = if (isSelected) colorScheme.onPrimary
                                            else colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ── Common fields ─────────────────────────────────────

                        // Name (only for Citizen)
                        if (role == "Citizen") {
                            SignUpField(
                                value         = name,
                                onValueChange = { name = it },
                                label         = "Full Name",
                                icon          = Icons.Default.Person,
                                colorScheme   = colorScheme
                            )
                            Spacer(modifier = Modifier.height(13.dp))
                        }

                        // Email
                        SignUpField(
                            value         = email,
                            onValueChange = { email = it },
                            label         = "Email address",
                            icon          = Icons.Default.Email,
                            colorScheme   = colorScheme
                        )
                        Spacer(modifier = Modifier.height(13.dp))

                        // Phone (only for Citizen)
                        if (role == "Citizen") {
                            SignUpField(
                                value         = phone,
                                onValueChange = { phone = it },
                                label         = "Phone number",
                                icon          = Icons.Default.Phone,
                                colorScheme   = colorScheme
                            )
                            Spacer(modifier = Modifier.height(13.dp))
                        }

                        // Password
                        OutlinedTextField(
                            value                = password,
                            onValueChange        = { password = it },
                            label                = { Text("Password") },
                            leadingIcon          = {
                                Icon(
                                    imageVector        = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint               = colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector        = if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint               = colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            singleLine           = true,
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            modifier             = Modifier.fillMaxWidth(),
                            shape                = RoundedCornerShape(14.dp),
                            colors               = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.outline,
                                focusedLabelColor    = colorScheme.primary,
                                cursorColor          = colorScheme.primary
                            )
                        )

                        // ── Field Worker extra fields ──────────────────────────
                        if (role == "FieldWorker") {
                            Spacer(modifier = Modifier.height(13.dp))

                            // Department label
                            Row(
                                modifier          = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colorScheme.primaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null,
                                    tint               = colorScheme.primary,
                                    modifier           = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text       = "Department Details",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = colorScheme.onPrimaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            SignUpField(
                                value         = departmentName,
                                onValueChange = { departmentName = it },
                                label         = "Department Name",
                                icon          = Icons.Default.Business,
                                colorScheme   = colorScheme
                            )
                            Spacer(modifier = Modifier.height(13.dp))
                            SignUpField(
                                value         = departmentId,
                                onValueChange = { departmentId = it },
                                label         = "Number of Members",
                                icon          = Icons.Default.Group,
                                colorScheme   = colorScheme
                            )
                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        // ── Sign up button — ORIGINAL LOGIC UNTOUCHED ─────────
                        Button(
                            onClick = {
                                if (role == "FieldWorker") {
                                    workerViewModel.registerTeam(
                                        name     = departmentName,
                                        email    = email,
                                        password = password,
                                        members  = departmentId,
                                        status   = "Available"
                                    ) {
                                        navController.navigate(Screen.SignIn.route)
                                    }
                                } else {
                                    authViewModel.register(
                                        name     = name,
                                        email    = email,
                                        phone    = phone,
                                        password = password
                                    )
                                }
                            },
                            enabled   = !authViewModel.isLoading.value,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape     = RoundedCornerShape(14.dp),
                            colors    = ButtonDefaults.buttonColors(
                                containerColor         = colorScheme.primary,
                                contentColor           = colorScheme.onPrimary,
                                disabledContainerColor = colorScheme.primary.copy(alpha = 0.5f),
                                disabledContentColor   = colorScheme.onPrimary.copy(alpha = 0.7f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            if (authViewModel.isLoading.value) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(22.dp),
                                    color       = colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text          = "Create Account",
                                    fontSize      = 16.sp,
                                    fontWeight    = FontWeight.SemiBold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ── Divider ───────────────────────────────────────────
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier          = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(
                                modifier  = Modifier.weight(1f),
                                thickness = 1.dp,
                                color     = colorScheme.outline.copy(alpha = 0.4f)
                            )
                            Text(
                                text     = "  or  ",
                                fontSize = 12.sp,
                                color    = colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(
                                modifier  = Modifier.weight(1f),
                                thickness = 1.dp,
                                color     = colorScheme.outline.copy(alpha = 0.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // ── Login link — ORIGINAL LOGIC UNTOUCHED ─────────────
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically,
                            modifier              = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text     = "Already have an account?",
                                fontSize = 14.sp,
                                color    = colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text(
                                    text       = "Sign In",
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Reusable text field for sign up ───────────────────────────────────────────
@Composable
private fun SignUpField(
    value:         String,
    onValueChange: (String) -> Unit,
    label:         String,
    icon:          ImageVector,
    colorScheme:   ColorScheme,
    singleLine:    Boolean = true
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        leadingIcon   = {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = colorScheme.primary
            )
        },
        singleLine = singleLine,
        modifier   = Modifier.fillMaxWidth(),
        shape      = RoundedCornerShape(14.dp),
        colors     = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedLabelColor    = colorScheme.primary,
            cursorColor          = colorScheme.primary
        )
    )
}