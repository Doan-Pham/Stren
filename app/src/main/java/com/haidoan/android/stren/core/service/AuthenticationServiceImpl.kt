package com.haidoan.android.stren.core.service

import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.service.model.UserAuthState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor() : AuthenticationService {
    override fun addAuthStateListeners(
        onUserAuthenticated: (userId: String) -> Unit,
        onUserNotAuthenticated: () -> Unit
    ) {
        Firebase.auth.addAuthStateListener {
            Timber.d("addAuthStateListeners() - currentUser: ${it.currentUser}")
            if (it.currentUser != null) {
                onUserAuthenticated(it.currentUser!!.uid)
            } else {
                onUserNotAuthenticated()
            }
        }
    }

    override suspend fun getCurrentUserId(): String =
        Firebase.auth.currentUser?.uid ?: ""


    override fun getUserAuthStateStream(): Flow<UserAuthState?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            try {
                Timber.d("getUserAuthIdFlow() - Flow emits new value: ${it.currentUser}")
                if (it.currentUser != null) {
                    var isUserSigningUp = !it.currentUser!!.isEmailVerified

                    // If user signs in with Facebook, their email is marked as unverified,
                    // and isUserSigningUp will be true, and requires user to verify their email,
                    // which is quite clunky
                    // Ref: https://stackoverflow.com/questions/38398545/firebase-facebook-auth-email-verified-always-false
                    if (it.currentUser!!.providerData.any
                        { userInfo -> userInfo.providerId == "facebook.com" }
                    ) {
                        isUserSigningUp = false
                    }

                    trySend(
                        UserAuthState(
                            user = User.undefined.copy(
                                id = it.currentUser!!.uid,
                                displayName = it.currentUser!!.displayName ?: "Undefined",
                                email = it.currentUser!!.email ?: "NOT_USE_EMAIL",
                                shouldShowOnboarding = true
                            ),
                            isUserSigningUp = isUserSigningUp
                        )
                    ).isSuccess
                } else {
                    trySend(null)
                }
            } catch (e: Throwable) {
                Timber.d("getUserAuthIdFlow() - Exception: $e")
            }
        }
        Firebase.auth.addAuthStateListener(authStateListener)
        Timber.d("getUserAuthIdFlow() - AuthStateListener has been added")

        awaitClose {
            Firebase.auth.removeAuthStateListener(authStateListener)
            Timber.d("getUserAuthIdFlow() - AuthStateListener has been removed")
        }
    }

    /**
     * This method may throw an exception if the [email] is not verified
     */
    override suspend fun authenticate(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
        if (Firebase.auth.currentUser?.isEmailVerified == false) {
            throw UnverifiedEmailException(email)
        }
        Firebase.auth.currentUser?.reload()
    }

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        Firebase.auth.currentUser?.sendEmailVerification()?.await()
    }

    override suspend fun authenticateWithFacebook(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        Firebase.auth.signInWithCredential(credential).await()
    }

    override suspend fun authenticateWithGoogle(tokenId: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(tokenId, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }
}