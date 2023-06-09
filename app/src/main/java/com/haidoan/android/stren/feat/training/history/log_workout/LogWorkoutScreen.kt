package com.haidoan.android.stren.feat.training.history.log_workout

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.app.ui.LocalSnackbarHostState
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import kotlinx.coroutines.launch
import timber.log.Timber

internal const val LOG_WORKOUT_SCREEN_ROUTE = "LOG_WORKOUT_SCREEN_ROUTE"

@Composable
internal fun LogWorkoutRoute(
    modifier: Modifier = Modifier,
    exercisesIdsToAdd: List<String>,
    viewModel: LogWorkoutViewModel = hiltViewModel(),
    onAddExercisesCompleted: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit,
    onNavigateToAddExercise: () -> Unit
) {
    if (exercisesIdsToAdd.isNotEmpty()) {
        viewModel.setExercisesIdsToAdd(exercisesIdsToAdd)
        onAddExercisesCompleted()
    }

    val secondaryUiState by viewModel.secondaryUiState.collectAsStateWithLifecycle()
    if (secondaryUiState.shouldShowBackConfirmDialog) {
        SimpleConfirmationDialog(
            onDismissDialog = { viewModel.updateBackConfirmDialogState(false) },
            title = "Your changes are not saved",
            body = "This action can't be undone. Are you sure don't want to save your changes?"
        ) {
            onBackToPreviousScreen()
        }
    }
    if (secondaryUiState.shouldShowRoutineWarningDialog) {
        SimpleConfirmationDialog(
            onDismissDialog = { viewModel.updateRoutineWarningDialogState(false) },
            title = "Switch to another routine",
            body = "Once you you switch to another routine, the exercises you've added will be lost. Are you sure you want to continue?"
        ) {
            secondaryUiState.onConfirmSwitchRoutine()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onBackClickHandler = {
        if (uiState is LogWorkoutUiState.IsLogging && (uiState as LogWorkoutUiState.IsLogging).trainedExercises.isNotEmpty()) {
            viewModel.updateBackConfirmDialogState(true)
        } else if (uiState is LogWorkoutUiState.EmptyWorkout) {
            onBackToPreviousScreen()
        }
    }
    BackHandler {
        onBackClickHandler()
    }

    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    val logWorkoutAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Workout",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackClickHandler),
        actionIcons = listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_save,
                description = "Menu Item Save",
                clickHandler = {
                    if (uiState is LogWorkoutUiState.EmptyWorkout) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Empty workout",
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        }
                    } else if (uiState is LogWorkoutUiState.IsLogging) {
                        val workoutName = viewModel.workoutNameTextFieldValue
                        Timber.d("Save clicked - workoutName: $workoutName")

                        if (workoutName.isBlank() || workoutName.isEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please input workout name",
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            }
                        } else {
                            viewModel.addEditWorkout()
                            onBackToPreviousScreen()
                        }
                    }
                }
            ),
        )
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(logWorkoutAppBarConfiguration)
        isAppBarConfigured = true
    }

    val routines by viewModel.routines.collectAsStateWithLifecycle()
    LogWorkoutScreen(
        modifier = modifier,
        uiState = uiState,
        workoutName = viewModel.workoutNameTextFieldValue,
        routines = routines,
        selectedRoutineId = secondaryUiState.selectedRoutineId,
        onSelectRoutine = viewModel::selectRoutine,
        onWorkoutNameChange = { viewModel.workoutNameTextFieldValue = it },
        onNavigateToAddExercise = onNavigateToAddExercise,
        onUpdateExercise = viewModel::updateExerciseTrainingSet,
        onAddSetToExercise = viewModel::addEmptyTrainingSet,
        onDeleteExercise = viewModel::deleteExercise,
        onDeleteTrainingSet = viewModel::deleteTrainingSet
    )
}

