package com.tu.health.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.ui.components.ConfirmationDialog
import com.tu.health.viewmodels.authentication.AuthViewModel
import com.tu.health.viewmodels.profile.ProfileUiEvent
import com.tu.health.viewmodels.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    val email by profileViewModel.email.collectAsState(initial = "")
    val firstName by profileViewModel.firstName.collectAsState(initial = "")
    val lastName by profileViewModel.lastName.collectAsState(initial = "")

    val uiState by profileViewModel.uiState.collectAsState()
    val activityLevel by profileViewModel.selectedActivityLevel.collectAsState(initial = null)

    val weightGoal = uiState.weightGoal
    val height = uiState.height

    var showGoalDialog by remember { mutableStateOf(false) }
    var showActivityDialog by remember { mutableStateOf(false) }
    var showHeightDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.events.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowMessage ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val displayName by remember(firstName, lastName) {
        derivedStateOf {
            if (firstName.isNotBlank() || lastName.isNotBlank()) {
                "${firstName.trim()} ${lastName.trim()}".trim()
            } else "User"
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(displayName, style = MaterialTheme.typography.titleLarge)
                        Text(
                            email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { navController.navigate("editProfile") },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.surfaceDim
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))

                ProfileOptionRow("Height") { showHeightDialog = true }
                ProfileOptionRow("Body Measurements") { navController.navigate("measurements") }
                ProfileOptionRow("Weight Goal") { showGoalDialog = true }
                ProfileOptionRow("Activity Level") { showActivityDialog = true }
                ProfileOptionRow("Diet Type") { navController.navigate("diet") }
                ProfileOptionRow("Health Conditions") { navController.navigate("conditions") }
                ProfileOptionRow("Change Password") { navController.navigate("change-password") }
                ProfileOptionRow("Delete Account") { showDeleteDialog = true }

                if (showDeleteDialog) {
                    ConfirmationDialog(
                        title = "Delete Account",
                        message = "Are you sure you want to permanently delete your account? This action cannot be undone.",
                        confirmText = "Delete",
                        onConfirm = {
                            authViewModel.deleteUser {
                                navController.navigate("authentication") {
                                    popUpTo("profile") { inclusive = true }
                                }
                            }
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            authViewModel.logout {
                                navController.navigate("authentication") {
                                    popUpTo("profile") { inclusive = true }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Logout", color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
        }
    }

    if (showGoalDialog) {
        WeightGoalDialog(
            currentGoal = weightGoal,
            onDismiss = { showGoalDialog = false },
            onSave = { newGoal ->
                profileViewModel.onWeightGoalChange(newGoal)
                profileViewModel.updateUserWeightGoal { success ->
                    if (success) showGoalDialog = false
                }
            }
        )
    }

    if (showActivityDialog) {
        ActivityLevelDialog(
            activityLevelName = activityLevel?.name,
            activityLevelDescription = activityLevel?.description,
            onDismiss = { showActivityDialog = false }
        )
    }

    if (showHeightDialog) {
        HeightDialog(
            currentHeight = height,
            onDismiss = { showHeightDialog = false },
            onSave = { newHeight ->
                profileViewModel.onHeightChange(newHeight)
                profileViewModel.updateUserHeight { success ->
                    if (success) showHeightDialog = false
                }
            }
        )
    }
}

@Composable
private fun ProfileOptionRow(text: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text, style = MaterialTheme.typography.titleSmall)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightGoalDialog(
    currentGoal: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val options = listOf(
        "Maintain Weight",
        "Lose Weight",
        "Gain Muscle"
    )

    var selected by remember { mutableStateOf(currentGoal) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Weight goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (currentGoal.isBlank())
                        "Current: Not set"
                    else
                        "Current: $currentGoal",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selected.ifBlank { "Choose goal" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Change to") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.tertiaryContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selected = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selected.isNotBlank() && selected != currentGoal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                onClick = { onSave(selected) }
            ) {
                Text("Save")
            }
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

@Composable
private fun ActivityLevelDialog(
    activityLevelName: String?,
    activityLevelDescription: String?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Activity level") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Text(
                    text = activityLevelName?.takeIf { it.isNotBlank() } ?: "Not set",
                    style = MaterialTheme.typography.titleSmall,
                )

                if (!activityLevelDescription.isNullOrBlank()) {
                    Text(
                        text = activityLevelDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Text("OK")
            }
        }
    )
}

@Composable
private fun HeightDialog(
    currentHeight: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    fun pretty(value: Float): String {
        val rounded = (value * 10f).toInt() / 10f
        return if (rounded == rounded.toInt().toFloat()) rounded.toInt().toString() else rounded.toString()
    }

    var text by remember {
        mutableStateOf(
            if (currentHeight > 0f) pretty(currentHeight) else ""
        )
    }

    val parsed = text.replace(",", ".").toFloatOrNull()
    val isValid = parsed != null && parsed in 50f..250f
    val changed = isValid && kotlin.math.abs(parsed - currentHeight) > 0.0001f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Height") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (currentHeight > 0f) "Current: ${pretty(currentHeight)} cm" else "Current: Not set",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    supportingText = {
                        if (text.isNotBlank() && !isValid) {
                            Text("Enter a valid height (50–250 cm)")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.tertiaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        confirmButton = {
            Button(
                enabled = changed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                onClick = { onSave(parsed!!) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
