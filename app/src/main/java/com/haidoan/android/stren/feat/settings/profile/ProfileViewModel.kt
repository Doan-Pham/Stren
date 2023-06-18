package com.haidoan.android.stren.feat.settings.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.repository.base.UserRepository
import com.haidoan.android.stren.core.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "UNDEFINED_USER_ID"

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _currentUserId =
        savedStateHandle.getStateFlow(USER_ID_PROFILE_NAV_ARG, UNDEFINED_USER_ID)

    // Biometrics records should only be added if the values after use input are different from the old values
    private var cachedBiometrics = listOf<BiometricsRecord>()

    private val _uiState: MutableStateFlow<ProfileUiState> =
        MutableStateFlow(ProfileUiState.Loading)
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState.Loading
    )

    init {
        viewModelScope.launch {
            _currentUserId.collect { userId ->
                Timber.d("flatMapLatest() - userId: $userId")
                if (userId == UNDEFINED_USER_ID) {
                    _uiState.update { ProfileUiState.Loading }
                } else {
                    val user = userRepository.getUser(userId)
                    cachedBiometrics = user.biometricsRecords
                    Timber.d("flatMapLatest() - user: $user")
                    _uiState.update { ProfileUiState.LoadComplete(user) }
                }
            }
        }
    }

    fun modifyUiState(user: User) {
        val newUser = user.copy()
        _uiState.update { ProfileUiState.LoadComplete(newUser) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val user = (uiState.value as ProfileUiState.LoadComplete).currentUser
            userRepository.modifyUserProfile(
                userId = user.id,
                displayName = user.displayName,
                sex = user.sex,
                age = user.age
            )
            val newUniqueBiometricsRecords =
                user.biometricsRecords
                    .filter { it !in cachedBiometrics }
                    .map { it.copy(recordDate = DateUtils.getCurrentDate()) }

            Timber.d("saveProfile() - cachedBiometrics: $cachedBiometrics")
            Timber.d("saveProfile() - newUniqueBiometricsRecord: $newUniqueBiometricsRecords")

            if (newUniqueBiometricsRecords.isNotEmpty()) {
                userRepository.addBiometricsRecords(
                    userId = user.id,
                    biometricsRecords = newUniqueBiometricsRecords
                )
            }
        }
    }
}