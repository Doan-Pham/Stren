package com.haidoan.android.stren.feat.training.programs.view_programs

import com.haidoan.android.stren.core.designsystem.component.ConfirmationDialogState
import com.haidoan.android.stren.core.model.TrainingProgram

internal sealed interface TrainingProgramsUiState {
    object Loading : TrainingProgramsUiState
    data class LoadComplete(val trainingPrograms: List<TrainingProgram>) : TrainingProgramsUiState
    object LoadEmpty : TrainingProgramsUiState
}

data class TrainingProgramsSecondaryUiState(
    val shouldShowConfirmDialog: Boolean = false,
    val confirmDialogState: ConfirmationDialogState = ConfirmationDialogState.undefined,
)