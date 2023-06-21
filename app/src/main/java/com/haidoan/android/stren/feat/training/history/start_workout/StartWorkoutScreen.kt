package com.haidoan.android.stren.feat.training.history.start_workout

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.LocalActivity
import com.haidoan.android.stren.app.WorkoutInProgressInitArgs
import com.haidoan.android.stren.app.WorkoutInProgressViewModel
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.app.ui.LocalSnackbarHostState
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.*
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import com.haidoan.android.stren.core.utils.ValidationUtils
import kotlinx.coroutines.launch
import timber.log.Timber

internal const val START_WORKOUT_SCREEN_ROUTE = "START_WORKOUT_SCREEN_ROUTE"

@Composable
internal fun StartWorkoutRoute(
    modifier: Modifier = Modifier,
    exercisesIdsToAdd: List<String>,
    viewModel: StartWorkoutViewModel = hiltViewModel(),
    workoutInProgressViewModel: WorkoutInProgressViewModel = hiltViewModel(LocalActivity.current),
    onAddExercisesCompleted: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit,
    onNavigateToAddExercise: () -> Unit
) {
    val trainedExercises by workoutInProgressViewModel.trainedExercises.collectAsStateWithLifecycle()
    val secondaryUiState by viewModel.secondaryUiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val routines by workoutInProgressViewModel.routines.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    workoutInProgressViewModel.setInitArgs(
        WorkoutInProgressInitArgs(
            userId = viewModel.navArgs.userId,
            selectedDate = viewModel.navArgs.selectedDate,
            selectedRoutineId = viewModel.navArgs.selectedRoutineId
        )
    )
    viewModel.setTrainedExercises(trainedExercises)

    if (exercisesIdsToAdd.isNotEmpty()) {
        workoutInProgressViewModel.setExercisesIdsToAdd(exercisesIdsToAdd)
        onAddExercisesCompleted()
    }

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

    val startWorkoutAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Workout",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
        actionIcons = listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_clock,
                description = "Menu Item Clock",
                clickHandler = {
//TODO: Clock Menu Item Click
                }
            ),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_check_mark,
                description = "Menu Item Finish Workout",
                clickHandler = {
                    //TODO: Finish Workout Click
                    if (uiState is StartWorkoutUiState.EmptyWorkout) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Empty workout",
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        }
                    } else if (uiState is StartWorkoutUiState.IsLogging) {
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
                            workoutInProgressViewModel.addWorkout()
                            onBackToPreviousScreen()
                        }
                    }
                }
            ),
        )
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(startWorkoutAppBarConfiguration)
        isAppBarConfigured = true
    }

    StartWorkoutScreen(
        modifier = modifier,
        uiState = uiState,
        workoutName = workoutInProgressViewModel.workoutNameTextFieldValue,
        routines = routines,
        selectedRoutineId = workoutInProgressViewModel.currentSelectedRoutineId,
        onSelectRoutine = {
            viewModel.selectRoutineDropdownOption(selectedOptionRoutineId = it, onSelectRoutine = {
                workoutInProgressViewModel.selectRoutine(it)
            })
        },
        onWorkoutNameChange = { workoutInProgressViewModel.workoutNameTextFieldValue = it },
        onNavigateToAddExercise = onNavigateToAddExercise,
        onUpdateExercise = workoutInProgressViewModel::updateExerciseTrainingSet,
        onAddSetToExercise = workoutInProgressViewModel::addEmptyTrainingSet,
        onDeleteExercise = workoutInProgressViewModel::deleteExercise,
        onDeleteTrainingSet = workoutInProgressViewModel::deleteTrainingSet,
        onChangeCompleteStatusTrainingSetClick = { trainedExercise, trainingSet, isComplete ->
            viewModel.toggleTrainingSetCompleteState(
                trainingSet = trainingSet,
                onToggleTrainingSetCompleteState = {
                    workoutInProgressViewModel.toggleTrainingSetCompleteState(
                        exerciseToUpdate = trainedExercise,
                        trainingSetToUpdate = trainingSet,
                        isTrainingSetComplete = isComplete
                    )
                })
        },
    )
}

