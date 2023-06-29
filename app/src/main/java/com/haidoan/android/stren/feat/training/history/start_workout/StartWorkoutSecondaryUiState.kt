package com.haidoan.android.stren.feat.training.history.start_workout

import com.haidoan.android.stren.core.designsystem.component.ConfirmationDialogState

/**
 * This class deals with secondary uiState (when to show some dialogs) while
 * [LogWorkoutUiState] deals with primary ui state (what data to show, whole screen state)
 */
data class StartWorkoutSecondaryUiState(
    val shouldShowConfirmDialog: Boolean = false,
    val confirmDialogState: ConfirmationDialogState = ConfirmationDialogState.undefined,
    val selectedRoutineId: String = NO_SELECTION_ROUTINE_ID,
    val onConfirmSwitchRoutine: () -> Unit = {}
)