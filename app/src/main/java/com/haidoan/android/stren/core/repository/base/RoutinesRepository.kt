package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutinesRepository {
    suspend fun getRoutinesStreamByUserId(userId: String): Flow<List<Routine>>
    suspend fun getRoutinesByUserId(userId: String): List<Routine>
    suspend fun getRoutineById(userId: String, routineId: String): Routine
    suspend fun addRoutine(userId: String, routine: Routine): String
    suspend fun updateRoutine(userId: String, routine: Routine)
    suspend fun deleteRoutine(userId: String, routineId: String)
}