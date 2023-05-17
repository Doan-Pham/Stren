package com.haidoan.android.stren.feat.training.history.log_workout

import com.haidoan.android.stren.core.model.TrainedExercise
import java.time.LocalDate

internal sealed interface LogWorkoutUiState {
    object Loading : LogWorkoutUiState
    object EmptyWorkout : LogWorkoutUiState
    data class IsLogging(val date: LocalDate, val trainedExercises: List<TrainedExercise>) :
        LogWorkoutUiState
}