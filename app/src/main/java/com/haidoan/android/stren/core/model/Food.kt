package com.haidoan.android.stren.core.model

data class Food(val id: String, val name: String, val nutrients: List<FoodNutrient>)

data class FoodNutrient(val name: String, val amount: Float, val unitName: String)
