package com.tu.health.ui.screens.insights.scores

import androidx.compose.foundation.layout.*
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
import com.tu.health.data.remote.dto.insights.scores.HealthScoresDTO
import com.tu.health.data.remote.dto.insights.scores.ScorePointDTO
import kotlin.math.ceil
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.FlowRow

@Composable
fun HealthScoresChartCard(
    data: HealthScoresDTO
) {
    val points = data.points
    val lastIndex = (points.size - 1).coerceAtLeast(1)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Scores over time", style = MaterialTheme.typography.titleMedium)

            if (points.isEmpty()) {
                Text(
                    "No scores for this period yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            val totalSeries = points.toSeries { it.total }
            val activitySeries = points.toSeries { it.activity }
            val recoverySeries = points.toSeries { it.recovery }
            val nutritionSeries = points.toSeries { it.nutrition }
            val bodySeries = points.toSeries { it.bodyComposition }

            val niceMax = 100f
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
                    v.roundToInt().toString()
                }
                .build()

            val totalColor = Color(0xFF00D4FF)
            val activityColor = Color(0xFF7C4DFF)
            val recoveryColor = Color(0xFF00C853)
            val nutritionColor = Color(0xFFFF6D00)
            val bodyColor = Color(0xFFFF1744)

            val lines = mutableListOf<Line>()

            if (totalSeries.isNotEmpty()) lines += Line(totalSeries, LineStyle(color = totalColor))
            if (activitySeries.isNotEmpty()) lines += Line(activitySeries, LineStyle(color = activityColor))
            if (recoverySeries.isNotEmpty()) lines += Line(recoverySeries, LineStyle(color = recoveryColor))
            if (nutritionSeries.isNotEmpty()) lines += Line(nutritionSeries, LineStyle(color = nutritionColor))
            if (bodySeries.isNotEmpty()) lines += Line(bodySeries, LineStyle(color = bodyColor))

            lines += Line(scaleLine, LineStyle(color = Color.Transparent))

            LegendRow5(
                aLabel = "Total", aColor = totalColor,
                bLabel = "Activity", bColor = activityColor,
                cLabel = "Recovery", cColor = recoveryColor,
                dLabel = "Nutrition", dColor = nutritionColor,
                eLabel = "Body", eColor = bodyColor,
            )

            val chartData = LineChartData(
                linePlotData = LinePlotData(lines = lines),
                xAxisData = xAxisData,
                yAxisData = yAxisData
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.TopStart
            ) {
                LineChart(modifier = Modifier.fillMaxSize(), lineChartData = chartData)
            }

            val latest = data.summary.latest
            val subtitle = buildString {
                append("Latest • Total: ${latest.total?.roundToInt() ?: "—"}")
                append(" • A: ${latest.activity?.roundToInt() ?: "—"}")
                append(" • R: ${latest.recovery?.roundToInt() ?: "—"}")
                append(" • N: ${latest.nutrition?.roundToInt() ?: "—"}")
                append(" • B: ${latest.bodyComposition?.roundToInt() ?: "—"}")
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

private fun List<ScorePointDTO>.toSeries(get: (ScorePointDTO) -> Double?): List<Point> {
    return mapIndexedNotNull { idx, p ->
        val v = get(p) ?: return@mapIndexedNotNull null
        Point(x = idx.toFloat(), y = v.toFloat())
    }
}

private fun computeLabelStep(size: Int): Int {
    if (size <= 8) return 1
    val desired = 7
    return ceil(size / desired.toFloat()).roundToInt().coerceAtLeast(1)
}

@Composable
private fun LegendRow5(
    aLabel: String, aColor: Color,
    bLabel: String, bColor: Color,
    cLabel: String, cColor: Color,
    dLabel: String, dColor: Color,
    eLabel: String, eColor: Color
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LegendItem(aLabel, aColor)
        LegendItem(bLabel, bColor)
        LegendItem(cLabel, cColor)
        LegendItem(dLabel, dColor)
        LegendItem(eLabel, eColor)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(color = color, shape = MaterialTheme.shapes.small, modifier = Modifier.size(10.dp)) {}
        Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}