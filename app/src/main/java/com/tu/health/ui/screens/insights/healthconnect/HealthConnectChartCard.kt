package com.tu.health.ui.screens.insights.healthconnect

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
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDetailsDTO
import com.tu.health.ui.components.LegendRow

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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Daily Snapshot", style = MaterialTheme.typography.titleMedium)

            HealthConnectMetricTabs(
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

            val scaleLine = listOf(
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
                    hcFormatY(metric, v)
                }
                .build()

            val seriesColor = when (metric) {
                HealthConnectMetric.STEPS -> Color(0xFF00D4FF)
                HealthConnectMetric.SLEEP_MIN -> Color(0xFF00C853)
                HealthConnectMetric.EXERCISE_MIN -> Color(0xFFFF6D00)
                HealthConnectMetric.ACTIVE_KCAL -> Color(0xFFFF1744)
                HealthConnectMetric.WORKOUTS -> Color(0xFF7C4DFF)
            }

            val avgColor = Color(0xFFFFD400)

            val seriesLine = Line(
                dataPoints = seriesPoints,
                lineStyle = LineStyle(color = seriesColor)
            )

            val avgLine = Line(
                dataPoints = avgPoints,
                lineStyle = LineStyle(color = avgColor)
            )

            val scaleHelper = Line(
                dataPoints = scaleLine,
                lineStyle = LineStyle(color = Color.Transparent)
            )

            LegendRow(
                leftLabel = "Daily",
                leftColor = seriesColor,
                rightLabel = "Avg (${hcFormatValue(metric, avgVal)})",
                rightColor = avgColor
            )

            val chartData = LineChartData(
                linePlotData = LinePlotData(lines = listOf(seriesLine, avgLine, scaleHelper)),
                xAxisData = xAxisData,
                yAxisData = yAxisData
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.TopStart
            ) {
                LineChart(
                    modifier = Modifier.fillMaxSize(),
                    lineChartData = chartData
                )
            }

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
private fun HealthConnectMetricTabs(
    selected: HealthConnectMetric,
    onSelected: (HealthConnectMetric) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HealthConnectMetric.entries.forEach { m ->
            FilterChip(
                selected = selected == m,
                onClick = { onSelected(m) },
                label = { Text(m.title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}
