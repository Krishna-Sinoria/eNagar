package com.example.enagar.presentation.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enagar.network.RetrofitClient
import com.example.enagar.utils.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadViewModel : ViewModel() {

    fun uploadProof(

        context: Context,

        reportId: String,

        file: File,

        lat: String,

        lng: String,
        onSuccess: () -> Boolean

    ) {

        viewModelScope.launch {

            try {

                // 🔐 Token
                val token =
                    SessionManager(context).getToken()

                Log.d("UPLOAD", "TOKEN = $token")

                // 🖼 Image File
                val requestFile =
                    file.readBytes().toRequestBody(
                        "image/*".toMediaTypeOrNull()
                    )

                // 🔥 IMPORTANT
                val imagePart =
                    MultipartBody.Part.createFormData(

                        "image",

                        file.name,

                        requestFile
                    )

                // 📦 Other fields
                val reportIdBody =
                    reportId.toRequestBody(
                        MultipartBody.FORM
                    )

                val latBody =
                    lat.toRequestBody(
                        MultipartBody.FORM
                    )

                val lngBody =
                    lng.toRequestBody(
                        MultipartBody.FORM
                    )

                // 🚀 API CALL
                val response =
                    RetrofitClient.api.uploadProof(

                        "Bearer $token",

                        reportIdBody,

                        latBody,

                        lngBody,

                        imagePart
                    )

                // ✅ DEBUG RESPONSE
                Log.d(
                    "UPLOAD",
                    "CODE = ${response.code()}"
                )

                Log.d(
                    "UPLOAD",
                    "BODY = ${response.body()}"
                )

                Log.d(
                    "UPLOAD",
                    "ERROR = ${response.errorBody()?.string()}"
                )

            } catch (e: Exception) {

                Log.e(
                    "UPLOAD",
                    "EXCEPTION = ${e.message}"
                )

                e.printStackTrace()
            }
        }
    }
}