package com.tu.health.ui.screens.profile.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BodyMeasurementsStep(
    weight: Float,
    waist: Float,
    neck: Float,
    onWeightChange: (Float) -> Unit,
    onWaistChange: (Float) -> Unit,
    onNeckChange: (Float) -> Unit
) {
    Column {
        Text(
            text = "Body measurements",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        MeasurementSlider("Weight (kg)", weight, 40f..200f, onWeightChange)
        MeasurementSlider("Waist (cm)", waist, 50f..150f, onWaistChange)
        MeasurementSlider("Neck (cm)", neck, 25f..60f, onNeckChange)
    }
}

@Composable
private fun MeasurementSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit
) {
    Spacer(Modifier.height(12.dp))
    Text("$label: ${value.toInt()}")
    Slider(
        value = value,
        onValueChange = onChange,
        valueRange = range,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.surface,
            activeTrackColor = MaterialTheme.colorScheme.surfaceDim
        )
    )
}
