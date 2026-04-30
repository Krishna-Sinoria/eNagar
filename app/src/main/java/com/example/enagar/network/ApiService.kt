package com.example.enagar.network

import com.example.enagar.domain.models.ReportResponse
import com.example.enagar.domain.models.SimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 🟢 Create Complaint (USER)
    @Multipart
    @POST("reports")
    suspend fun createReport(
        @Part("problem_type") problemType: RequestBody,
        @Part("description") description: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ReportResponse>


    // 🔵 Get User Reports
    @GET("reports/user/{userId}")
    suspend fun getUserReports(
        @Path("userId") userId: String
    ): Response<List<ReportResponse>>


    // 🟣 Get Single Report
    @GET("reports/{id}")
    suspend fun getReportById(
        @Path("id") id: String
    ): Response<ReportResponse>


    // 👷 Get Assigned Tasks
    @GET("reports/team/{teamId}")
    suspend fun getAssignedReports(
        @Path("teamId") teamId: String
    ): Response<List<ReportResponse>>


    // 🟡 Start Work
    @POST("reports/start")
    suspend fun startWork(
        @Body body: Map<String, String>
    ): Response<SimpleResponse>


    // 🔥 Upload Completion Proof
    @Multipart
    @POST("reports/complete-request")
    suspend fun uploadCompletion(
        @Part("reportId") reportId: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<SimpleResponse>




    // 🔴 Verify (Admin - optional in app)
    @POST("reports/verify")
    suspend fun verifyReport(
        @Body body: Map<String, Any>
    ): Response<SimpleResponse>



}