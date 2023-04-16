package com.example.stren.feat.auth

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isSignupFailed: Boolean = false,
    val errorMessage: String = "",
    val isSignupSuccess: Boolean = false,
    val isLoading: Boolean = false
)
