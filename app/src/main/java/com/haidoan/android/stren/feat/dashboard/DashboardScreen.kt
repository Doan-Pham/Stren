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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.defaultFormat
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val exercisesToTrack by viewModel.exercisesToTrack.collectAsStateWithLifecycle()
    val biometricsToTrack by viewModel.biometricsToTrack.collectAsStateWithLifecycle()

    val trackExercisesBottomSheet = BottomSheetWrapper(
        type = DashBoardBottomSheetType.TRACK_EXERCISE,
        shouldShow = rememberSaveable { mutableStateOf(false) },
        bottomSheetComposable = { onDismiss ->
            ListModalBottomSheet(
                onDismissRequest = onDismiss,
                bottomSheetState = rememberModalBottomSheetState(),
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
                bottomSheetState = rememberModalBottomSheetState(),
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
                bottomSheetState = rememberModalBottomSheetState(),
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

    val dataOutputStateFlows by viewModel.dataOutputs.collectAsStateWithLifecycle()
    val dataOutputsAsState = mutableListOf<State<DashboardViewModel.DataOutput>>()
    dataOutputStateFlows.forEach { dataOutputStateFlow ->
        dataOutputsAsState.add(dataOutputStateFlow.collectAsStateWithLifecycle())
    }

    DashboardScreen(
        modifier = modifier,
        isUpdating = viewModel.isUpdating,
        onUpdateComplete = { viewModel.isUpdating = false },
        chartEntryModelProducers = viewModel.chartEntryModelProducers,
        dataOutputsAsState = dataOutputsAsState,
        onDateOptionClick = viewModel::updateDateRange,
        bottomSheetWrappers = bottomSheetWrappers,
        onDismissBottomSheet = { bottomSheetToDismiss ->
            bottomSheetWrappers
                .find { it.type == bottomSheetToDismiss.type }
                ?.shouldShow?.value = false
        },
        onRemoveItemClick = viewModel::stopTrackingCategory
    )
}

@Composable
private fun DashboardScreen(
    modifier: Modifier = Modifier,
    isUpdating: Boolean,
    onUpdateComplete: () -> Unit,
    chartEntryModelProducers: Map<String, ChartEntryModelProducer>,
    dataOutputsAsState: List<State<DashboardViewModel.DataOutput>>,
    onDateOptionClick: (dataSourceId: String, startDate: LocalDate, endDate: LocalDate) -> Unit,
    bottomSheetWrappers: List<BottomSheetWrapper>,
    onDismissBottomSheet: (BottomSheetWrapper) -> Unit,
    onRemoveItemClick: (dataSourceId: String) -> Unit
) {
    var shouldShowConfirmDialog by remember { mutableStateOf(false) }
    var dataSourceIdToDelete by remember { mutableStateOf("") }
    var dataSourceTitleToDelete by remember { mutableStateOf("") }

    val progressItemListState = rememberLazyListState()
    var previousFirstVisibleItemIndex by
    rememberSaveable { mutableStateOf(progressItemListState.firstVisibleItemIndex) }
    var previousDataOutputsSize by rememberSaveable { mutableStateOf(dataOutputsAsState.size) }

    if (dataOutputsAsState.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingAnimation()
        }
        return
    }
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
                            .height(dimensionResource(id = R.dimen.icon_size_large)),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                }
                is DashboardViewModel.DataOutput.Calories,
                is DashboardViewModel.DataOutput.Exercise,
                is DashboardViewModel.DataOutput.Biometrics -> {
                    if (chartEntryModelProducers.containsKey(dataOutput.dataSourceId)) {
                        ProgressItem(
                            chartEntryModelProducer = chartEntryModelProducers[dataOutput.dataSourceId]!!,
                            onDateOptionClick = { startDate, endDate ->

                                val itemToUpdateIndex = dataOutputsAsState
                                    .indexOfFirst { it.value.dataSourceId == dataOutput.dataSourceId }

                                previousFirstVisibleItemIndex = itemToUpdateIndex


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
            }
        }
    }

    if (shouldShowConfirmDialog) {
        SimpleConfirmationDialog(
            onDismissDialog = { shouldShowConfirmDialog = false },
            title = "Stop tracking category",
            body = "Are you sure you want to stop tracking this category: $dataSourceTitleToDelete"
        ) {
            val itemToRemoveIndex = dataOutputsAsState
                .indexOfFirst { it.value.dataSourceId == dataSourceIdToDelete }
            previousFirstVisibleItemIndex = itemToRemoveIndex - 1
            onRemoveItemClick(dataSourceIdToDelete)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(dataOutputsAsState.size) {
        if (dataOutputsAsState.size > previousDataOutputsSize) {
            coroutineScope.launch {
                previousFirstVisibleItemIndex = dataOutputsAsState.size - 1
                previousDataOutputsSize = dataOutputsAsState.size
            }
        }
    }
    LaunchedEffect(isUpdating) {
        Timber.d("LaunchedEffect(isUpdating) - isUpdating: $isUpdating")
        if (isUpdating) {
            coroutineScope.launch {
                // Delay a bit to wait for the list to be completely updated before
                // scrolling, or else the scrolling will happen on an incomplete list
                // TODO: This is very brittle, and more like a workaround
                delay(1000)
                progressItemListState.animateScrollToItem(previousFirstVisibleItemIndex)
            }
            onUpdateComplete()
        }

    }

    bottomSheetWrappers.forEach {
        if (it.shouldShow.value) {
            it.bottomSheetComposable(onDismiss = { onDismissBottomSheet(it) })
        }
    }
}

@Composable
private fun ProgressItem(
    chartEntryModelProducer: ChartEntryModelProducer,
    dataOutput: DashboardViewModel.DataOutput,
    onDateOptionClick: (startDate: LocalDate, endDate: LocalDate) -> Unit,
    onRemoveOptionClick: () -> Unit
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
    val bottomSheetComposable: @Composable (onDismiss: () -> Unit) -> Unit
)

/**
 * Each dialog is differentiated by its type, so there can be only 1 dialog of each type at
 * a time
 */
private enum class DashBoardBottomSheetType {
    CATEGORIES, TRACK_EXERCISE, TRACK_BIOMETRICS
}