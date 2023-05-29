package com.haidoan.android.stren.core.datasource.remote.base

import com.haidoan.android.stren.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    fun getUserStream(userId: String): Flow<User>
}