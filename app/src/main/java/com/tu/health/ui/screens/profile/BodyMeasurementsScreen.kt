package com.tu.health.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.data.remote.dto.BodyMeasurementDTO
import com.tu.health.ui.components.ConfirmationDialog
import com.tu.health.viewmodels.profile.bodyMeasurements.BodyMeasurementsViewModel
import com.tu.health.viewmodels.profile.ProfileUiEvent
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMeasurementsScreen(
    navController: NavController,
    viewModel: BodyMeasurementsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val measurements = uiState.measurements
    val isLoading = uiState.isLoading

    var createOpen by remember { mutableStateOf(false) }
    var deleteConfirmFor by remember { mutableStateOf<BodyMeasurementDTO?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshMeasurements()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Body measurements") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { createOpen = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            when {
                isLoading && measurements.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                measurements.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No measurements yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tap + to add your first entry.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = measurements,
                            key = { it.id }
                        ) { m ->
                            MeasurementRowCard(
                                m = m,
                                onDeleteClick = { deleteConfirmFor = m }
                            )
                        }

                        item { Spacer(Modifier.height(6.dp)) }
                    }
                }
            }

            if (isLoading && measurements.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (createOpen) {
        CreateMeasurementDialog(
            onDismiss = { createOpen = false },
            onSave = { w, waist, neck ->
                viewModel.onWeightChange(w)
                viewModel.onWaistChange(waist)
                viewModel.onNeckChange(neck)

                viewModel.createBodyMeasurement { ok ->
                    if (ok) {
                        Toast.makeText(context, "Measurement added", Toast.LENGTH_SHORT).show()
                    }
                }
                createOpen = false
            }
        )
    }

    deleteConfirmFor?.let { m ->
        ConfirmationDialog(
            title = "Delete measurement?",
            message = "This cannot be undone",
            onDismiss = { deleteConfirmFor = null },
            onConfirm = {
                viewModel.deleteBodyMeasurement(m.id) { ok ->
                    if (ok) {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                deleteConfirmFor = null
            }
        )
    }
}

@Composable
private fun MeasurementRowCard(
    m: BodyMeasurementDTO,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatDateForUi(m.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Weight: ${formatNumber2(m.weight)} kg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Waist: ${formatNumber2(m.waist)} cm • Neck: ${formatNumber2(m.neck)} cm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CreateMeasurementDialog(
    onDismiss: () -> Unit,
    onSave: (weight: Float, waist: Float, neck: Float) -> Unit,
) {
    var weightText by remember { mutableStateOf("") }
    var waistText by remember { mutableStateOf("") }
    var neckText by remember { mutableStateOf("") }

    var submitted by remember { mutableStateOf(false) }

    val w = weightText.toFloatOrNullSmart()
    val wa = waistText.toFloatOrNullSmart()
    val n = neckText.toFloatOrNullSmart()

    val valid = (w != null && w > 0f && w <= 500f) &&
            (wa == null || (wa > 0f && wa <= 300f)) &&
            (n == null || (n > 0f && n <= 100f))

    val weightValue = weightText.trim().toFloatOrNull()
    val waistValue = waistText.trim().toFloatOrNull()
    val neckValue = neckText.trim().toFloatOrNull()

    val weightMissing = weightText.isBlank()
    val weightInvalid = !weightMissing && weightValue == null

    val waistInvalid = waistText.isNotBlank() && waistValue == null
    val neckInvalid = neckText.isNotBlank() && neckValue == null

    val canSave = weightValue != null && !waistInvalid && !neckInvalid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add measurement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg) *") },
                    supportingText = { Text("Required") },
                    isError = submitted && (!valid && (weightMissing || weightInvalid)),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.tertiaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                OutlinedTextField(
                    value = waistText,
                    onValueChange = { waistText = it },
                    label = { Text("Waist (cm)") },
                    supportingText = { Text("Optional") },
                    isError = submitted && (!valid && waistInvalid),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.tertiaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                OutlinedTextField(
                    value = neckText,
                    onValueChange = { neckText = it },
                    label = { Text("Neck (cm)") },
                    supportingText = { Text("Optional") },
                    isError = submitted && (!valid && neckInvalid),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.tertiaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                if (submitted && (!valid || !canSave)) {
                    val message = when {
                        weightMissing -> "Weight is required."
                        weightInvalid -> "Weight must be a valid number."
                        waistInvalid || neckInvalid -> "Optional fields must be valid numbers (or left blank)."
                        else -> "Please enter valid values."
                    }
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = true,
                onClick = {
                    submitted = true
                    if (!valid || !canSave) return@TextButton

                    onSave(
                        w,
                        wa ?: 0f,
                        n ?: 0f
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) { Text("Cancel") }
        }
    )
}

private fun String.toFloatOrNullSmart(): Float? =
    trim().replace(",", ".").toFloatOrNull()

private fun formatDateForUi(createdAt: String): String =
    createdAt.replace("T", " ").take(16)


private fun formatNumber2(value: Number?): String {
    val v = value?.toDouble() ?: 0.0
    return String.format(Locale.US, "%.2f", v)
}
