package com.haidoan.android.stren.feat.profile.edit

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
internal class EditProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _currentUserId =
        savedStateHandle.getStateFlow(USER_ID_EDIT_PROFILE_NAV_ARG, UNDEFINED_USER_ID)

    // Biometrics records should only be added if the values after use input are different from the old values
    private var cachedBiometrics = listOf<BiometricsRecord>()

    private val _uiState: MutableStateFlow<EditProfileUiState> =
        MutableStateFlow(EditProfileUiState.Loading)
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), EditProfileUiState.Loading
    )

    init {
        viewModelScope.launch {
            _currentUserId.collect { userId ->
                Timber.d("flatMapLatest() - userId: $userId")
                if (userId == UNDEFINED_USER_ID) {
                    _uiState.update { EditProfileUiState.Loading }
                } else {
                    val user = userRepository.getUser(userId)
                    cachedBiometrics = user.biometricsRecords
                    Timber.d("flatMapLatest() - user: $user")
                    _uiState.update { EditProfileUiState.LoadComplete(user) }
                }
            }
        }
    }

    fun modifyUiState(user: User) {
        val newUser = user.copy()
        _uiState.update { EditProfileUiState.LoadComplete(newUser) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val user = (uiState.value as EditProfileUiState.LoadComplete).currentUser
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
                userRepository.addBiometricsRecord(
                    userId = user.id,
                    biometricsRecords = newUniqueBiometricsRecords
                )
            }
        }
    }
}