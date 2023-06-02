package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.haidoan.android.stren.core.utils.DateUtils
import java.time.LocalDate

data class User(
    @DocumentId
    val id: String,
    val email: String,
    val shouldShowOnboarding: Boolean,
    val biometricsRecords: List<BiometricsRecord> = listOf(),
    val goals: List<Goal> = listOf(),
    val trackedCategories: List<TrackedCategory> = listOf()
) {
    companion object {
        val undefined = User("Undefined", "Undefined", true, listOf())
    }
}

enum class CommonBiometricsIds {
    BIOMETRICS_ID_WEIGHT,
    BIOMETRICS_ID_HEIGHT
}

data class BiometricsRecord(
    @DocumentId
    val id: String = "Undefined Document Id",
    val biometricsId: String,
    val recordDate: LocalDate,
    val measurementUnit: String,
    val value: Float
) {
    companion object {
        fun createWeightBiometrics(weightInKg: Float, date: LocalDate) =
            BiometricsRecord(
                biometricsId = CommonBiometricsIds.BIOMETRICS_ID_WEIGHT.toString(),
                recordDate = date,
                measurementUnit = "kg",
                value = weightInKg
            )

        fun createHeightBiometrics(heightInCm: Float, date: LocalDate) =
            BiometricsRecord(
                biometricsId = CommonBiometricsIds.BIOMETRICS_ID_HEIGHT.toString(),
                recordDate = date,
                measurementUnit = "cm",
                value = heightInCm
            )
    }

}

const val GOAL_ID_CALORIES = "GOAL_ID_CALORIES"
private fun createCalorieGoal(amountInKcal: Float) =
    Goal.FoodNutrientGoal(
        id = GOAL_ID_CALORIES,
        name = "Calories",
        type = GoalType.FOOD_NUTRIENT,
        value = amountInKcal,
        foodNutrientId = FOOD_NUTRIENT_ID_CALORIES
    )

fun CoreNutrient.toGoal(nutrientAmountInGram: Float) = Goal.FoodNutrientGoal(
    id = "GOAL_ID" + "_" + this.id,
    name = this.nutrientName,
    type = GoalType.FOOD_NUTRIENT,
    value = nutrientAmountInGram,
    foodNutrientId = this.id
)

enum class GoalType {
    FOOD_NUTRIENT
}

sealed class Goal {
    abstract val id: String
    abstract val name: String
    abstract val type: GoalType
    abstract val value: Float

    data class FoodNutrientGoal(
        override val id: String,
        override val name: String,
        override val type: GoalType = GoalType.FOOD_NUTRIENT,
        override val value: Float,
        val foodNutrientId: String
    ) : Goal() {
        companion object {
            fun createCoreNutrientAndCalorieGoals(
                coreNutrientAmountInGram: Map<CoreNutrient, Float>,
                caloriesAmountInKcal: Float
            ) = coreNutrientAmountInGram.keys.map {
                it.toGoal(
                    coreNutrientAmountInGram[it] ?: 0f
                )
            } + createCalorieGoal(caloriesAmountInKcal)
        }
    }
}


sealed class TrackedCategory {
    @get:PropertyName("isDefaultCategory")
    abstract val isDefaultCategory: Boolean
    abstract val categoryType: TrackedCategoryType
    abstract val dataSourceId: String
    abstract val startDate: LocalDate
    abstract val endDate: LocalDate
    abstract fun withStartDate(startDate: LocalDate): TrackedCategory
    abstract fun withEndDate(endDate: LocalDate): TrackedCategory
    data class Calories(
        override val dataSourceId: String = DataSourceBaseId.DATA_SOURCE_ID_CALORIES.toString(),
        override val startDate: LocalDate = DateUtils.getCurrentDate().minusWeeks(1),
        override val endDate: LocalDate = DateUtils.getCurrentDate(),
        override val categoryType: TrackedCategoryType = TrackedCategoryType.CALORIES,
        override val isDefaultCategory: Boolean
    ) : TrackedCategory() {
        override fun withStartDate(startDate: LocalDate): TrackedCategory =
            this.copy(startDate = startDate)

        override fun withEndDate(endDate: LocalDate): TrackedCategory = this.copy(endDate = endDate)
    }


    data class ExerciseOneRepMax(
        override val categoryType: TrackedCategoryType = TrackedCategoryType.EXERCISE_1RM,
        override val startDate: LocalDate = DateUtils.getCurrentDate().minusWeeks(1),
        override val endDate: LocalDate = DateUtils.getCurrentDate(),
        val exerciseId: String,
        val exerciseName: String,
        override val dataSourceId: String =
            DataSourceBaseId.DATA_SOURCE_ID_EXERCISE_1RM.toString() + "_" + exerciseId,
        override val isDefaultCategory: Boolean
    ) : TrackedCategory() {
        override fun withStartDate(startDate: LocalDate): TrackedCategory =
            this.copy(startDate = startDate)

        override fun withEndDate(endDate: LocalDate): TrackedCategory = this.copy(endDate = endDate)
    }
}

enum class TrackedCategoryType {
    CALORIES, EXERCISE_1RM
}

enum class DataSourceBaseId {
    DATA_SOURCE_ID_CALORIES, DATA_SOURCE_ID_EXERCISE_1RM
}