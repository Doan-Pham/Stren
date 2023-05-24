package com.haidoan.android.stren.core.model

import com.haidoan.android.stren.core.model.FoodNutrient.Companion.undefinedFoodNutrient

data class Food(
    val id: String = "Undefined",
    val name: String = "Undefined ",
    val calories: FoodNutrient = undefinedFoodNutrient,
    val coreNutrients: List<FoodNutrient> = listOf(),
    val otherNutrients: List<FoodNutrient> = listOf(),
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

data class FoodNutrient(
    val nutrientName: String = "Undefined",
    val amount: Float = -1f,
    val unitName: String = "Undefined"
) {
    companion object {
        val undefinedFoodNutrient = FoodNutrient("Undefined", -1F, "Undefined")
    }
}
