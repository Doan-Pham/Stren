package com.haidoan.android.stren.feat.training.cardio_tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.platform.android.ClockTicker.secondsElapsed
import com.haidoan.android.stren.core.repository.base.CoordinatesRepository
import com.haidoan.android.stren.feat.training.cardio_tracking.model.CardioTrackingUiState
import com.haidoan.android.stren.feat.training.cardio_tracking.model.ExtraInfoUiModel
import com.haidoan.android.stren.feat.training.cardio_tracking.model.toLatitudeLongitude
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class CardioTrackingViewModel @Inject constructor(
    private val coordinatesRepository: CoordinatesRepository
) : ViewModel() {

    val totalDurationInSecs =
        secondsElapsed
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0L
            )

    val distanceTravelledInKm =
        coordinatesRepository.getTotalDistanceTravelled().map {
            (it ?: 0f) / 1000
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0f
            )

    private val _extraInfo = MutableStateFlow(ExtraInfoUiModel(0L, 0F))
    val extraInfo = _extraInfo.asStateFlow()


    val uiState = coordinatesRepository.getCoordinates().map { coordinates ->
        if (coordinates.isEmpty()) {
            CardioTrackingUiState.Loading
        } else {
            CardioTrackingUiState.CoordinateLoaded(
                coordinates = coordinates.map { it.toLatitudeLongitude() },
                currentCoordinate = coordinates.maxBy { it.timestamp }.toLatitudeLongitude()
            )
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            CardioTrackingUiState.Loading
        )
}