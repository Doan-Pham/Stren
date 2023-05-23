package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.Workout
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkoutsRepository {
    fun getWorkoutsByUserIdAndDate(userId: String, date: LocalDate): Flow<List<Workout>>
    fun getDatesThatHaveWorkoutByUserId(userId: String): Flow<List<LocalDate>>
    suspend fun addWorkout(userId: String, workout: Workout): String

    suspend fun getWorkoutById(workoutId: String): Workout
    suspend fun updateWorkout(userId: String, workout: Workout)
}