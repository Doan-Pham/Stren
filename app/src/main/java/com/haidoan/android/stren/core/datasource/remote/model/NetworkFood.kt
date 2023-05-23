package com.haidoan.android.stren.core.datasource.remote.model

import com.haidoan.android.stren.core.model.Food
import com.haidoan.android.stren.core.model.FoodNutrient
import com.haidoan.android.stren.core.model.FoodNutrient.Companion.undefinedFoodNutrient
import com.haidoan.android.stren.core.utils.StringFormatUtils.capitalizeEveryWord
import com.haidoan.android.stren.core.utils.StringFormatUtils.capitalizeFirstChar
import kotlinx.serialization.SerialName


/**
 *  [NetworkFoodNutrient] is an interface so it doesn't hold any Serialization info,
 *  so parameterizing this class with a subtype of NetworkFoodNutrient allows developer
 *  to decide which subtype to use depending on the serialization strategy (Ex: Different
 *  schemas for different use cases)
 */
@kotlinx.serialization.Serializable
data class NetworkFood<T : NetworkFoodNutrient>(
    val fdcId: Long,
    val description: String,
    val foodNutrients: List<T> = listOf(),
    val brandName: String = ""
)

/**
 * These are the names of the core nutrients as per the FDC API Specs, should not
 * be changed unless the API Specs itself changes, or else compatibility issues
 * will happen
 */
private val coreNutrients =
    listOf("Protein", "Total lipid (fat)", "Carbohydrate, by difference")
private const val caloriesApiField = "Energy"

fun <T : NetworkFoodNutrient> NetworkFood<T>.asExternalModel() =
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
            .filter { it.name !in coreNutrients && it.name != caloriesApiField }
            .map { it.asExternalModel() },
        brandName = brandName.capitalizeEveryWord()
    )

/**
 * Food API returns data of different schema depending on use case, but creating
 * new model class will force rewriting a lot of code to use the new class.
 * Instead, extend this interface (which application already depends on from the start). Each child
 * class represents a different schema
 */
sealed interface NetworkFoodNutrient {
    val name: String
    val amount: Float
    val unitName: String

    @kotlinx.serialization.Serializable
    data class DefaultNetworkFoodNutrient(
        @SerialName("name")
        override val name: String,
        @SerialName("amount")
        override val amount: Float,
        @SerialName("unitName")
        override val unitName: String
    ) : NetworkFoodNutrient

    @kotlinx.serialization.Serializable
    data class SearchResultNetworkFoodNutrient(
        @SerialName("nutrientName")
        override val name: String,
        @SerialName("value")
        override val amount: Float,
        @SerialName("unitName")
        override val unitName: String
    ) : NetworkFoodNutrient
}


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


