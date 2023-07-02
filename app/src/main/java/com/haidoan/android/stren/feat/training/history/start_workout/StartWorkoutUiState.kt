package com.haidoan.android.stren.feat.training.history.start_workout

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.haidoan.android.stren.core.model.TrainedExercise
import java.time.LocalDate

internal sealed interface StartWorkoutUiState {
    object Loading : StartWorkoutUiState
    object EmptyWorkout : StartWorkoutUiState
    data class IsLogging(
        val date: LocalDate,
        val trainedExercises: SnapshotStateList<TrainedExercise> = mutableStateListOf()
    ) :
        StartWorkoutUiState
}