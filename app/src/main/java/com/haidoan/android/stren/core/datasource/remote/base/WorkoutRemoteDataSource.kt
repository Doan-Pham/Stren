package com.haidoan.android.stren.core.datasource.remote.base

import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.Workout
import java.time.LocalDate

interface WorkoutRemoteDataSource {
    suspend fun getWorkoutsByUserIdAndDate(userId: String, date: LocalDate): List<Workout>
    suspend fun getDatesThatHaveWorkoutByUserId(userId: String): List<LocalDate>

    suspend fun addWorkout(userId: String, workout: Workout): String
    suspend fun getWorkoutById(workoutId: String): Workout
    suspend fun updateWorkout(userId: String, workout: Workout)
    suspend fun getAllExercisesTrained(userId: String): List<TrainedExercise>
}