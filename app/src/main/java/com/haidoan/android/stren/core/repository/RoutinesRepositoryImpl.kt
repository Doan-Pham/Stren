package com.haidoan.android.stren.core.repository

import com.haidoan.android.stren.core.datasource.RoutinesRemoteDataSource
import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import javax.inject.Inject

class RoutinesRepositoryImpl @Inject constructor(
    private val routinesRemoteDataSource: RoutinesRemoteDataSource
) : RoutinesRepository {


    override suspend fun getRoutinesStreamByUserId(userId: String): Flow<List<Routine>> =
        routinesRemoteDataSource.getRoutinesStreamByUserId(userId).catch {
            Timber.e("getRoutinesByUserId() - Exception: ${it.message}")
        }

    override suspend fun getRoutinesByUserId(userId: String): List<Routine> {
        return try {
            routinesRemoteDataSource.getRoutinesByUserId(userId)
        } catch (exception: Exception) {
            Timber.e("getRoutinesByUserId() - Exception: $exception")
            listOf()
        }
    }

    override suspend fun getRoutineById(userId: String, routineId: String): Routine {
        Timber.d("getRoutineById() is called: userId: $userId; routineId: $routineId")
        return try {
            routinesRemoteDataSource.getRoutineById(userId, routineId)
        } catch (ex: Exception) {
            Timber.e("getRoutineById() - exception: $ex")
            Routine(name = "Undefined", trainedExercises = listOf())
        }
    }

    override suspend fun addRoutine(userId: String, routine: Routine): String {
        return try {
            val newRoutineId = routinesRemoteDataSource.addRoutine(userId, routine)
            Timber.d("addRoutine() - Success, new routine id: $newRoutineId")
            newRoutineId
        } catch (exception: Exception) {
            Timber.e("addRoutine() - Exception: ${exception.message}")
            "Undefined Routine ID"
        }
    }

    override suspend fun updateRoutine(userId: String, routine: Routine) {
        try {
            Timber.d("updateRoutine() - userId:$userId ; routineId: ${routine.id}")
            routinesRemoteDataSource.updateRoutine(userId, routine)
        } catch (exception: Exception) {
            Timber.e("updateRoutine() - Exception: ${exception.message}")
        }
    }
}