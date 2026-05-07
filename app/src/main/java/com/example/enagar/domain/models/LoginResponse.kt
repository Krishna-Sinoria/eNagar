package com.example.enagar.domain.models


// Team response
data class Team(
    val _id: String,
    val name: String,
    val email: String
)
// Login response (token + team)
data class LoginResponse(
    val token: String,
    val team: Team
)
