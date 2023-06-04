package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.haidoan.android.stren.core.utils.DateUtils
import java.time.LocalDate

data class User(
    @DocumentId
    val id: String,
    val displayName: String,
    val email: String,
    val age: Long,
    val sex: String,
    val shouldShowOnboarding: Boolean,
    val biometricsRecords: List<BiometricsRecord> = listOf(),
    val goals: List<Goal> = listOf(),
    val trackedCategories: List<TrackedCategory> = listOf()
) {
    companion object {
        val undefined = User("Undefined", "Undefined", "Undefined", -1, "Undefined", true, listOf())
    }
}

enum class CommonBiometrics(val id: String, val biometricsName: String) {
    WEIGHT(id = "BIOMETRICS_ID_WEIGHT", biometricsName = "Weight"),
    HEIGHT(id = "BIOMETRICS_ID_HEIGHT", biometricsName = "Height"),
}

data class BiometricsRecord(
    @DocumentId
    val id: String = "Undefined Document Id",
    val biometricsId: String,
    val biometricsName: String,
    val recordDate: LocalDate,
    val measurementUnit: String,
    val value: Float
) {
    companion object {
        fun createWeightBiometrics(weightInKg: Float, date: LocalDate) =
            BiometricsRecord(
                biometricsId = CommonBiometrics.WEIGHT.id,
                biometricsName = CommonBiometrics.WEIGHT.biometricsName,
                recordDate = date,
                measurementUnit = "kg",
                value = weightInKg
            )

        fun createHeightBiometrics(heightInCm: Float, date: LocalDate) =
            BiometricsRecord(
                biometricsId = CommonBiometrics.HEIGHT.id,
                biometricsName = CommonBiometrics.HEIGHT.biometricsName,
                recordDate = date,
                measurementUnit = "cm",
                value = heightInCm
            )
    }

}

/**
 * Return a list of the latest records for each biometricsId (which means for records with
 * the same biometricsId, only the latest ones are included and the earlier ones are removed
 */
fun List<BiometricsRecord>.filterLatest(): List<BiometricsRecord> {
    val latestRecordByBiometricsId = mutableMapOf<String, Pair<String, LocalDate>>()
    this.forEach { biometricsRecord ->
        val recordId = biometricsRecord.id
        val biometricsId = biometricsRecord.biometricsId
        val recordDate = biometricsRecord.recordDate
        if (!latestRecordByBiometricsId.containsKey(biometricsId) ||
            recordDate.isAfter(latestRecordByBiometricsId[biometricsId]?.second)
        ) {
            latestRecordByBiometricsId[biometricsId] = Pair(recordId, recordDate)
        }
    }
    val latestRecordsIds = latestRecordByBiometricsId.values.map { it.first }
    return this.filter { it.id in latestRecordsIds }
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

    /**
     * Mostly for deserialization purpose (Since the category type is actually represented by the child classes themselves - each class represents a type), database stores a corresponding
     * "categoryType" field from which the application deserializes to the correct child class
     */
    abstract val categoryType: TrackedCategoryType

    /**
     * Identifier of each actual tracked category
     */
    abstract val dataSourceId: String
    abstract val startDate: LocalDate
    abstract val endDate: LocalDate

    /**
     * Base copy() method for inheritance in parent sealed class isn't viable in Kotlin, since the language
     * has no way to modify the copy() method's signature for each child class, but
     * the copy() method is absolutely essential in this application. So,
     * this method works as a lesser version of copy() method
     */
    abstract fun withStartDate(startDate: LocalDate): TrackedCategory

    /**
     * Base copy() method for inheritance in parent sealed class isn't viable in Kotlin, since the language
     * has no way to modify the copy() method's signature for each child class, but
     * the copy() method is absolutely essential in this application. So,
     * this method works as a lesser version of copy() method
     */
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

    data class Biometrics(
        override val categoryType: TrackedCategoryType = TrackedCategoryType.BIOMETRICS,
        override val startDate: LocalDate = DateUtils.getCurrentDate().minusWeeks(1),
        override val endDate: LocalDate = DateUtils.getCurrentDate(),
        override val isDefaultCategory: Boolean,
        val biometricsId: String,
        val biometricsName: String,
        override val dataSourceId: String =
            DataSourceBaseId.DATA_SOURCE_ID_BIOMETRICS.toString() + "_" + biometricsId
    ) : TrackedCategory() {

        override fun withStartDate(startDate: LocalDate): TrackedCategory =
            this.copy(startDate = startDate)

        override fun withEndDate(endDate: LocalDate): TrackedCategory = this.copy(endDate = endDate)
    }
}

enum class TrackedCategoryType {
    CALORIES, EXERCISE_1RM, BIOMETRICS
}

enum class DataSourceBaseId {
    DATA_SOURCE_ID_CALORIES, DATA_SOURCE_ID_EXERCISE_1RM, DATA_SOURCE_ID_BIOMETRICS
}