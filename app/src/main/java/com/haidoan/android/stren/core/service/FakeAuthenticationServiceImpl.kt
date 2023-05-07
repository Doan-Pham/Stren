package com.haidoan.android.stren.core.service

import androidx.annotation.VisibleForTesting
import com.facebook.AccessToken
import timber.log.Timber

@VisibleForTesting
class FakeAuthenticationServiceImpl : AuthenticationService {
    private var onUserAuthenticated: (String) -> Unit = {}
    private var onUserNotAuthenticated: () -> Unit = {}

    var isUserSignedIn = false
        set(value) {
            Timber.d("setIsUserSignedIn: $field")
            if (field != value) {
                field = value
                checkAuthStateAndExecuteCallbacks()
            }
        }

    private fun checkAuthStateAndExecuteCallbacks() {
        if (isUserSignedIn) {
            onUserAuthenticated("")
        } else {
            onUserNotAuthenticated()
        }
    }

    override fun addAuthStateListeners(
        onUserAuthenticated: (String) -> Unit,
        onUserNotAuthenticated: () -> Unit
    ) {
        this.onUserAuthenticated = onUserAuthenticated
        this.onUserNotAuthenticated = onUserNotAuthenticated
        checkAuthStateAndExecuteCallbacks()
    }

    override suspend fun authenticate(email: String, password: String) {
        if (email == "asd@gmail.com" && password == "asdqwe") {
            isUserSignedIn = true
            Timber.d("authenticate() - Called: Email and passwords are correct")
        }
    }

    override suspend fun signUp(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun authenticateWithFacebook(token: AccessToken) {
        TODO("Not yet implemented")
    }

    override suspend fun authenticateWithGoogle(tokenId: String) {
        TODO("Not yet implemented")
    }
}