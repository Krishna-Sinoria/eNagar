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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.enagar.R
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.AuthViewModel
import com.example.enagar.presentation.viewModel.WorkerViewModel

@Composable
fun SignInScreen(
    navController: NavHostController
) {
    // ── All original state — UNTOUCHED ────────────────────────────────────────
    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") }

    val colorScheme = MaterialTheme.colorScheme
    val context     = navController.context

    // ── Original ViewModels — UNTOUCHED ───────────────────────────────────────
    val authViewModel: AuthViewModel     = hiltViewModel()
    val workerViewModel: WorkerViewModel = viewModel()

    // ── New UI-only state ─────────────────────────────────────────────────────
    var passwordVisible by remember { mutableStateOf(false) }
    var panelVisible    by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { panelVisible = true }

    // ── Original success navigation — UNTOUCHED ───────────────────────────────
    LaunchedEffect(authViewModel.isLoginSuccess.value) {
        if (authViewModel.isLoginSuccess.value) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.SignIn.route) { inclusive = true }
            }
        }
    }

    // ── Root: two-zone layout ─────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        // ── Gradient top zone ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
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

            Spacer(modifier = Modifier.height(68.dp))

            // ── Logo + Brand ──────────────────────────────────────────────────
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
                        fontSize      = 34.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text          = "Civic Issue Resolution System",
                        fontSize      = 13.sp,
                        color         = Color.White.copy(alpha = 0.72f),
                        textAlign     = TextAlign.Center,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ── Sign-in Card ──────────────────────────────────────────────────
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
                            text       = "Welcome back",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text      = "Sign in to continue",
                            fontSize  = 13.sp,
                            color     = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ── Role toggle strip ─────────────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colorScheme.surfaceVariant)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("User", "Field Worker").forEach { role ->
                                val isSelected = selectedRole == role
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
                                        onClick  = { selectedRole = role },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                    ) {
                                        Text(
                                            text       = role,
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

                        Spacer(modifier = Modifier.height(22.dp))

                        // ── Email field ───────────────────────────────────────
                        OutlinedTextField(
                            value         = email,
                            onValueChange = { email = it },
                            label         = { Text("Email address") },
                            leadingIcon   = {
                                Icon(
                                    imageVector        = Icons.Default.Email,
                                    contentDescription = null,
                                    tint               = colorScheme.primary
                                )
                            },
                            singleLine = true,
                            modifier   = Modifier.fillMaxWidth(),
                            shape      = RoundedCornerShape(14.dp),
                            colors     = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.outline,
                                focusedLabelColor    = colorScheme.primary,
                                cursorColor          = colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // ── Password field ────────────────────────────────────
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
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible }
                                ) {
                                    Icon(
                                        imageVector        = if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
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

                        // ── Forgot password ───────────────────────────────────
                        Box(
                            modifier         = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(onClick = { /* TODO: forgot password */ }) {
                                Text(
                                    text       = "Forgot password?",
                                    fontSize   = 13.sp,
                                    color      = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // ── Login Button — ORIGINAL LOGIC UNTOUCHED ───────────
                        Button(
                            onClick = {
                                if (selectedRole == "User") {
                                    authViewModel.login(
                                        email    = email,
                                        password = password
                                    )
                                } else {
                                    workerViewModel.login(
                                        context  = context,
                                        email    = email,
                                        password = password
                                    ) {
                                        navController.navigate(
                                            Screen.FieldWorkerMain.createRoute(email)
                                        )
                                    }
                                }
                            },
                            modifier  = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled   = !authViewModel.isLoading.value,
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
                                    text          = "Sign In",
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

                        // ── Sign Up link — ORIGINAL LOGIC UNTOUCHED ───────────
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically,
                            modifier              = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text     = "Don't have an account?",
                                fontSize = 14.sp,
                                color    = colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { navController.navigate(Screen.SignUp.route) }
                            ) {
                                Text(
                                    text       = "Sign Up",
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