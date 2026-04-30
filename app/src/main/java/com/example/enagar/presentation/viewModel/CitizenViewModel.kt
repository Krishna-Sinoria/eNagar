package com.example.enagar.presentation.viewModel

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enagar.domain.models.IssueReportItem
import com.example.enagar.network.RetrofitClient
import com.example.enagar.utils.createPartFromString
import com.example.enagar.utils.prepareFilePart
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@HiltViewModel
class CitizenViewModel @Inject constructor(
    private val appContext: android.app.Application
) : ViewModel() {

    val isSuccess = mutableStateOf(false)

    private val _issueReport = mutableStateOf(IssueReportItem())
    val issueReport: State<IssueReportItem> = _issueReport

    private val _description = mutableStateOf<String?>(null)
    val description: State<String?> = _description

    private val _reportId = mutableStateOf<String?>(null)
    val reportId: State<String?> = _reportId

    fun generateReportId() {
        val reportId = "REP-${UUID.randomUUID().toString().take(8).uppercase()}"
        _reportId.value = reportId
    }

    fun desriptionValue(desc: String?) {
        _description.value = desc
    }

    fun setImageUri(uri: Uri) {
        _issueReport.value = issueReport.value.copy(imageUri = uri)
    }

    fun setLocation(location: String) {
        _issueReport.value = issueReport.value.copy(location = location)
    }

    fun setDescription(description: String) {
        _issueReport.value = issueReport.value.copy(description = description)
    }

    fun setIssueType(issueType: String) {
        _issueReport.value = issueReport.value.copy(issueType = issueType)
    }

    // 🔥 MAIN FUNCTION
    fun submitReportToBackend(
        problemType: String,
        description: String,
        latitude: String,
        longitude: String,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            try {

                Log.d("API_DEBUG", "🚀 FUNCTION CALLED")

                // 📌 Step 1: Read image from URI
                val inputStream = appContext.contentResolver.openInputStream(imageUri)
                val originalFile = File.createTempFile("upload", ".jpg")

                inputStream?.use { input ->
                    originalFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                Log.d("API_DEBUG", "📸 Original file size: ${originalFile.length()}")

                // 📌 Step 2: Compress image
                val compressedFile = File.createTempFile("compressed", ".jpg")

                val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
                val out = FileOutputStream(compressedFile)

                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 40, out)

                out.flush()
                out.close()

                Log.d("API_DEBUG", "🗜️ Compressed file size: ${compressedFile.length()}")

                // 📌 Step 3: Log all data
                Log.d("API_DEBUG", "ProblemType: $problemType")
                Log.d("API_DEBUG", "Description: $description")
                Log.d("API_DEBUG", "Latitude: $latitude")
                Log.d("API_DEBUG", "Longitude: $longitude")

                // 📌 Step 4: API CALL
                Log.d("API_DEBUG", "📡 Sending request to backend...")

                val response = RetrofitClient.api.createReport(
                    problemType = createPartFromString(problemType),
                    description = createPartFromString(description),
                    latitude = createPartFromString(latitude),
                    longitude = createPartFromString(longitude),
                    image = prepareFilePart("image", compressedFile)
                )

                // 📌 Step 5: Handle response
                if (response.isSuccessful) {
                    Log.d("API_SUCCESS", "✅ SUCCESS: ${response.body()}")
                    isSuccess.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "❌ ERROR ${response.code()}: $errorBody")
                    isSuccess.value = false
                }

            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "💥 EXCEPTION: ${e.message}", e)
                isSuccess.value = false
            }
        }
    }
}