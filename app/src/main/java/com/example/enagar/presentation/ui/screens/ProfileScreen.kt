package com.example.enagar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.enagar.components.BottomNavBar
import com.example.enagar.presentation.navigation.Screen

// Dummy Profile Data
data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val joinedDate: String,
    val role: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val user = UserProfile(
        name = "HackSmiths",
        email = "hacksmiths@email.com",
        phone = "+91 7753992292",
        address = "University of Lucknow, Lucknow",
        joinedDate = "Joined: Jan 2024",
        role = "Citizen"
    )

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF121212) else Color(0xFFF6F7F9)
    val cardBgColor = if (isDark) Color(0xFF1F1F1F) else Color.White
    val surfaceVariant = if (isDark) Color(0xFF242424) else Color(0xFFF0F2F5)
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile",
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorScheme.primary)
            )
        },
        bottomBar = { BottomNavBar(navController) },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(0.8f)
                .padding(padding)
                .verticalScroll(scrollState)
//                .padding(16.dp)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            // Profile Picture Placeholder

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.first().toString(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = user.role,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Info Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileRow(label = "Email", value = user.email, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 25.dp, bottom = 5.dp))

                    ProfileRow(label = "Phone", value = user.phone, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp))

                    ProfileRow(label = "Address", value = user.address, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp))

                    AadharVerificationRow("Aadhar Number", "5245 5623 8545 6356", navController)

                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Logout Button
            Button(
                onClick = {navController.navigate(Screen.SignIn.route)},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    "Logout",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String,modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 8.dp),
            thickness = 2.dp
        )
    }
}

@Composable
fun AadharVerificationRow(label: String, value: String, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 25.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center)
    {
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){

            Column(
                modifier = Modifier.fillMaxWidth(0.7f)
                ,
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            )  {
                Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            }

            Surface (modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = true,
                    onClick = { navController.navigate(Screen.AadhaarVerification.route) }),
                color = Color.White){
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(Color.Green),
                    contentAlignment = Alignment.Center){
                    Text(text = "Verify",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3),
                    )
                }
            }
        }
    }
}
