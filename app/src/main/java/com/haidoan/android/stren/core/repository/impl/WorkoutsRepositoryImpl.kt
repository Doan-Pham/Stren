package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.base.WorkoutRemoteDataSource
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.Workout
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class WorkoutsRepositoryImpl @Inject constructor(private val workoutRemoteDataSource: WorkoutRemoteDataSource) :
    WorkoutsRepository {
    override fun getWorkoutsByUserIdAndDate(userId: String, date: LocalDate): Flow<List<Workout>> =
        flow {
            Timber.d("getWorkoutsByUserIdAndDate() has been called - userId: $userId; date: $date")
            emit(workoutRemoteDataSource.getWorkoutsByUserIdAndDate(userId, date))
        }.catch {
            Timber.e("getWorkoutsByUserIdAndDate() - Exception: ${it.message}")
        }

    override fun getDatesThatHaveWorkoutByUserId(userId: String): Flow<List<LocalDate>> =
        flow {
            Timber.d("getDatesThatHaveWorkoutByUserId() has been called - userId: $userId")
            emit(workoutRemoteDataSource.getDatesThatHaveWorkoutByUserId(userId))
        }.catch {
            Timber.e("getDatesThatHaveWorkoutByUserId() - Exception: ${it.message}")
        }

    override suspend fun getAllExercisesTrained(userId: String): List<TrainedExercise> {
        return try {
            workoutRemoteDataSource.getAllExercisesTrained(userId)
        } catch (ex: Exception) {
            Timber.e("getAllExercisesTrained() - Exception: ${ex.message}")
            emptyList()
        }
    }

    override suspend fun addWorkout(userId: String, workout: Workout): String {
        return try {
            val newWorkoutId = workoutRemoteDataSource.addWorkout(userId, workout)
            Timber.d("addWorkout() - Success, new workout id: $newWorkoutId")
            newWorkoutId
        } catch (exception: Exception) {
            Timber.e("addWorkout() - Exception: ${exception.message}")
            "Undefined Workout ID"
        }
    }

    override suspend fun getWorkoutById(workoutId: String): Workout {
        Timber.d("getWorkoutById() is called: workoutId: $workoutId;")
        return try {
            workoutRemoteDataSource.getWorkoutById(workoutId)
        } catch (ex: Exception) {
            Timber.e("getWorkoutById() - exception: $ex")
            Workout(
                name = "Undefined",
                trainedExercises = listOf(),
                date = LocalDate.of(1900, 20, 12)
            )
        }
    }

    override suspend fun updateWorkout(userId: String, workout: Workout) {
        try {
            Timber.d("updateWorkout() - userId: $userId; workoutId: ${workout.id}")
            workoutRemoteDataSource.updateWorkout(userId, workout)
        } catch (exception: Exception) {
            Timber.e("updateWorkout() - Exception: ${exception.message}")
        }
    }
}