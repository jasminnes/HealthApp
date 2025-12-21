package com.tu.health.ui.screens.profile.steps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.ActivityDTO

@Composable
fun ActivityLevelStep(
    items: List<ActivityDTO>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = "Activity level",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        items.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(item.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (item.id == selectedId)
                        MaterialTheme.colorScheme.surfaceDim
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(item.name, style = MaterialTheme.typography.bodyLarge)
                    Text(item.description, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
