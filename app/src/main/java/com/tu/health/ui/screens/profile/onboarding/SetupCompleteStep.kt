package com.tu.health.ui.screens.profile.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun SetupCompleteStep() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Setup complete 🎉",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "You're ready to start tracking. You can change the provided data anytime in Profile.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
