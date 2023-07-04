package com.haidoan.android.stren.feat.training.routines

import com.haidoan.android.stren.core.designsystem.component.ConfirmationDialogState
import com.haidoan.android.stren.core.model.Routine

internal sealed interface RoutinesUiState {
    object Loading : RoutinesUiState
    data class LoadComplete(val routines: List<Routine>) : RoutinesUiState
    object LoadEmpty : RoutinesUiState
}

data class RoutinesSecondaryUiState(
    val shouldShowConfirmDialog: Boolean = false,
    val confirmDialogState: ConfirmationDialogState = ConfirmationDialogState.undefined,
)