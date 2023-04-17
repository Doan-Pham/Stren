package com.example.stren.feat.auth.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isAuthFailed: Boolean = false,
    val errorMessage: String = "",
    val isAuthSuccess: Boolean = false,
    val isLoading: Boolean = false
)
