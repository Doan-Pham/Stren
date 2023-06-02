package com.haidoan.android.stren.core.domain

import com.haidoan.android.stren.core.model.CoreNutrient

object NutritionCalculationUseCase {
    val weightGoals = listOf(
        WeightGoal("Lose Weight", "(-0.5kg/week)", -500),
        WeightGoal("Lose Weight", "(-0.25kg/week)", -250),
        WeightGoal("Maintain Weight", "", 0),
        WeightGoal("Gain Weight", "(0.25kg/week)", 250),
        WeightGoal("Gain Weight", "(0.5kg/week)", 500),
    )

    val activityLevels = listOf(
        ActivityLevel("Sedentary", "Little or no exercise", 1.2F),
        ActivityLevel("Lightly Active", "Light exercise/sports 1-3 days/week", 1.375F),
        ActivityLevel("Moderately Active", "Moderate exercise/sports 3-5 days/week", 1.55F),
        ActivityLevel("Very Active", "Hard exercise/sports 6-7 days a week", 1.725F),
        ActivityLevel(
            "Athlete",
            "Very hard exercise/sports & physical job or training 2x per day",
            1.9F
        )
    )

    enum class Sex {
        MALE, FEMALE
    }

    private val defaultCoreNutrientsRatio = CoreNutrient.values()
        .associateWith { it.defaultRatioInEatingPlan }

    fun calculateCaloriesGoal(
        weightInKg: Float,
        heightInCm: Float,
        age: Long,
        sex: Sex,
        bmrActivityFactor: Float,
        amountToModifyBasedOnGoal: Long
    ): Float {
        // Basal Metabolic Rate (BMR)
        val bmr = when (sex) {
            Sex.MALE -> weightInKg * 10F + heightInCm * 6.25F - age * 5F
            Sex.FEMALE -> weightInKg * 10F + heightInCm * 6.25F - age * 5F - 161F
        }
        // Total Daily Energy Expenditure (TDEE)
        val tdee = bmr * bmrActivityFactor
        return tdee + amountToModifyBasedOnGoal
    }

    fun calculateCoreNutrientGoals(
        calories: Float,
        coreNutrientsRatio: Map<CoreNutrient, Float> = defaultCoreNutrientsRatio
    ): Map<CoreNutrient, Float> {
        // Basal Metabolic Rate (BMR)
        val sumOfRatio = coreNutrientsRatio.values.sum()
        return coreNutrientsRatio.mapValues { coreNutrientRatio ->
            val coreNutrient = coreNutrientRatio.key
            val ratio = coreNutrientRatio.value
            (calories * ratio / sumOfRatio) / coreNutrient.energyPerGram
        }
    }
}

data class WeightGoal(
    val name: String,
    val description: String,
    val caloriesAmountToModify: Long
)


data class ActivityLevel(
    val name: String,
    val description: String,
    val bmrFactor: Float
)