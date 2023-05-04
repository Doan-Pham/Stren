package com.haidoan.android.stren.core.service

import androidx.annotation.VisibleForTesting
import com.facebook.AccessToken
import timber.log.Timber

private const val TAG = "FakeAuthenticationServiceImpl"

@VisibleForTesting
class FakeAuthenticationServiceImpl : AuthenticationService {
    private var onUserAuthenticated: () -> Unit = {}
    private var onUserNotAuthenticated: () -> Unit = {}

    var isUserSignedIn = false
        set(value) {
            Timber.d(TAG, "setIsUserSignedIn: $field")
            if (field != value) {
                field = value
                checkAuthStateAndExecuteCallbacks()
            }
        }

    private fun checkAuthStateAndExecuteCallbacks() {
        if (isUserSignedIn) {
            onUserAuthenticated()
        } else {
            onUserNotAuthenticated()
        }
    }

    override fun addAuthStateListeners(
        onUserAuthenticated: () -> Unit,
        onUserNotAuthenticated: () -> Unit
    ) {
        this.onUserAuthenticated = onUserAuthenticated
        this.onUserNotAuthenticated = onUserNotAuthenticated
        checkAuthStateAndExecuteCallbacks()
    }

    override suspend fun authenticate(email: String, password: String) {
        if (email == "asd@gmail.com" && password == "asdqwe") {
            isUserSignedIn = true
            Timber.d(TAG, "authenticate() - Called: Email and passwords are correct")
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