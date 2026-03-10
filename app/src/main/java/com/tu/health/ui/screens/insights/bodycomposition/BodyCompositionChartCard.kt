package com.tu.health.ui.screens.insights.bodycomposition

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
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDetailsDTO
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionPointDTO
import com.tu.health.ui.components.LegendRow2
import com.tu.health.ui.components.LegendRow3
import com.tu.health.ui.components.MetricTabs
import com.tu.health.ui.components.computeLabelStep
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun BodyCompositionChartCard(
    data: BodyCompositionDetailsDTO,
    metric: BodyMetric,
    onMetricChange: (BodyMetric) -> Unit
) {
    val points = data.points

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Trend", style = MaterialTheme.typography.titleMedium)

            MetricTabs(selected = metric, onSelected = onMetricChange)

            if (points.count() <= 3) {
                Text(
                    "No data available to visualize yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            when (metric) {
                BodyMetric.COMBINED -> CombinedChart(points = points, data = data)
                BodyMetric.WAIST -> WaistChart(points = points, data = data)
            }
        }
    }
}

@Composable
private fun CombinedChart(
    points: List<BodyCompositionPointDTO>,
    data: BodyCompositionDetailsDTO
) {
    val weightUnit = bodyUnitWeight(data)
    val lbmUnit = bodyUnitLbm(data)

    val lastWeightIndex = points.indexOfLast { it.weight != null }
    if (lastWeightIndex == -1) {
        Text(
            "No weight data in this period.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    val trimmed = points.take(lastWeightIndex + 1)

    val weightSeries = trimmed.mapIndexedNotNull { idx, p ->
        val v = p.weight ?: return@mapIndexedNotNull null
        Point(idx.toFloat(), v.toFloat())
    }
    val lbmSeries = trimmed.mapIndexedNotNull { idx, p ->
        val v = p.lbm ?: return@mapIndexedNotNull null
        Point(idx.toFloat(), v.toFloat())
    }
    val fatMassSeries = trimmed.mapIndexedNotNull { idx, p ->
        val w = p.weight ?: return@mapIndexedNotNull null
        val lbm = p.lbm ?: return@mapIndexedNotNull null
        val fatKg = (w - lbm).coerceAtLeast(0.0)
        Point(idx.toFloat(), fatKg.toFloat())
    }

    if (weightSeries.isEmpty() && lbmSeries.isEmpty() && fatMassSeries.isEmpty()) {
        Text(
            "No weight, LBM, or fat-mass points in this period.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val maxKgRaw = maxOf(
        trimmed.mapNotNull { it.weight }.maxOrNull() ?: 0.0,
        trimmed.mapNotNull { it.lbm }.maxOrNull() ?: 0.0,
        trimmed.mapNotNull { p ->
            val w = p.weight
            val lbm = p.lbm
            if (w != null && lbm != null) (w - lbm).coerceAtLeast(0.0) else null
        }.maxOrNull() ?: 0.0
    ).toFloat().coerceAtLeast(1f)

    val niceMaxKg = bodyNiceMaxKg(maxKgRaw)

    val lastIndex = (trimmed.size - 1).coerceAtLeast(1)
    val scaleLine = listOf(Point(0f, 0f), Point(lastIndex.toFloat(), niceMaxKg))

    val labelStep = computeLabelStep(trimmed.size)

    val xAxisData = AxisData.Builder()
        .steps(lastIndex)
        .labelData { i ->
            val safe = i.coerceIn(0, trimmed.lastIndex)
            if (safe % labelStep == 0) trimmed[safe].date.takeLast(5) else ""
        }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { stepIndex ->
            val v = (niceMaxKg / 5f) * stepIndex
            bodyFormatKgAxis(v, weightUnit)
        }
        .build()

    val weightColor = MaterialTheme.colorScheme.primary
    val lbmColor = MaterialTheme.colorScheme.tertiary
    val fatColor = Color(0xFFFFD400)
    val bfpInfoColor = MaterialTheme.colorScheme.secondary

    val lines = mutableListOf<Line>()

    if (weightSeries.isNotEmpty()) {
        lines += Line(
            dataPoints = weightSeries,
            lineStyle = LineStyle(color = weightColor, width = 3.4f),
            intersectionPoint = IntersectionPoint(color = weightColor, radius = 4.dp),
            selectionHighlightPoint = SelectionHighlightPoint(color = weightColor, radius = 6.dp),
            shadowUnderLine = null,
            selectionHighlightPopUp = null
        )
    }
    if (lbmSeries.isNotEmpty()) {
        lines += Line(
            dataPoints = lbmSeries,
            lineStyle = LineStyle(color = lbmColor, width = 2.2f)
        )
    }
    if (fatMassSeries.isNotEmpty()) {
        lines += Line(
            dataPoints = fatMassSeries,
            lineStyle = LineStyle(color = fatColor, width = 2.2f)
        )
    }
    lines += Line(scaleLine, LineStyle(color = Color.Transparent))

    LegendRow3(
        aLabel = "Weight ($weightUnit)", aColor = weightColor,
        bLabel = "LBM ($lbmUnit)", bColor = lbmColor,
        cLabel = "Fat mass ($weightUnit)", cColor = fatColor
    )

    var selectedIndex by remember(trimmed.size) {
        mutableIntStateOf(trimmed.lastIndex.coerceAtLeast(0))
    }
    selectedIndex = selectedIndex.coerceIn(0, trimmed.lastIndex)

    val selected = trimmed[selectedIndex]
    val selectedFatKg = run {
        val w = selected.weight
        val lbm = selected.lbm
        if (w != null && lbm != null) (w - lbm).coerceAtLeast(0.0) else null
    }
    val selectedBfp = selected.bfp

    BodyCompositionSelectedPanel(
        selectedDate = selected.date,
        weight = selected.weight,
        lbm = selected.lbm,
        fatKg = selectedFatKg,
        bfp = selectedBfp,
        weightUnit = weightUnit,
        lbmUnit = lbmUnit,
        weightColor = weightColor,
        lbmColor = lbmColor,
        fatColor = fatColor,
        bfpColor = bfpInfoColor
    )

    val chartData = LineChartData(
        linePlotData = LinePlotData(lines = lines),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )

    InteractiveSelectableBodyChart(
        chartData = chartData,
        selectedIndex = selectedIndex,
        lastIndex = lastIndex,
        referenceSeries = weightSeries.ifEmpty { lbmSeries.ifEmpty { fatMassSeries } },
        onSelectIndex = { idx -> selectedIndex = idx.coerceIn(0, trimmed.lastIndex) }
    )
}

@Composable
private fun WaistChart(
    points: List<BodyCompositionPointDTO>,
    data: BodyCompositionDetailsDTO
) {
    val waistUnit = bodyUnitWaist(data)

    val lastWaistIndex = points.indexOfLast { it.waist != null }
    if (lastWaistIndex == -1) {
        Text(
            "No waist data for this period.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    val trimmed = points.take(lastWaistIndex + 1)

    val series = trimmed.mapIndexedNotNull { idx, p ->
        val v = p.waist ?: return@mapIndexedNotNull null
        Point(idx.toFloat(), v.toFloat())
    }

    if (series.isEmpty()) {
        Text(
            "No waist data for this period.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val rawMax = series.maxOf { it.y }.coerceAtLeast(1f)
    val niceMax = bodyNiceMaxWaist(rawMax)

    val lastIndex = (trimmed.size - 1).coerceAtLeast(1)
    val scaleLine = listOf(Point(0f, 0f), Point(lastIndex.toFloat(), niceMax))

    val labelStep = computeLabelStep(trimmed.size)

    val xAxisData = AxisData.Builder()
        .steps(lastIndex)
        .labelData { i ->
            val safe = i.coerceIn(0, trimmed.lastIndex)
            if (safe % labelStep == 0) trimmed[safe].date.takeLast(5) else ""
        }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { stepIndex ->
            val v = (niceMax / 5f) * stepIndex
            bodyFormatKgAxis(v, waistUnit)
        }
        .build()

    val waistColor = MaterialTheme.colorScheme.primary
    val avgColor = Color(0xFFFFD400)

    val avg = series.map { it.y }.average().toFloat()
    val avgLine = trimmed.mapIndexed { idx, _ -> Point(idx.toFloat(), avg) }

    LegendRow2(
        leftLabel = "Waist ($waistUnit)",
        leftColor = waistColor,
        rightLabel = "Avg",
        rightColor = MaterialTheme.colorScheme.tertiary
    )

    var selectedIndex by remember(trimmed.size) {
        mutableIntStateOf(trimmed.lastIndex.coerceAtLeast(0))
    }
    selectedIndex = selectedIndex.coerceIn(0, trimmed.lastIndex)

    val selected = trimmed[selectedIndex]
    WaistSelectedPanel(
        selectedDate = selected.date,
        waist = selected.waist,
        waistUnit = waistUnit,
        waistColor = waistColor
    )

    val waistLine = Line(
        dataPoints = series,
        lineStyle = LineStyle(color = waistColor, width = 3.4f),
        intersectionPoint = IntersectionPoint(color = waistColor, radius = 4.dp),
        selectionHighlightPoint = SelectionHighlightPoint(color = waistColor, radius = 6.dp),
        shadowUnderLine = null,
        selectionHighlightPopUp = null
    )

    val chartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                waistLine,
                Line(avgLine, LineStyle(color = avgColor, width = 2.2f)),
                Line(scaleLine, LineStyle(color = Color.Transparent))
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )

    InteractiveSelectableBodyChart(
        chartData = chartData,
        selectedIndex = selectedIndex,
        lastIndex = lastIndex,
        referenceSeries = series,
        onSelectIndex = { idx -> selectedIndex = idx.coerceIn(0, trimmed.lastIndex) }
    )
}

@Composable
private fun InteractiveSelectableBodyChart(
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
private fun BodyCompositionSelectedPanel(
    selectedDate: String?,
    weight: Double?,
    lbm: Double?,
    fatKg: Double?,
    bfp: Double?,
    weightUnit: String,
    lbmUnit: String,
    weightColor: Color,
    lbmColor: Color,
    fatColor: Color,
    bfpColor: Color
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

            BodyValueRow(
                label = "Weight ($weightUnit)",
                valueText = weight?.let { bodyFmtValue(it, weightUnit) } ?: "—",
                color = weightColor,
                emphasized = true
            )

            BodyValueRow(
                label = "LBM ($lbmUnit)",
                valueText = lbm?.let { bodyFmtValue(it, lbmUnit) } ?: "—",
                color = lbmColor
            )

            BodyValueRow(
                label = "Fat mass ($weightUnit)",
                valueText = fatKg?.let { bodyFmtValue(it, weightUnit) } ?: "—",
                color = fatColor
            )

            BodyValueRow(
                label = "Body fat (%)",
                valueText = bfp?.let { bodyFmtPercent(it, "%") } ?: "—",
                color = bfpColor
            )
        }
    }
}

@Composable
private fun WaistSelectedPanel(
    selectedDate: String?,
    waist: Double?,
    waistUnit: String,
    waistColor: Color
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

            BodyValueRow(
                label = "Waist ($waistUnit)",
                valueText = waist?.let { bodyFmtValue(it, waistUnit) } ?: "—",
                color = waistColor,
                emphasized = true
            )
        }
    }
}

@Composable
private fun BodyValueRow(
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
