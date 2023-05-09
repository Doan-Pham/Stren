package com.haidoan.android.stren.core.datasource

import com.haidoan.android.stren.core.model.Workout
import java.time.LocalDate

interface WorkoutRemoteDataSource {
    suspend fun getWorkoutsByUserIdAndDate(userId: String, date: LocalDate): List<Workout>
}