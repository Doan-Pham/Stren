package com.haidoan.android.stren.feat.training.cardio_tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.repository.base.CoordinatesRepository
import com.haidoan.android.stren.feat.training.cardio_tracking.model.CardioTrackingUiState
import com.haidoan.android.stren.feat.training.cardio_tracking.model.toLatitudeLongitude
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class CardioTrackingViewModel @Inject constructor(
    private val coordinatesRepository: CoordinatesRepository
) : ViewModel() {

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