@SuppressLint("NewApi")
@Composable
internal fun StartWorkoutScreen(
    modifier: Modifier = Modifier,
    uiState: StartWorkoutUiState,
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
    onDeleteTrainingSet: (TrainedExercise, TrainingMeasurementMetrics) -> Unit,
    onChangeCompleteStatusTrainingSetClick: (TrainedExercise, TrainingMeasurementMetrics, Boolean) -> Unit
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
                is StartWorkoutUiState.Loading -> {
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
                is StartWorkoutUiState.EmptyWorkout -> {
                    Timber.d("Empty")
                    item {
                        EmptyScreen()
                    }
                }
                is StartWorkoutUiState.IsLogging -> {
//                    Timber.d("IsAdding")
//                    Timber.d("trainedExercises: ${uiState.trainedExercises}")
                    items(uiState.trainedExercises) { trainedExercise ->
                        TrainedExerciseRegion(
                            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)),
                            trainedExercise = trainedExercise,
                            onUpdateExercise = onUpdateExercise,
                            onAddSetButtonClick = onAddSetToExercise,
                            onDeleteExerciseClick = onDeleteExercise,
                            onDeleteTrainingSetClick = onDeleteTrainingSet,
                            onChangeCompleteStatusTrainingSetClick = onChangeCompleteStatusTrainingSetClick
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
            painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Icon edit"
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
    onDeleteTrainingSetClick: (TrainedExercise, TrainingMeasurementMetrics) -> Unit,
    onChangeCompleteStatusTrainingSetClick: (TrainedExercise, TrainingMeasurementMetrics, Boolean) -> Unit
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
                isHeader = true,
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
                onDeleteTrainingSetClick = { },
                onChangeCompleteStatusTrainingSetClick = {}
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
                    },
                    onChangeCompleteStatusTrainingSetClick = { isComplete ->
                        onChangeCompleteStatusTrainingSetClick(
                            trainedExercise,
                            trainingSet,
                            isComplete
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
                TextFieldByNumberType(modifier = modifierParam,
                    numberType = NumberType.DOUBLE,
                    number = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).kilometers,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise, oldMetrics, oldMetrics.copy(
                                kilometers = it.toString().toDouble()
                            )
                        )
                    })
            }, { modifierParam, oldMetrics ->
                TextFieldByNumberType(modifier = modifierParam,
                    numberType = NumberType.DOUBLE,
                    number = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).hours,
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
                TextFieldByNumberType(modifier = modifierParam,
                    numberType = NumberType.LONG,
                    number = (oldMetrics as TrainingMeasurementMetrics.DurationOnly).seconds,
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
                TextFieldByNumberType(modifier = modifierParam,
                    numberType = NumberType.DOUBLE,
                    number = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).weight,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise, oldMetrics, oldMetrics.copy(
                                weight = it.toString().toDouble()
                            )
                        )
                    })
            }, { modifierParam, oldMetrics ->
                TextFieldByNumberType(modifier = modifierParam,
                    numberType = NumberType.LONG,
                    number = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).repAmount,
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
 * Encapsulate the bulky logic for creating and sanitizing trainingSet TextField
 */
@Composable
private fun TextFieldByNumberType(
    modifier: Modifier, numberType: NumberType, number: Number, onValueChange: (Number) -> Unit
) {
    when (numberType) {
        NumberType.LONG -> {
            val numberInput = number as Long
            val textFieldValue = if (numberInput == 0L) "" else numberInput.toString()

            SimpleTextField(modifier = modifier, value = textFieldValue, onValueChange = {
                val newTextFieldValue = it.filter { char -> char.isDigit() }
                val newNumberValue =
                    if (newTextFieldValue.isEmpty() || newTextFieldValue.isBlank()) 0L
                    else newTextFieldValue.toLong()
                onValueChange(newNumberValue)
            })
        }
        NumberType.DOUBLE -> {
            /**
             * TextField should only show decimal point in 2 cases:
             * - The value behind decimal point is not 0.0
             * - That decimal point is input from user
             *
             * So "hasDecimalPoint" var solves the 2nd case and "previousTextFieldValue" helps
            when user deletes the decimal point and reset "hasDecimalPoint"
             */
            var hasDecimalPoint by remember { mutableStateOf(false) }
            var previousTextFieldValue by remember { mutableStateOf("") }

            val numberInput = number as Double
            val textFieldValue =
                if (numberInput == 0.0) ""
                else if (numberInput.rem(1) == 0.0) {
                    if (hasDecimalPoint) numberInput.toLong().toString() + "."
                    else numberInput.toLong().toString()
                } else numberInput.toString()

            SimpleTextField(modifier = modifier,
                value = textFieldValue,
                onValueChange = { newTextFieldValue ->

                    // This block solves the decimal point input problem:
                    // - When user adds decimal point, it should be shown,
                    // - When user deletes decimal point, it should be gone and the value become
                    // the digits before decimal point
                    // Without this block, a "40" value will be shown as "40.0" automatically
                    // which is confusing, and a "40.0" value when deleting the decimal point
                    // will become "400" not "40", since the zero behind decimal point is not deleted
                    val decimalSanitizedTextFieldValue: String
                    if (newTextFieldValue.contains('.')) {
                        hasDecimalPoint = true
                        previousTextFieldValue = newTextFieldValue
                        decimalSanitizedTextFieldValue = newTextFieldValue
                    } else {
                        if (hasDecimalPoint) {
                            decimalSanitizedTextFieldValue =
                                previousTextFieldValue.substringBefore('.')
                            hasDecimalPoint = false
                        } else {
                            decimalSanitizedTextFieldValue = newTextFieldValue
                        }
                    }

                    Timber.d("newTextFieldValue: $newTextFieldValue")
                    Timber.d("decimalSanitizedTextFieldValue: $decimalSanitizedTextFieldValue")

                    val sanitizedTextFieldValue =
                        ValidationUtils.validateDouble(decimalSanitizedTextFieldValue)
                    Timber.d("sanitizedTextFieldValue: $sanitizedTextFieldValue")
                    val newNumberValue =
                        if (sanitizedTextFieldValue.isEmpty() || sanitizedTextFieldValue.isBlank()) 0.0 else sanitizedTextFieldValue.toDouble()
                    onValueChange(newNumberValue)
                })
        }
    }
}

private enum class NumberType { LONG, DOUBLE }


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
    isHeader: Boolean = false,
    firstColumnText: String,
    firstColumnWidth: Int,
    onFirstColumnWidthChange: (Int) -> Unit,
    trainingSet: TrainingMeasurementMetrics = TrainingMeasurementMetrics.DurationOnly(-1),
    remainingCells: List<@Composable (Modifier, TrainingMeasurementMetrics) -> Unit>,
    onDeleteTrainingSetClick: () -> Unit,
    onChangeCompleteStatusTrainingSetClick: (Boolean) -> Unit
) {

    val widthInDp = with(LocalDensity.current) {
        firstColumnWidth.toDp()
    }

    Row(
//        modifier = if (trainingSet.isComplete) Modifier.background(Green70)
//        else Modifier.background(Color.White),
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

        var checkButtonModifier =
            if (trainingSet.isComplete)
                Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Green95)
            else
                Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gray90)

        checkButtonModifier =
            if (isHeader) checkButtonModifier
                .background(White)
                .alpha(0f) else checkButtonModifier.alpha(1f)

        // TODO: If user tries to complete an empty set, shake the button or do some signaling
        IconButton(
            modifier = checkButtonModifier,
            enabled = !isHeader,
            onClick = {
                if (trainingSet.isComplete) onChangeCompleteStatusTrainingSetClick(false)
                else onChangeCompleteStatusTrainingSetClick(true)
            }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_mark),
                contentDescription = "Delete icon",
                tint = if (trainingSet.isComplete) Green50 else Gray60
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
