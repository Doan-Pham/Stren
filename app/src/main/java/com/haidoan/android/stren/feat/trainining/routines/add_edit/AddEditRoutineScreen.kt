package com.haidoan.android.stren.feat.trainining.routines.add_edit

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import timber.log.Timber

internal const val ADD_EDIT_ROUTINE_SCREEN_ROUTE = "add_edit_routine_screen_route"
internal const val ROUTINE_ID_NAV_ARG = "routine_id_arg"
internal const val IS_ADDING_ROUTINE_NAV_ARG = "is_adding_arg"

@Composable
internal fun AddEditRoutineRoute(
    modifier: Modifier = Modifier,
    exercisesIdsToAdd: List<String>,
    viewModel: AddEditRoutineViewModel = hiltViewModel(),
    onAddExercisesCompleted: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit,
    onNavigateToAddExercise: () -> Unit
) {
    if (exercisesIdsToAdd.isNotEmpty()) {
        viewModel.setExercisesIdsToAdd(exercisesIdsToAdd)
        onAddExercisesCompleted()
    }

    val addEditRoutineAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Routine",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_save,
                description = "Menu Item Save",
                clickHandler = {
                    //TODO: Implement "save" menu item
                }),
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(addEditRoutineAppBarConfiguration)
        isAppBarConfigured = true
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AddEditRoutineScreen(
        modifier = modifier,
        uiState = uiState,
        routineName = viewModel.routineNameTextFieldValue,
        onRoutineNameChange = { viewModel.routineNameTextFieldValue = it },
        onNavigateToAddExercise = onNavigateToAddExercise,
        onUpdateExercise = viewModel::updateExerciseTrainingSet
    )
}

