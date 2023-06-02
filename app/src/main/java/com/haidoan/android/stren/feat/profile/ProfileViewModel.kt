package com.haidoan.android.stren.feat.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.*
import com.haidoan.android.stren.core.repository.base.UserRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "UNDEFINED_USER_ID"

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    userRepository: UserRepository,
) : ViewModel() {
    private val _currentUserId = MutableStateFlow(UNDEFINED_USER_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _currentUserId.flatMapLatest { userId ->
        Timber.d("flatMapLatest() - user: ${userId}")
        if (userId == UNDEFINED_USER_ID) {
            flowOf(ProfileUiState.Loading)
        } else {
            userRepository.getUserStream(userId = userId).map { user ->
                ProfileUiState.LoadComplete(user)
            }
        }
    }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState.Loading
        )

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
}