package com.haidoan.android.stren.feat.nutrition.diary.add_food

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.designsystem.component.NumberTextField
import com.haidoan.android.stren.core.designsystem.component.PieChartWithLegend
import com.haidoan.android.stren.core.model.FoodNutrient.Companion.with
import timber.log.Timber
import kotlin.math.roundToLong


internal const val EDIT_FOOD_ENTRY_SCREEN_ROUTE = "EDIT_FOOD_ENTRY_SCREEN_ROUTE"
internal const val USER_ID_EDIT_FOOD_ENTRY_NAV_ARG = "USER_ID_EDIT_FOOD_ENTRY_NAV_ARG"
internal const val EATING_DAY_ID_EDIT_FOOD_ENTRY_NAV_ARG = "EATING_DAY_ID_EDIT_FOOD_ENTRY_NAV_ARG"
internal const val MEAL_ID_EDIT_FOOD_ENTRY_NAV_ARG = "MEAL_ID_EDIT_FOOD_ENTRY_NAV_ARG"
internal const val MEAL_NAME_EDIT_FOOD_ENTRY_NAV_ARG = "MEAL_NAME_EDIT_FOOD_ENTRY_NAV_ARG"
internal const val FOOD_ID_EDIT_FOOD_ENTRY_NAV_ARG = "FOOD_ID_EDIT_FOOD_ENTRY_NAV_ARG"

@Composable
internal fun EditFoodEntryRoute(
    modifier: Modifier = Modifier,
    viewModel: EditFoodEntryViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured && uiState is EditFoodEntryUiState.LoadComplete) {
        val food = (uiState as EditFoodEntryUiState.LoadComplete).food
        val foodAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
            title = food.name +
                    if (food.brandName.isEmpty()) "" else "(${food.brandName})",
            navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
            actionIcons = listOf(
                IconButtonInfo(
                    drawableResourceId = R.drawable.ic_check_mark,
                    description = "MenuItem Check Mark",
                    clickHandler = {
                        //TODO: Save
                        viewModel.addFoodToMeal()
                        onBackToPreviousScreen()
                    })
            )
        )
        appBarConfigurationChangeHandler(foodAppBarConfiguration)
        isAppBarConfigured = true
    }

    EditFoodEntryScreen(
        modifier = modifier,
        uiState = uiState,
        foodAmountInGram = viewModel.foodAmountInGram,
        onChangeFoodAmount = viewModel::onChangeFoodAmount
    )
}

@Composable
internal fun EditFoodEntryScreen(
    modifier: Modifier,
    uiState: EditFoodEntryUiState,
    foodAmountInGram: Float,
    onChangeFoodAmount: (Float) -> Unit
) {
    when (uiState) {
        is EditFoodEntryUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is EditFoodEntryUiState.LoadComplete -> {
            Timber.d("food: ${uiState.food}")
            val calories = uiState.food.calories.with(foodAmountInGram)
            val coreNutrients = uiState.food.coreNutrients.map { it.with(foodAmountInGram) }
            val otherNutrients = uiState.food.otherNutrients.map { it.with(foodAmountInGram) }

            Column(
                modifier = modifier
                    .verticalScroll(state = rememberScrollState())
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            ) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Amount",
                        style = MaterialTheme.typography.titleMedium
                    )

                    NumberTextField(
                        modifier = Modifier.weight(1f),
                        number = foodAmountInGram,
                        onValueChange = {
                            onChangeFoodAmount(it.toFloat())
                        })
                }
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Serving unit",
                        style = MaterialTheme.typography.titleMedium
                    )
                    //TODO: Remove hardcoded serving unit
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "g",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Right
                    )
                }
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))

                Text(text = "Summary", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))

                PieChartWithLegend(
                    modifier = Modifier.fillMaxWidth(),
                    valuesByLabel = coreNutrients
                        .associate { it.nutrientName to it.amount },
                    middleSubTitle = calories.nutrientName,
                    middleTitle = calories.amount.roundToLong().toString(),
                    valueMeasurementUnit = coreNutrients.first().unitName
                )
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))

                Text(text = "Details", style = MaterialTheme.typography.titleMedium)

                val allNutrients = (listOf(calories) + coreNutrients + otherNutrients)
                allNutrients.map { nutrient ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = nutrient.nutrientName, modifier = Modifier.weight(1f))
                        Text(text = "${nutrient.amount}${nutrient.unitName}")
                    }
                }
            }
        }
    }
}