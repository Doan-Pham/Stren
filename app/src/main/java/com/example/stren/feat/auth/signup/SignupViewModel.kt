package com.example.stren.feat.auth.signup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stren.core.service.AuthenticationService
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SignupViewModel"

@HiltViewModel
class SignupViewModel @Inject constructor(private val authService: AuthenticationService) :
    ViewModel() {
    var uiState = mutableStateOf(SignupUiState())
        private set

    fun onEmailChange(newEmail: String) {
        uiState.value = uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        uiState.value = uiState.value.copy(password = newPassword)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun resetAuthState() {
        uiState.value = uiState.value.copy(
            isSignupSuccess = false,
            isLoading = false,
            isSignupFailed = false
        )
    }

    fun onSignUpClick() {
        uiState.value = uiState.value.copy(isLoading = true)
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            var errorMessage = "Invalid input. Please try again!"
            if (throwable is FirebaseAuthUserCollisionException) {
                errorMessage = "Email already exists"
            }
            Log.e(TAG, "onSignUpClick - exception - ${throwable::class}: ${throwable.message}")
            uiState.value = uiState.value.copy(
                isSignupFailed = true,
                isLoading = false,
                errorMessage = errorMessage
            )
        }, block = {
            authService.signUp(uiState.value.email, uiState.value.password)
            uiState.value = uiState.value.copy(
                isSignupSuccess = true,
                isLoading = false,
                isSignupFailed = false
            )
        })
    }
}
