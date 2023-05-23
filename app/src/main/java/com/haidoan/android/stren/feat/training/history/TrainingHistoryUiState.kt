package com.haidoan.android.stren.feat.training.history

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