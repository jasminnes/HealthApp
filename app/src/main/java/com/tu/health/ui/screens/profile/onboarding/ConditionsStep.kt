package com.tu.health.ui.screens.profile.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.ConditionDTO

@Composable
fun ConditionsStep(
    items: List<ConditionDTO>,
    selectedIds: Set<Int>,
    onToggle: (Int) -> Unit
) {
    Column {
        Text(
            text = "Health conditions",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = items,
                key = { it.id }
            ) { condition ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(condition.id) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = condition.name,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Checkbox(
                        checked = selectedIds.contains(condition.id),
                        onCheckedChange = { onToggle(condition.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkmarkColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

    }
}
