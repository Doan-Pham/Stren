package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Green70
import com.haidoan.android.stren.core.designsystem.theme.Orange60
import com.haidoan.android.stren.core.designsystem.theme.Red60

@Composable
fun StrenPieChart(
    modifier: Modifier = Modifier,
    valuesByLabel: Map<String, Float>,
    valueMeasurementUnit: String,
    middleSubTitle: String,
    middleTitle: String
) {
    val sliceColors = listOf(Red60, Orange60, Green70)
    var sliceColorAlpha = 1f

    val data = valuesByLabel.toList().mapIndexed { index, labelAndValue ->
        if (index % sliceColors.size == 0 && sliceColorAlpha > 0) sliceColorAlpha -= 0.1f
        val currentSliceColor = sliceColors[index % sliceColors.size]
        Triple(
            labelAndValue.first,
            labelAndValue.second,
            currentSliceColor
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            PieChart(
                modifier = modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                pieChartData = PieChartData(
                    data.map {
                        PieChartData.Slice(
                            it.second,
                            it.third
                        )
                    }
                ),
                animation = simpleChartAnimation(),
                sliceDrawer = SimpleSliceDrawer()
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = middleSubTitle,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    color = Gray60
                )
                Text(
                    text = middleTitle,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            data.forEach {
                Row {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(it.third)
                            .size(dimensionResource(id = R.dimen.icon_size_medium))
                    )
                    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(it.first + ":")
                            }
                            append(" " + it.second.toString() + valueMeasurementUnit)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )

                }
            }
        }
    }


}
