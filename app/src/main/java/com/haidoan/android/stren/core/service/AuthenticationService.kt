package com.haidoan.android.stren.core.service

import com.facebook.AccessToken
import com.haidoan.android.stren.core.service.model.UserAuthState
import kotlinx.coroutines.flow.Flow

interface AuthenticationService {
    fun addAuthStateListeners(
        onUserAuthenticated: (userId: String) -> Unit,
        onUserNotAuthenticated: () -> Unit
    )

    suspend fun authenticate(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun authenticateWithFacebook(token: AccessToken)
    suspend fun authenticateWithGoogle(tokenId: String)

    fun getUserAuthStateStream(): Flow<UserAuthState?>
    fun signOut()
}

data class UnverifiedEmailException(val email: String) :
    Exception("Trying to authenticated with an unverified email: $email")
