package com.tu.health.ui.screens.insights.scores

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
import co.yml.charts.ui.linechart.model.*
import com.tu.health.data.remote.dto.insights.scores.HealthScoresDTO
import com.tu.health.data.remote.dto.insights.scores.ScorePointDTO
import com.tu.health.ui.components.LegendRow5
import com.tu.health.ui.components.computeLabelStep
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun HealthScoresChartCard(data: HealthScoresDTO) {
    val points = remember(data.points) {
        data.points.sortedBy { it.date }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Scores over time", style = MaterialTheme.typography.titleMedium)

            if (points.count() <= 3) {
                Text(
                    "No data available to visualize yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            fun nearestTotalIndex(from: Int): Int {
                if (points.isEmpty()) return 0
                val clamped = from.coerceIn(0, points.lastIndex)
                var left = clamped
                var right = clamped
                while (left >= 0 || right <= points.lastIndex) {
                    if (left >= 0 && points[left].total != null) return left
                    if (right <= points.lastIndex && points[right].total != null) return right
                    left--
                    right++
                }
                return clamped
            }

            var selectedIndex by remember { mutableIntStateOf(0) }

            LaunchedEffect(points.size) {
                val lastTotal = points.indexOfLast { it.total != null }.let { if (it == -1) 0 else it }
                selectedIndex = lastTotal
            }
            selectedIndex = selectedIndex.coerceIn(0, points.lastIndex)

            val selected = points[selectedIndex]

            val totalSeries = remember(points) { points.toSeries { it.total } }
            val activitySeries = remember(points) { points.toSeries { it.activity } }
            val recoverySeries = remember(points) { points.toSeries { it.recovery } }
            val nutritionSeries = remember(points) { points.toSeries { it.nutrition } }
            val bodySeries = remember(points) { points.toSeries { it.bodyComposition } }

            val lastIndex = (points.size - 1).coerceAtLeast(1)

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

            val rawMax = points.maxOfOrNull { p ->
                listOfNotNull(p.total, p.activity, p.recovery, p.nutrition, p.bodyComposition).maxOrNull() ?: 0.0
            } ?: 0.0

            val niceMax = run {
                val m = rawMax.coerceAtLeast(100.0)
                (ceil(m / 10.0) * 10.0).toFloat()
            }

            val helperColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.01f)
            val scaleLine = listOf(Point(0f, 0f), Point(lastIndex.toFloat(), niceMax))

            val yAxisData = AxisData.Builder()
                .steps(5)
                .labelAndAxisLinePadding(16.dp)
                .labelData { "" }
                .build()
            val totalColor = Color(0xFFDFFF75)
            val activityColor = Color(0xFF4EE8DE)
            val recoveryColor = Color(0xFFD169FF)
            val nutritionColor = Color(0xFF375CDE)
            val bodyColor = Color(0xFF104C4D)

            val lines = buildList {
                if (totalSeries.isNotEmpty()) {
                    add(
                        Line(
                            dataPoints = totalSeries,
                            lineStyle = LineStyle(color = totalColor, width = 3.4f),
                            intersectionPoint = IntersectionPoint(color = totalColor, radius = 4.dp),
                            selectionHighlightPoint = SelectionHighlightPoint(color = totalColor, radius = 6.dp),
                            shadowUnderLine = null,
                            selectionHighlightPopUp = null
                        )
                    )
                }

                if (activitySeries.isNotEmpty()) add(Line(activitySeries, LineStyle(color = activityColor, width = 2.2f)))
                if (recoverySeries.isNotEmpty()) add(Line(recoverySeries, LineStyle(color = recoveryColor, width = 2.2f)))
                if (nutritionSeries.isNotEmpty()) add(Line(nutritionSeries, LineStyle(color = nutritionColor, width = 2.2f)))
                if (bodySeries.isNotEmpty()) add(Line(bodySeries, LineStyle(color = bodyColor, width = 2.2f)))

                add(Line(scaleLine, LineStyle(color = helperColor)))
            }

            LegendRow5(
                aLabel = "Total", aColor = totalColor,
                bLabel = "Activity", bColor = activityColor,
                cLabel = "Recovery", cColor = recoveryColor,
                dLabel = "Nutrition", dColor = nutritionColor,
                eLabel = "Body", eColor = bodyColor,
            )

            ScoresValuesPanel(
                selectedDate = selected.date,
                total = selected.total,
                activity = selected.activity,
                recovery = selected.recovery,
                nutrition = selected.nutrition,
                body = selected.bodyComposition,
                totalColor = totalColor,
                activityColor = activityColor,
                recoveryColor = recoveryColor,
                nutritionColor = nutritionColor,
                bodyColor = bodyColor
            )

            val chartData = LineChartData(
                linePlotData = LinePlotData(lines = lines),
                xAxisData = xAxisData,
                yAxisData = yAxisData
            )

            InteractiveSelectableChart(
                chartData = chartData,
                selectedIndex = selectedIndex,
                lastIndex = lastIndex,
                totalSeries = totalSeries,
                onSelectIndex = { rawIndex ->
                    selectedIndex = nearestTotalIndex(rawIndex)
                }
            )
        }
    }
}

@Composable
private fun InteractiveSelectableChart(
    chartData: LineChartData,
    selectedIndex: Int,
    lastIndex: Int,
    totalSeries: List<Point>,
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
            .height(360.dp)
            .onSizeChanged { chartSize = it }
            .clipToBounds()
            .pointerInput(chartSize, lastIndex, startInsetPx, endInsetPx, totalSeries) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    if (lastIndex <= 0 || totalSeries.isEmpty()) return@awaitEachGesture

                    val fullW = chartSize.width.toFloat().coerceAtLeast(1f)

                    val plotW = (fullW - startInsetPx - endInsetPx).coerceAtLeast(1f)

                    val xInPlot = (down.position.x - startInsetPx).coerceIn(0f, plotW)

                    val xValue = (xInPlot / plotW) * lastIndex.toFloat()

                    val nearest = totalSeries.minByOrNull { kotlin.math.abs(it.x - xValue) }!!
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

private fun List<ScorePointDTO>.toSeries(get: (ScorePointDTO) -> Double?): List<Point> {
    return mapIndexedNotNull { idx, p ->
        val v = get(p) ?: return@mapIndexedNotNull null
        Point(x = idx.toFloat(), y = v.toFloat())
    }
}

@Composable
private fun ScoresValuesPanel(
    selectedDate: String?,
    total: Double?,
    activity: Double?,
    recovery: Double?,
    nutrition: Double?,
    body: Double?,
    totalColor: Color,
    activityColor: Color,
    recoveryColor: Color,
    nutritionColor: Color,
    bodyColor: Color
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

            ValueRow("Total", total, totalColor, emphasized = true)
            ValueRow("Activity", activity, activityColor)
            ValueRow("Recovery", recovery, recoveryColor)
            ValueRow("Nutrition", nutrition, nutritionColor)
            ValueRow("Body composition", body, bodyColor)
        }
    }
}

@Composable
private fun ValueRow(
    label: String,
    value: Double?,
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
            text = value?.roundToInt()?.toString() ?: "—",
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
    }
}
