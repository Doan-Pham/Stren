package com.haidoan.android.stren.feat.nutrition.diary

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.model.FoodNutrient
import com.haidoan.android.stren.core.model.FoodToConsume
import com.haidoan.android.stren.core.model.Meal
import com.haidoan.android.stren.core.utils.DateUtils
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
internal fun NutritionDiaryRoute(
    modifier: Modifier = Modifier,
    viewModel: NutritionDiaryViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onNavigateToAddFoodToMeal: (userId: String, selectedDate: LocalDate, mealId: String, mealName: String) -> Unit,
    onNavigateToEditFoodEntry: (userId: String, selectedDate: LocalDate, mealId: String, mealName: String, foodId: String, foodAmount: Float) -> Unit,

    ) {
    var shouldShowCalendarDialog by remember {
        mutableStateOf(false)
    }

    val trainingHistoryAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons = listOf(
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
        onButtonAddFoodClick = onNavigateToAddFoodToMeal,
        onFoodEntryClick = onNavigateToEditFoodEntry
    )
}

@OptIn(ExperimentalFoundationApi::class)
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
    onFoodEntryClick: (
        userId: String, selectedDate: LocalDate, mealId: String,
        mealName: String, foodId: String, foodAmount: Float
    ) -> Unit,
    onButtonAddFoodClick: (
        userId: String, selectedDate: LocalDate, mealId: String, mealName: String
    ) -> Unit
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
            Timber.d("dates with workouts: ${uiState.datesTracked}")

            val selectedDate = uiState.selectedDate
            val eatingDay = uiState.eatingDay
            val datesTracked = uiState.datesTracked

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            ) {
                item {
                    DateHeader(
                        modifier = Modifier.fillMaxWidth(),
                        headerTitle = if (selectedDate.isEqual(DateUtils.getCurrentDate())) "Today"
                        else selectedDate.format(
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.LONG
                            )
                        ),
                        onHeaderClickHandler = onSelectCurrentDate,
                        onIconPreviousClickHandler = onMoveToPreviousWeek,
                        onIconNextClickHandler = onMoveToNextWeek
                    )
                }

                // Remove hardcoded goal calories
                val goalCalories = 2000f
                val consumedCalories = eatingDay.totalCalories

                item {
                    /**
                     * For some reason, defining the variable's type causes type inference error,
                     * while specifying explicit type for "listOf()" method works fine
                     */
                    @Suppress("RemoveExplicitTypeArguments")
                    val summaryNutrientInfoPages =
                        listOf<Pair<String, @Composable () -> Unit>>(Pair("Calories") {
                            TotalCaloriesRow(
                                goalCalories = goalCalories,
                                consumedCalories = consumedCalories.toFloat()
                            )
                        }, Pair("Macronutrients") {
                            MacronutrientsRow(
                                macronutrientsByGoal = eatingDay.totalMacros()
                                    .associateWith { 500f }
                            )
                        })

                    val pagerState = rememberPagerState(initialPage = 0)
                    Text(
                        text = summaryNutrientInfoPages[pagerState.currentPage].first,
                        style = MaterialTheme.typography.titleMedium
                    )
                    StrenHorizontalPager(pagerState = pagerState,
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        contents = summaryNutrientInfoPages.map { it.second })
                }

                items(eatingDay.meals) { meal ->
                    MealItem(
                        meal = meal,
                        onButtonAddFoodClickHandler = {
                            onButtonAddFoodClick(
                                uiState.userId, uiState.selectedDate, meal.id, meal.name
                            )
                        },
                        onFoodEntryClick = { foodToConsume ->
                            onFoodEntryClick(
                                uiState.userId,
                                uiState.selectedDate,
                                meal.id,
                                meal.name,
                                foodToConsume.food.id,
                                foodToConsume.amountInGram
                            )
                        }
                    )
                }
            }

            if (shouldShowCalendarDialog) {
                CalendarDialog(
                    onDismissDialog = onDismissCalendarDialog,
                    selectedDate = selectedDate,
                    markedDates = datesTracked,
                    onSelectDate = onSelectDate
                )
            }
        }
    }
}

@Composable
private fun DateHeader(
    modifier: Modifier = Modifier,
    headerTitle: String,
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
    onButtonAddFoodClickHandler: () -> Unit,
    onFoodEntryClick: (food: FoodToConsume) -> Unit,
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
                modifier = Modifier.weight(1f),
                text = meal.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = meal.totalCaloriesString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        meal.foods.forEach {
            Row(
                modifier = Modifier
                    .clickable { onFoodEntryClick(it) }
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = dimensionResource(id = R.dimen.padding_small)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = it.food.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = it.totalCaloriesString,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        StrenFilledButton(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.padding_extra_large))
                .align(CenterHorizontally),
            textStyle = MaterialTheme.typography.bodyMedium,
            text = "Add food",
            onClickHandler = onButtonAddFoodClickHandler
        )
    }
}


@Composable
private fun TotalCaloriesRow(goalCalories: Float, consumedCalories: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        val remainingCalories = goalCalories - consumedCalories
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
                text = goalCalories.toInt().toString(),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        StrenPieChart(
            modifier = Modifier.weight(1f),
            values = listOf(consumedCalories),
            goalValue = goalCalories,
            isShowProgress = true,
            middleSubTitle = "Remaining",
            size = PieChartSize.MEDIUM,
            middleTitle = remainingCalories.toInt().toString(),
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
                text = consumedCalories.toInt().toString(),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MacronutrientsRow(macronutrientsByGoal: Map<FoodNutrient, Float>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        var firstColumnWidth by remember { mutableStateOf(0) }

        val widthInDp = with(LocalDensity.current) {
            firstColumnWidth.toDp()
        }
        // TODO: Remove hardcoded macro nutrient goals
        macronutrientsByGoal.forEach { macronutrientsByGoal ->
            val macronutrient = macronutrientsByGoal.key
            val goal = macronutrientsByGoal.value

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Bottom) {
                if (widthInDp == 0.dp)
                    Text(
                        modifier = Modifier
                            .onGloballyPositioned {
                                firstColumnWidth = it.size.width
                            },
                        text = macronutrient.nutrientName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                else {
                    Text(
                        modifier = Modifier.width(width = widthInDp),
                        text = macronutrient.nutrientName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "${macronutrient.amount.toInt()}${macronutrient.unitName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${goal.toInt()}${macronutrient.unitName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray60
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        strokeCap = StrokeCap.Round,
                        progress = macronutrient.amount / goal,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Gray90
                    )
                }
            }
        }
    }
}