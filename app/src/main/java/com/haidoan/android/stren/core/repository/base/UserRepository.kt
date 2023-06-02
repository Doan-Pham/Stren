package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.model.Goal
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
    suspend fun addUser(user: User): Any?
    suspend fun addBiometricsRecord(userId: String, biometricsRecords: List<BiometricsRecord>)
    suspend fun addGoals(userId: String, goals: List<Goal>)
    suspend fun completeOnboarding(userId: String)
    suspend fun modifyUserProfile(userId: String, age: Long, sex: String)
}