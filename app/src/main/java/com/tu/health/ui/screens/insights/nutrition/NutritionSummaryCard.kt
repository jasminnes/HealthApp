package com.tu.health.ui.screens.insights.nutrition

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
import com.tu.health.data.remote.dto.insights.nutrition.NutritionDetailsDTO

@Composable
fun NutritionSummaryCard(
    data: NutritionDetailsDTO,
    metric: MacroMetric
) {
    val avg = when (metric) {
        MacroMetric.CALORIES -> data.summary.avgCalories
        MacroMetric.PROTEIN -> data.summary.avgProteinG
        MacroMetric.CARBS -> data.summary.avgCarbsG
        MacroMetric.FAT -> data.summary.avgFatG
    }

    val target = when (metric) {
        MacroMetric.CALORIES -> data.plan.calories?.toDouble()
        MacroMetric.PROTEIN -> data.plan.protein
        MacroMetric.CARBS -> data.plan.carbs
        MacroMetric.FAT -> data.plan.fat
    }

    ElevatedCard(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Quick summary", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Avg ${metric.title.lowercase()}: ${nutritionFormatValue(metric, avg)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Target: ${target?.let { nutritionFormatValue(metric, it) } ?: "—"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (metric == MacroMetric.CALORIES) {
                Spacer(Modifier.height(10.dp))

                val rmr = data.summary.latestRmr
                val tdee = data.summary.latestTdee

                Text(
                    text = "RMR: ${rmr?.let { "${it.toInt()} kcal" } ?: "—"} • TDEE: ${tdee?.let { "${it.toInt()} kcal" } ?: "—"}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "RMR is calories your body burns at rest. TDEE is your estimated daily burn including activity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}