@SuppressLint("NewApi")
@Composable
internal fun AddEditRoutineScreen(
    modifier: Modifier = Modifier,
    uiState: AddEditRoutineUiState,
    routineName: String,
    onRoutineNameChange: (String) -> Unit,
    onNavigateToAddExercise: () -> Unit,
    onUpdateExercise: (exerciseToUpdate: TrainedExercise, oldMetric: TrainingMeasurementMetrics, newMetric: TrainingMeasurementMetrics) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StrenOutlinedTextField(
                text = routineName,
                onTextChange = onRoutineNameChange,
                label = "Routine name",
                isError = routineName.isBlank() || routineName.isEmpty(),
                errorText = "Routine name can't be empty"
            )
            when (uiState) {
                is AddEditRoutineUiState.Loading -> {
                    Timber.d("Loading")
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingAnimation()
                    }
                }
                is AddEditRoutineUiState.EmptyRoutine -> {
                    Timber.d("Empty")
                    EmptyScreen()
                }
                is AddEditRoutineUiState.IsAdding -> {
                    Timber.d("IsAdding")
                    Timber.d("trainedExercises: ${uiState.trainedExercises}")
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
                    ) {
                        items(uiState.trainedExercises) { trainedExercise ->
                            TrainedExerciseRegion(
                                trainedExercise = trainedExercise,
                                onUpdateExercise = onUpdateExercise
                            )
                        }
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
private fun ColumnScope.EmptyScreen() {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Icon edit"
        )
        Text(
            text = "Empty routine",
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
    onUpdateExercise: (exerciseToUpdate: TrainedExercise, oldMetric: TrainingMeasurementMetrics, newMetric: TrainingMeasurementMetrics) -> Unit
) {
    val headerTitles = mutableListOf<String>()
    when (trainedExercise.trainingSets.first()) {
        is TrainingMeasurementMetrics.DistanceAndDuration -> headerTitles.addAll(
            listOf(
                "Kilometers",
                "Hours"
            )
        )
        is TrainingMeasurementMetrics.DurationOnly -> headerTitles.addAll(listOf("Seconds"))

        is TrainingMeasurementMetrics.WeightAndRep -> headerTitles.addAll(listOf("Kg", "Reps"))

    }

    val measurementMetricsTextFields = createTrainingSetTextFields(
        trainedExercise.trainingSets.first(),
        trainedExercise,
        onUpdateExercise
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

            Icon(modifier = Modifier
                .clickable {
                    //TODO
                }
                .size(dimensionResource(id = R.dimen.icon_size_medium)),
                painter = painterResource(id = R.drawable.ic_more_horizontal),
                contentDescription = "Icon more",
                tint = MaterialTheme.colorScheme.primary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp),
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
            )

            trainedExercise.trainingSets.forEachIndexed { index, trainingSet ->
                TrainingSetRow(
                    firstColumnText = (index + 1).toString(),
                    firstColumnWidth = firstColumnWidth,
                    onFirstColumnWidthChange = {
                        if (it > firstColumnWidth) {
                            firstColumnWidth = it
                        }
                    },
                    remainingCells = measurementMetricsTextFields,
                    trainingSet = trainingSet
                )
            }

        }

        StrenOutlinedButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = dimensionResource(id = R.dimen.padding_medium)),
            onClick = { /*TODO*/ },
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
    trainingSet: TrainingMeasurementMetrics, trainedExercise: TrainedExercise,
    onUpdateExercise: (
        exerciseToUpdate: TrainedExercise,
        oldMetric: TrainingMeasurementMetrics,
        newMetric: TrainingMeasurementMetrics
    ) -> Unit
): List<@Composable (Modifier, TrainingMeasurementMetrics) -> Unit> {
    when (trainingSet) {
        is TrainingMeasurementMetrics.DistanceAndDuration -> {
            return listOf(
                { modifierParam, oldMetrics ->
                    TextFieldByNumberType(
                        modifier = modifierParam,
                        numberType = NumberType.DOUBLE,
                        number = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).kilometers,
                        onValueChange = {
                            onUpdateExercise(
                                trainedExercise,
                                oldMetrics,
                                oldMetrics.copy(
                                    kilometers = it.toString().toDouble()
                                )
                            )
                        })
                },
                { modifierParam, oldMetrics ->
                    TextFieldByNumberType(
                        modifier = modifierParam,
                        numberType = NumberType.DOUBLE,
                        number = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).hours,
                        onValueChange = {
                            onUpdateExercise(
                                trainedExercise,
                                oldMetrics,
                                oldMetrics.copy(
                                    hours = it.toString().toDouble()
                                )
                            )
                        })
                })
        }
        is TrainingMeasurementMetrics.DurationOnly -> {
            return listOf { modifierParam, oldMetrics ->
                TextFieldByNumberType(
                    modifier = modifierParam,
                    numberType = NumberType.LONG,
                    number = (oldMetrics as TrainingMeasurementMetrics.DurationOnly).seconds,
                    onValueChange = {
                        onUpdateExercise(
                            trainedExercise,
                            oldMetrics,
                            oldMetrics.copy(
                                seconds = it.toString().toLong()
                            )
                        )
                    })
            }
        }
        is TrainingMeasurementMetrics.WeightAndRep -> {
            return listOf(
                { modifierParam, oldMetrics ->
                    TextFieldByNumberType(
                        modifier = modifierParam,
                        numberType = NumberType.DOUBLE,
                        number = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).weight,
                        onValueChange = {
                            onUpdateExercise(
                                trainedExercise,
                                oldMetrics,
                                oldMetrics.copy(
                                    weight = it.toString().toDouble()
                                )
                            )
                        })
                },
                { modifierParam, oldMetrics ->
                    TextFieldByNumberType(
                        modifier = modifierParam,
                        numberType = NumberType.LONG,
                        number = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).repAmount,
                        onValueChange = {
                            onUpdateExercise(
                                trainedExercise,
                                oldMetrics,
                                oldMetrics.copy(
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
    modifier: Modifier,
    numberType: NumberType,
    number: Any,
    onValueChange: (Any) -> Unit
) {
    when (numberType) {
        NumberType.LONG -> {
            val numberInput = number as Long
            val textFieldValue = if (numberInput == 0L) "" else numberInput.toString()

            SimpleTextField(
                modifier = modifier,
                value = textFieldValue,
                onValueChange = {
                    val newNumberValue =
                        if (it.isEmpty() || it.isBlank()) 0L
                        else it.filter { char -> char.isDigit() }.toLong()
                    onValueChange(newNumberValue)
                })
        }
        NumberType.DOUBLE -> {
            val numberInput = number as Double
            val textFieldValue = if (numberInput == 0.0) "" else numberInput.toString()

            SimpleTextField(
                modifier = modifier,
                value = textFieldValue,
                onValueChange = {
                    val newNumberValue =
                        if (it.isEmpty() || it.isBlank()) 0.0 else it.toDouble()
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
    firstColumnText: String,
    firstColumnWidth: Int,
    onFirstColumnWidthChange: (Int) -> Unit,
    trainingSet: TrainingMeasurementMetrics = TrainingMeasurementMetrics.DurationOnly(-1),
    remainingCells: List<@Composable (Modifier, TrainingMeasurementMetrics) -> Unit>
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
    }
}
