package com.haidoan.android.stren.feat.training.exercises

import com.haidoan.android.stren.core.model.Exercise

internal sealed interface ExercisesUiState {
    object Loading : ExercisesUiState
    data class LoadComplete(val exercises: List<Exercise>) : ExercisesUiState
}
