package com.haidoan.android.stren.feat.settings.measurements

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.domain.GetUserFullLatestBiometricsRecordsUseCase
import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.repository.base.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "UNDEFINED_USER_ID"

@HiltViewModel
internal class MeasurementsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val getUserBiometricsRecords: GetUserFullLatestBiometricsRecordsUseCase
) : ViewModel() {

    private var cachedUserId = UNDEFINED_USER_ID


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = savedStateHandle.getStateFlow(USER_ID_MEASUREMENTS_NAV_ARG, UNDEFINED_USER_ID)
        .flatMapLatest { userId ->
            cachedUserId = userId
            getUserBiometricsRecords(userId).map { MeasurementsUiState.LoadComplete(it) }
        }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), MeasurementsUiState.Loading
        )

    fun addBiometricsRecord(biometricsRecord: BiometricsRecord) {
        viewModelScope.launch {
            userRepository.addBiometricsRecord(
                userId = cachedUserId,
                biometricsRecord = biometricsRecord
            )
        }
    }
}