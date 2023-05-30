package com.haidoan.android.stren.core.domain

import com.haidoan.android.stren.core.datasource.remote.di.IoDispatcher
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.repository.base.DefaultValuesRepository
import com.haidoan.android.stren.core.repository.base.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserFullDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val defaultValuesRepository: DefaultValuesRepository,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        userId: String,
    ): Flow<User> {
        withContext(dispatcher) {
            val user = userRepository.getUser(userId)
            val defaultTrackedCategories = defaultValuesRepository.getDefaultTrackedCategories()
            defaultTrackedCategories.forEach { defaultCategory ->
                if (!user.trackedCategories.any { it.dataSourceId == defaultCategory.dataSourceId }) {
                    userRepository.trackCategory(userId, defaultCategory)
                }
            }
        }
        return userRepository.getUserStream(userId)
    }
}