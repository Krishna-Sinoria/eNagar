package com.example.enagar.presentation.viewModel



import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enagar.domain.models.LoginRequest
import com.example.enagar.network.RetrofitClient
import com.example.enagar.utils.SessionManager
import com.example.enagar.domain.models.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    // ✅ LOADING
    val isLoading =
        mutableStateOf(false)

    // ✅ LOGIN SUCCESS
    val isLoginSuccess =
        mutableStateOf(false)

    // ✅ REGISTER SUCCESS
    val isRegisterSuccess =
        mutableStateOf(false)

    private val sessionManager =
        SessionManager(app)

    // =========================
    // LOGIN
    // =========================

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response =
                    RetrofitClient.api.userLogin(

                        LoginRequest(
                            email = email,
                            password = password
                        )
                    )

                if (response.isSuccessful) {

                    response.body()?.let {

                        sessionManager.saveUserAuth(

                            token = it.token,

                            userId = it.user._id,

                            userName = it.user.name
                        )

                        isLoginSuccess.value = true
                    }

                } else {

                    Toast.makeText(
                        app,
                        "Login Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    app,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()

            } finally {

                isLoading.value = false
            }
        }
    }

    // =========================
    // REGISTER
    // =========================

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response =
                    RetrofitClient.api.userRegister(

                        RegisterRequest(

                            name = name,

                            email = email,

                            phone = phone,

                            password = password
                        )
                    )

                if (response.isSuccessful) {

                    response.body()?.let {

                        // ✅ SAVE USER SESSION
                        sessionManager.saveUserAuth(

                            token = it.token,

                            userId = it.user._id,

                            userName = it.user.name
                        )

                        isRegisterSuccess.value = true
                    }

                } else {

                    Toast.makeText(
                        app,
                        "Registration Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    app,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()

            } finally {

                isLoading.value = false
            }
        }
    }
}