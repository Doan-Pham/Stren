package com.haidoan.android.stren.core.repository

import com.haidoan.android.stren.core.datasource.WorkoutRemoteDataSource
import com.haidoan.android.stren.core.model.Workout
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

}