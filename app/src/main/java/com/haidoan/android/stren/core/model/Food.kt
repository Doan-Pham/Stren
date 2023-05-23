package com.haidoan.android.stren.core.model

import com.haidoan.android.stren.core.model.FoodNutrient.Companion.undefinedFoodNutrient

data class Food(
    val id: String,
    val name: String,
    val calories: FoodNutrient,
    val coreNutrients: List<FoodNutrient>,
    val otherNutrients: List<FoodNutrient>,
    val brandName: String = "",
) {
    companion object {
        val undefinedFood = Food(
            "Undefined",
            "Undefined",
            undefinedFoodNutrient,
            listOf(),
            listOf()
        )
    }
}

data class FoodNutrient(val nutrientName: String, val amount: Float, val unitName: String) {
    companion object {
        val undefinedFoodNutrient = FoodNutrient("Undefined", -1F, "Undefined")
    }
}
