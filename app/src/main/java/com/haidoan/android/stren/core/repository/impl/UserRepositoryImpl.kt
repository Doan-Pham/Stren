package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.base.UserRemoteDataSource
import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.model.Goal
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.repository.base.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val dataSource: UserRemoteDataSource) :
    UserRepository {
    override fun getUserStream(userId: String): Flow<User> =
        dataSource.getUserStream(userId).catch {
            Timber.e("getUserStream() - Exception: $it")
        }

    override suspend fun getUser(userId: String): User =
        try {
            dataSource.getUser(userId)
        } catch (ex: Exception) {
            Timber.e("getUser() - Exception: $ex")
            User.undefined
        }

    override suspend fun modifyUserProfile(
        userId: String,
        displayName: String,
        age: Long,
        sex: String
    ) {
        try {
            dataSource.modifyUserProfile(userId, displayName, age, sex)
        } catch (ex: Exception) {
            Timber.e("modifyUserProfile() - userId: $userId - Exception: $ex")
        }
    }

    override suspend fun isUserExists(userId: String): Boolean =
        try {
            dataSource.isUserExists(userId = userId)
        } catch (ex: Exception) {
            Timber.e("isUserExists() - Exception: $ex")
            false
        }

    override suspend fun addUser(user: User) = try {
        dataSource.addUser(user)
    } catch (ex: Exception) {
        Timber.e("addUser() - Exception: $ex")
    }

    override suspend fun getAllBiometricsToTrack(userId: String): List<BiometricsRecord> {
        return try {
            dataSource.getAllBiometricsToTrack(userId)
        } catch (ex: Exception) {
            Timber.e("getAllBiometricsToTrack() - Exception: $ex \nStack Trace: ${ex.stackTrace} ")
            emptyList()
        }
    }

    override fun getBiometricsRecordsStream(
        userId: String,
        biometricsId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<BiometricsRecord>> {
        Timber.d(
            "getBiometricsRecordsStream() is called; " +
                    "userId: $userId, " +
                    "biometricsId: $biometricsId" +
                    ",startDate: $startDate" +
                    ", endDate: $endDate "
        )

        return dataSource
            .getBiometricsRecordsStream(userId, biometricsId, startDate, endDate)
            .catch {
                Timber.e("getBiometricsRecordsStream() - Exception: $it - stacktrace: ${it.stackTrace}")
            }
    }

    override suspend fun addBiometricsRecord(
        userId: String, biometricsRecords: List<BiometricsRecord>
    ) {
        try {
            dataSource.addBiometricsRecord(userId, biometricsRecords)
        } catch (ex: Exception) {
            Timber.e("addBiometricsRecord() - userId: $userId - Exception: $ex")
        }
    }

    override suspend fun addGoals(
        userId: String, goals: List<Goal>
    ) {
        try {
            dataSource.addGoals(userId, goals)
        } catch (ex: Exception) {
            Timber.e("addGoals() - userId: $userId - Exception: $ex")
        }
    }

    override suspend fun trackCategory(userId: String, category: TrackedCategory) {
        try {
            dataSource.trackCategory(userId, category)

        } catch (ex: Exception) {
            Timber.e("trackCategory() - userId: $userId, category: $category - Exception: $ex")
        }
    }

    override suspend fun updateTrackCategory(
        userId: String,
        dataSourceId: String,
        newStartDate: LocalDate,
        newEndDate: LocalDate
    ) {
        try {
            dataSource.updateTrackCategory(userId, dataSourceId, newStartDate, newEndDate)
        } catch (ex: Exception) {
            Timber.e("updateTrackCategory() - userId: $userId, dataSourceId: $dataSourceId - Exception: $ex")
        }
    }

    override suspend fun stopTrackingCategory(
        userId: String, dataSourceId: String
    ) {
        try {
            dataSource.stopTrackingCategory(userId, dataSourceId)
        } catch (ex: Exception) {
            Timber.e("stopTrackingCategory() - userId: $userId, dataSourceId: $dataSourceId - Exception: $ex")
        }
    }


    override suspend fun completeOnboarding(userId: String) {
        try {
            dataSource.completeOnboarding(userId)
        } catch (ex: Exception) {
            Timber.e("completeOnboarding() - userId: $userId - Exception: $ex")
        }
    }

}