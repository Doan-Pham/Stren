package com.haidoan.android.stren.core.repository.fake

import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.Workout
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class FakeWorkoutsRepository : WorkoutsRepository {
    // Backing MutableFlow for testing
    private val _workoutsFlow: MutableSharedFlow<Map<String, List<Workout>>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun setWorkouts(userId: String, workouts: List<Workout>) {
        _workoutsFlow.tryEmit(mapOf(userId to workouts))
    }

    override fun getWorkoutsStreamByUserIdAndDate(
        userId: String,
        date: LocalDate
    ): Flow<List<Workout>> {
        TODO("Not yet implemented")
    }

    override fun getWorkoutsByUserIdAndDate(userId: String, date: LocalDate): Flow<List<Workout>> =
        _workoutsFlow.map {
            it.getOrDefault(userId, listOf()).filter { workout -> workout.date.isEqual(date) }
        }

    override fun getDatesThatHaveWorkoutByUserId(userId: String): Flow<List<LocalDate>> {
        TODO("Not yet implemented")
    }

    override suspend fun addWorkout(userId: String, workout: Workout): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWorkout(workoutId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkoutById(workoutId: String): Workout {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkout(userId: String, workout: Workout) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllExercisesTrained(userId: String): List<TrainedExercise> {
        TODO("Not yet implemented")
    }

    override fun getExerciseOneRepMaxesStream(
        userId: String,
        exerciseId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Map<LocalDate, Float>> {
        TODO("Not yet implemented")
    }
}