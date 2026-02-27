package com.tu.health.ui.screens.insights.nutrition

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
import com.tu.health.data.remote.dto.insights.nutrition.NutritionDetailsDTO
import com.tu.health.ui.components.LegendRow2
import com.tu.health.ui.components.LegendRow4
import com.tu.health.ui.components.MetricTabs
import com.tu.health.ui.components.computeLabelStep
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun NutritionMacrosChartCard(
    data: NutritionDetailsDTO,
    metric: MacroMetric,
    onMetricChange: (MacroMetric) -> Unit,
    showEnergyOverlay: Boolean
) {
    val points = data.points
    val plan = data.plan

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Consumed vs Target", style = MaterialTheme.typography.titleMedium)

            MetricTabs(selected = metric, onSelected = onMetricChange)

            if (points.isEmpty()) {
                Text(
                    "No tracked nutrition for this period yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            val consumedVals = points.map { nutritionMetricValue(metric, it) }
            val targetVal = nutritionTargetValue(metric, plan)

            val energyEnabled = metric == MacroMetric.CALORIES && showEnergyOverlay
            val rmrVals = if (energyEnabled) points.mapNotNull { it.rmr } else emptyList()
            val tdeeVals = if (energyEnabled) points.mapNotNull { it.tdee } else emptyList()

            val dataMax = maxOf(
                consumedVals.maxOrNull() ?: 0.0,
                targetVal ?: 0.0,
                rmrVals.maxOrNull() ?: 0.0,
                tdeeVals.maxOrNull() ?: 0.0
            ).toFloat().coerceAtLeast(1f)

            val niceMax = nutritionNiceMaxY(metric, dataMax)

            val consumedPoints = points.mapIndexed { idx, p ->
                Point(x = idx.toFloat(), y = nutritionMetricValue(metric, p).toFloat())
            }

            val targetPoints = if (targetVal != null) {
                points.mapIndexed { idx, _ -> Point(x = idx.toFloat(), y = targetVal.toFloat()) }
            } else emptyList()

            val rmrPoints = if (energyEnabled) {
                points.mapIndexedNotNull { idx, p ->
                    val v = p.rmr ?: return@mapIndexedNotNull null
                    Point(x = idx.toFloat(), y = v.toFloat())
                }
            } else emptyList()

            val tdeePoints = if (energyEnabled) {
                points.mapIndexedNotNull { idx, p ->
                    val v = p.tdee ?: return@mapIndexedNotNull null
                    Point(x = idx.toFloat(), y = v.toFloat())
                }
            } else emptyList()

            val lastIndex = points.lastIndex
            val scaleLinePoints = listOf(
                Point(x = 0f, y = 0f),
                Point(x = lastIndex.coerceAtLeast(1).toFloat(), y = niceMax)
            )

            val labelStep = computeLabelStep(points.size)

            val xAxisData = AxisData.Builder()
                .steps(lastIndex.coerceAtLeast(1))
                .labelData { i ->
                    val safeIndex = i.coerceIn(0, lastIndex)
                    if (safeIndex % labelStep == 0) points[safeIndex].date.takeLast(5) else ""
                }
                .build()

            val yAxisData = AxisData.Builder()
                .steps(5)
                .labelData { stepIndex ->
                    val v = (niceMax / 5f) * stepIndex
                    nutritionFormatY(metric, v)
                }
                .build()

            val consumedColor = Color(0xFFDFFF75)
            val targetColor = Color(0xFF56A6A4)
            val rmrColor = Color(0xFFD169FF)
            val tdeeColor = Color(0xFF375CDE)

            val consumedLine = Line(
                dataPoints = consumedPoints,
                lineStyle = LineStyle(
                    color = consumedColor,
                    width = 3.4f
                ),
                intersectionPoint = IntersectionPoint(color = consumedColor, radius = 4.dp),
                selectionHighlightPoint = SelectionHighlightPoint(color = consumedColor, radius = 6.dp),
                shadowUnderLine = null,
                selectionHighlightPopUp = null
            )

            val lines = mutableListOf<Line>()
            lines += consumedLine

            if (targetPoints.isNotEmpty()) {
                lines += Line(
                    dataPoints = targetPoints,
                    lineStyle = LineStyle(color = targetColor, width = 2.2f)
                )
            }

            if (rmrPoints.isNotEmpty()) {
                lines += Line(
                    dataPoints = rmrPoints,
                    lineStyle = LineStyle(color = rmrColor, width = 2.2f)
                )
            }

            if (tdeePoints.isNotEmpty()) {
                lines += Line(
                    dataPoints = tdeePoints,
                    lineStyle = LineStyle(color = tdeeColor, width = 2.2f)
                )
            }

            lines += Line(
                dataPoints = scaleLinePoints,
                lineStyle = LineStyle(color = Color.Transparent)
            )

            if (energyEnabled) {
                LegendRow4(
                    aLabel = "Consumed", aColor = consumedColor,
                    bLabel = if (targetVal != null) "Target" else "Target (not set)", bColor = targetColor,
                    cLabel = "RMR", cColor = rmrColor,
                    dLabel = "TDEE", dColor = tdeeColor
                )
            } else {
                LegendRow2(
                    leftLabel = "Consumed",
                    leftColor = consumedColor,
                    rightLabel = if (targetVal != null) "Target" else "Target (not set)",
                    rightColor = targetColor
                )
            }

            var selectedIndex by remember(points.size, metric, showEnergyOverlay) {
                mutableIntStateOf(points.lastIndex.coerceAtLeast(0))
            }
            selectedIndex = selectedIndex.coerceIn(0, points.lastIndex)

            val selected = points[selectedIndex]
            val selectedConsumed = nutritionMetricValue(metric, selected)
            val selectedRmr = if (energyEnabled) selected.rmr else null
            val selectedTdee = if (energyEnabled) selected.tdee else null

            NutritionValuesPanel(
                selectedDate = selected.date,
                metric = metric,
                consumed = selectedConsumed,
                target = targetVal,
                showEnergy = energyEnabled,
                rmr = selectedRmr,
                tdee = selectedTdee,
                consumedColor = consumedColor,
                targetColor = targetColor,
                rmrColor = rmrColor,
                tdeeColor = tdeeColor
            )

            val chartData = LineChartData(
                linePlotData = LinePlotData(lines = lines),
                xAxisData = xAxisData,
                yAxisData = yAxisData
            )

            InteractiveSelectableNutritionChart(
                chartData = chartData,
                selectedIndex = selectedIndex,
                lastIndex = lastIndex.coerceAtLeast(1),
                referenceSeries = consumedPoints,
                onSelectIndex = { idx -> selectedIndex = idx.coerceIn(0, points.lastIndex) }
            )
        }
    }
}

