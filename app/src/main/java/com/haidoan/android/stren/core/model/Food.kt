package com.haidoan.android.stren.core.model

data class Food(val id: String, val name: String, val nutrients: List<FoodNutrient>) {
    companion object {
        val undefinedFood = Food("Undefined", "Undefined", listOf())
    }
}

data class FoodNutrient(val name: String, val amount: Float, val unitName: String)
