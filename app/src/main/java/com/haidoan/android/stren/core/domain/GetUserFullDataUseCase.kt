package com.haidoan.android.stren.core.domain

import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.repository.base.DefaultValuesRepository
import com.haidoan.android.stren.core.repository.base.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserFullDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val defaultValuesRepository: DefaultValuesRepository
) {
    operator fun invoke(
        userId: String,
    ): Flow<User> =
        userRepository.getUserStream(userId).map { user ->
            val defaultTrackedCategories = defaultValuesRepository.getDefaultTrackedCategories()
            val resultTrackedCategories = user.trackedCategories.toMutableList()
            defaultTrackedCategories.forEach { defaultCategory ->
                if (!user.trackedCategories.any { it.dataSourceId == defaultCategory.dataSourceId }) {
                    resultTrackedCategories.add(defaultCategory)
                }
            }
            user.copy(trackedCategories = resultTrackedCategories)
        }
}