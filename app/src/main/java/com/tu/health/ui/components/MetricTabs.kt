package com.tu.health.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tu.health.ui.screens.insights.bodycomposition.BodyMetric
import com.tu.health.ui.screens.insights.healthconnect.HealthConnectMetric
import com.tu.health.ui.screens.insights.nutrition.MacroMetric
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun MetricTabs(selected: MacroMetric, onSelected: (MacroMetric) -> Unit) {
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
fun MetricTabs(
    selected: BodyMetric,
    onSelected: (BodyMetric) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyMetric.entries.forEach { m ->
            FilterChip(
                selected = selected == m,
                onClick = { onSelected(m) },
                label = {
                    Text(
                        text = m.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                enabled = true,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == m,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    borderWidth = 1.dp
                )
            )
        }
    }
}

@Composable
fun MetricTabs(
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


fun computeLabelStep(size: Int): Int {
    if (size <= 8) return 1
    val desired = 7
    return ceil(size / desired.toFloat()).roundToInt().coerceAtLeast(1)
}
