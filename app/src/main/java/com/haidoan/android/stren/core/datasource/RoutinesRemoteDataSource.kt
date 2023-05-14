package com.haidoan.android.stren.core.datasource

import com.haidoan.android.stren.core.model.Routine

interface RoutinesRemoteDataSource {
    suspend fun getRoutinesByUserId(userId: String): List<Routine>

    suspend fun addRoutine(userId: String, routine: Routine): String
}