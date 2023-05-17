package com.haidoan.android.stren.core.network.model

import com.haidoan.android.stren.core.model.Food
import com.haidoan.android.stren.core.model.FoodNutrient


@kotlinx.serialization.Serializable
data class NetworkFood(
    val fdcId: Long,
    val description: String,
    val foodNutrients: List<NetworkFoodNutrient> = listOf()
)

@kotlinx.serialization.Serializable
data class NetworkFoodNutrient(val name: String, val amount: Float, val unitName: String)

fun NetworkFood.asExternalModel() =
    Food(
        id = fdcId.toString(),
        name = description,
        nutrients = foodNutrients.map { it.asExternalModel() })

fun NetworkFoodNutrient.asExternalModel() =
    FoodNutrient(name = name, amount = amount, unitName)

