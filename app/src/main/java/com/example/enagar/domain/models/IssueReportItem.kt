package com.example.enagar.domain.models

import android.net.Uri

data class IssueReportItem(
    val imageUri: Uri? = null,
    val location: String? = null,
    val issueType: String? = null,
    val description: String? = null
)
