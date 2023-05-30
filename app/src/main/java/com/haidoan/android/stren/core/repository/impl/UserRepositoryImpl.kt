package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.base.UserRemoteDataSource
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.repository.base.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val dataSource: UserRemoteDataSource) :
    UserRepository {
    override fun getUserStream(userId: String): Flow<User> =
        dataSource.getUserStream(userId).catch {
            Timber.e("getUserStream() - Exception: $it")
        }

    override suspend fun trackCategory(userId: String, category: TrackedCategory) {
        try {
            dataSource.trackCategory(userId, category)

        } catch (ex: Exception) {
            Timber.e("trackCategory() - userId: $userId, category: $category - Exception: $ex")
        }
    }
}