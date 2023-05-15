package com.haidoan.android.stren.core.repository

import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutinesRepository {
    suspend fun getRoutinesByUserId(userId: String): Flow<List<Routine>>
    suspend fun getRoutineById(userId: String, routineId: String): Routine
    suspend fun addRoutine(userId: String, routine: Routine): String
}