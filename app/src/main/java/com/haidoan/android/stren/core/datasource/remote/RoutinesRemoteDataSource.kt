package com.haidoan.android.stren.core.datasource.remote

import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutinesRemoteDataSource {
    suspend fun getRoutinesStreamByUserId(userId: String): Flow<List<Routine>>

    suspend fun getRoutinesByUserId(userId: String): List<Routine>

    suspend fun getRoutineById(userId: String, routineId: String): Routine

    suspend fun addRoutine(userId: String, routine: Routine): String

    suspend fun updateRoutine(userId: String, routine: Routine)
}