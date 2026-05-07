package com.example.enagar.domain.models



data class TeamRegisterRequest(

    val name: String,

    val email: String,

    val password: String,

    val members: String,

    val status: String
)