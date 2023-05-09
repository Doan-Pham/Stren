package com.haidoan.android.stren.feat.trainining.history


import androidx.lifecycle.ViewModel
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject

const val UNDEFINED_USER_ID = "Undefined User ID"

@HiltViewModel
internal class TrainingHistoryViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    workoutsRepository: WorkoutsRepository
) :
    ViewModel() {

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = {
                _currentUserId.value = it
                Timber.d("authStateListen - User signed in - userId: $it")
            },
            onUserNotAuthenticated = {
                _currentUserId.value = UNDEFINED_USER_ID
                Timber.d("authStateListen - User signed out")
            })
    }

    val _currentUserId: MutableStateFlow<String> =
        MutableStateFlow(UNDEFINED_USER_ID)

}


