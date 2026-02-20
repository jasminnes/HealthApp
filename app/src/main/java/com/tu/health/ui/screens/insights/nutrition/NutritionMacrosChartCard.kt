package com.tu.health.ui.screens.insights.nutrition

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
import com.tu.health.data.remote.dto.insights.nutrition.NutritionDetailsDTO
import kotlin.math.ceil
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

            val consumedColor = Color(0xFFDAE72B)
            val targetColor = Color(0xFF00FF25)

            val rmrColor = Color(0xFFE8702F)
            val tdeeColor = Color(0xFFFF2600)

            val lines = mutableListOf<Line>()
            lines += Line(consumedPoints, LineStyle(color = consumedColor))

            if (targetPoints.isNotEmpty()) lines += Line(targetPoints, LineStyle(color = targetColor))
            if (rmrPoints.isNotEmpty()) lines += Line(rmrPoints, LineStyle(color = rmrColor))
            if (tdeePoints.isNotEmpty()) lines += Line(tdeePoints, LineStyle(color = tdeeColor))

            lines += Line(scaleLinePoints, LineStyle(color = Color.Transparent))

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

            val chartData = LineChartData(
                linePlotData = LinePlotData(lines = lines),
                xAxisData = xAxisData,
                yAxisData = yAxisData
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                contentAlignment = Alignment.TopStart
            ) {
                LineChart(modifier = Modifier.fillMaxSize(), lineChartData = chartData)
            }

            val latest = points.lastOrNull()
            if (latest != null) {
                val latestConsumed = nutritionMetricValue(metric, latest)
                val subtitle = buildString {
                    append("Latest: ${nutritionFormatValue(metric, latestConsumed)}")
                    if (targetVal != null) append(" • Target: ${nutritionFormatValue(metric, targetVal)}")

                    if (energyEnabled) {
                        val rmr = latest.rmr
                        val tdee = latest.tdee
                        if (rmr != null) append(" • RMR ${rmr.toInt()}")
                        if (tdee != null) append(" • TDEE ${tdee.toInt()}")
                    }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MacroMetric.entries.forEach { m ->
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
private fun LegendRow4(
    aLabel: String, aColor: Color,
    bLabel: String, bColor: Color,
    cLabel: String, cColor: Color,
    dLabel: String, dColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(aLabel, aColor)
        LegendItem(bLabel, bColor)
        LegendItem(cLabel, cColor)
        LegendItem(dLabel, dColor)
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