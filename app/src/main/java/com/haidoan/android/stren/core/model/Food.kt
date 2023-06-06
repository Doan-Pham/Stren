package com.haidoan.android.stren.core.model

import com.haidoan.android.stren.core.model.FoodNutrient.Companion.undefinedFoodNutrient
import java.math.RoundingMode
import java.text.DecimalFormat


data class Food(
    val id: String = "Undefined",
    val name: String = "Undefined ",
    val calories: FoodNutrient = undefinedFoodNutrient,
    val coreNutrients: List<FoodNutrient> = listOf(),
    val otherNutrients: List<FoodNutrient> = listOf(),
    val brandName: String = "",
) {
    companion object {
        val defaultCoreNutrients = CoreNutrient.values().map {
            FoodNutrient(
                id = it.id,
                nutrientName = it.name,
                amount = 0f,
                unitName = it.measurementUnit,
            )
        }

        val undefinedFood = Food(
            "Undefined", "Undefined", undefinedFoodNutrient, listOf(), listOf()
        )
    }
}

const val FOOD_NUTRIENT_ID_CALORIES = "FOOD_NUTRIENT_ID_CALORIES"

enum class CoreNutrient(
    val id: String,
    val energyPerGram: Float,
    val nutrientName: String,
    val measurementUnit: String,
    val defaultRatioInEatingPlan: Float,
) {
    PROTEIN(
        id = "FOOD_NUTRIENT_ID_PROTEIN",
        nutrientName = "Protein",
        energyPerGram = 4F,
        measurementUnit = "g",
        defaultRatioInEatingPlan = 1f
    ),
    CARB(
        id = "FOOD_NUTRIENT_ID_CARB",
        nutrientName = "Carb",
        energyPerGram = 4F,
        measurementUnit = "g",
        defaultRatioInEatingPlan = 1.15f
    ),
    FAT(
        id = "FOOD_NUTRIENT_ID_FAT",
        nutrientName = "Fat",
        energyPerGram = 9F,
        measurementUnit = "g",
        defaultRatioInEatingPlan = 1.15f
    );

    companion object {
        private val nutrientNameMap = CoreNutrient.values().associateBy(CoreNutrient::nutrientName)
        fun fromNutrientName(nutrientName: String) =
            if (nutrientNameMap[nutrientName] == null) {
                throw IllegalArgumentException("CoreNutrient.fromNutrientName() - Enum doesn't have any value with \"nutrientName\" being: $nutrientName")
            } else {
                nutrientNameMap[nutrientName]
            }

    }
}

data class FoodNutrient(
    val id: String = "Undefined Id",
    val nutrientName: String = "Undefined",
    val amount: Float = -1f,
    val unitName: String = "Undefined"
) {
    companion object {
        /**
         * By default, [FoodNutrient] has its amount calculated per 100gram of food
         */
        const val DEFAULT_FOOD_AMOUNT_IN_GRAM = 100f

        val undefinedFoodNutrient = FoodNutrient("Undefined Id", "Undefined", -1F, "Undefined")

        fun FoodNutrient.with(foodAmountInGram: Float): FoodNutrient {
            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.DOWN
            val newAmount =
                df.format(amount * foodAmountInGram / DEFAULT_FOOD_AMOUNT_IN_GRAM).toFloat()
            return this.copy(amount = newAmount)
        }
    }
}
