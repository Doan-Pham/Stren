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
        val defaultCoreNutrients = listOf(
            FoodNutrient("Protein", 0f, "g"),
            FoodNutrient("Carb", 0f, "g"),
            FoodNutrient("Fat", 0f, "g"),
        )
        val undefinedFood = Food(
            "Undefined",
            "Undefined",
            undefinedFoodNutrient,
            listOf(),
            listOf()
        )
    }
}


data class FoodNutrient(
    val nutrientName: String = "Undefined",
    val amount: Float = -1f,
    val unitName: String = "Undefined"
) {
    companion object {
        /**
         * By default, [FoodNutrient] has its amount calculated per 100gram of food
         */
        const val DEFAULT_FOOD_AMOUNT_IN_GRAM = 100f

        val undefinedFoodNutrient = FoodNutrient("Undefined", -1F, "Undefined")

        fun FoodNutrient.with(foodAmountInGram: Float): FoodNutrient {
            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.DOWN
            val newAmount =
                df.format(amount * foodAmountInGram / DEFAULT_FOOD_AMOUNT_IN_GRAM)
                    .toFloat()
            return this.copy(amount = newAmount)
        }
    }
}
