package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import com.haidoan.android.stren.core.model.Food.Companion.defaultCoreNutrients
import com.haidoan.android.stren.core.model.FoodNutrient.Companion.with
import com.haidoan.android.stren.core.utils.ListUtils.replaceWith
import timber.log.Timber
import java.time.LocalDate
import java.util.*

data class EatingDay(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val meals: List<Meal> = listOf()
) {
    val totalCalories = this.meals.sumOf { it.totalCalories() }
    val totalMacros: List<FoodNutrient>
        get() {
            var result = defaultCoreNutrients.toMutableList()

            this.meals.forEach { meals ->
                meals.totalMacros().forEach { macronutrient ->
                    Timber.d("Day - macronutrient: $macronutrient ; result - before: $result")

                    if (!result.any { it.nutrientName == macronutrient.nutrientName }) {
                        result.add(macronutrient)
                    } else {
                        val oldNutrientAmount =
                            result.first { it.nutrientName == macronutrient.nutrientName }
                        result = result.replaceWith(
                            macronutrient.copy(
                                amount = oldNutrientAmount.amount + macronutrient
                                    .amount
                            )
                        ) { it.nutrientName == macronutrient.nutrientName }.toMutableList()
                    }
                    Timber.d("Day - result - after: $result")
                }
            }

            Timber.d("Day - result - final: $result")
            return result.sortedBy { it.nutrientName }
        }


    companion object {
        val undefined = EatingDay(id = "Undefined", date = LocalDate.of(1000, 10, 10))
    }
}

enum class DefaultMeal(val id: String, val indexWhenSorted: Int) {
    BREAKFAST(MEAL_BREAKFAST_ID, 0),
    LUNCH(MEAL_LUNCH_ID, 1),
    DINNER(MEAL_DINNER_ID, 2),
    SNACKS(MEAL_SNACKS_ID, 3);

    companion object {
        fun from(id: String): DefaultMeal? = DefaultMeal.values().find { it.id == id }
    }
}

const val MEAL_BREAKFAST_ID = "MEAL_BREAKFAST_ID"
const val MEAL_LUNCH_ID = "MEAL_LUNCH_ID"
const val MEAL_DINNER_ID = "MEAL_DINNER_ID"
const val MEAL_SNACKS_ID = "MEAL_SNACKS_ID"

data class Meal(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Undefined Meal name",
    val foods: List<FoodToConsume> = listOf()
) {
    fun totalCalories() = this.foods.sumOf { it.totalCalories() }
    val totalCaloriesString
        get() = "${totalCalories()}kcal"

    fun totalMacros(): List<FoodNutrient> {
        var result = mutableListOf<FoodNutrient>()
        this.foods.forEach { foodToConsume ->
            foodToConsume.totalMacros().forEach { macronutrient ->
                Timber.d("Meal - macronutrient: $macronutrient ; result - before: $result")
                if (!result.any { it.nutrientName == macronutrient.nutrientName }) {
                    result.add(macronutrient)
                } else {
                    val nutrientInResult =
                        result.first { it.nutrientName == macronutrient.nutrientName }
                    result = result.replaceWith(
                        macronutrient.copy(
                            amount = nutrientInResult.amount + macronutrient
                                .amount
                        )
                    ) { it.nutrientName == macronutrient.nutrientName }.toMutableList()
                }
                Timber.d("Meal - result - after: $result")

            }
        }
        Timber.d("Meal - result - final: $result")
        return result
    }

    companion object {
        val defaultMeals = listOf(
            Meal(id = MEAL_BREAKFAST_ID, name = "Breakfast"),
            Meal(id = MEAL_LUNCH_ID, name = "Lunch"),
            Meal(id = MEAL_DINNER_ID, name = "Dinner"),
            Meal(id = MEAL_SNACKS_ID, name = "Snacks")
        )
    }
}

data class FoodToConsume(val food: Food = Food(), val amountInGram: Float = -1f) {
    fun totalCalories(): Int =
        this.food.calories.with(foodAmountInGram = amountInGram).amount.toInt()

    fun totalMacros(): List<FoodNutrient> =
        this.food.coreNutrients.map { it.with(foodAmountInGram = amountInGram) }

    val totalCaloriesString
        get() = "${totalCalories()}kcal"
}