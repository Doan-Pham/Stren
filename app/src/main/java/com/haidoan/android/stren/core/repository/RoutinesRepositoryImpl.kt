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


    override suspend fun getRoutinesByUserId(userId: String): Flow<List<Routine>> =
        routinesRemoteDataSource.getRoutinesStreamByUserId(userId).catch {
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