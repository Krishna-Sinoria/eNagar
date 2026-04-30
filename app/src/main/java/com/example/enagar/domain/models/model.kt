package com.example.enagar.domain.models

data class ReportResponse(
    val _id: String,
    val problem_type: String,
    val description: String,
    val status: String,
    val image: String?,
    val latitude: Double,
    val longitude: Double
)

data class SimpleResponse(
    val msg: String
)