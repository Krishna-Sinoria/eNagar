package com.example.enagar.utils

import android.content.Context
import androidx.core.content.edit

class SessionManager(
    context: Context
) {

    private val pref =
        context.getSharedPreferences(
            "app",
            Context.MODE_PRIVATE
        )

    // =========================
    // TEAM / ADMIN AUTH
    // =========================

    fun saveToken(token: String) {

        pref.edit {
            putString("token", token)
        }
    }

    fun getToken(): String {

        return pref.getString(
            "token",
            ""
        ) ?: ""
    }

    // =========================
    // USER AUTH
    // =========================

    fun saveUserAuth(
        token: String,
        userId: String,
        userName: String
    ) {

        pref.edit {

            putString(
                "user_token",
                token
            )

                .putString(
                    "user_id",
                    userId
                )

                .putString(
                    "user_name",
                    userName
                )

                .putBoolean(
                    "user_logged_in",
                    true
                )

        }
    }

    fun getUserToken(): String {

        return pref.getString(
            "user_token",
            ""
        ) ?: ""
    }

    fun getUserId(): String {

        return pref.getString(
            "user_id",
            ""
        ) ?: ""
    }

    fun getUserName(): String {

        return pref.getString(
            "user_name",
            ""
        ) ?: ""
    }

    fun isUserLoggedIn(): Boolean {

        return pref.getBoolean(
            "user_logged_in",
            false
        )
    }

    fun logoutUser() {

        pref.edit {

            remove("user_token")

                .remove("user_id")

                .remove("user_name")

                .remove("user_logged_in")

        }
    }

    // =========================
    // CLEAR ALL
    // =========================

    fun clear() {

        pref.edit {
            clear()
        }
    }
}