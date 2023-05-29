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
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val allTrainedExercises by viewModel.allTrainedExercises.collectAsStateWithLifecycle()
    val trackExercisesBottomSheet = BottomSheetWrapper(
        type = DashBoardBottomSheetType.TRACK_EXERCISE,
        shouldShow = rememberSaveable { mutableStateOf(false) },
        bottomSheetComposable = { onDismiss ->
            ListModalBottomSheet(
                onDismissRequest = onDismiss,
                bottomSheetState = rememberModalBottomSheetState(),
                title = "Track exercise",
                sheetItems =
                allTrainedExercises.map {
                    BottomSheetItem(
                        text = it.exercise.name,
                        onClickHandler = {
                            viewModel.showExerciseProgress(it.exercise.id)
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
                        })
                )
            )
        })

    val bottomSheetWrappers = listOf(categoriesBottomSheet, trackExercisesBottomSheet)

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

    val dataOutputStateFlows = viewModel.dataOutputs
    val dataOutputsAsState = mutableListOf<State<DashboardViewModel.DataOutput>>()
    dataOutputStateFlows.forEach { dataOutputStateFlow ->
        dataOutputsAsState.add(dataOutputStateFlow.collectAsStateWithLifecycle())
    }

    DashboardScreen(
        modifier = modifier,
        chartEntryModelProducers = viewModel.chartEntryModelProducers,
        dataOutputsAsState = dataOutputsAsState,
        onDateOptionClick = viewModel::updateDateRange,
        bottomSheetWrappers = bottomSheetWrappers,
        onDismissBottomSheet = { bottomSheetToDismiss ->
            bottomSheetWrappers
                .find { it.type == bottomSheetToDismiss.type }
                ?.shouldShow?.value = false
        }
    )
}

@Composable
private fun DashboardScreen(
    modifier: Modifier = Modifier,
    chartEntryModelProducers: Map<String, ChartEntryModelProducer>,
    dataOutputsAsState: List<State<DashboardViewModel.DataOutput>>,
    onDateOptionClick: (dataSourceId: String, startDate: LocalDate, endDate: LocalDate) -> Unit,
    bottomSheetWrappers: List<BottomSheetWrapper>,
    onDismissBottomSheet: (BottomSheetWrapper) -> Unit
) {
    val progressItemListState = rememberLazyListState()
    LazyColumn(
        state = progressItemListState,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        items(dataOutputsAsState) { dataOutputAsState ->
            val dataOutput = dataOutputAsState.value
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
                    dataOutput = dataOutput
                )
            }
        }
    }
    var previousDataOutputsSize by rememberSaveable { mutableStateOf(dataOutputsAsState.size) }
    LaunchedEffect(dataOutputsAsState.size) {
        if (dataOutputsAsState.size > previousDataOutputsSize) {
            progressItemListState.animateScrollToItem(dataOutputsAsState.size - 1)
            previousDataOutputsSize = dataOutputsAsState.size
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
    onDateOptionClick: (startDate: LocalDate, endDate: LocalDate) -> Unit
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
                text = dataOutput.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
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

private enum class DashBoardBottomSheetType {
    CATEGORIES, TRACK_EXERCISE
}