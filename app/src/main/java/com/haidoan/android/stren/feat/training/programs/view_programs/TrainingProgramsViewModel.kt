package com.haidoan.android.stren.feat.training.programs.view_programs

import androidx.lifecycle.ViewModel
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "Undefined User ID"

@HiltViewModel
internal class TrainingProgramsViewModel @Inject constructor(
    authenticationService: AuthenticationService,
) : ViewModel() {

    private var _cachedUserId = UNDEFINED_USER_ID
    val userId = _cachedUserId

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = { userId ->
                _cachedUserId = userId
                Timber.d("authStateListen - User signed in - userId: $userId")
            },
            onUserNotAuthenticated = {
                Timber.d("authStateListen - User signed out")
            })
    }
}