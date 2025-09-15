package com.example.enagar.models

data class Report(
    val id: Int,
    val title: String,
    val status: String, // "Pending", "In Progress", "Resolved"
    val date: String
)
