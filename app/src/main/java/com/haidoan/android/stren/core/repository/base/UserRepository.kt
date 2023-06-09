package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserRepository {
    /**
     * The result [User] only includes the latest biometricsRecords (not all of them)
     */
    fun getUserStream(userId: String): Flow<User>

    /**
     * The result [User] only includes the latest biometricsRecords (not all of them)
     */
    suspend fun getUser(userId: String): User
    suspend fun modifyUserProfile(
        userId: String,
        displayName: String,
        age: Long,
        sex: Sex,
        activityLevel: ActivityLevel,
        weightGoal: WeightGoal
    )

    suspend fun isUserExists(userId: String): Boolean
    suspend fun addUser(user: User): Any?

    suspend fun getAllBiometricsToTrack(userId: String): List<BiometricsRecord>
    fun getBiometricsRecordsStreamById(
        userId: String,
        biometricsId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<BiometricsRecord>>

    fun getAllBiometricsRecordsStream(
        userId: String,
    ): Flow<List<BiometricsRecord>>

    suspend fun addBiometricsRecords(userId: String, biometricsRecords: List<BiometricsRecord>)
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
    suspend fun addBiometricsRecord(userId: String, biometricsRecord: BiometricsRecord)

}