@Composable
private fun InteractiveSelectableNutritionChart(
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
            .height(290.dp)
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
private fun NutritionValuesPanel(
    selectedDate: String,
    metric: MacroMetric,
    consumed: Double,
    target: Double?,
    showEnergy: Boolean,
    rmr: Double?,
    tdee: Double?,
    consumedColor: Color,
    targetColor: Color,
    rmrColor: Color,
    tdeeColor: Color
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
                text = "Selected: $selectedDate",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            NutritionValueRow(
                label = "Consumed",
                valueText = nutritionFormatValue(metric, consumed),
                color = consumedColor,
                emphasized = true
            )

            NutritionValueRow(
                label = if (target != null) "Target" else "Target (not set)",
                valueText = target?.let { nutritionFormatValue(metric, it) } ?: "—",
                color = targetColor
            )

            if (showEnergy) {
                NutritionValueRow(
                    label = "RMR",
                    valueText = rmr?.let { nutritionFormatValue(MacroMetric.CALORIES, it) } ?: "—",
                    color = rmrColor
                )
                NutritionValueRow(
                    label = "TDEE",
                    valueText = tdee?.let { nutritionFormatValue(MacroMetric.CALORIES, it) } ?: "—",
                    color = tdeeColor
                )
            }
        }
    }
}

@Composable
private fun NutritionValueRow(
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