@SuppressLint("NewApi")
@Composable
internal fun LogWorkoutScreen(
    modifier: Modifier = Modifier,
    uiState: LogWorkoutUiState,
    workoutName: String,
    routines: List<Routine>,
    onSelectRoutine: (routineId: String) -> Unit,
    selectedRoutineId: String,
    onWorkoutNameChange: (String) -> Unit,
    onNavigateToAddExercise: () -> Unit,
    onUpdateExercise: (
        exerciseToUpdate: TrainedExercise,
        oldMetric: TrainingMeasurementMetrics,
        newMetric: TrainingMeasurementMetrics
    ) -> Unit,
    onAddSetToExercise: (TrainedExercise) -> Unit,
    onDeleteExercise: (TrainedExercise) -> Unit,
    onDeleteTrainingSet: (TrainedExercise, TrainingMeasurementMetrics) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        LazyColumn(
            modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            item {
                StrenOutlinedTextField(
                    text = workoutName,
                    onTextChange = onWorkoutNameChange,
                    label = "Workout name",
                    isError = workoutName.isBlank() || workoutName.isEmpty(),
                    errorText = "Workout name can't be empty"
                )

                ExposedDropDownMenuTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldLabel = "Choose routine",
                    selectedText = routines.firstOrNull { it.id == selectedRoutineId }?.name
                        ?: NO_SELECTION_ROUTINE_NAME,
                    menuItemsTextAndClickHandler = routines.associate {
                        it.name to { onSelectRoutine(it.id) }
                    }
                )
            }

            when (uiState) {
                is LogWorkoutUiState.Loading -> {
                    Timber.d("Loading")
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimation()
                        }
                    }
                }
                is LogWorkoutUiState.EmptyWorkout -> {
                    Timber.d("Empty")
                    item {
                        EmptyScreen()
                    }
                }
                is LogWorkoutUiState.IsLogging -> {
//                    Timber.d("IsAdding")
//                    Timber.d("trainedExercises: ${uiState.trainedExercises}")
                    items(uiState.trainedExercises) { trainedExercise ->
                        TrainedExerciseRegion(
                            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)),
                            trainedExercise = trainedExercise,
                            onUpdateExercise = onUpdateExercise,
                            onAddSetButtonClick = onAddSetToExercise,
                            onDeleteExerciseClick = onDeleteExercise,
                            onDeleteTrainingSetClick = onDeleteTrainingSet
                        )
                    }
                }
            }
        }

        StrenFilledButton(
            text = "Add exercise",
            onClickHandler = onNavigateToAddExercise,
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun LazyItemScope.EmptyScreen() {
    Column(
        // This is a workaround since mixing scrollable column and lazycolum is
        // impossible
        modifier = Modifier
            .fillParentMaxHeight(0.5f)
            .fillParentMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit_72), contentDescription = "Icon edit"
        )
        Text(
            text = "Empty workout",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Get started by adding exercises",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TrainedExerciseRegion(
    modifier: Modifier = Modifier,
    trainedExercise: TrainedExercise,
    onUpdateExercise: (
        exerciseToUpdate: TrainedExercise,
        oldMetric: TrainingMeasurementMetrics,
        newMetric: TrainingMeasurementMetrics
    ) -> Unit,
    onAddSetButtonClick: (TrainedExercise) -> Unit,
    onDeleteExerciseClick: (TrainedExercise) -> Unit,
    onDeleteTrainingSetClick: (TrainedExercise, TrainingMeasurementMetrics) -> Unit
) {
    val headerTitles = mutableListOf<String>()
    when (trainedExercise.trainingSets.first()) {
        is TrainingMeasurementMetrics.DistanceAndDuration -> headerTitles.addAll(
            listOf(
                "Kilometers", "Hours"
            )
        )
        is TrainingMeasurementMetrics.DurationOnly -> headerTitles.addAll(listOf("Seconds"))

        is TrainingMeasurementMetrics.WeightAndRep -> headerTitles.addAll(listOf("Kg", "Reps"))

    }

    val measurementMetricsTextFields = createTrainingSetTextFields(
        trainedExercise.trainingSets.first(), trainedExercise, onUpdateExercise
    )
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = trainedExercise.exercise.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
            DropDownMenuScaffold(menuItemsTextAndClickHandler = mapOf("Delete" to {
                onDeleteExerciseClick(trainedExercise)
            })) { onExpandMenu ->
                Icon(modifier = Modifier
                    .clickable {
                        onExpandMenu()
                    }
                    .size(dimensionResource(id = R.dimen.icon_size_medium)),
                    painter = painterResource(id = R.drawable.ic_more_horizontal),
                    contentDescription = "Icon more",
                    tint = MaterialTheme.colorScheme.primary)
            }

        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            var firstColumnWidth by remember { mutableStateOf(0) }
            TrainingSetRow(
                firstColumnText = "Set",
                firstColumnWidth = firstColumnWidth,
                onFirstColumnWidthChange = {
                    if (it > firstColumnWidth) {
                        firstColumnWidth = it
                    }
                },
                remainingCells = headerTitles.map<String, @Composable (Modifier, TrainingMeasurementMetrics) -> Unit> { title ->
                    { modifier, _ ->
                        Text(
                            modifier = modifier,
                            text = title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                onDeleteTrainingSetClick = { }
            )

            trainedExercise.trainingSets.forEachIndexed { index, trainingSet ->
                TrainingSetRow(
                    isFirstRow = index == 0,
                    firstColumnText = (index + 1).toString(),
                    firstColumnWidth = firstColumnWidth,
                    onFirstColumnWidthChange = {
                        if (it > firstColumnWidth) {
                            firstColumnWidth = it
                        }
                    },
                    remainingCells = measurementMetricsTextFields,
                    trainingSet = trainingSet,
                    onDeleteTrainingSetClick = {
                        onDeleteTrainingSetClick(
                            trainedExercise,
                            trainingSet
                        )
                    }
                )
            }

        }

        StrenOutlinedButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = dimensionResource(id = R.dimen.padding_medium)),
            onClick = {
                onAddSetButtonClick(trainedExercise)
            },
            text = "Add set",
            leadingIconResId = R.drawable.ic_add
        )
    }
}

