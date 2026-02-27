package com.tu.health.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LegendRow3(
    aLabel: String, aColor: Color,
    bLabel: String, bColor: Color,
    cLabel: String, cColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(aLabel, aColor)
        LegendItem(bLabel, bColor)
        LegendItem(cLabel, cColor)
    }
}
