package com.haidoan.android.stren.feat.training.history.workout_detail

import com.haidoan.android.stren.core.model.TrainedExercise

internal sealed interface WorkoutDetailUiState {
    object Loading : WorkoutDetailUiState
    data class IsLogging(val trainedExercises: List<TrainedExercise>) :
        WorkoutDetailUiState
}