package com.tu.health.ui.screens.insights.bodycomposition

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDetailsDTO
import kotlin.math.ceil
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

            if (points.isEmpty()) {
                Text(
                    "No body composition data for this period.",
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
    points: List<com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionPointDTO>,
    data: BodyCompositionDetailsDTO
) {
    val weightUnit = bodyUnitWeight(data)
    val lbmUnit = bodyUnitLbm(data)
    val bfpUnit = bodyUnitBfp(data)

    val weightVals = points.map { it.weight }
    val lbmVals = points.map { it.lbm }

    val weightSeries = points.mapIndexedNotNull { idx, p ->
        val v = p.weight ?: return@mapIndexedNotNull null
        Point(idx.toFloat(), v.toFloat())
    }
    val lbmSeries = points.mapIndexedNotNull { idx, p ->
        val v = p.lbm ?: return@mapIndexedNotNull null
        Point(idx.toFloat(), v.toFloat())
    }

    val maxKgRaw = maxOf(
        weightVals.filterNotNull().maxOrNull() ?: 0.0,
        lbmVals.filterNotNull().maxOrNull() ?: 0.0
    ).toFloat().coerceAtLeast(1f)

    val niceMaxKg = bodyNiceMaxKg(maxKgRaw)

    val bfpScaledSeries = points.mapIndexedNotNull { idx, p ->
        val bfp = p.bfp ?: return@mapIndexedNotNull null
        val scaled = bodyScaleBfpToKgAxis(bfp, niceMaxKg)
        Point(idx.toFloat(), scaled)
    }

    if (weightSeries.isEmpty() && lbmSeries.isEmpty() && bfpScaledSeries.isEmpty()) {
        Text(
            "No weight, LBM, or BFP points in this period.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val lastIndex = (points.size - 1).coerceAtLeast(1)
    val scaleLine = listOf(
        Point(0f, 0f),
        Point(lastIndex.toFloat(), niceMaxKg)
    )

    val labelStep = computeLabelStep(points.size)

    val xAxisData = AxisData.Builder()
        .steps(lastIndex)
        .labelData { i ->
            val safe = i.coerceIn(0, points.lastIndex)
            if (safe % labelStep == 0) points[safe].date.takeLast(5) else ""
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
    val bfpColor = Color(0xFFFFD400) // highlight line

    val lines = mutableListOf<Line>()
    if (weightSeries.isNotEmpty()) lines += Line(weightSeries, LineStyle(color = weightColor))
    if (lbmSeries.isNotEmpty()) lines += Line(lbmSeries, LineStyle(color = lbmColor))
    if (bfpScaledSeries.isNotEmpty()) lines += Line(bfpScaledSeries, LineStyle(color = bfpColor))
    lines += Line(scaleLine, LineStyle(color = Color.Transparent))

    LegendRow3(
        aLabel = "Weight ($weightUnit)", aColor = weightColor,
        bLabel = "LBM ($lbmUnit)", bColor = lbmColor,
        cLabel = "BFP ($bfpUnit, scaled)", cColor = bfpColor
    )

    val chartData = LineChartData(
        linePlotData = LinePlotData(lines = lines),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.TopStart
    ) {
        LineChart(modifier = Modifier.fillMaxSize(), lineChartData = chartData)
    }

    val latest = data.summary.latest
    if (latest != null) {
        val w = latest.weight?.let { bodyFmtValue(it, weightUnit) } ?: "—"
        val lbm = latest.lbm?.let { bodyFmtValue(it, lbmUnit) } ?: "—"
        val bfp = latest.bfp?.let { bodyFmtPercent(it, bfpUnit) } ?: "—"

        Text(
            text = "Latest • Weight: $w • LBM: $lbm • BFP: $bfp",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WaistChart(
    points: List<com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionPointDTO>,
    data: BodyCompositionDetailsDTO
) {
    val waistUnit = bodyUnitWaist(data)

    val series = points.mapIndexedNotNull { idx, p ->
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

    val lastIndex = (points.size - 1).coerceAtLeast(1)
    val scaleLine = listOf(Point(0f, 0f), Point(lastIndex.toFloat(), niceMax))

    val labelStep = computeLabelStep(points.size)

    val xAxisData = AxisData.Builder()
        .steps(lastIndex)
        .labelData { i ->
            val safe = i.coerceIn(0, points.lastIndex)
            if (safe % labelStep == 0) points[safe].date.takeLast(5) else ""
        }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { stepIndex ->
            val v = (niceMax / 5f) * stepIndex
            bodyFormatKgAxis(v, waistUnit)
        }
        .build()

    val color = MaterialTheme.colorScheme.primary
    val avg = series.map { it.y }.average().toFloat()
    val avgLine = points.mapIndexed { idx, _ -> Point(idx.toFloat(), avg) }

    LegendRow2(
        leftLabel = "Waist ($waistUnit)",
        leftColor = color,
        rightLabel = "Avg",
        rightColor = Color(0xFFFFD400)
    )

    val chartData = LineChartData(
        linePlotData = LinePlotData(lines = listOf(
            Line(series, LineStyle(color = color)),
            Line(avgLine, LineStyle(color = Color(0xFFFFD400))),
            Line(scaleLine, LineStyle(color = Color.Transparent))
        )),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.TopStart
    ) {
        LineChart(modifier = Modifier.fillMaxSize(), lineChartData = chartData)
    }

    val latest = data.summary.latest
    if (latest != null) {
        val w = latest.waist?.let { bodyFmtValue(it, waistUnit) } ?: "—"
        Text(
            text = "Latest waist: $w",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MetricTabs(selected: BodyMetric, onSelected: (BodyMetric) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyMetric.entries.forEach { m ->
            FilterChip(
                selected = selected == m,
                onClick = { onSelected(m) },
                label = { Text(m.title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

@Composable
private fun LegendRow2(leftLabel: String, leftColor: Color, rightLabel: String, rightColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(leftLabel, leftColor)
        LegendItem(rightLabel, rightColor)
    }
}

@Composable
private fun LegendRow3(
    aLabel: String, aColor: Color,
    bLabel: String, bColor: Color,
    cLabel: String, cColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(aLabel, aColor)
        LegendItem(bLabel, bColor)
        LegendItem(cLabel, cColor)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(color = color, shape = MaterialTheme.shapes.small, modifier = Modifier.size(10.dp)) {}
        Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun computeLabelStep(size: Int): Int {
    if (size <= 8) return 1
    val desired = 7
    return ceil(size / desired.toFloat()).roundToInt().coerceAtLeast(1)
}