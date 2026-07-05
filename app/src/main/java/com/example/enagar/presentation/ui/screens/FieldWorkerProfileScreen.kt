package com.example.enagar.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.enagar.domain.models.FieldWorkerTeam
import com.example.enagar.network.RetrofitClient
import com.example.enagar.presentation.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldWorkerProfileScreen(

    navController: NavHostController,
    workerEmail: String


) {

    val colorScheme = MaterialTheme.colorScheme

    var team by remember {

        mutableStateOf<FieldWorkerTeam?>(null)
    }

    var isLoading by remember {

        mutableStateOf(true)
    }

    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {

        scope.launch {

            try {

                val response =
                    RetrofitClient.api.getTeams()

                if (response.isSuccessful) {

                    val teams =
                        response.body() ?: emptyList()

                    team =
                        teams.find {

                            it.email == workerEmail
                        }
                }
            } catch (e: Exception) {

                e.printStackTrace()

            } finally {

                isLoading = false
            }
        }
    }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {

                    Text(

                        text = "My Profile",

                        color =
                            colorScheme.onPrimary,

                        fontWeight =
                            FontWeight.Bold
                    )
                },

                colors =
                    TopAppBarDefaults
                        .centerAlignedTopAppBarColors(

                            containerColor =
                                colorScheme.primary
                        )
            )
        },

        containerColor =
            colorScheme.background

    ) { padding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (isLoading) {

                CircularProgressIndicator(

                    modifier =
                        Modifier.align(Alignment.Center)
                )

            } else {

                Column(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally

                ) {

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )

                    // 👷 Profile Icon
                    Box(

                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                colorScheme.primaryContainer
                            ),

                        contentAlignment =
                            Alignment.Center

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Groups,

                            contentDescription = null,

                            modifier =
                                Modifier.size(70.dp),

                            tint =
                                colorScheme.primary
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // 👷 Team Name
                    Text(

                        text =
                            team?.name
                                ?: "Field Worker",

                        style =
                            MaterialTheme.typography
                                .headlineMedium,

                        fontWeight = FontWeight.Bold,

                        color = colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )

                    // 📋 INFO CARD
                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )

                    ) {

                        Column(

                            modifier =
                                Modifier.padding(20.dp)
                        ) {

                            Text(

                                text =
                                    "Field Worker Information",

                                style =
                                    MaterialTheme.typography
                                        .titleLarge,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(16.dp)
                            )

                            Text(
                                text =
                                    "Department: ${team?.name ?: "N/A"}"
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    "Email: ${team?.email ?: "N/A"}"
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    "Members: ${team?.members ?: "N/A"}"
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    "Status: ${team?.status ?: "N/A"}"
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )

                    // 🚪 LOGOUT BUTTON
                    Button(

                        onClick = {

                            navController.navigate(
                                Screen.SignIn.route
                            ) {

                                popUpTo(0)
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )

                    ) {

                        Icon(

                            imageVector =
                                Icons.AutoMirrored
                                    .Filled.Logout,

                            contentDescription = null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(

                            text = "Logout",

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}