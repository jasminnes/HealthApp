package com.tu.health.ui.screens.profile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tu.health.data.remote.dto.DietTypeDTO

@Composable
fun DietTypeStep(
    items: List<DietTypeDTO>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = "Diet preference",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.size) { index ->
                val item = items[index]
                val backgroundColor =
                    if (item.id == selectedId) MaterialTheme.colorScheme.surfaceDim
                    else MaterialTheme.colorScheme.surface

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .clickable { onSelect(item.id) }
                ) {
                    Text(text = item.name)
                }
            }
        }
    }
}
