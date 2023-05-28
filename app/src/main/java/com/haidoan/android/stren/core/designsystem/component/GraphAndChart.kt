package com.haidoan.android.stren.core.designsystem.component

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.overlayingComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.chart.segment.SegmentProperties
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.marker.Marker
import java.time.LocalDate

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


@Composable
fun StrenLineChart(
    modifier: Modifier = Modifier,
    chartEntryModelProducer: ChartEntryModelProducer
) {
    val axisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
            (chartValues.chartEntryModel.entries.firstOrNull()
                ?.getOrNull(value.toInt()) as? DateChartEntry)
                ?.localDate
                ?.run { "$dayOfMonth/$monthValue" }
                .orEmpty()
        }
    val marker = rememberMarker()
    Chart(
        modifier = modifier,
        marker = marker,
        chart = lineChart(),
        chartModelProducer = chartEntryModelProducer,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(
            guideline = lineComponent(thickness = 0.dp),
            valueFormatter = axisValueFormatter,
            tickPosition = HorizontalAxis.TickPosition.Edge
        ),
    )
}

fun List<Pair<LocalDate, Float>>.toCharEntryModelProducer() = this.mapIndexed { index, (date, y) ->
    DateChartEntry(
        date,
        index.toFloat(),
        y
    )
}.let { ChartEntryModelProducer(it) }

fun List<Pair<LocalDate, Float>>.toCharEntries() = this.mapIndexed { index, (date, y) ->
    DateChartEntry(
        date,
        index.toFloat(),
        y
    )
}

class DateChartEntry(
    val localDate: LocalDate,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = DateChartEntry(localDate, x, y)
}

@Composable
private fun rememberMarker(): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelBackground = remember(labelBackgroundColor) {
        ShapeComponent(labelBackgroundShape, labelBackgroundColor.toArgb()).setShadow(
            radius = LABEL_BACKGROUND_SHADOW_RADIUS,
            dy = LABEL_BACKGROUND_SHADOW_DY,
            applyElevationOverlay = true,
        )
    }
    val label = textComponent(
        background = labelBackground,
        lineCount = LABEL_LINE_COUNT,
        padding = labelPadding,
        typeface = Typeface.MONOSPACE,
    )
    val indicatorInnerComponent =
        shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.surface)
    val indicatorCenterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicatorOuterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicator = overlayingComponent(
        outer = indicatorOuterComponent,
        inner = overlayingComponent(
            outer = indicatorCenterComponent,
            inner = indicatorInnerComponent,
            innerPaddingAll = indicatorInnerAndCenterComponentPaddingValue,
        ),
        innerPaddingAll = indicatorCenterAndOuterComponentPaddingValue,
    )
    val guideline = lineComponent(
        MaterialTheme.colorScheme.onSurface.copy(GUIDELINE_ALPHA),
        guidelineThickness,
        guidelineShape,
    )
    return remember(label, indicator, guideline) {
        object : MarkerComponent(label, indicator, guideline) {
            init {
                indicatorSizeDp = INDICATOR_SIZE_DP
                onApplyEntryColor = { entryColor ->
                    indicatorOuterComponent.color =
                        entryColor.copyColor(INDICATOR_OUTER_COMPONENT_ALPHA)
                    with(indicatorCenterComponent) {
                        color = entryColor
                        setShadow(
                            radius = INDICATOR_CENTER_COMPONENT_SHADOW_RADIUS,
                            color = entryColor
                        )
                    }
                }
            }

            override fun getInsets(
                context: MeasureContext,
                outInsets: Insets,
                segmentProperties: SegmentProperties
            ) =
                with(context) {
                    outInsets.top =
                        label.getHeight(context) + labelBackgroundShape.tickSizeDp.pixels +
                                LABEL_BACKGROUND_SHADOW_RADIUS.pixels * SHADOW_RADIUS_MULTIPLIER -
                                LABEL_BACKGROUND_SHADOW_DY.pixels
                }
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS = 4f
private const val LABEL_BACKGROUND_SHADOW_DY = 2f
private const val LABEL_LINE_COUNT = 1
private const val GUIDELINE_ALPHA = .2f
private const val INDICATOR_SIZE_DP = 36f
private const val INDICATOR_OUTER_COMPONENT_ALPHA = 32
private const val INDICATOR_CENTER_COMPONENT_SHADOW_RADIUS = 12f
private const val GUIDELINE_DASH_LENGTH_DP = 8f
private const val GUIDELINE_GAP_LENGTH_DP = 4f
private const val SHADOW_RADIUS_MULTIPLIER = 1.3f

private val labelBackgroundShape = MarkerCorneredShape(Corner.FullyRounded)
private val labelHorizontalPaddingValue = 8.dp
private val labelVerticalPaddingValue = 4.dp
private val labelPadding = dimensionsOf(labelHorizontalPaddingValue, labelVerticalPaddingValue)
private val indicatorInnerAndCenterComponentPaddingValue = 5.dp
private val indicatorCenterAndOuterComponentPaddingValue = 10.dp
private val guidelineThickness = 2.dp
private val guidelineShape =
    DashedShape(Shapes.pillShape, GUIDELINE_DASH_LENGTH_DP, GUIDELINE_GAP_LENGTH_DP)