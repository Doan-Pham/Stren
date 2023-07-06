package com.haidoan.android.stren.core.domain

import com.haidoan.android.stren.core.model.FoodNutrient
import com.haidoan.android.stren.core.model.Goal
import com.haidoan.android.stren.core.model.filterLatest
import com.haidoan.android.stren.core.repository.base.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject


class GetUserNutrientGoalsUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    companion object {
        /**
         * Map a list of FoodNutrients to their goals based on userId
         * @param macroNutrients The list of nutrients to map by
         */
        fun getLatestGoalByFoodNutrients(
            macroNutrients: List<FoodNutrient>,
            goals: List<Goal.FoodNutrientGoal>
        ): Map<FoodNutrient, Float> {
            val result = mutableMapOf<FoodNutrient, Float>()
            val latestGoals = goals.filterLatest()
            macroNutrients.forEach { macroNutrient ->
                result[macroNutrient] =
                    latestGoals.first { it.foodNutrientId == macroNutrient.id }.value
            }
            Timber.d("getGoalByFoodNutrients() - result: $result")
            return result
        }
    }

    operator fun invoke(
        userId: String
    ): Flow<List<Goal.FoodNutrientGoal>> =
        userRepository.getUserStream(userId).map { user ->
            Timber.d("invoke() - user: $user")
            user.goals.filterIsInstance(Goal.FoodNutrientGoal::class.java)
        }
}