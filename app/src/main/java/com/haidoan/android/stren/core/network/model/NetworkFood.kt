package com.haidoan.android.stren.core.network.model

import com.haidoan.android.stren.core.model.Food
import com.haidoan.android.stren.core.model.FoodNutrient
import com.haidoan.android.stren.core.model.FoodNutrient.Companion.undefinedFoodNutrient
import com.haidoan.android.stren.core.utils.StringFormatUtils.capitalizeFirstChar


@kotlinx.serialization.Serializable
data class NetworkFood(
    val fdcId: Long,
    val description: String,
    val foodNutrients: List<NetworkFoodNutrient> = listOf()
)

/**
 * These are the names of the core nutrients as per the FDC API Specs, should not
 * be changed unless the API Specs itself changes, or else compatibility issues
 * will happen
 */
private val coreNutrients =
    listOf("Protein", "Total lipid (fat)", "Carbohydrate, by difference")
private const val caloriesApiField = "Energy"

fun NetworkFood.asExternalModel() =
    Food(
        id = fdcId.toString(),
        name = description.capitalizeFirstChar(),
        calories = foodNutrients
            .firstOrNull { it.name == caloriesApiField }?.asExternalModel()
            ?: undefinedFoodNutrient,
        coreNutrients = foodNutrients
            .filter { it.name in coreNutrients }
            .map { it.asExternalModel() },
        otherNutrients = foodNutrients
            .filterNot { it.name in coreNutrients && it.name != caloriesApiField }
            .map { it.asExternalModel() })


@kotlinx.serialization.Serializable
data class NetworkFoodNutrient(val name: String, val amount: Float, val unitName: String)

/**
 * Nutrient name taken straight from the FDC API is quite lengthy, so this helps
 * shortening them
 */
private val nutrientNameMapping = mapOf(
    "Total lipid (fat)" to "Fat",
    "Carbohydrate" to "Carb",
    "Energy" to "Calories"
)

private val nutrientToShowFullName =
    listOf("Fatty acids, total saturated", "Fatty acids, total trans")

fun NetworkFoodNutrient.asExternalModel(): FoodNutrient {
    // Nutrient name from API can be lengthy (ex: it may contain both the nutrient name, its
    // abbreviation, and other things, separated by commas)
    var nutrientName =
        if (name in nutrientToShowFullName) name
        else name.substringBefore(",")

    // Certain nutrients need special mapping outside of the above processing
    nutrientName = nutrientNameMapping[nutrientName] ?: nutrientName

    // Nutrients' measurement units taken from API are uppercase by default, which we don't want
    // so this handles that case
    val unitName =
        if (unitName.equals("iu", true)) unitName.uppercase()
        else unitName.lowercase()

    return FoodNutrient(
        nutrientName = nutrientName,
        amount = amount,
        unitName = unitName
    )
}


