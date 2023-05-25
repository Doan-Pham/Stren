package com.haidoan.android.stren.feat.nutrition.diary

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.model.Meal
import timber.log.Timber
import java.time.LocalDate


@Composable
internal fun NutritionDiaryRoute(
    modifier: Modifier = Modifier,
    viewModel: NutritionDiaryViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onNavigateToAddWorkoutScreen: (userId: String, selectedDate: LocalDate) -> Unit,
    onNavigateToAddFoodToMeal: (userId: String, eatingDayId: String, mealId: String) -> Unit,
) {
    var shouldShowCalendarDialog by remember {
        mutableStateOf(false)
    }

    val trainingHistoryAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_add,
                description = "MenuItem-Add",
                clickHandler = {
                    val uiStateValue =
                        viewModel.uiState.value as NutritionDiaryUiState.LoadComplete
                    onNavigateToAddWorkoutScreen(uiStateValue.userId, uiStateValue.selectedDate)
                }),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_calendar,
                description = "MenuItem-Calendar",
                clickHandler = {
                    shouldShowCalendarDialog = true
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NutritionDiaryScreen(
        modifier = modifier,
        uiState = uiState,
        onSelectDate = viewModel::selectDate,
        onSelectCurrentDate = viewModel::setCurrentDateToDefault,
        onMoveToNextWeek = viewModel::moveToNextDay,
        onMoveToPreviousWeek = viewModel::moveToPreviousDay,
        shouldShowCalendarDialog = shouldShowCalendarDialog,
        onDismissCalendarDialog = { shouldShowCalendarDialog = false },
        onLogWorkoutButtonClick = {
            val uiStateValue = viewModel.uiState.value as NutritionDiaryUiState.LoadComplete
            onNavigateToAddWorkoutScreen(uiStateValue.userId, uiStateValue.selectedDate)
        },
        onButtonAddFoodClick = onNavigateToAddFoodToMeal
    )
}

@SuppressLint("NewApi")
@Composable
internal fun NutritionDiaryScreen(
    modifier: Modifier = Modifier,
    uiState: NutritionDiaryUiState,
    shouldShowCalendarDialog: Boolean,
    onDismissCalendarDialog: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectCurrentDate: () -> Unit,
    onMoveToPreviousWeek: () -> Unit,
    onMoveToNextWeek: () -> Unit,
    onLogWorkoutButtonClick: () -> Unit,
    onButtonAddFoodClick: (userId: String, eatingDayId: String, mealId: String) -> Unit,
) {
    when (uiState) {
        is NutritionDiaryUiState.Loading -> {
            Timber.d("Loading")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is NutritionDiaryUiState.LoadComplete -> {
            Timber.d("selectedDate: ${uiState.selectedDate}")
            Timber.d("eatingDay: ${uiState.eatingDay}")
            Timber.d("dates with workouts: ${uiState.datesThatHaveWorkouts}")

            val selectedDate = uiState.selectedDate
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            ) {
                item {
                    DateHeader(
                        modifier = Modifier.fillMaxWidth(),
                        headerTitle = "$selectedDate",
                        onHeaderClickHandler = onSelectCurrentDate,
                        onIconPreviousClickHandler = onMoveToPreviousWeek,
                        onIconNextClickHandler = onMoveToNextWeek
                    )
                }

                item {
                    Text(text = "Calories", style = MaterialTheme.typography.titleMedium)
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                    ) {
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Goal",
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                color = Gray60
                            )
                            Text(
                                text = "2000",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        StrenPieChart(
                            modifier = Modifier.weight(1f),
                            values = listOf(1600f),
                            goalValue = 2000f,
                            isShowProgress = true,
                            middleSubTitle = "Remaining",
                            size = PieChartSize.MEDIUM,
                            middleTitle = (2000f - 1600f).toInt().toString(),
                        )

                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Consumed",
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                color = Gray60
                            )
                            Text(
                                text = "400",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(uiState.eatingDay.meals) {
                    MealItem(meal = it, onButtonAddFoodClickHandler = { mealId ->
                        onButtonAddFoodClick(uiState.userId, uiState.eatingDay.id, mealId)
                    })
                }

            }

            if (shouldShowCalendarDialog) {
                CalendarDialog(
                    onDismissDialog = onDismissCalendarDialog,
                    selectedDate = selectedDate,
                    datesThatHaveWorkouts = uiState.datesThatHaveWorkouts,
                    onSelectDate = onSelectDate
                )
            }
        }
    }
}

@Composable
private fun DateHeader(
    modifier: Modifier = Modifier, headerTitle: String,
    onHeaderClickHandler: () -> Unit,
    onIconPreviousClickHandler: () -> Unit,
    onIconNextClickHandler: () -> Unit,

    ) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onIconPreviousClickHandler) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Icon arrow left"
            )
        }
        Text(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .clickable { onHeaderClickHandler() },
            text = headerTitle,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = onIconNextClickHandler) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Icon arrow left"
            )
        }
    }
}

@Composable
private fun MealItem(
    meal: Meal,
    onButtonAddFoodClickHandler: (mealId: String) -> Unit,
    onEditMealClickHandler: (mealId: String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.padding_large))
            .fillMaxWidth()
            .border(width = (1.5).dp, color = Gray90, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
            DropDownMenuScaffold(
                menuItemsTextAndClickHandler = mapOf(
                    "Edit" to { onEditMealClickHandler(meal.id) })
            )
            { onExpandMenu ->
                Icon(
                    modifier = Modifier
                        .clickable {
                            onExpandMenu()
                        }
                        .size(dimensionResource(id = R.dimen.icon_size_medium)),
                    painter = painterResource(id = R.drawable.ic_more_horizontal),
                    contentDescription = "Icon more"
                )
            }
        }

        meal.foods.subList(0, minOf(meal.foods.size, 3))
            .forEach {
                Text(
                    text = it.food.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray60
                )
            }

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        StrenFilledButton(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.padding_extra_large))
                .align(CenterHorizontally),
            textStyle = MaterialTheme.typography.bodyMedium,
            text = "Add food",
            onClickHandler = {
                onButtonAddFoodClickHandler(meal.id)
            })
    }
}
