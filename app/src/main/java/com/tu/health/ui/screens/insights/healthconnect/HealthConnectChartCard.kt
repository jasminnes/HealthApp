package com.tu.health.ui.screens.insights.healthconnect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDetailsDTO
import com.tu.health.ui.components.LegendRow
import com.tu.health.ui.components.MetricTabs
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HealthConnectChartCard(
    data: HealthConnectDetailsDTO,
    metric: HealthConnectMetric,
    onMetricChange: (HealthConnectMetric) -> Unit
) {
    val points = data.points

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Daily Snapshot", style = MaterialTheme.typography.titleMedium)

            MetricTabs(
                selected = metric,
                onSelected = onMetricChange
            )

            if (points.isEmpty()) {
                Text(
                    "No Health Connect data for this period yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            val vals = points.map { hcMetricValue(metric, it) }
            val avgVal = hcAvgValue(metric, data)

            val dataMax = maxOf(vals.maxOrNull() ?: 0.0, avgVal).toFloat().coerceAtLeast(1f)
            val niceMax = hcNiceMaxY(metric, dataMax)

            val seriesPoints = points.mapIndexed { idx, p ->
                Point(x = idx.toFloat(), y = hcMetricValue(metric, p).toFloat())
            }

            val avgPoints = points.mapIndexed { idx, _ ->
                Point(x = idx.toFloat(), y = avgVal.toFloat())
            }

            val lastIndex = (points.size - 1).coerceAtLeast(1)
            val scaleLine = listOf(
                Point(x = 0f, y = 0f),
                Point(x = lastIndex.toFloat(), y = niceMax)
            )

            val labelStep = computeLabelStep(points.size)

            val xAxisData = AxisData.Builder()
                .steps(lastIndex)
                .labelAndAxisLinePadding(8.dp)
                .labelData { i ->
                    val safe = i.coerceIn(0, points.lastIndex)
                    if (safe == 0 || safe == points.lastIndex || safe % labelStep == 0) {
                        points[safe].date.takeLast(5)
                    } else ""
                }
                .build()

            val yAxisData = AxisData.Builder()
                .steps(5)
                .labelAndAxisLinePadding(16.dp)
                .labelData { stepIndex ->
                    val v = (niceMax / 5f) * stepIndex
                    hcFormatY(metric, v)
                }
                .build()

            val seriesColor = when (metric) {
                HealthConnectMetric.STEPS -> MaterialTheme.colorScheme.primary
                HealthConnectMetric.SLEEP_MIN -> MaterialTheme.colorScheme.secondaryContainer
                HealthConnectMetric.EXERCISE_MIN -> MaterialTheme.colorScheme.primary
                HealthConnectMetric.ACTIVE_KCAL -> MaterialTheme.colorScheme.secondaryContainer
                HealthConnectMetric.WORKOUTS -> MaterialTheme.colorScheme.primary
            }
            val avgColor = MaterialTheme.colorScheme.tertiary

            var selectedIndex by remember(points.size, metric) {
                mutableIntStateOf(points.lastIndex.coerceAtLeast(0))
            }
            selectedIndex = selectedIndex.coerceIn(0, points.lastIndex)

            val selected = points[selectedIndex]
            val selectedValue = hcMetricValue(metric, selected)

            HealthConnectSelectedPanel(
                selectedDate = selected.date,
                metricTitle = metric.title,
                valueText = hcFormatValue(metric, selectedValue),
                avgText = hcFormatValue(metric, avgVal),
                seriesColor = seriesColor,
                avgColor = avgColor
            )

            val lines = buildList {
                add(
                    Line(
                        dataPoints = seriesPoints,
                        lineStyle = LineStyle(color = seriesColor, width = 3.4f),
                        intersectionPoint = IntersectionPoint(color = seriesColor, radius = 4.dp),
                        selectionHighlightPoint = SelectionHighlightPoint(color = seriesColor, radius = 6.dp),
                        shadowUnderLine = null,
                        selectionHighlightPopUp = null
                    )
                )

                add(
                    Line(
                        dataPoints = avgPoints,
                        lineStyle = LineStyle(color = avgColor, width = 2.2f)
                    )
                )

                add(
                    Line(
                        dataPoints = scaleLine,
                        lineStyle = LineStyle(color = Color.Transparent)
                    )
                )
            }

            LegendRow(
                leftLabel = "Daily",
                leftColor = seriesColor,
                rightLabel = "Avg (${hcFormatValue(metric, avgVal)})",
                rightColor = avgColor
            )

            val chartData = LineChartData(
                linePlotData = LinePlotData(lines = lines),
                xAxisData = xAxisData,
                yAxisData = yAxisData
            )

            InteractiveSelectableHcChart(
                chartData = chartData,
                selectedIndex = selectedIndex,
                lastIndex = lastIndex,
                referenceSeries = seriesPoints,
                onSelectIndex = { idx ->
                    selectedIndex = idx.coerceIn(0, points.lastIndex)
                }
            )

            val latest = points.lastOrNull()
            if (latest != null) {
                val latestVal = hcMetricValue(metric, latest)
                Text(
                    "Latest: ${hcFormatValue(metric, latestVal)} • Avg: ${hcFormatValue(metric, avgVal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InteractiveSelectableHcChart(
    chartData: LineChartData,
    selectedIndex: Int,
    lastIndex: Int,
    referenceSeries: List<Point>,
    onSelectIndex: (Int) -> Unit
) {
    val startInset = 44.dp
    val endInset = 18.dp

    val density = LocalDensity.current
    val startInsetPx = with(density) { startInset.toPx() }
    val endInsetPx = with(density) { endInset.toPx() }

    var chartSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .onSizeChanged { chartSize = it }
            .clipToBounds()
            .pointerInput(chartSize, lastIndex, startInsetPx, endInsetPx, referenceSeries) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    if (lastIndex <= 0 || referenceSeries.isEmpty()) return@awaitEachGesture

                    val fullW = chartSize.width.toFloat().coerceAtLeast(1f)
                    val plotW = (fullW - startInsetPx - endInsetPx).coerceAtLeast(1f)

                    val xInPlot = (down.position.x - startInsetPx).coerceIn(0f, plotW)
                    val xValue = (xInPlot / plotW) * lastIndex.toFloat()

                    val nearest = referenceSeries.minByOrNull { abs(it.x - xValue) } ?: return@awaitEachGesture
                    onSelectIndex(nearest.x.roundToInt().coerceIn(0, lastIndex))
                }
            },
        contentAlignment = Alignment.TopStart
    ) {
        LineChart(
            modifier = Modifier.fillMaxSize().clipToBounds(),
            lineChartData = chartData
        )

        if (lastIndex > 0) {
            val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
            Canvas(modifier = Modifier.fillMaxSize().clipToBounds()) {
                val fullW = size.width.coerceAtLeast(1f)
                val plotW = (fullW - startInsetPx - endInsetPx).coerceAtLeast(1f)

                val xInPlot = (selectedIndex.toFloat() / lastIndex.toFloat()) * plotW
                val x = startInsetPx + xInPlot

                drawLine(
                    color = color,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 2f
                )
            }
        }
    }
}

@Composable
private fun HealthConnectSelectedPanel(
    selectedDate: String?,
    metricTitle: String,
    valueText: String,
    avgText: String,
    seriesColor: Color,
    avgColor: Color
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = selectedDate?.let { "Selected: $it" } ?: "Selected: —",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HcValueRow(
                label = metricTitle,
                valueText = valueText,
                color = seriesColor,
                emphasized = true
            )

            HcValueRow(
                label = "Avg",
                valueText = avgText,
                color = avgColor
            )
        }
    }
}

@Composable
private fun HcValueRow(
    label: String,
    valueText: String,
    color: Color,
    emphasized: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = color,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.size(if (emphasized) 10.dp else 8.dp)
        ) {}
        Spacer(Modifier.width(10.dp))

        Text(
            text = label,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valueText,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
    }
}
