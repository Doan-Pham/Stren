package com.example.stren.feat.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(private val authService: AuthenticationService) :
    ViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(newEmail: String) {
        uiState.value = uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        uiState.value = uiState.value.copy(password = newPassword)
    }

    fun resetAuthState() {
        uiState.value = uiState.value.copy(
            isAuthFailed = false,
            isAuthSuccess = false,
            isLoading = false,
            errorMessage = ""
        )
    }

    fun onSignInClick() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, "onSignInClick - exception: ${throwable.message}")
            uiState.value = uiState.value.copy(
                isAuthFailed = true,
                isLoading = false,
                errorMessage = "Invalid input. Please try again!"
            )
        }, block = {
            authService.authenticate(uiState.value.email, uiState.value.password)
            uiState.value = uiState.value.copy(
                isAuthSuccess = true,
                isLoading = false,
                isAuthFailed = false
            )
        })
    }
}
