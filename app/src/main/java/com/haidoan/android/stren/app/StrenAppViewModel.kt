package com.haidoan.android.stren.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.domain.HandleUserCreationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StrenAppViewModel @Inject constructor(
    handleUserCreationUseCase: HandleUserCreationUseCase
) : ViewModel() {
    var isUserSignedIn = mutableStateOf(false)
        private set
    var shouldShowOnboarding = mutableStateOf(false)
        private set

    init {
        handleUserCreationUseCase(
            onUserCreatedOrAuthenticated = { currentUser, isUserSignedIn ->
                this.isUserSignedIn.value = isUserSignedIn
                this.shouldShowOnboarding.value = currentUser.shouldShowOnboarding
                Timber.d(" handleUserCreationUseCase.invoke() - onUserCreated is called - user: $currentUser")
            },
            onUserNotAuthenticated = {
                isUserSignedIn.value = false
                Timber.d(" handleUserCreationUseCase.invoke() - onUserNotAuthenticated is called")
            },
            coroutineScope = viewModelScope
        )
    }

    class Factory(
        private val handleUserCreationUseCase: HandleUserCreationUseCase
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StrenAppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StrenAppViewModel(handleUserCreationUseCase) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}