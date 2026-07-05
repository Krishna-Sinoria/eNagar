package com.example.enagar.network

import com.example.enagar.domain.models.FieldWorkerTeam
import com.example.enagar.domain.models.LoginRequest
import com.example.enagar.domain.models.LoginResponse
import com.example.enagar.domain.models.Report
import com.example.enagar.domain.models.ReportResponse
import com.example.enagar.domain.models.SimpleResponse
import com.example.enagar.domain.models.TeamRegisterRequest
import com.example.enagar.domain.models.UserLoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 🟢 Create Complaint (USER)
    @Multipart
    @POST("reports")
    suspend fun createReport(
        @Header("Authorization")
        token: String,
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
    suspend fun getAssignedTasks(
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





        // 🔐 Login
        @POST("teams/login")
        suspend fun login(
            @Body request: LoginRequest
        ): Response<LoginResponse>


        // 📥 Get assigned reports (FIELD WORKER)
        @GET("reports/assigned")
        suspend fun getAssignedReports(
            @Header("Authorization") token: String
        ): Response<List<Report>>


        // ▶️ Start Work
        @POST("reports/start")
        suspend fun startWork(
            @Header("Authorization") token: String,
            @Body body: Map<String, String>
        ): Response<SimpleResponse>


        // 📤 Upload Completion Proof
        @Multipart
        @POST("reports/complete-request")
        suspend fun uploadProof(
            @Header("Authorization") token: String,
            @Part("reportId") reportId: RequestBody,
            @Part("lat") lat: RequestBody,
            @Part("lng") lng: RequestBody,
            @Part image: MultipartBody.Part
        ): Response<SimpleResponse>


    @POST("teams")
    suspend fun registerTeam(

        @Body request: TeamRegisterRequest

    ): Response<SimpleResponse>


    @GET("api/teams")
    suspend fun getTeams(): Response<List<FieldWorkerTeam>>

    // User routes


    @POST("users/login")
    suspend fun userLogin(

        @Body request: LoginRequest

    ): Response<UserLoginResponse>


    @POST("users/register")
    suspend fun userRegister(

        @Body request: com.example.enagar.domain.models.RegisterRequest

    ): Response<UserLoginResponse>

}


