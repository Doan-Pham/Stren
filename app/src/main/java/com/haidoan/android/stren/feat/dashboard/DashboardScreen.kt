package com.haidoan.android.stren.feat.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.haidoan.android.stren.core.designsystem.component.DropDownMenuScaffold
import com.haidoan.android.stren.core.designsystem.component.StrenLineChart
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.defaultFormat
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.time.LocalDate

@Composable
internal fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
        isAppBarConfigured = true
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardScreen(
        modifier = modifier,
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onDateOptionClick = viewModel::updateDateRange,
        dataType = uiState
    )
}

@Composable
internal fun DashboardScreen(
    modifier: Modifier = Modifier,
    chartEntryModelProducer: ChartEntryModelProducer,
    dataType: DashboardViewModel.DataType,
    onDateOptionClick: (startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {

        ProgressItem(
            chartEntryModelProducer = chartEntryModelProducer,
            onDateOptionClick = onDateOptionClick,
            dataType = dataType
        )
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
            DropDownMenuScaffold(menuItemsTextAndClickHandler = mapOf("Last Week" to {
                // TODO
                onDateOptionClick(currentDate.minusWeeks(1), currentDate)
            }, "Last Month" to {
                // TODO
                onDateOptionClick(currentDate.minusMonths(1), currentDate)
            }, "Last 2 Months" to {
                // TODO
                onDateOptionClick(currentDate.minusMonths(2), currentDate)
            }, "Last 3 Months" to {
                // TODO
                onDateOptionClick(currentDate.minusMonths(3), currentDate)
            }, "Last 6 Months" to {
                // TODO
                onDateOptionClick(currentDate.minusMonths(6), currentDate)
            }, "Last Year" to {
                // TODO
                onDateOptionClick(currentDate.minusYears(1), currentDate)
            })) { onExpandMenu ->
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
