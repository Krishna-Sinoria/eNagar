package com.example.enagar.domain.models



data class RegisterRequest(

    val name: String,

    val email: String,

    val phone: String,

    val password: String
)