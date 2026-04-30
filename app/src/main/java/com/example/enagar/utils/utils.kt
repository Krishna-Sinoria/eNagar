package com.example.enagar.utils

import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun createPartFromString(value: String): RequestBody {
    return value.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
    val requestFile = RequestBody.create(
        "image/jpeg".toMediaTypeOrNull(),  // 🔥 FIX MIME TYPE
        file
    )

    return MultipartBody.Part.createFormData(
        partName,
        file.name,   // 🔥 IMPORTANT (filename required)
        requestFile
    )
}

fun getRealPathFromURI(uri: Uri): String {
    return uri.path ?: ""
}