package com.example.enagar.presentation.viewModel

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enagar.domain.models.IssueReportItem
import com.example.enagar.domain.models.ReportResponse
import com.example.enagar.network.RetrofitClient
import com.example.enagar.utils.SessionManager
import com.example.enagar.utils.createPartFromString
import com.example.enagar.utils.prepareFilePart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CitizenViewModel @Inject constructor(
    private val appContext: android.app.Application
) : ViewModel() {

    // ── MyReports: real API state ─────────────────────────────────────────────
    private val _reports   = MutableStateFlow<List<ReportResponse>>(emptyList())
    val reports: StateFlow<List<ReportResponse>> = _reports

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ── ReportIssue: compose state ────────────────────────────────────────────
    val isSuccess = mutableStateOf(false)

    private val _issueReport = mutableStateOf(IssueReportItem())
    val issueReport: State<IssueReportItem> = _issueReport

    private val _description = mutableStateOf<String?>(null)
    val description: State<String?> = _description

    private val _reportId = mutableStateOf("")
    val reportId: State<String> = _reportId

    // ── Setters — UNTOUCHED ───────────────────────────────────────────────────
    fun setReportId(id: String)           { _reportId.value = id }
    fun desriptionValue(desc: String?)    { _description.value = desc }
    fun setImageUri(uri: Uri)             { _issueReport.value = issueReport.value.copy(imageUri = uri) }
    fun setLocation(location: String)     { _issueReport.value = issueReport.value.copy(location = location) }
    fun setDescription(description: String) { _issueReport.value = issueReport.value.copy(description = description) }
    fun setIssueType(issueType: String)   { _issueReport.value = issueReport.value.copy(issueType = issueType) }

    // ── Fetch user reports from backend ───────────────────────────────────────
    // API: GET /api/reports/user/{userId}
    fun fetchUserReports(context: android.content.Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value     = null
            try {
                val sessionManager = SessionManager(appContext)
                val userId         = sessionManager.getUserId()

                if (userId.isNullOrBlank()) {
                    _error.value     = "User not logged in. Please sign in again."
                    _isLoading.value = false
                    return@launch
                }

                Log.d("CitizenVM", "Fetching reports for userId: $userId")

                val response = RetrofitClient.api.getUserReports(userId)

                Log.d("REPORT_DEBUG", "Code = ${response.code()}")
                Log.d("REPORT_DEBUG", "Message = ${response.message()}")
                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    _reports.value = body
                    Log.d("CitizenVM", "Fetched ${body.size} reports")
                } else {
                    val errMsg = response.errorBody()?.string()
                    _error.value = "Failed to load reports (${response.code()})"
                    Log.e("CitizenVM", "Error ${response.code()}: $errMsg")
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
                Log.e("CitizenVM", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Submit report to backend — UNTOUCHED ──────────────────────────────────
    fun submitReportToBackend(
        problemType: String,
        description: String,
        latitude:    String,
        longitude:   String,
        imageUri:    Uri
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                Log.d("API_DEBUG", "🚀 FUNCTION CALLED")

                val sessionManager = SessionManager(appContext)
                val token          = sessionManager.getUserToken()

                Log.d("TOKEN_DEBUG", token ?: "null")

                // Image processing
                val optimizedFile = withContext(Dispatchers.IO) {
                    val inputStream  = appContext.contentResolver.openInputStream(imageUri)
                    val originalFile = File.createTempFile("upload", ".jpg")

                    inputStream?.use { input ->
                        originalFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    Log.d("API_DEBUG", "📸 Original Size: ${originalFile.length()}")

                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(originalFile.absolutePath, options)

                    options.inSampleSize     = calculateInSampleSize(options, 1280, 1280)
                    options.inJustDecodeBounds = false

                    val bitmap         = BitmapFactory.decodeFile(originalFile.absolutePath, options)
                    val compressedFile = File.createTempFile("optimized", ".jpg")
                    val out            = FileOutputStream(compressedFile)

                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 75, out)
                    out.flush()
                    out.close()

                    Log.d("API_DEBUG", "🗜️ Optimized Size: ${compressedFile.length()}")

                    compressedFile
                }

                // API call
                val response = RetrofitClient.api.createReport(
                    token       = "Bearer $token",
                    problemType = createPartFromString(problemType),
                    description = createPartFromString(description),
                    latitude    = createPartFromString(latitude),
                    longitude   = createPartFromString(longitude),
                    image       = prepareFilePart("image", optimizedFile)
                )

                if (response.isSuccessful) {
                    response.body()?.let { report ->
                        Log.d("FULL_RESPONSE",    report.toString())
                        Log.d("REPORT_ID_DEBUG", "ID = ${report._id}")
                        setReportId(report._id)
                        isSuccess.value = true
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "❌ ERROR ${response.code()}: $errorBody")
                    isSuccess.value = false
                }

            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "💥 EXCEPTION: ${e.message}", e)
                isSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Image resize helper — UNTOUCHED ───────────────────────────────────────
    private fun calculateInSampleSize(
        options:   BitmapFactory.Options,
        reqWidth:  Int,
        reqHeight: Int
    ): Int {
        val height       = options.outHeight
        val width        = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth  = width / 2
            while (
                (halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth  / inSampleSize) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}