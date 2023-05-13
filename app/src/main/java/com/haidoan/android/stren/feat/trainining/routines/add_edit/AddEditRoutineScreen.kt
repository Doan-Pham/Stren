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
        onNavigateToAddExercise = onNavigateToAddExercise
    )
}

@SuppressLint("NewApi")
@Composable
internal fun AddEditRoutineScreen(
    modifier: Modifier = Modifier,
    uiState: AddEditRoutineUiState,
    routineName: String,
    onRoutineNameChange: (String) -> Unit,
    onNavigateToAddExercise: () -> Unit
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
                            TrainedExerciseRegion(trainedExercise = trainedExercise)
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
    modifier: Modifier = Modifier, trainedExercise: TrainedExercise
) {
    val headerTitles = mutableListOf<String>()
    val measurementMetricsTextFields = mutableListOf<@Composable (Modifier) -> Unit>()

    when (trainedExercise.trainingSets.first()) {
        is TrainingMeasurementMetrics.DistanceAndDuration -> {
            headerTitles.addAll(listOf("Kilometers", "Hours"))
            measurementMetricsTextFields.addAll(
                listOf(
                    { modifierParam ->
                        SimpleTextField(
                            modifier = modifierParam,
                            value = "Kilometers",
                            onValueChange = {})
                    },
                    { modifierParam ->
                        SimpleTextField(
                            modifier = modifierParam,
                            value = "Hours",
                            onValueChange = {})
                    })
            )
        }
        is TrainingMeasurementMetrics.DurationOnly -> {
            headerTitles.addAll(listOf("Seconds"))
            measurementMetricsTextFields.addAll(listOf { modifierParam ->
                SimpleTextField(modifier = modifierParam,
                    value = "Secs",
                    onValueChange = {})
            })
        }
        is TrainingMeasurementMetrics.WeightAndRep -> {
            headerTitles.addAll(listOf("Kg", "Reps"))
            measurementMetricsTextFields.addAll(
                listOf(
                    { modifierParam ->
                        SimpleTextField(
                            modifier = modifierParam,
                            value = "Kg",
                            onValueChange = {})
                    },
                    { modifierParam ->
                        SimpleTextField(
                            modifier = modifierParam,
                            value = "Reps",
                            onValueChange = {})
                    })
            )
        }
    }

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
                remainingCells = headerTitles.map<String, @Composable (Modifier) -> Unit> { title ->
                    { modifier ->
                        Text(
                            modifier = modifier,
                            text = title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
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
                    remainingCells = measurementMetricsTextFields
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
    remainingCells: List<@Composable (Modifier) -> Unit>
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
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}
