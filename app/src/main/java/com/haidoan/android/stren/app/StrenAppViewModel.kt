package com.haidoan.android.stren.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "StrenAppViewModel"

@HiltViewModel
class StrenAppViewModel @Inject constructor(
    authenticationService: AuthenticationService
) : ViewModel() {
    var isUserSignedIn = mutableStateOf(false)
        private set

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = {
                isUserSignedIn.value = true
                Timber.d(TAG, "stateListen - isUserSignedIn: ${isUserSignedIn.value}")
            },
            onUserNotAuthenticated = {
                isUserSignedIn.value = false
                Timber.d(TAG, "stateListen - isUserSignedIn: ${isUserSignedIn.value}")

            })
    }

    class Factory(
        private val authenticationService: AuthenticationService
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StrenAppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StrenAppViewModel(authenticationService) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}