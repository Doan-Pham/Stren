package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserStream(userId: String): Flow<User>

    suspend fun trackCategory(userId: String, category: TrackedCategory)
}