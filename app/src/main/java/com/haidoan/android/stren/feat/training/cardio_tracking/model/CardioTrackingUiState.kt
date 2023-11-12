package com.haidoan.android.stren.feat.training.cardio_tracking.model

import com.google.android.gms.maps.model.LatLng

internal sealed interface CardioTrackingUiState {
    object Loading : CardioTrackingUiState
    data class CoordinateLoaded(val coordinates: List<LatLng>, val currentCoordinate: LatLng) :
        CardioTrackingUiState
}

