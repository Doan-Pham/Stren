package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate
import java.util.*

data class EatingDay(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val meals: List<Meal> = listOf()
) {
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

    companion object {
        val defaultMeals = listOf(
            Meal(id = MEAL_BREAKFAST_ID, name = "Breakfast"),
            Meal(id = MEAL_LUNCH_ID, name = "Lunch"),
            Meal(id = MEAL_DINNER_ID, name = "Dinner"),
            Meal(id = MEAL_SNACKS_ID, name = "Snacks")
        )
    }
}

data class FoodToConsume(val food: Food = Food(), val amountInGram: Float = -1f)