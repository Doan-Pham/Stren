package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserRepository {
    fun getUserStream(userId: String): Flow<User>

    suspend fun trackCategory(userId: String, category: TrackedCategory)
    suspend fun updateTrackCategory(
        userId: String,
        dataSourceId: String,
        newStartDate: LocalDate,
        newEndDate: LocalDate
    )

    suspend fun getUser(userId: String): User
    suspend fun stopTrackingCategory(userId: String, dataSourceId: String)
    suspend fun isUserExists(userId: String): Boolean
}