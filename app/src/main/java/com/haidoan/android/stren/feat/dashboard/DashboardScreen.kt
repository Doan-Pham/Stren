package com.haidoan.android.stren.feat.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.defaultFormat
import com.haidoan.android.stren.feat.dashboard.model.TodayTrainingProgram
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToLogWorkoutScreen: (routineId: String) -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val exercisesToTrack by viewModel.exercisesToTrack.collectAsStateWithLifecycle()
    val biometricsToTrack by viewModel.biometricsToTrack.collectAsStateWithLifecycle()
    val todayTrainingPrograms by viewModel.todayTrainingPrograms.collectAsStateWithLifecycle()

    viewModel.dataOutputsCentralStateFlow.collectAsStateWithLifecycle()
    val dataOutputsAsState = viewModel.dataOutputsStreams.map { it.collectAsStateWithLifecycle() }

    val trackExercisesBottomSheet = BottomSheetWrapper(
        type = DashBoardBottomSheetType.TRACK_EXERCISE,
        shouldShow = rememberSaveable { mutableStateOf(false) },
        bottomSheetComposable = { onDismiss ->
            ListModalBottomSheet(
                onDismissRequest = onDismiss,
                bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                title = "Track exercise",
                sheetItems =
                exercisesToTrack.map {
                    BottomSheetItem(
                        text = it.exercise.name,
                        onClickHandler = {
                            viewModel.trackExerciseProgress(it.exercise.id)
                            onDismiss()
                        })
                },
            )
        })

    val trackBiometricsBottomSheet = BottomSheetWrapper(
        type = DashBoardBottomSheetType.TRACK_BIOMETRICS,
        shouldShow = rememberSaveable { mutableStateOf(false) },
        bottomSheetComposable = { onDismiss ->
            ListModalBottomSheet(
                onDismissRequest = onDismiss,
                bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                title = "Track biometrics",
                sheetItems =
                biometricsToTrack.map {
                    BottomSheetItem(
                        text = it.biometricsName,
                        onClickHandler = {
                            viewModel.trackBiometrics(it.biometricsId)
                            onDismiss()
                        })
                },
            )
        })

    val categoriesBottomSheet = BottomSheetWrapper(
        type = DashBoardBottomSheetType.CATEGORIES,
        shouldShow = rememberSaveable { mutableStateOf(false) },
        bottomSheetComposable = { onDismiss ->
            ListModalBottomSheet(
                onDismissRequest = onDismiss,
                title = "Track category",
                sheetItems = listOf(
                    BottomSheetItem(
                        imageResId = R.drawable.ic_training,
                        text = "Exercise",
                        onClickHandler = {
                            viewModel.refreshAllTrainedExercises()
                            onDismiss()
                            trackExercisesBottomSheet.shouldShow.value = true
                        }),
                    BottomSheetItem(
                        imageResId = R.drawable.ic_heart,
                        text = "Biometrics",
                        onClickHandler = {
                            viewModel.refreshAllBiometrics()
                            onDismiss()
                            trackBiometricsBottomSheet.shouldShow.value = true
                        })
                )
            )
        })

    val bottomSheetWrappers =
        listOf(categoriesBottomSheet, trackExercisesBottomSheet, trackBiometricsBottomSheet)

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(
            AppBarConfiguration.NavigationAppBar(
                actionIcons = listOf(
                    IconButtonInfo(
                        drawableResourceId = R.drawable.ic_add,
                        description = "MenuItem-Add",
                        clickHandler = {
                            categoriesBottomSheet.shouldShow.value = true
                        })
                )
            )
        )
        isAppBarConfigured = true
    }

    DashboardScreen(
        modifier = modifier,
        todayTrainingPrograms = todayTrainingPrograms,
        chartEntryModelProducers = viewModel.chartEntryModelProducers,
        dataOutputsAsState = dataOutputsAsState,
        onDateOptionClick = viewModel::updateDateRange,
        bottomSheetWrappers = bottomSheetWrappers,
        onDismissBottomSheet = { bottomSheetToDismiss ->
            bottomSheetWrappers
                .find { it.type == bottomSheetToDismiss.type }
                ?.shouldShow?.value = false
        },
        onLogWorkoutWithRoutine = onNavigateToLogWorkoutScreen,
        onRemoveItemClick = viewModel::stopTrackingCategory
    )
}

