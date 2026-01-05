package com.tu.health.ui.screens.profile

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.ui.components.ConfirmationDialog
import com.tu.health.viewmodels.authentication.AuthViewModel
import com.tu.health.viewmodels.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    val email by profileViewModel.email.collectAsState(initial = "")
    val firstName by profileViewModel.firstName.collectAsState(initial = "")
    val lastName by profileViewModel.lastName.collectAsState(initial = "")

    val weightGoal by profileViewModel.weightGoal.collectAsState(initial = "")
    var showGoalDialog by remember { mutableStateOf(false) }

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

                // Profile Header
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

                // Options
                ProfileOptionRow("Body Measurements") {
                    navController.navigate("measurements")
                }

                ProfileOptionRow("Weight Goal") {
                    showGoalDialog = true
                }

                ProfileOptionRow("Change Password") {
                    navController.navigate("change-password")
                }

                ProfileOptionRow("Delete Account") { showDeleteDialog = true }

                if (showDeleteDialog) {
                    ConfirmationDialog(
                        title = "Delete Account",
                        message = "Are you sure you want to permanently delete your account? This action cannot be undone.",
                        confirmText = "Delete",
                        onConfirm = {
                            showDeleteDialog = false
                            authViewModel.deleteUser { success, errorMessage ->
                                if (success) {
                                    navController.navigate("authentication") {
                                        popUpTo("profile") { inclusive = true }
                                    }
                                } else {
                                    println("Delete failed: $errorMessage")
                                }
                            }
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Logout Button
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
                profileViewModel.updateUserWeightGoal { success, error ->
                    if (!success) {
                        println("Goal update failed: $error")
                    }
                }
                showGoalDialog = false
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
            modifier = Modifier,
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

