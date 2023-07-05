package com.haidoan.android.stren.core.domain

import com.haidoan.android.stren.core.model.CoreNutrient
import com.haidoan.android.stren.core.model.Sex

object NutritionCalculationUseCase {
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