@Composable
private fun DashboardScreen(
    modifier: Modifier = Modifier,
    todayTrainingPrograms: List<TodayTrainingProgram>,
    chartEntryModelProducers: Map<String, ChartEntryModelProducer>,
    dataOutputsAsState: List<State<DashboardViewModel.DataOutput>>,
    onDateOptionClick: (dataSourceId: String, startDate: LocalDate, endDate: LocalDate) -> Unit,
    bottomSheetWrappers: List<BottomSheetWrapper>,
    onDismissBottomSheet: (BottomSheetWrapper) -> Unit,
    onLogWorkoutWithRoutine: (routineId: String) -> Unit,
    onRemoveItemClick: (dataSourceId: String) -> Unit,
) {
    var shouldShowConfirmDialog by remember { mutableStateOf(false) }
    var dataSourceIdToDelete by remember { mutableStateOf("") }
    var dataSourceTitleToDelete by remember { mutableStateOf("") }
    if (dataOutputsAsState.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingAnimation()
        }
        return
    }

    val progressItemComposable: @Composable (DashboardViewModel.DataOutput) -> Unit =
        { dataOutput ->
            if (chartEntryModelProducers.containsKey(dataOutput.dataSourceId)) {
                ProgressItem(
                    chartEntryModelProducer = chartEntryModelProducers[dataOutput.dataSourceId]!!,
                    onDateOptionClick = { startDate, endDate ->
                        onDateOptionClick(
                            dataOutput.dataSourceId,
                            startDate,
                            endDate
                        )
                    },
                    dataOutput = dataOutput,
                    onRemoveOptionClick = {
                        dataSourceIdToDelete = dataOutput.dataSourceId
                        dataSourceTitleToDelete = dataOutput.title
                        shouldShowConfirmDialog = true
                    }
                )
            }
        }

    TabLayout(
        userScrollEnabled = false,
        tabNamesAndScreenComposables = listOf(
            Pair("Today") {
                TrainingProgramsList(
                    modifier = modifier.padding(
                        PaddingValues(dimensionResource(id = R.dimen.padding_medium))
                    ),
                    todayTrainingPrograms = todayTrainingPrograms,
                    onLogWorkoutWithRoutine = onLogWorkoutWithRoutine
                )
            },
            Pair("Nutrition") {
                ProgressItemList(
                    modifier = modifier,
                    dataOutputsAsState = dataOutputsAsState
                        .filter {
                            it.value is DashboardViewModel.DataOutput.EmptyData ||
                                    it.value is DashboardViewModel.DataOutput.Calories
                        },
                    item = progressItemComposable
                )
            },
            Pair("Training") {
                ProgressItemList(
                    modifier = modifier,
                    dataOutputsAsState = dataOutputsAsState
                        .filter {
                            it.value is DashboardViewModel.DataOutput.EmptyData ||
                                    it.value is DashboardViewModel.DataOutput.Exercise
                        },
                    item = progressItemComposable
                )
            },
            Pair("Biometrics") {
                ProgressItemList(
                    modifier = modifier,
                    dataOutputsAsState = dataOutputsAsState
                        .filter {
                            it.value is DashboardViewModel.DataOutput.EmptyData ||
                                    it.value is DashboardViewModel.DataOutput.Biometrics
                        },
                    item = progressItemComposable
                )
            },
        )
    )

    if (shouldShowConfirmDialog) {
        SimpleConfirmationDialog(
            onDismissDialog = { shouldShowConfirmDialog = false },
            title = "Stop tracking category",
            body = "Are you sure you want to stop tracking this category: $dataSourceTitleToDelete"
        ) {
            onRemoveItemClick(dataSourceIdToDelete)
        }
    }

    bottomSheetWrappers.forEach {
        if (it.shouldShow.value) {
            it.bottomSheetComposable(onDismiss = { onDismissBottomSheet(it) })
        }
    }
}

@Composable
private fun TrainingProgramsList(
    modifier: Modifier = Modifier,
    todayTrainingPrograms: List<TodayTrainingProgram>,
    onLogWorkoutWithRoutine: (routineId: String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        items(todayTrainingPrograms) { trainingProgram ->
            TrainingProgramItem(
                trainingProgram = trainingProgram,
                onLogWorkoutWithRoutine = onLogWorkoutWithRoutine
            )
        }
    }
}

@Composable
private fun TrainingProgramItem(
    trainingProgram: TodayTrainingProgram,
    onLogWorkoutWithRoutine: (routineId: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = (2).dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
//            .clickable { onItemClickHandler(trainingProgram.id) }
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Top,
    ) {

        //region PROGRAM's NAME
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_extra_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${trainingProgram.programName} - Week ${trainingProgram.weekIndex + 1} Day ${trainingProgram.weeklyDayOffset + 1}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.weight(1f))
        }

        //endregion

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        //region ROUTINES

        trainingProgram.routines.take(2).forEach { routine ->
            RoutineItem(routine = routine, onItemClickHandler = onLogWorkoutWithRoutine)
            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
        }

        //endregion
    }
}

