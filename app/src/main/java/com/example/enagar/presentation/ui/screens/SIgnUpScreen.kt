package com.example.enagar.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavHostController
import com.example.enagar.R
import com.example.enagar.presentation.navigation.Screen
import com.example.enagar.presentation.viewModel.AuthViewModel
import com.example.enagar.presentation.viewModel.WorkerViewModel

@Composable
fun SignUpScreen(
    navController: NavHostController
) {

    // ✅ USER AUTH VIEWMODEL
    val authViewModel: AuthViewModel =
        hiltViewModel()

    // ✅ FIELD WORKER VIEWMODEL
    val workerViewModel: WorkerViewModel =
        hiltViewModel()

    // ✅ STATES
    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    // ✅ ROLE
    var role by remember {
        mutableStateOf("Citizen")
    }

    // ✅ FIELD WORKER EXTRA
    var departmentName by remember {
        mutableStateOf("")
    }

    var departmentId by remember {
        mutableStateOf("")
    }

    val colorScheme =
        MaterialTheme.colorScheme

    // ✅ USER REGISTER SUCCESS
    LaunchedEffect(
        authViewModel.isRegisterSuccess.value
    ) {

        if (
            authViewModel.isRegisterSuccess.value
        ) {

            navController.navigate(
                Screen.Home.route
            ) {

                popUpTo(
                    Screen.SignUp.route
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            // ✅ LOGO
            Image(
                painter = painterResource(
                    id = R.drawable.logo
                ),

                contentDescription = "App Logo",

                modifier = Modifier.size(200.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ✅ TITLE
            Text(
                text = "Create your eNagar Account",

                fontSize = 22.sp,

                fontWeight = FontWeight.Bold,

                color = colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ✅ MAIN CARD
            Card(

                shape = RoundedCornerShape(20.dp),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 8.dp
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

                    // ✅ NAME
                    OutlinedTextField(

                        value = name,

                        onValueChange = {
                            name = it
                        },

                        label = {
                            Text("Full Name")
                        },

                        leadingIcon = {

                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // ✅ EMAIL
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
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // ✅ PHONE
                    OutlinedTextField(

                        value = phone,

                        onValueChange = {
                            phone = it
                        },

                        label = {
                            Text("Phone")
                        },

                        leadingIcon = {

                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // ✅ PASSWORD
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
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        },

                        visualTransformation =
                            PasswordVisualTransformation(),

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // ✅ ROLE SELECT
                    Text(
                        "Select Role",

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceEvenly,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected =
                                    role == "Citizen",

                                onClick = {
                                    role = "Citizen"
                                }
                            )

                            Text("Citizen")
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected =
                                    role == "FieldWorker",

                                onClick = {
                                    role = "FieldWorker"
                                }
                            )

                            Text("Field Worker")
                        }
                    }

                    // ✅ FIELD WORKER EXTRA
                    if (role == "FieldWorker") {

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        OutlinedTextField(

                            value = departmentName,

                            onValueChange = {
                                departmentName = it
                            },

                            label = {
                                Text("Department Name")
                            },

                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        OutlinedTextField(

                            value = departmentId,

                            onValueChange = {
                                departmentId = it
                            },

                            label = {
                                Text("Number of Members")
                            },

                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    // ✅ SIGN UP BUTTON
                    Button(

                        onClick = {

                            // 👷 FIELD WORKER
                            if (
                                role == "FieldWorker"
                            ) {

                                workerViewModel.registerTeam(

                                    name = departmentName,

                                    email = email,

                                    password = password,

                                    members = departmentId,

                                    status = "Available"

                                ) {

                                    navController.navigate(
                                        Screen.SignIn.route
                                    )
                                }

                            } else {

                                // 👤 USER REGISTER
                                authViewModel.register(

                                    name = name,

                                    email = email,

                                    phone = phone,

                                    password = password
                                )
                            }
                        },

                        enabled =
                            !authViewModel.isLoading.value,

                        modifier = Modifier.fillMaxWidth(),

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
                                "Sign Up",

                                fontSize = 18.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    // ✅ LOGIN NAVIGATION
                    TextButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Text(
                            "Already have an account? Login",

                            color = colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}