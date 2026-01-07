package com.tu.health.ui.screens.profile.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.DietTypeDTO

@Composable
fun RecommendedDietsStep(
    items: List<DietTypeDTO>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    val top3 = remember(items) { items.take(3) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Recommended diets", style = MaterialTheme.typography.titleLarge)
        Text(
            "Top recommended diets for you. Choose only if willing to change your diet.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (top3.isEmpty()) {
            Text(
                "No recommendations available.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            return
        }

        top3.forEachIndexed { index, diet ->
            val isSelected = diet.id == selectedId

            ElevatedCard(
                onClick = { onSelect(diet.id) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("${index + 1}") }
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(diet.name, fontWeight = FontWeight.SemiBold)
                        if (diet.description.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                diet.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("P ${diet.proteinRatio}% • F ${diet.fatRatio}% • C ${diet.carbsRatio}%") }
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelect(diet.id) }
                    )
                }
            }
        }
    }
}

