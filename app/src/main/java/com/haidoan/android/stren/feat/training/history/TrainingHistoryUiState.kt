package com.haidoan.android.stren.feat.training.history

import com.haidoan.android.stren.core.designsystem.component.ConfirmationDialogState
import com.haidoan.android.stren.core.designsystem.component.SingleSelectionDialogState
import com.haidoan.android.stren.core.model.Workout
import java.time.LocalDate

internal sealed interface TrainingHistoryUiState {
    object Loading : TrainingHistoryUiState
    data class LoadComplete(
        val userId: String,
        val workouts: List<Workout>,
        val selectedDate: LocalDate,
        val datesThatHaveWorkouts: List<LocalDate>
    ) : TrainingHistoryUiState
}

data class TrainingHistorySecondaryUiState(
    val shouldShowConfirmDialog: Boolean = false,
    val confirmDialogState: ConfirmationDialogState = ConfirmationDialogState.undefined,
    val shouldShowSingleSelectionDialog: Boolean = false,
    val workoutOptionDialogState: SingleSelectionDialogState = SingleSelectionDialogState.undefined,
)