package com.haidoan.android.stren.core.service

import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor() : AuthenticationService {

    override fun addAuthStateListeners(
        onUserAuthenticated: () -> Unit,
        onUserNotAuthenticated: () -> Unit
    ) {
        //Firebase.auth.signOut()
        Firebase.auth.addAuthStateListener {
            Timber.d("addAuthStateListeners() - currentUser: ${it.currentUser}")
            if (it.currentUser != null) {
                onUserAuthenticated()
            } else {
                onUserNotAuthenticated()
            }
        }
    }

    override suspend fun authenticate(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        Firebase.auth.signOut()
    }

    override suspend fun authenticateWithFacebook(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        Firebase.auth.signInWithCredential(credential).await()
    }

    override suspend fun authenticateWithGoogle(tokenId: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(tokenId, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
    }
}