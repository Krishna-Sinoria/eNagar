package com.example.enagar.domain.models

import com.google.gson.annotations.SerializedName

data class Report(

    val _id: String,

    val problem_type: String,

    val description: String,

    val image: String?,

    @SerializedName("latitude")
    val lat: Double?,

    @SerializedName("longitude")
    val lng: Double?,

    val status: String?,

    val priority: String?,

    val assignedTeam: String?,

    val verificationStatus: String?,

    // ❌ NEW
    val rejectionMessage: String? = null,

    val submission_date: String?,

    val __v: Int?
)