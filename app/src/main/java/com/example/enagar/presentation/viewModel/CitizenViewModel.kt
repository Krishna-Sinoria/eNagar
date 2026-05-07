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
import com.example.enagar.utils.SessionManager
import com.example.enagar.utils.createPartFromString
import com.example.enagar.utils.prepareFilePart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CitizenViewModel @Inject constructor(
    private val appContext: android.app.Application
) : ViewModel() {

    // ✅ SUCCESS STATE
    val isSuccess = mutableStateOf(false)

    // ✅ LOADING STATE
    val isLoading = mutableStateOf(false)

    // ✅ ISSUE REPORT STATE
    private val _issueReport =
        mutableStateOf(IssueReportItem())

    val issueReport: State<IssueReportItem> =
        _issueReport

    // ✅ DESCRIPTION
    private val _description =
        mutableStateOf<String?>(null)

    val description: State<String?> =
        _description

    // ✅ REPORT ID
    private val _reportId =
        mutableStateOf("")

    val reportId: State<String> =
        _reportId

    // ✅ SET REPORT ID
    fun setReportId(id: String) {

        _reportId.value = id
    }

    // ✅ DESCRIPTION VALUE
    fun desriptionValue(desc: String?) {

        _description.value = desc
    }

    // ✅ IMAGE
    fun setImageUri(uri: Uri) {

        _issueReport.value =
            issueReport.value.copy(
                imageUri = uri
            )
    }

    // ✅ LOCATION
    fun setLocation(location: String) {

        _issueReport.value =
            issueReport.value.copy(
                location = location
            )
    }

    // ✅ DESCRIPTION
    fun setDescription(description: String) {

        _issueReport.value =
            issueReport.value.copy(
                description = description
            )
    }

    // ✅ ISSUE TYPE
    fun setIssueType(issueType: String) {

        _issueReport.value =
            issueReport.value.copy(
                issueType = issueType
            )
    }

    // 🚀 MAIN REPORT FUNCTION
    fun submitReportToBackend(

        problemType: String,

        description: String,

        latitude: String,

        longitude: String,

        imageUri: Uri

    ) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                Log.d(
                    "API_DEBUG",
                    "🚀 FUNCTION CALLED"
                )

                // ✅ GET USER TOKEN
                val sessionManager =
                    SessionManager(appContext)

                val token =
                    sessionManager.getUserToken()

                Log.d(
                    "TOKEN_DEBUG",
                    token
                )

                // ✅ IMAGE PROCESSING
                val optimizedFile =
                    withContext(Dispatchers.IO) {

                        val inputStream =
                            appContext.contentResolver
                                .openInputStream(imageUri)

                        val originalFile =
                            File.createTempFile(
                                "upload",
                                ".jpg"
                            )

                        inputStream?.use { input ->

                            originalFile.outputStream()
                                .use { output ->

                                    input.copyTo(output)
                                }
                        }

                        Log.d(
                            "API_DEBUG",
                            "📸 Original Size: ${originalFile.length()}"
                        )

                        // ✅ DECODE IMAGE
                        val options =
                            BitmapFactory.Options()
                                .apply {

                                    inJustDecodeBounds = true
                                }

                        BitmapFactory.decodeFile(
                            originalFile.absolutePath,
                            options
                        )

                        // ✅ RESIZE HUGE IMAGE
                        options.inSampleSize =
                            calculateInSampleSize(
                                options,
                                1280,
                                1280
                            )

                        options.inJustDecodeBounds =
                            false

                        val bitmap =
                            BitmapFactory.decodeFile(
                                originalFile.absolutePath,
                                options
                            )

                        val compressedFile =
                            File.createTempFile(
                                "optimized",
                                ".jpg"
                            )

                        val out =
                            FileOutputStream(
                                compressedFile
                            )

                        // ✅ COMPRESS IMAGE
                        bitmap.compress(
                            android.graphics.Bitmap
                                .CompressFormat.JPEG,
                            75,
                            out
                        )

                        out.flush()

                        out.close()

                        Log.d(
                            "API_DEBUG",
                            "🗜️ Optimized Size: ${compressedFile.length()}"
                        )

                        compressedFile
                    }

                // ✅ API CALL
                val response =
                    RetrofitClient.api.createReport(

                        token = "Bearer $token",

                        problemType =
                            createPartFromString(
                                problemType
                            ),

                        description =
                            createPartFromString(
                                description
                            ),

                        latitude =
                            createPartFromString(
                                latitude
                            ),

                        longitude =
                            createPartFromString(
                                longitude
                            ),

                        image =
                            prepareFilePart(
                                "image",
                                optimizedFile
                            )
                    )

                // ✅ HANDLE RESPONSE
                if (response.isSuccessful) {

                    response.body()?.let { report ->

                        Log.d(
                            "FULL_RESPONSE",
                            report.toString()
                        )

                        Log.d(
                            "REPORT_ID_DEBUG",
                            "ID = ${report._id}"
                        )

                        // ✅ SAVE REPORT ID
                        setReportId(
                            report._id
                        )

                        isSuccess.value = true
                    }

                } else {

                    val errorBody =
                        response.errorBody()?.string()

                    Log.e(
                        "API_ERROR",
                        "❌ ERROR ${response.code()}: $errorBody"
                    )

                    isSuccess.value = false
                }

            } catch (e: Exception) {

                Log.e(
                    "API_EXCEPTION",
                    "💥 EXCEPTION: ${e.message}",
                    e
                )

                isSuccess.value = false

            } finally {

                isLoading.value = false
            }
        }
    }

    // ✅ IMAGE RESIZE FUNCTION
    private fun calculateInSampleSize(

        options: BitmapFactory.Options,

        reqWidth: Int,

        reqHeight: Int

    ): Int {

        val height = options.outHeight

        val width = options.outWidth

        var inSampleSize = 1

        if (
            height > reqHeight ||
            width > reqWidth
        ) {

            val halfHeight =
                height / 2

            val halfWidth =
                width / 2

            while (

                (halfHeight / inSampleSize)
                >= reqHeight &&

                (halfWidth / inSampleSize)
                >= reqWidth

            ) {

                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}