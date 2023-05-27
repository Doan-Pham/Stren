package com.haidoan.android.stren.feat.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.StrenLineChart
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
    DashboardScreen(
        modifier = modifier,
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onModifyChart = viewModel::onModifyChartEntries
    )
}

@Composable
internal fun DashboardScreen(
    modifier: Modifier = Modifier,
    chartEntryModelProducer: ChartEntryModelProducer,
    onModifyChart: (List<Pair<LocalDate, Float>>) -> Unit
) {
    Column() {
        StrenLineChart(
            modifier = Modifier.requiredSize(300.dp),
            chartEntryModelProducer = chartEntryModelProducer,
        )
        Button(onClick = {
            onModifyChart(
                listOf(
                    LocalDate.parse("2022-07-14") to 2f,
                    LocalDate.parse("2022-07-15") to 4f,
                    LocalDate.parse("2022-07-17") to 2f,
                    LocalDate.parse("2022-08-01") to 8f
                )
            )
        }) {
            Text(text = "button")
        }
    }

}
