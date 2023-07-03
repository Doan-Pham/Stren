package com.haidoan.android.stren.feat.training.history.workout_detail

import com.haidoan.android.stren.core.model.Workout

internal sealed interface WorkoutDetailUiState {
    object Loading : WorkoutDetailUiState
    data class LoadComplete(val workout: Workout) : WorkoutDetailUiState
}