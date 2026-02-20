package com.tu.health.ui.screens.insights.healthconnect

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectActivityLevelDTO

@Composable
fun HealthConnectActivityLevelCard(
    activity: HealthConnectActivityLevelDTO?
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Activity level", style = MaterialTheme.typography.titleMedium)

            if (activity == null) {
                Text(
                    "No activity level info for this period yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            val current = activity.currentActivityLevel ?: "—"
            Text(
                "Current: $current",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val change = activity.lastChange
            if (change != null && (change.from != null || change.to != null)) {
                val from = change.from ?: "—"
                val to = change.to ?: "—"
                val at = change.at ?: "—"
                Text(
                    "Last change: $from → $to",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "At: $at",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "No changes detected in this period.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
