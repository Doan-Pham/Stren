package com.example.stren.core.service

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor() : AuthenticationService {

    override fun addAuthStateListeners(
        onUserAuthenticated: () -> Unit,
        onUserNotAuthenticated: () -> Unit
    ) {
        Firebase.auth.addAuthStateListener {
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
}