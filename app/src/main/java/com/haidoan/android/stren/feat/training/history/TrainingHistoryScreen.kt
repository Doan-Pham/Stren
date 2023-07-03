package com.haidoan.android.stren.feat.training.history

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray50
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.designsystem.theme.Gray95
import com.haidoan.android.stren.core.model.Workout
import com.haidoan.android.stren.core.utils.DateUtils
import timber.log.Timber
import java.time.LocalDate


@Composable
internal fun TrainingHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: TrainingHistoryViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onNavigateToAddWorkoutScreen: (userId: String, selectedDate: LocalDate) -> Unit,
    onNavigateToStartWorkoutScreen: (userId: String, selectedDate: LocalDate) -> Unit,
    onNavigateToEditWorkoutScreen: (userId: String, workoutId: String) -> Unit,
    onNavigateToWorkoutDetailScreen: (userId: String, workoutId: String) -> Unit
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
                        viewModel.uiState.value as TrainingHistoryUiState.LoadComplete
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
    TrainingHistoryScreen(
        modifier = modifier,
        uiState = uiState,
        onSelectDate = viewModel::selectDate,
        onSelectCurrentDate = viewModel::setCurrentDateToDefault,
        onMoveToNextWeek = viewModel::moveToNextWeek,
        onMoveToPreviousWeek = viewModel::moveToPreviousWeek,
        shouldShowCalendarDialog = shouldShowCalendarDialog,
        onDismissCalendarDialog = { shouldShowCalendarDialog = false },
        onLogWorkoutButtonClick = {
            val uiStateValue = viewModel.uiState.value as TrainingHistoryUiState.LoadComplete
            onNavigateToAddWorkoutScreen(uiStateValue.userId, uiStateValue.selectedDate)
        },
        onStartWorkoutButtonClick = {
            val uiStateValue = viewModel.uiState.value as TrainingHistoryUiState.LoadComplete
            onNavigateToStartWorkoutScreen(uiStateValue.userId, uiStateValue.selectedDate)
        },
        onNavigateToEditWorkoutScreen = { workoutId ->
            val uiStateValue = viewModel.uiState.value as TrainingHistoryUiState.LoadComplete
            onNavigateToEditWorkoutScreen(uiStateValue.userId, workoutId)
        },
        onWorkoutItemClick = { workoutId ->
            val uiStateValue = viewModel.uiState.value as TrainingHistoryUiState.LoadComplete
            onNavigateToWorkoutDetailScreen(uiStateValue.userId, workoutId)
        }
    )
}

@SuppressLint("NewApi")
@Composable
internal fun TrainingHistoryScreen(
    modifier: Modifier = Modifier,
    uiState: TrainingHistoryUiState,
    shouldShowCalendarDialog: Boolean,
    onDismissCalendarDialog: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectCurrentDate: () -> Unit,
    onMoveToPreviousWeek: () -> Unit,
    onMoveToNextWeek: () -> Unit,
    onLogWorkoutButtonClick: () -> Unit,
    onStartWorkoutButtonClick: () -> Unit,
    onNavigateToEditWorkoutScreen: (String) -> Unit,
    onWorkoutItemClick: (String) -> Unit,
) {
    when (uiState) {
        is TrainingHistoryUiState.Loading -> {
            Timber.d("Loading")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is TrainingHistoryUiState.LoadComplete -> {
            Timber.d("selectedDate: ${uiState.selectedDate}")
            Timber.d("workouts: ${uiState.workouts}")
            Timber.d("dates with workouts: ${uiState.datesThatHaveWorkouts}")

            val currentMonth = uiState.selectedDate.month.name.toLowerCase(Locale.current)
                .capitalize(Locale.current)
            val currentYear = uiState.selectedDate.year
            val selectedDate = uiState.selectedDate

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            ) {
                MonthYearHeader(
                    modifier = Modifier.fillMaxWidth(),
                    headerTitle = "$currentMonth $currentYear",
                    onHeaderClickHandler = onSelectCurrentDate,
                    onIconPreviousClickHandler = onMoveToPreviousWeek,
                    onIconNextClickHandler = onMoveToNextWeek
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DateUtils.getAllWeekDays(selectedDate).forEach { dateInWeek ->
                        DateItem(
                            date = dateInWeek,
                            isSelected = dateInWeek.isEqual(selectedDate),
                            isDateNotInCurrentMonth = dateInWeek.monthValue != selectedDate.monthValue,
                            onClickHandler = onSelectDate
                        )
                    }
                }

                if (uiState.workouts.isNotEmpty()) {
                    WorkoutList(
                        onNavigateToEditWorkoutScreen = onNavigateToEditWorkoutScreen,
                        onWorkoutItemClick = onWorkoutItemClick,
                        workouts = uiState.workouts
                    )
                } else {
                    EmptyScreen(
                        onLogWorkoutButtonClick = onLogWorkoutButtonClick,
                        selectedDate = uiState.selectedDate,
                        onStartWorkoutButtonClick = onStartWorkoutButtonClick
                    )
                }
            }

            if (shouldShowCalendarDialog) {
                CalendarDialog(
                    onDismissDialog = onDismissCalendarDialog,
                    selectedDate = selectedDate,
                    markedDates = uiState.datesThatHaveWorkouts,
                    onSelectDate = onSelectDate
                )
            }
        }
    }
}

