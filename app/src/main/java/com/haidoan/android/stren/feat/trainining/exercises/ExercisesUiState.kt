package com.haidoan.android.stren.feat.trainining.exercises

import com.haidoan.android.stren.core.model.Exercise

internal sealed interface ExercisesUiState {
    object Loading : ExercisesUiState
    data class LoadComplete(val exercises: List<Exercise>) : ExercisesUiState
}
