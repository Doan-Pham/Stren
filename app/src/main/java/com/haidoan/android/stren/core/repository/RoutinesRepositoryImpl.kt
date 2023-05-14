package com.haidoan.android.stren.core.repository

import com.haidoan.android.stren.core.datasource.RoutinesRemoteDataSource
import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class RoutinesRepositoryImpl @Inject constructor(
    private val routinesRemoteDataSource: RoutinesRemoteDataSource
) : RoutinesRepository {

    override fun getRoutinesByUserId(userId: String): Flow<List<Routine>> =
        flow {
            Timber.d("getRoutinesByUserId() has been called - userId: $userId")
            emit(routinesRemoteDataSource.getRoutinesByUserId(userId))
        }.catch {
            Timber.e("getRoutinesByUserId() - Exception: ${it.message}")
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
}