package com.haidoan.android.stren.core.datasource.remote.base

import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserRemoteDataSource {
    fun getUserStream(userId: String): Flow<User>
    suspend fun trackCategory(userId: String, category: TrackedCategory)
    suspend fun updateTrackCategory(
        userId: String,
        dataSourceId: String,
        newStartDate: LocalDate,
        newEndDate: LocalDate
    )
}