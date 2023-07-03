package com.haidoan.android.stren.feat.training.history.workout_detail

import android.annotation.SuppressLint
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

internal const val WORKOUT_DETAIL_SCREEN_ROUTE = "WORKOUT_DETAIL_SCREEN_ROUTE"

@Composable
internal fun WorkoutDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit,
    onNavigateToEditWorkout: (userId: String, workoutId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val logWorkoutAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Workout",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
        actionIcons = listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_edit_24,
                description = "Menu Item Edit",
                clickHandler = {
                    onNavigateToEditWorkout(viewModel.navArgs.userId, viewModel.navArgs.workoutId)
                }
            ),
        )
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(logWorkoutAppBarConfiguration)
        isAppBarConfigured = true
    }

    WorkoutDetailScreen(
        modifier = modifier,
        uiState = uiState,
    )
}

@SuppressLint("NewApi")
@Composable
internal fun WorkoutDetailScreen(
    modifier: Modifier = Modifier,
    uiState: WorkoutDetailUiState,
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

            when (uiState) {
                is WorkoutDetailUiState.Loading -> {
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

                is WorkoutDetailUiState.IsLogging -> {
//                    Timber.d("IsAdding")
//                    Timber.d("trainedExercises: ${uiState.trainedExercises}")
                    items(uiState.trainedExercises) { trainedExercise ->
                        TrainedExerciseRegion(
                            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)),
                            trainedExercise = trainedExercise
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun TrainedExerciseRegion(
    modifier: Modifier = Modifier,
    trainedExercise: TrainedExercise,
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
        trainedExercise.trainingSets.first()
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
                    trainingSet = trainingSet,
                )
            }
        }

    }
}

private fun createTrainingSetTextFields(
    trainingSet: TrainingMeasurementMetrics,
): List<@Composable (Modifier, TrainingMeasurementMetrics) -> Unit> {
    when (trainingSet) {
        is TrainingMeasurementMetrics.DistanceAndDuration -> {
            return listOf({ modifierParam, oldMetrics ->
                Text(
                    modifier = modifierParam,
                    text = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).kilometers.toString(),
                    textAlign = TextAlign.Center
                )
            }, { modifierParam, oldMetrics ->
                Text(
                    modifier = modifierParam,
                    text = (oldMetrics as TrainingMeasurementMetrics.DistanceAndDuration).hours.toString(),
                    textAlign = TextAlign.Center
                )
            })
        }
        is TrainingMeasurementMetrics.DurationOnly -> {
            return listOf { modifierParam, oldMetrics ->
                Text(
                    modifier = modifierParam,
                    text = (oldMetrics as TrainingMeasurementMetrics.DurationOnly).seconds.toString(),
                    textAlign = TextAlign.Center
                )
            }
        }
        is TrainingMeasurementMetrics.WeightAndRep -> {
            return listOf({ modifierParam, oldMetrics ->
                Text(
                    modifier = modifierParam,
                    text = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).weight.toString(),
                    textAlign = TextAlign.Center
                )
            }, { modifierParam, oldMetrics ->
                Text(
                    modifier = modifierParam,
                    text = (oldMetrics as TrainingMeasurementMetrics.WeightAndRep).repAmount.toString(),
                    textAlign = TextAlign.Center
                )
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
    firstColumnText: String,
    firstColumnWidth: Int,
    onFirstColumnWidthChange: (Int) -> Unit,
    trainingSet: TrainingMeasurementMetrics = TrainingMeasurementMetrics.DurationOnly(-1),
    remainingCells: List<@Composable (Modifier, TrainingMeasurementMetrics) -> Unit>,
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
