package com.tu.health.ui.screens.insights.nutrition

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import kotlin.math.ceil
import kotlin.math.roundToInt
import com.tu.health.data.remote.dto.insights.NutritionDetailsDTO
import com.tu.health.data.remote.dto.insights.NutritionDetailsPointDTO


@Composable
fun NutritionMacrosChartCard(
    data: NutritionDetailsDTO,
    metric: MacroMetric,
    onMetricChange: (MacroMetric) -> Unit
) {

    val points = data.points
    val plan = data.plan

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Consumed vs Target", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(10.dp))

            MetricTabs(selected = metric, onSelected = onMetricChange)

            Spacer(Modifier.height(10.dp))

            if (points.isEmpty()) {
                Text(
                    "No tracked nutrition for this period yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            val consumedVals = points.map { metricValue(metric, it) }
            val targetVal = targetValue(metric, plan)

            val dataMax = maxOf(
                consumedVals.maxOrNull() ?: 0.0,
                targetVal ?: 0.0
            ).toFloat().coerceAtLeast(1f)

            val niceMax = niceMaxY(metric, dataMax)

            val consumedPoints = points.mapIndexed { idx, p ->
                Point(x = idx.toFloat(), y = metricValue(metric, p).toFloat())
            }

            val targetPoints = if (targetVal != null) {
                points.mapIndexed { idx, _ ->
                    Point(x = idx.toFloat(), y = targetVal.toFloat())
                }
            } else emptyList()

            val scaleLinePoints = listOf(
                Point(x = 0f, y = 0f),
                Point(x = (points.size - 1).coerceAtLeast(1).toFloat(), y = niceMax)
            )

            val labelStep = computeLabelStep(points.size)

            val xAxisData = AxisData.Builder()
                .steps((points.size - 1).coerceAtLeast(1))
                .labelData { i ->
                    if (i % labelStep == 0) points[i].date.takeLast(5) else ""
                }
                .build()

            val yAxisData = AxisData.Builder()
                .steps(5)
                .labelData { stepIndex ->
                    val v = (niceMax / 5f) * stepIndex
                    formatY(metric, v)
                }
                .build()

            val consumedColor = MaterialTheme.colorScheme.primary
            val targetColor = MaterialTheme.colorScheme.tertiary

            val consumedLine = Line(
                dataPoints = consumedPoints,
                lineStyle = LineStyle(color = consumedColor)
            )

            val lines = mutableListOf(consumedLine)

            if (targetPoints.isNotEmpty()) {
                val targetLine = Line(
                    dataPoints = targetPoints,
                    lineStyle = LineStyle(color = targetColor)
                )
                lines.add(targetLine)
            }

            val scaleLine = Line(
                dataPoints = scaleLinePoints,
                lineStyle = LineStyle(color = androidx.compose.ui.graphics.Color.Transparent)
            )
            lines.add(scaleLine)

            LegendRow(
                leftLabel = "Consumed",
                leftColor = consumedColor,
                rightLabel = targetVal?.let { "Target (${formatValue(metric, it)})" } ?: "Target (not set)",
                rightColor = targetColor
            )

            Spacer(Modifier.height(10.dp))

            val chartData = LineChartData(
                linePlotData = LinePlotData(lines = lines),
                xAxisData = xAxisData,
                yAxisData = yAxisData
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp)
            ) {
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp),
                    lineChartData = chartData
                )
            }

            Spacer(Modifier.height(10.dp))

            val latest = points.lastOrNull()
            if (latest != null) {
                val latestConsumed = metricValue(metric, latest)
                val subtitle = buildString {
                    append("Latest: ${formatValue(metric, latestConsumed)}")
                    if (targetVal != null) append(" • Target: ${formatValue(metric, targetVal)}")
                }
                Text(
                    subtitle,
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
private fun MetricTabs(selected: MacroMetric, onSelected: (MacroMetric) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MacroMetric.entries.forEach { m ->
            FilterChip(
                selected = selected == m,
                onClick = { onSelected(m) },
                label = { Text(m.title) }
            )
        }
    }
}

@Composable
private fun LegendRow(
    leftLabel: String,
    leftColor: androidx.compose.ui.graphics.Color,
    rightLabel: String,
    rightColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(label = leftLabel, color = leftColor)
        LegendItem(label = rightLabel, color = rightColor)
    }
}

@Composable
private fun LegendItem(label: String, color: androidx.compose.ui.graphics.Color) {
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

private fun metricValue(metric: MacroMetric, p: NutritionDetailsPointDTO): Double {
    return when (metric) {
        MacroMetric.CALORIES -> p.calories
        MacroMetric.PROTEIN -> p.proteinG
        MacroMetric.CARBS -> p.carbsG
        MacroMetric.FAT -> p.fatG
    }
}

private fun targetValue(metric: MacroMetric, plan: com.tu.health.data.remote.dto.insights.NutritionPlanDTO): Double? {
    return when (metric) {
        MacroMetric.CALORIES -> plan.calories?.toDouble()
        MacroMetric.PROTEIN -> plan.proteinG
        MacroMetric.CARBS -> plan.carbsG
        MacroMetric.FAT -> plan.fatG
    }
}

private fun niceMaxY(metric: MacroMetric, rawMax: Float): Float {
    val base = when (metric) {
        MacroMetric.CALORIES -> 250f
        else -> 10f
    }
    val padded = rawMax * 1.10f
    return (ceil(padded / base) * base).coerceAtLeast(base)
}

@SuppressLint("DefaultLocale")
private fun formatY(metric: MacroMetric, v: Float): String {
    return if (metric == MacroMetric.CALORIES) {
        if (v >= 1000f) String.format("%.1fk", v / 1000f) else v.roundToInt().toString()
    } else {
        v.roundToInt().toString()
    }
}

fun formatValue(metric: MacroMetric, v: Double): String {
    val n = v.roundToInt()
    return if (metric == MacroMetric.CALORIES) "$n kcal" else "$n g"
}
