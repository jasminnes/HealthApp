package com.tu.health.ui.screens.insights.scores

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.insights.scores.HealthScoresDTO
import kotlin.math.roundToInt

@Composable
fun HealthScoresSummaryCard(
    data: HealthScoresDTO
) {
    val latest = data.summary.latest
    val avg = data.summary.avg
    val delta = data.summary.delta

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Quick summary", style = MaterialTheme.typography.titleMedium)

            Text(
                "Latest total: ${latest.total?.roundToInt() ?: "—"} • Avg: ${avg.total.roundToInt()}",
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            ScoreRow(
                title = "Activity",
                latest = latest.activity,
                avg = avg.activity,
            )
            ScoreRow(
                title = "Recovery",
                latest = latest.recovery,
                avg = avg.recovery,
            )
            ScoreRow(
                title = "Nutrition",
                latest = latest.nutrition,
                avg = avg.nutrition,
            )
            ScoreRow(
                title = "Body composition",
                latest = latest.bodyComposition,
                avg = avg.bodyComposition,
            )

            Text(
                "Tip: subscores help you see what’s limiting the total score. Improve the lowest one first for faster progress.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScoreRow(title: String, latest: Double?, avg: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Latest: ${latest?.roundToInt() ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
            Text("Avg: ${avg.roundToInt()}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
