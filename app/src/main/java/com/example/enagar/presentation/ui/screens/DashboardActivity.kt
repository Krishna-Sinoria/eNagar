//package com.example.enagar.presentation.ui.screens
//
//import android.content.Context.MODE_PRIVATE
//import com.example.enagar.network.RetrofitClient
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import retrofit2.Call
//import java.io.File
//
//fun uploadProof(reportId: String, file: File, lat: String, lng: String) {
//
//    val token = getSharedPreferences("app", MODE_PRIVATE)
//        .getString("token", "")
//
//    val requestFile = okhttp3.RequestBody.create(
//        "image/*".toMediaTypeOrNull(),
//        file
//    )
//
//    val imagePart = MultipartBody.Part.createFormData(
//        "file",
//        file.name,
//        requestFile
//    )
//
//    val reportIdBody = okhttp3.RequestBody.create(
//        okhttp3.MultipartBody.FORM, reportId
//    )
//
//    val latBody = okhttp3.RequestBody.create(
//        okhttp3.MultipartBody.FORM, lat
//    )
//
//    val lngBody = okhttp3.RequestBody.create(
//        okhttp3.MultipartBody.FORM, lng
//    )
//
//    RetrofitClient.api.uploadProof(
//        "Bearer $token",
//        reportIdBody,
//        latBody,
//        lngBody,
//        imagePart
//    ).enqueue(object : retrofit2.Callback<Map<String, String>> {
//
//        override fun onResponse(
//            call: Call<Map<String, String>>,
//            response: retrofit2.Response<Map<String, String>>
//        ) {
//            // 👉 Status becomes "Verification Pending"
//        }
//
//        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {}
//    })
//}