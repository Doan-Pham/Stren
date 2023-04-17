package com.example.stren.feat.auth

import com.example.stren.feat.auth.utils.ValidationUtils

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isSignupFailed: Boolean = false,
    val errorMessage: String = "",
    val isSignupSuccess: Boolean = false,
    val isLoading: Boolean = false
) {
    val isEmailValid: Boolean = ValidationUtils.isEmailValid(email)
    val isPasswordValid = password.length >= 6
    val isRepeatPasswordValid = password == repeatPassword
    val isInputValid = isEmailValid && isPasswordValid && isRepeatPasswordValid
}
