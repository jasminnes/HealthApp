package com.tu.health.ui.screens.insights.healthconnect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDetailsDTO

@Composable
fun HealthConnectSummaryCard(
    data: HealthConnectDetailsDTO,
    metric: HealthConnectMetric
) {
    val avg = hcAvgValue(metric, data)

    val latestPoint = data.summary.latest
    val latest = when (metric) {
        HealthConnectMetric.STEPS -> latestPoint.steps?.toDouble()
        HealthConnectMetric.SLEEP_MIN -> latestPoint.sleepMin?.toDouble()
        HealthConnectMetric.EXERCISE_MIN -> latestPoint.exerciseMinutes?.toDouble()
        HealthConnectMetric.ACTIVE_KCAL -> latestPoint.activeKcal
        HealthConnectMetric.WORKOUTS -> latestPoint.workouts?.toDouble()
    }

    ElevatedCard(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Quick summary", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Latest ${metric.title.lowercase()}: ${latest?.let { hcFormatValue(metric, it) } ?: "—"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Avg: ${hcFormatValue(metric, avg)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
