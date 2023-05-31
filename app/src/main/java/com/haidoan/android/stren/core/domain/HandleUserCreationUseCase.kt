package com.haidoan.android.stren.core.domain

import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.repository.base.UserRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HandleUserCreationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationService: AuthenticationService,
) {
    /**
     * This method collects a flow of current auth state, and handles the correct user creation
     * logic based on the auth state. Then it calls the param callbacks
     * @param coroutineScope A coroutineScope guarantees that the flow of auth state is collected
     * in a controlled manner
     */
    operator fun invoke(
        onUserCreatedOrAuthenticated: (currentUser: User, isUserSignedIn: Boolean) -> Unit,
        onUserNotAuthenticated: () -> Unit,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            authenticationService.getUserAuthStateStream().collect { userAuthState ->
                Timber.d("authenticationService.getUserAuthIdFlow().collect - userAuthState: $userAuthState")
                if (userAuthState != null) {
                    val currentUser = userAuthState.user
                    val isUserSignedIn = !userAuthState.isUserSigningUp
                    if (!userRepository.isUserExists(userId = currentUser.id)) {
                        userRepository.addUser(currentUser)
                    }
                    // In Firestore, right after signing up a new user, the
                    // newly signed up user is signed in automatically. Due to this
                    // implementation detail, without the manual below signout, the application
                    // will navigate to the main screen before the user even verifies their email,
                    // let alone signing in
                    if (userAuthState.isUserSigningUp) {
                        authenticationService.signOut()
                    }
                    onUserCreatedOrAuthenticated(
                        userRepository.getUser(currentUser.id),
                        isUserSignedIn
                    )
                } else {
                    onUserNotAuthenticated()
                }
            }
        }
    }
}