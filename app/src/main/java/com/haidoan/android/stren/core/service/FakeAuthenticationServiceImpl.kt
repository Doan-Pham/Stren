package com.haidoan.android.stren.core.service

import androidx.annotation.VisibleForTesting
import com.facebook.AccessToken
import com.haidoan.android.stren.core.service.model.UserAuthState
import com.haidoan.android.stren.feat.training.history.UNDEFINED_USER_ID
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

@VisibleForTesting
class FakeAuthenticationServiceImpl : AuthenticationService {
    private var onUserAuthenticated: (String) -> Unit = {}
    private var onUserNotAuthenticated: () -> Unit = {}
    private var userId = UNDEFINED_USER_ID

    var isUserSignedIn = false
        set(value) {
            Timber.d("setIsUserSignedIn: $field")
            if (field != value) {
                field = value
                checkAuthStateAndExecuteCallbacks()
            }
        }

    fun setUserId(userId: String) {
        this.userId = userId
        this.isUserSignedIn = true
        checkAuthStateAndExecuteCallbacks()
    }

    private fun checkAuthStateAndExecuteCallbacks() {
        if (isUserSignedIn) {
            onUserAuthenticated(userId)
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

    override suspend fun getCurrentUserId(): String {
        TODO("Not yet implemented")
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

    override fun getUserAuthStateStream(): Flow<UserAuthState?> {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        TODO("Not yet implemented")
    }

}