@Composable
private fun MonthYearHeader(
    modifier: Modifier = Modifier, headerTitle: String,
    onHeaderClickHandler: () -> Unit,
    onIconPreviousClickHandler: () -> Unit,
    onIconNextClickHandler: () -> Unit
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
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    isDateNotInCurrentMonth: Boolean,
    onClickHandler: (LocalDate) -> Unit
) {
    var backgroundColorStart = Gray90
    var backgroundColorEnd = Gray90
    var textColor = Gray50

    if (isSelected) {
        backgroundColorStart = MaterialTheme.colorScheme.primary
        backgroundColorEnd = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        textColor = Color.White
    } else if (isDateNotInCurrentMonth) {
        backgroundColorStart = Gray95
        backgroundColorEnd = Gray95
        textColor = Gray50
    }
    Column(
        modifier = Modifier
            .clickable { onClickHandler(date) }
            .width(dimensionResource(id = R.dimen.icon_size_extra_large))
            .clip(RoundedCornerShape(5.dp))
            .background(
                brush = Brush.linearGradient(
                    0f to backgroundColorStart,
                    1f to backgroundColorEnd
                )
            )
            .padding(
                vertical = dimensionResource(id = R.dimen.padding_small),
            ),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.name.first().toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = textColor
        )
    }
}

@Composable
private fun WorkoutList(
    onNavigateToEditWorkoutScreen: (String) -> Unit,
    onWorkoutItemClick: (String) -> Unit,
    workouts: List<Workout>
) {
    workouts.forEach { workout ->
        WorkoutItem(
            workout = workout,
            onItemClickHandler = { onWorkoutItemClick(workout.id) },
            onEditWorkoutClickHandler = onNavigateToEditWorkoutScreen
        )
    }
}

@Composable
private fun WorkoutItem(
    workout: Workout,
    onItemClickHandler: () -> Unit,
    onEditWorkoutClickHandler: (workoutId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onItemClickHandler() }
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
                text = workout.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
            DropDownMenuScaffold(
                menuItemsTextAndClickHandler = mapOf(
                    "Edit" to { onEditWorkoutClickHandler(workout.id) })
            ) { onExpandMenu ->
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

        workout.trainedExercises.subList(0, minOf(workout.trainedExercises.size, 3))
            .forEach {
                Text(
                    text = it.exercise.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray60
                )
            }
    }
}

@Composable
private fun EmptyScreen(
    selectedDate: LocalDate,
    onStartWorkoutButtonClick: () -> Unit,
    onLogWorkoutButtonClick: () -> Unit,
    onCopyPreviousButtonClick: () -> Unit = {
        //TODO
    }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_72),
                contentDescription = "Icon edit"
            )
            Text(
                text = "Empty History",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Get started by logging workouts",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            if (selectedDate.isEqual(DateUtils.getCurrentDate())) {
                StrenFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Start workout",
                    onClickHandler = onStartWorkoutButtonClick,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                StrenTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Log workout",
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onClickHandler = onLogWorkoutButtonClick,
                )
            } else {
                StrenFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Log workout",
                    onClickHandler = onLogWorkoutButtonClick,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
//            StrenTextButton(
//                modifier = Modifier.fillMaxWidth(), text = "Copy previous workout",
//                textStyle = MaterialTheme.typography.bodyMedium
//            ) {
//                /*TODO*/
//            }
        }
    }
}