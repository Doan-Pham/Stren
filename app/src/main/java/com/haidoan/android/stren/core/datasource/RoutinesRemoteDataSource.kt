package com.haidoan.android.stren.core.datasource

import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutinesRemoteDataSource {
    suspend fun getRoutinesStreamByUserId(userId: String): Flow<List<Routine>>

    suspend fun addRoutine(userId: String, routine: Routine): String
}