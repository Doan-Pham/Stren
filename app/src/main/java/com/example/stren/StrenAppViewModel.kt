package com.example.stren

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StrenAppViewModel @Inject constructor(
    authenticationService: AuthenticationService
) :
    ViewModel() {

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = {
                isUserSignedIn.value = true
            },
            onUserNotAuthenticated = {
                isUserSignedIn.value = false
            })
    }

    var isUserSignedIn = mutableStateOf(false)
        private set
}