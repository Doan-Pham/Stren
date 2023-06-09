package com.haidoan.android.stren.feat.training.history.log_workout

/**
 * This class deals with secondary uiState (when to show some dialogs) while
 * [LogWorkoutUiState] deals with primary ui state (what data to show, whole screen state)
 */
data class LogWorkoutSecondaryUiState(
    val shouldShowBackConfirmDialog: Boolean = false,
    val shouldShowRoutineWarningDialog: Boolean = false,
    val selectedRoutineId: String = NO_SELECTION_ROUTINE_ID,
    val onConfirmSwitchRoutine: () -> Unit = {}
)