/**
 * A simple method to encapsulate the complex logic for creating TextField for a training set's
 * measurement metrics
 */
private fun createTrainingSetTextFields(
    trainingSet: TrainingMeasurementMetrics,
    trainedExercise: TrainedExercise,
    onUpdateExercise: (
        exerciseToUpdate: TrainedExercise,
        oldMetric: TrainingMeasurementMetrics,
        newMetric: TrainingMeasurementMetrics
    ) -> Unit
): List<@Composable (Modifier, TrainingMeasurementMetrics) -> Unit> {
    when (trainingSet) {
        is TrainingMeasurementMetrics.DistanceAndDuration -> {
            return listOf({ modifierParam, oldMetrics ->
                SimpleNumberTextField(
                    modifier = modifierParam,
                    value = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).kilometers,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise, oldMetrics, oldMetrics.copy(
                                kilometers = it.toString().toDouble()
                            )
                        )
                    })
            }, { modifierParam, oldMetrics ->
                SimpleNumberTextField(
                    modifier = modifierParam,
                    value = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).hours,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise, oldMetrics, oldMetrics.copy(
                                hours = it.toString().toDouble()
                            )
                        )
                    })
            })
        }
        is TrainingMeasurementMetrics.DurationOnly -> {
            return listOf { modifierParam, oldMetrics ->
                SimpleNumberTextField(
                    modifier = modifierParam,
                    value = (oldMetrics as TrainingMeasurementMetrics.DurationOnly).seconds,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise, oldMetrics, oldMetrics.copy(
                                seconds = it.toString().toLong()
                            )
                        )
                    })
            }
        }
        is TrainingMeasurementMetrics.WeightAndRep -> {
            return listOf({ modifierParam, oldMetrics ->
                SimpleNumberTextField(
                    modifier = modifierParam,
                    value = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).weight,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise, oldMetrics, oldMetrics.copy(
                                weight = it.toString().toDouble()
                            )
                        )
                    })
            }, { modifierParam, oldMetrics ->
                SimpleNumberTextField(
                    modifier = modifierParam,
                    value = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).repAmount,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise, oldMetrics, oldMetrics.copy(
                                repAmount = it.toString().toLong()
                            )
                        )
                    })
            })

        }
    }
}

/**
 * Used together with an outer Column to create a table layout, this function can draw a row with the first columns having the same width, while the remaining columns divide the remaining width between
 * themselves
 *
 * @param remainingCells The list of composables that make up the remaining cells in the row. A Modifier parameter will be provided by the function to the "remainingCells" composables to divide
 * the remaining width between them, any composables passed to this function should use this Modifier so
 * the function can properly calculate their width
 *
 * Example usage:
 *
 * ~~~
 *
 *   var firstColumnWidth by remember { mutableStateOf(0) }
 *
 *   TrainingSetRow(
 *      firstColumnText = "Set",
 *      firstColumnWidth = firstColumnWidth,
 *      onFirstColumnWidthChange = {
 *          if (it > firstColumnWidth) {
 *              firstColumnWidth = it
 *          }
 *      },
 *      remainingCells = listOf(
 *          { modifier -> Text(modifier = modifier, text = "test") },
 *          { modifier -> Text(modifier = modifier, text = "test") })
 *      )
 *
 */
@Suppress("SameParameterValue")
@Composable
private fun TrainingSetRow(
    isFirstRow: Boolean = true,
    firstColumnText: String,
    firstColumnWidth: Int,
    onFirstColumnWidthChange: (Int) -> Unit,
    trainingSet: TrainingMeasurementMetrics = TrainingMeasurementMetrics.DurationOnly(-1),
    remainingCells: List<@Composable (Modifier, TrainingMeasurementMetrics) -> Unit>,
    onDeleteTrainingSetClick: () -> Unit
) {

    val widthInDp = with(LocalDensity.current) {
        firstColumnWidth.toDp()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (widthInDp == 0.dp) {
            Text(
                text = firstColumnText,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                    .onGloballyPositioned {
                        onFirstColumnWidthChange(it.size.width)
                    },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = firstColumnText,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                    .width(width = widthInDp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        remainingCells.forEach {
            it(
                Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                trainingSet
            )
        }

        IconButton(modifier = if (isFirstRow) Modifier.alpha(0f) else Modifier.alpha(1f),
            enabled = !isFirstRow,
            onClick = { onDeleteTrainingSetClick() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Delete icon"
            )
        }
    }
}
