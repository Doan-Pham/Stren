package com.haidoan.android.stren.feat.trainining.routines

import com.haidoan.android.stren.core.model.Routine

internal sealed interface RoutinesUiState {
    object Loading : RoutinesUiState
    data class LoadComplete(val routines: List<Routine>) : RoutinesUiState
    object LoadEmpty : RoutinesUiState
}