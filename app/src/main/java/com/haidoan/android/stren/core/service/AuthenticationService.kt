package com.haidoan.android.stren.core.service

import com.facebook.AccessToken

interface AuthenticationService {
    fun addAuthStateListeners(onUserAuthenticated: () -> Unit, onUserNotAuthenticated: () -> Unit)
    suspend fun authenticate(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun authenticateWithFacebook(token: AccessToken)
    suspend fun authenticateWithGoogle(tokenId: String)

}
