package com.tu.health.ui.screens.insights.bodycomposition

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDetailsDTO

@Composable
fun BodyCompositionSummaryCard(
    data: BodyCompositionDetailsDTO
) {
    val latest = data.summary.latest
    val delta = data.summary.delta

    val weightUnit = bodyUnitWeight(data)
    val lbmUnit = bodyUnitLbm(data)
    val bfpUnit = bodyUnitBfp(data)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Quick summary", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatLine(
                    title = "Weight",
                    value = latest?.weight?.let { bodyFmtValue(it, weightUnit) } ?: "—",
                    sub = delta?.weight?.let { bodyFmtDelta(it, weightUnit) } ?: "—"
                )
                StatLine(
                    title = "LBM",
                    value = latest?.lbm?.let { bodyFmtValue(it, lbmUnit) } ?: "—",
                    sub = delta?.lbm?.let { bodyFmtDelta(it, lbmUnit) } ?: "—"
                )
                StatLine(
                    title = "BFP",
                    value = latest?.bfp?.let { bodyFmtPercent(it, bfpUnit) } ?: "—",
                    sub = delta?.bfp?.let { bodyFmtDeltaPercent(it, bfpUnit) } ?: "—"
                )
            }

            HorizontalDivider()

            Text(
                text = "• BFP (Body Fat %) is the percent of your body weight that is fat. " +
                        "For the same weight, a lower BFP generally means a higher proportion of lean tissue.\n" +
                        "• LBM (Lean Body Mass) is everything that’s not fat: muscles, organs, bones, and water. " +
                        "LBM trends are useful for spotting muscle gain/loss during diet or training phases.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RowScope.StatLine(title: String, value: String, sub: String) {
    Column(modifier = Modifier.weight(1f)) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(
            text = "Δ $sub",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}