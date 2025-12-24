package com.tu.health.ui.screens.profile.goals

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tu.health.ui.components.DatePicker
import com.tu.health.ui.components.DatePickerDialog
import com.tu.health.viewmodels.profile.WeightGoalViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightGoalEdit(
    viewModel: WeightGoalViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val name by viewModel.name.collectAsState()
    val startingWeight by viewModel.startingWeight.collectAsState()
    val goalWeight by viewModel.goalWeight.collectAsState()
    val finalDate by viewModel.finalDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val goalOptions = listOf("Maintain Weight", "Lose Weight", "Gain Muscle")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = if (viewModel.selectedGoalId.value == 0) "Create a Goal" else "Edit Goal",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Goal name") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.secondary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.secondary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    goalOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.onNameChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = startingWeight.toString(),
                onValueChange = { viewModel.onStartingWeightChange(it.toFloatOrNull() ?: 0f) },
                label = { Text("Starting weight") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.secondary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = goalWeight.toString(),
                onValueChange = { viewModel.onGoalWeightChange(it.toFloatOrNull() ?: 0f) },
                label = { Text("Goal weight") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.secondary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            DatePicker(
                selectedDate = finalDate,
                displayText = "Final Date",
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val isEdit = viewModel.selectedGoalId.value != 0
                        if (isEdit) {
                            viewModel.updateGoalWeight { success, error ->
                                if (success) {
                                    onSave()
                                } else {
                                    scope.launch {
                                        snackBarHostState.showSnackbar(error ?: "Unknown error")
                                    }
                                }
                            }
                        } else {
                            viewModel.createGoalWeight { success, error ->
                                if (success) {
                                    onSave()
                                } else {
                                    scope.launch {
                                        snackBarHostState.showSnackbar(error ?: "Unknown error")
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Save", style = MaterialTheme.typography.titleMedium)
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "Cancel", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    show = true,
                    initialDateMillis = finalDate.takeIf { it.isNotEmpty() }?.let { dateStr ->
                        try {
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .parse(dateStr)?.time
                        } catch (_: Exception) { null }
                    },
                    onDismiss = { showDatePicker = false },
                    onConfirm = { millis ->
                        millis?.let {
                            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date(it))
                            viewModel.onFinalDateChange(formatted)
                        }
                        showDatePicker = false
                    }
                )
            }
        }

    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
