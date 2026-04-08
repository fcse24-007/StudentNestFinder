package com.example.studentnestfinder.ui.auth

data class AuthUiState(
    val studentId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val selectedUniversity: String = "University of Botswana",
    val role: String = "STUDENT",
    val isLogin: Boolean = true, // Toggle between Login and Register
    val isLoading: Boolean = false,
    val authSuccess: Boolean = false,
    val error: String? = null
)
