package com.haidoan.android.stren.feat.nutrition.diary.add_food

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.designsystem.component.PieChartWithLegend
import timber.log.Timber
import kotlin.math.roundToLong


internal const val EDIT_FOOD_ENTRY_SCREEN_ROUTE = "EDIT_FOOD_ENTRY_SCREEN_ROUTE"
internal const val USER_ID_EDIT_FOOD_ENTRY_NAV_ARG = "USER_ID_EDIT_FOOD_ENTRY_NAV_ARG"
internal const val EATING_DAY_ID_EDIT_FOOD_ENTRY_NAV_ARG = "EATING_DAY_ID_EDIT_FOOD_ENTRY_NAV_ARG"
internal const val MEAL_ID_EDIT_FOOD_ENTRY_NAV_ARG = "MEAL_ID_EDIT_FOOD_ENTRY_NAV_ARG"
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
            navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen)
        )
        appBarConfigurationChangeHandler(foodAppBarConfiguration)
        isAppBarConfigured = true
    }

    EditFoodEntryScreen(
        modifier = modifier,
        uiState = uiState
    )
}

@Composable
internal fun EditFoodEntryScreen(
    modifier: Modifier,
    uiState: EditFoodEntryUiState
) {
    when (uiState) {
        is EditFoodEntryUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is EditFoodEntryUiState.LoadComplete -> {
            Timber.d("food: ${uiState.food}")
            val currentFood = uiState.food
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Text(text = "Summary", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))

                PieChartWithLegend(
                    modifier = Modifier.fillMaxWidth(),
                    valuesByLabel = currentFood.coreNutrients
                        .associate { it.nutrientName to it.amount },
                    middleSubTitle = currentFood.calories.nutrientName,
                    middleTitle = currentFood.calories.amount.roundToLong().toString(),
                    valueMeasurementUnit = currentFood.coreNutrients.first().unitName
                )

                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))

                Text(text = "Details", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(
                        listOf(currentFood.calories) +
                                currentFood.coreNutrients +
                                currentFood.otherNutrients
                    ) { nutrient ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = nutrient.nutrientName, modifier = Modifier.weight(1f))
                            Text(text = "${nutrient.amount}${nutrient.unitName}")
                        }
                    }
                }
            }
        }
    }
}