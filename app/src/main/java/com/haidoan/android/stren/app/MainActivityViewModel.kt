package com.haidoan.android.stren.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(authenticationService: AuthenticationService) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MainActivityUiState.LOADING)
    val uiState: StateFlow<MainActivityUiState> = _uiState

    init {
        val firestore = Firebase.firestore
        firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        viewModelScope.launch {
            authenticationService.getUserAuthStateStream().collect {
                // A delay to wait for the UI to navigate to correct screen
                // based on auth state (Calling delay() in MainActivity seems
                // more logical but causes app crash for some reason)
                delay(2500)
                _uiState.update { MainActivityUiState.LOADING_DONE }
            }
        }
    }
}

internal enum class MainActivityUiState {
    LOADING, LOADING_DONE
}