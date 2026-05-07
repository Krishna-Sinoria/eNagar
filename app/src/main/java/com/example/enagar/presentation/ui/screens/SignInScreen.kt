package com.example.enagar.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var selectedRole by remember {
        mutableStateOf("User")
    }

    val colorScheme = MaterialTheme.colorScheme

    val context = navController.context

    // ✅ USER AUTH VIEWMODEL
    val authViewModel: AuthViewModel =
        hiltViewModel()

    // ✅ FIELD WORKER VIEWMODEL
    val workerViewModel: WorkerViewModel =
        viewModel()

    // ✅ USER LOGIN SUCCESS
    LaunchedEffect(
        authViewModel.isLoginSuccess.value
    ) {

        if (authViewModel.isLoginSuccess.value) {

            navController.navigate(
                Screen.Home.route
            ) {

                popUpTo(
                    Screen.SignIn.route
                ) {
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                colorScheme.background
            )
            .padding(16.dp),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center,

            modifier = Modifier.fillMaxWidth()
        ) {

            // ✅ LOGO
            Image(
                painter = painterResource(
                    id = R.drawable.logo
                ),

                contentDescription = "App Logo",

                modifier = Modifier.size(200.dp)
            )

            Text(
                text = "Welcome to eNagar",

                fontSize = 26.sp,

                fontWeight = FontWeight.Bold,

                color = colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // ✅ LOGIN CARD
            Card(

                shape = RoundedCornerShape(20.dp),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),

                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor =
                        colorScheme.surface,

                    contentColor =
                        colorScheme.onSurface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    // ✅ EMAIL FIELD
                    OutlinedTextField(

                        value = email,

                        onValueChange = {
                            email = it
                        },

                        label = {
                            Text("Email")
                        },

                        leadingIcon = {

                            Icon(
                                Icons.Default.Email,
                                contentDescription = null
                            )
                        },

                        singleLine = true,

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    colorScheme.primary,

                                cursorColor =
                                    colorScheme.primary
                            )
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // ✅ PASSWORD FIELD
                    OutlinedTextField(

                        value = password,

                        onValueChange = {
                            password = it
                        },

                        label = {
                            Text("Password")
                        },

                        leadingIcon = {

                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null
                            )
                        },

                        singleLine = true,

                        visualTransformation =
                            PasswordVisualTransformation(),

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(12.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    colorScheme.primary,

                                cursorColor =
                                    colorScheme.primary
                            )
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // ✅ ROLE SELECT
                    Text(
                        text = "Login as",

                        fontWeight = FontWeight.Medium,

                        fontSize = 16.sp,

                        color = colorScheme.onSurface
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(16.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        FilterChip(

                            selected =
                                selectedRole == "User",

                            onClick = {
                                selectedRole = "User"
                            },

                            label = {
                                Text("User")
                            },

                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor =
                                        colorScheme.primary,

                                    selectedLabelColor =
                                        colorScheme.onPrimary
                                )
                        )

                        FilterChip(

                            selected =
                                selectedRole == "Field Worker",

                            onClick = {
                                selectedRole =
                                    "Field Worker"
                            },

                            label = {
                                Text("Field Worker")
                            },

                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor =
                                        colorScheme.primary,

                                    selectedLabelColor =
                                        colorScheme.onPrimary
                                )
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    // ✅ LOGIN BUTTON
                    Button(

                        onClick = {

                            // USER LOGIN
                            if (selectedRole == "User") {

                                authViewModel.login(
                                    email = email,
                                    password = password
                                )

                            } else {

                                // FIELD WORKER LOGIN
                                workerViewModel.login(
                                    context = context,

                                    email = email,

                                    password = password
                                ) {

                                    navController.navigate(
                                        Screen.FieldWorkerMain.route
                                    )
                                }
                            }
                        },

                        modifier = Modifier.fillMaxWidth(),

                        enabled =
                            !authViewModel.isLoading.value,

                        shape = RoundedCornerShape(12.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    colorScheme.primary,

                                contentColor =
                                    colorScheme.onPrimary
                            )

                    ) {

                        if (
                            authViewModel.isLoading.value
                        ) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),

                                color =
                                    colorScheme.onPrimary,

                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                "Login",
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    // ✅ SIGN UP
                    TextButton(

                        onClick = {

                            navController.navigate(
                                Screen.SignUp.route
                            )
                        }
                    ) {

                        Text(
                            "Don’t have an account? Sign Up",

                            color = colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}