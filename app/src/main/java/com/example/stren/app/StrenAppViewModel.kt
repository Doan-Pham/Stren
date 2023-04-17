package com.example.stren.app

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
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
                Log.d(TAG, "stateListen - isUserSignedIn: ${isUserSignedIn.value}")
            },
            onUserNotAuthenticated = {
                isUserSignedIn.value = false
                Log.d(TAG, "stateListen - isUserSignedIn: ${isUserSignedIn.value}")

            })
        Log.d(TAG, "init() - isUserSignedIn: ${isUserSignedIn.value}")
    }
}