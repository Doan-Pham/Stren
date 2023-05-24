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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.*

@Composable
fun PieChartWithLegend(
    modifier: Modifier = Modifier,
    valuesByLabel: Map<String, Float>,
    valueMeasurementUnit: String,
    middleSubTitle: String,
    middleTitle: String
) {
    val sliceColors = listOf(Red60, Orange60, Green70)
    var sliceColorAlpha = 1f

    val data = valuesByLabel.toList().sortedBy { it.first }.mapIndexed { index, value ->
        if (index % sliceColors.size == 0 && sliceColorAlpha > 0) sliceColorAlpha -= 0.1f
        val currentSliceColor = sliceColors[index % sliceColors.size]
        Triple(
            value.first,
            value.second,
            currentSliceColor
        )
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StrenPieChart(
            modifier = Modifier.weight(1f),
            values = valuesByLabel.values.toList(),
            middleSubTitle = middleSubTitle,
            middleTitle = middleTitle
        )
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

/**
 * @param isShowProgress If this is false, the values provided through [values] are sufficient, and the pie chart shows the ratio between them. If true, that means the pie chart is supposed to show the progress towards
 * a [goalValue] by subtracting the sum of [values] from [goalValue]
 *
 */
@Composable
fun StrenPieChart(
    modifier: Modifier = Modifier,
    values: List<Float>,
    goalValue: Float = values.sum(),
    isShowProgress: Boolean = false,
    middleSubTitle: String,
    middleTitle: String,
    size: PieChartSize = PieChartSize.DEFAULT
) {
    val unreachedGoalSliceColor = Gray90
    val sliceColors = listOf(Red60, Orange60, Green70)
    var sliceColorAlpha = 1f

    val data = values.toList().sortedBy { it }.mapIndexed { index, value ->
        if (index % sliceColors.size == 0 && sliceColorAlpha > 0) sliceColorAlpha -= 0.1f
        val currentSliceColor = sliceColors[index % sliceColors.size]
        Pair(
            value,
            currentSliceColor
        )
    }.toMutableList()

    if (isShowProgress && goalValue > values.sum()) {
        data.add(Pair(goalValue - values.sum(), unreachedGoalSliceColor))
    }

    // Unless you specify a size, pie chart won't be drawn
    Box(
        modifier = modifier.size(size.value)
    ) {
        PieChart(
            modifier = modifier,
            pieChartData = PieChartData(
                data.map {
                    PieChartData.Slice(
                        it.first,
                        it.second
                    )
                }
            ),
            animation = simpleChartAnimation(),
            sliceDrawer = SimpleSliceDrawer()
        )
        Column(
            Modifier
                .fillMaxSize()
                .align(Alignment.Center),
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
}

enum class PieChartSize(val value: Dp) {
    MEDIUM(140.dp),
    DEFAULT(150.dp)
}
