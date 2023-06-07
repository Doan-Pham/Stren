package com.haidoan.android.stren.core.datasource.remote.base

import com.haidoan.android.stren.core.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserRemoteDataSource {
    fun getUserStream(userId: String): Flow<User>
    suspend fun getUser(userId: String): User
    suspend fun isUserExists(userId: String): Boolean
    suspend fun addUser(user: User)
    suspend fun modifyUserProfile(userId: String, displayName: String, age: Long, sex: String)

    suspend fun getAllBiometricsToTrack(userId: String): List<BiometricsRecord>
    fun getBiometricsRecordsStream(
        userId: String,
        biometricsId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<BiometricsRecord>>

    suspend fun addBiometricsRecord(userId: String, biometricsRecords: List<BiometricsRecord>)
    suspend fun addGoals(userId: String, goals: List<Goal>)

    suspend fun trackCategory(userId: String, category: TrackedCategory)
    suspend fun updateTrackCategory(
        userId: String,
        dataSourceId: String,
        newStartDate: LocalDate,
        newEndDate: LocalDate
    )

    suspend fun stopTrackingCategory(userId: String, dataSourceId: String)
    suspend fun completeOnboarding(userId: String)
    suspend fun createCustomExercise(userId: String, exercise: Exercise)

}