package com.example.enagar.presentation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enagar.domain.models.LoginRequest
import com.example.enagar.domain.models.Report
import com.example.enagar.domain.models.TeamRegisterRequest
import com.example.enagar.network.RetrofitClient
import com.example.enagar.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor() : ViewModel() {

    // 🔥 Reports
    private val _reports =
        MutableStateFlow<List<Report>>(emptyList())

    val reports: StateFlow<List<Report>> =
        _reports

    // 🔥 Loading
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading

    // 🔥 Error
    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error


    // =========================================================
    // 🔐 LOGIN
    // =========================================================
    fun login(
        context: Context,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val response =
                    RetrofitClient.api.login(

                        LoginRequest(
                            email,
                            password
                        )
                    )

                if (response.isSuccessful) {

                    val token =
                        response.body()?.token ?: ""

                    SessionManager(context)
                        .saveToken(token)

                    onSuccess()

                } else {

                    _error.value =
                        "Invalid email or password"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Login Failed"

                e.printStackTrace()

            } finally {

                _isLoading.value = false
            }
        }
    }


    // =========================================================
    // 📥 FETCH ASSIGNED REPORTS
    // =========================================================
    fun fetchAssignedReports(
        context: Context
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val token =
                    SessionManager(context)
                        .getToken()

                if (token.isNullOrEmpty()) {

                    _error.value = "Token Missing"
                    return@launch
                }

                val response =
                    RetrofitClient.api
                        .getAssignedReports(
                            "Bearer $token"
                        )

                if (response.isSuccessful) {

                    _reports.value =
                        response.body()
                            ?: emptyList()

                } else {

                    _error.value =
                        "Failed to load reports"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Unknown Error"

                e.printStackTrace()

            } finally {

                _isLoading.value = false
            }
        }
    }


    // =========================================================
    // ▶️ START WORK
    // =========================================================
    fun startWork(

        context: Context,

        reportId: String,

        onDone: () -> Unit

    ) {

        viewModelScope.launch {

            try {

                val token =
                    SessionManager(context)
                        .getToken()

                RetrofitClient.api.startWork(

                    "Bearer $token",

                    mapOf(
                        "reportId" to reportId
                    )
                )

                onDone()

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    // =========================================================
    // 👷 REGISTER TEAM
    // =========================================================
    fun registerTeam(

        name: String,

        email: String,

        password: String,

        members: String,

        status: String,

        onSuccess: () -> Unit

    ) {

        viewModelScope.launch {

            try {

                val response =
                    RetrofitClient.api.registerTeam(

                        TeamRegisterRequest(
                            name,
                            email,
                            password,
                            members,
                            status
                        )
                    )

                if (response.isSuccessful) {

                    onSuccess()

                } else {

                    _error.value =
                        response.errorBody()?.string()
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Registration Failed"

                e.printStackTrace()
            }
        }
    }
}