@Composable
private fun RoutineItem(
    routine: Routine,
    onItemClickHandler: (routineId: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = (1.5).dp, color = Gray90, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
//            .clickable { onItemClickHandler(routine.id) }
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
                text = routine.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
            Button(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(
                    vertical = dimensionResource(id = R.dimen.padding_small),
                    horizontal = 0.dp
                ),
                onClick = {
                    onItemClickHandler(routine.extraId)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_training),
                    contentDescription = "Icon arrow right",
                    tint = Color.White
                )
            }

        }

        routine.trainedExercises.subList(0, minOf(routine.trainedExercises.size, 3))
            .forEach {
                Text(
                    text = "${it.exercise.name} x ${it.trainingSets.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray60
                )
            }
    }
}

@Composable
private fun ProgressItemList(
    modifier: Modifier = Modifier,
    dataOutputsAsState: List<State<DashboardViewModel.DataOutput>>,
    item: @Composable (dataOutput: DashboardViewModel.DataOutput) -> Unit,
) {
    val progressItemListState = rememberLazyListState()
    var previousDataOutputsSize: Int? by rememberSaveable { mutableStateOf(null) }
    LazyColumn(
        state = progressItemListState,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        items(dataOutputsAsState) { dataOutputAsState ->
            when (val dataOutput = dataOutputAsState.value) {
                is DashboardViewModel.DataOutput.EmptyData -> {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                }

                is DashboardViewModel.DataOutput.Calories,
                is DashboardViewModel.DataOutput.Exercise,
                is DashboardViewModel.DataOutput.Biometrics,
                -> {
                    item(dataOutput)
                }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(dataOutputsAsState.size) {
        Timber.d("dataOutputsAsState.size: ${dataOutputsAsState.size}")
        Timber.d("previousDataOutputsSize: $previousDataOutputsSize")

        if (previousDataOutputsSize != null && dataOutputsAsState.size > previousDataOutputsSize!!) {
            Timber.d("scrolling to : ${dataOutputsAsState.size - 1}")
            coroutineScope.launch {
                progressItemListState.animateScrollToItem(dataOutputsAsState.size - 1)
            }
        }
        previousDataOutputsSize = dataOutputsAsState.size
    }

}

@Composable
private fun ProgressItem(
    chartEntryModelProducer: ChartEntryModelProducer,
    dataOutput: DashboardViewModel.DataOutput,
    onDateOptionClick: (startDate: LocalDate, endDate: LocalDate) -> Unit,
    onRemoveOptionClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = (1.5).dp, color = Gray90, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = dataOutput.title,
                style = MaterialTheme.typography.titleMedium,
            )
            val currentDate = DateUtils.getCurrentDate()
            DropDownMenuScaffold(
                menuItemsTextAndClickHandler = mapOf(
                    "Last Week" to {
                        onDateOptionClick(currentDate.minusWeeks(1), currentDate)
                    },
                    "Last Month" to {
                        onDateOptionClick(currentDate.minusMonths(1), currentDate)
                    },
                    "Last 2 Months" to {
                        onDateOptionClick(currentDate.minusMonths(2), currentDate)
                    },
                    "Last 3 Months" to {
                        onDateOptionClick(currentDate.minusMonths(3), currentDate)
                    },
                    "Last 6 Months" to {
                        onDateOptionClick(currentDate.minusMonths(6), currentDate)
                    },
                    "Last Year" to {
                        onDateOptionClick(currentDate.minusYears(1), currentDate)
                    })
            ) { onExpandMenu ->
                Icon(modifier = Modifier
                    .clickable {
                        onExpandMenu()
                    }
                    .size(dimensionResource(id = R.dimen.icon_size_medium)),
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Icon more")
            }

            if (!dataOutput.isDefaultCategory) {
                DropDownMenuScaffold(
                    menuItemsTextAndClickHandler = mapOf(
                        "Remove" to {
                            onRemoveOptionClick()
                        }
                    )) { onExpandMenu ->
                    Icon(modifier = Modifier
                        .clickable {
                            onExpandMenu()
                        }
                        .size(dimensionResource(id = R.dimen.icon_size_medium)),
                        painter = painterResource(id = R.drawable.ic_more_vertical),
                        contentDescription = "Icon more")
                }
            }
        }
        Text(
            text = "${dataOutput.startDate.defaultFormat()} - ${dataOutput.endDate.defaultFormat()}",
            style = MaterialTheme.typography.bodyMedium
        )
        StrenLineChart(
            chartEntryModelProducer = chartEntryModelProducer,
        )
    }
}


private data class BottomSheetWrapper(
    val type: DashBoardBottomSheetType,
    val shouldShow: MutableState<Boolean>,
    val bottomSheetComposable: @Composable (onDismiss: () -> Unit) -> Unit,
)

/**
 * Each dialog is differentiated by its type, so there can be only 1 dialog of each type at
 * a time
 */
private enum class DashBoardBottomSheetType {
    CATEGORIES, TRACK_EXERCISE, TRACK_BIOMETRICS
}