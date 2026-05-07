package com.example.enagar.domain.models



data class UserLoginResponse(

    val success: Boolean,

    val token: String,

    val user: User
)