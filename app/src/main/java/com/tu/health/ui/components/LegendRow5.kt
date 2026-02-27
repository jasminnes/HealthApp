package com.tu.health.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LegendRow5(
    aLabel: String, aColor: Color,
    bLabel: String, bColor: Color,
    cLabel: String, cColor: Color,
    dLabel: String, dColor: Color,
    eLabel: String, eColor: Color
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LegendItem(aLabel, aColor)
        LegendItem(bLabel, bColor)
        LegendItem(cLabel, cColor)
        LegendItem(dLabel, dColor)
        LegendItem(eLabel, eColor)
    }
}