package com.haidoan.android.stren.feat.trainining.routines.add_edit

import com.haidoan.android.stren.core.model.TrainedExercise

internal sealed interface AddEditRoutineUiState {
    object Loading : AddEditRoutineUiState
    object EmptyRoutine : AddEditRoutineUiState
    data class IsAdding(val trainedExercises: List<TrainedExercise>) : AddEditRoutineUiState
}