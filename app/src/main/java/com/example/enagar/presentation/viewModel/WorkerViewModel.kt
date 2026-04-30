package com.example.enagar.presentation.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enagar.network.RetrofitClient
import com.example.enagar.utils.createPartFromString
import com.example.enagar.utils.prepareFilePart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val appContext: Application
) : ViewModel() {

    fun uploadWork(
        reportId: String,
        lat: String,
        lng: String,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            try {

                // 🔥 Convert URI → File
                val inputStream = appContext.contentResolver.openInputStream(imageUri)
                val file = File.createTempFile("work", ".jpg")

                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                Log.d("WORKER_API", "Uploading proof...")

                val response = RetrofitClient.api.uploadCompletion(
                    reportId = createPartFromString(reportId),
                    lat = createPartFromString(lat),
                    lng = createPartFromString(lng),
                    image = prepareFilePart("image", file)
                )

                if (response.isSuccessful) {
                    Log.d("WORKER_API", "Upload success")
                } else {
                    val err = response.errorBody()?.string()
                    Log.e("WORKER_API", "Error: ${response.code()} - $err")
                }

            } catch (e: Exception) {
                Log.e("WORKER_API", "Exception: ${e.message}")
            }
        }
    }
}