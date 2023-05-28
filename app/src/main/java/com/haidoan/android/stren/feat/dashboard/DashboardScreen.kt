package com.haidoan.android.stren.feat.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import timber.log.Timber
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val trackExercisesBottomSheet = BottomSheetWrapper(
        type = DashBoardBottomSheetType.TRACK_EXERCISE,
        shouldShow = rememberSaveable { mutableStateOf(false) },
        bottomSheetComposable = { onDismiss ->
            ListModalBottomSheet(
                onDismissRequest = onDismiss,
                bottomSheetState = rememberModalBottomSheetState(),
                title = "Track exercise",
                sheetItems = listOf(
                    BottomSheetItem(
                        text = "Bench Press",
                        onClickHandler = {
                        }),
                    BottomSheetItem(
                        text = "Cable Row",
                        onClickHandler = {
                        }),
                    BottomSheetItem(
                        text = "Squat",
                        onClickHandler = {
                        }),
                    BottomSheetItem(
                        text = "Deadlift",
                        onClickHandler = {
                        })
                )
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
                            bottomSheetWrappers.first().shouldShow.value = true
                        })
                )
            )
        )
        isAppBarConfigured = true
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardScreen(
        modifier = modifier,
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onDateOptionClick = viewModel::updateDateRange,
        dataType = uiState,
        bottomSheetWrappers = bottomSheetWrappers,
        onDismissBottomSheet = { bottomSheetToDismiss ->
            Timber.d("bottomSheetToDismiss")
            bottomSheetWrappers
                .find { it.type == bottomSheetToDismiss.type }
                ?.shouldShow?.value = false
        }
    )
}

@Composable
private fun DashboardScreen(
    modifier: Modifier = Modifier,
    chartEntryModelProducer: ChartEntryModelProducer,
    dataType: DashboardViewModel.DataType,
    onDateOptionClick: (startDate: LocalDate, endDate: LocalDate) -> Unit,
    bottomSheetWrappers: List<BottomSheetWrapper>,
    onDismissBottomSheet: (BottomSheetWrapper) -> Unit
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {

        ProgressItem(
            chartEntryModelProducer = chartEntryModelProducer,
            onDateOptionClick = onDateOptionClick,
            dataType = dataType
        )
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
    dataType: DashboardViewModel.DataType,
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
                text = "Calories",
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
            text = "${dataType.startDate.defaultFormat()} - ${dataType.endDate.defaultFormat()}",
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