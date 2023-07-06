package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.haidoan.android.stren.core.utils.DateUtils
import java.time.LocalDate

enum class ActivityLevel(
    val activityLevelName: String,
    val description: String,
    val bmrFactor: Float
) {
    SEDENTARY("Sedentary", "Little or no exercise", 1.2F),
    LIGHTLY_ACTIVE("Lightly Active", "Light exercise/sports 1-3 days/week", 1.375F),
    MODERATELY_ACTIVE("Moderately Active", "Moderate exercise/sports 3-5 days/week", 1.55F),
    VERY_ACTIVE("Very Active", "Hard exercise/sports 6-7 days a week", 1.725F),
    ATHLETE(
        "Athlete",
        "Very hard exercise/sports & physical job or training 2x per day",
        1.9F
    );

    companion object {
        private val valuesByActivityLevelName =
            ActivityLevel.values().associateBy(ActivityLevel::activityLevelName)

        fun findByActivityLevelName(name: String) = valuesByActivityLevelName[name]
    }
}

enum class WeightGoal(
    val weightGoalName: String,
    val description: String,
    val caloriesAmountToModify: Long
) {
    LOSE_WEIGHT_FAST("Lose Weight Fast", "(-0.5kg/week)", -500),
    LOSE_WEIGHT_SLOW("Lose Weight Slow", "(-0.25kg/week)", -250),
    MAINTAIN_WEIGHT("Maintain Weight", "", 0),
    GAIN_WEIGHT_SLOW("Gain Weight Slow", "(0.25kg/week)", 250),
    GAIN_WEIGHT_FAST(
        "Gain Weight Fast", "(0.5kg/week)", 500
    );

    companion object {
        private val valuesByWeightGoalName =
            WeightGoal.values().associateBy(WeightGoal::weightGoalName)

        fun findByWeightGoalName(name: String) = valuesByWeightGoalName[name]
    }
}

enum class Sex(val sexName: String) {
    MALE("Male"), FEMALE("Female");

    companion object {
        private val valuesBySexName = Sex.values().associateBy(Sex::sexName)
        fun findBySexName(name: String) = valuesBySexName[name]
    }
}

data class User(
    @DocumentId
    val id: String,
    val displayName: String,
    val email: String,
    val age: Long,
    val sex: Sex,
    val activityLevel: ActivityLevel,
    val weightGoal: WeightGoal,
    val shouldShowOnboarding: Boolean,
    val biometricsRecords: List<BiometricsRecord> = listOf(),
    val goals: List<Goal> = listOf(),
    val trackedCategories: List<TrackedCategory> = listOf()
) {
    companion object {
        val undefined = User(
            id = "Undefined",
            displayName = "Undefined",
            email = "Undefined",
            age = -1,
            sex = Sex.FEMALE,
            activityLevel = ActivityLevel.ATHLETE,
            weightGoal = WeightGoal.GAIN_WEIGHT_FAST,
            shouldShowOnboarding = true,
        )
    }
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
                measurementUnit = CommonBiometrics.WEIGHT.measurementUnit,
                value = weightInKg
            )

        fun createHeightBiometrics(heightInCm: Float, date: LocalDate) =
            BiometricsRecord(
                biometricsId = CommonBiometrics.HEIGHT.id,
                biometricsName = CommonBiometrics.HEIGHT.biometricsName,
                recordDate = date,
                measurementUnit = CommonBiometrics.HEIGHT.measurementUnit,
                value = heightInCm
            )
    }

}

/**
 * Return a list of the latest records for each biometricsId (which means for records with
 * the same biometricsId, only the latest ones are included and the earlier ones are removed
 */
@JvmName("filterLatestBiometrics")
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
        foodNutrientId = FOOD_NUTRIENT_ID_CALORIES,
        effectiveFrom = DateUtils.getCurrentDate()
    )

fun CoreNutrient.toGoal(nutrientAmountInGram: Float) = Goal.FoodNutrientGoal(
    id = "GOAL_ID" + "_" + this.id,
    name = this.nutrientName,
    type = GoalType.FOOD_NUTRIENT,
    value = nutrientAmountInGram,
    foodNutrientId = this.id,
    effectiveFrom = DateUtils.getCurrentDate()
)

enum class GoalType {
    FOOD_NUTRIENT
}

sealed class Goal {
    abstract val id: String
    abstract val name: String
    abstract val type: GoalType
    abstract val value: Float
    abstract val effectiveFrom: LocalDate

    data class FoodNutrientGoal(
        override val id: String,
        override val name: String,
        override val type: GoalType = GoalType.FOOD_NUTRIENT,
        override val value: Float,
        val foodNutrientId: String, override val effectiveFrom: LocalDate
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

/**
 * Return a list of the latest values for each goalId
 */
@JvmName("filterLatestGoals")
fun <GoalSubType : Goal> List<GoalSubType>.filterLatest(): List<GoalSubType> {
    val latestValueByGoalId = mutableMapOf<String, GoalSubType>()
    this.forEach { goal ->
        val goalId = goal.id
        val effectiveDate = goal.effectiveFrom
        if (!latestValueByGoalId.containsKey(goalId) ||
            effectiveDate.isAfter(latestValueByGoalId[goalId]?.effectiveFrom)
        ) {
            latestValueByGoalId[goalId] = goal
        }
    }
    return latestValueByGoalId.values.toList()
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