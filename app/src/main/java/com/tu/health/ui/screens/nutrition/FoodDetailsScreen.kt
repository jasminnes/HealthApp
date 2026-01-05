package com.tu.health.ui.screens.nutrition

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tu.health.ui.components.ConfirmationDialog
import com.tu.health.viewmodels.nutrition.MacrosViewModel
import kotlin.math.roundToInt

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreen(
    navController: NavController,
) {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry("macros")
    }
    val viewModel: MacrosViewModel = hiltViewModel(parentEntry)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val id = backStackEntry?.arguments?.getString("id")?.toIntOrNull() ?: 0
    val state by viewModel.uiState.collectAsState()
    val trackedFoods = state.trackedFoods
    val isLoading = state.isLoading


    var editOpen by remember { mutableStateOf(false) }
    var deleteOpen by remember { mutableStateOf(false) }

    val food = trackedFoods.firstOrNull { it.id == id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                isLoading && food == null -> {
                    CircularProgressIndicator()
                }
                food == null -> {
                    Text(
                        "Food not found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    ElevatedCard(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = food.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "${food.quantity.pretty1()} g " +
                                        "• ${food.calories.roundToInt()} kcal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider()

                            MacroLine("Protein", food.protein)
                            MacroLine("Carbs", food.carbs)
                            MacroLine("Fat", food.fat)

                            Spacer(Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        editOpen = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Edit")
                                }

                                Button(
                                    onClick = { deleteOpen = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    )
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editOpen && food != null) {
        EditQuantityDialog(
            initialQuantity = food.quantity,
            onDismiss = { editOpen = false },
            onSave = { newQty ->
                viewModel.loadUpdateData(food)
                viewModel.onQuantityChange(newQty)
                viewModel.updateFood()
                navController.popBackStack()
                editOpen = false
            }
        )
    }

    if (deleteOpen && food != null) {
        ConfirmationDialog(
            title = "Delete food",
            message = "Are you sure you want to remove this food from today's consumed ones?",
            onConfirm = {
                viewModel.onSelectedIdChange(food.id)
                viewModel.deleteFood()
                deleteOpen = false
                navController.popBackStack()
            },
            onDismiss = { deleteOpen = false },
        )
    }
}

@Composable
private fun MacroLine(label: String, grams: Float) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text("${grams.pretty1()} g", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EditQuantityDialog(
    initialQuantity: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var text by remember { mutableStateOf(if (initialQuantity == initialQuantity.toInt().toFloat())
        initialQuantity.toInt().toString() else initialQuantity.toString()
    ) }

    val parsed = text.replace(",", ".").toFloatOrNull()
    val isValid = parsed != null && parsed > 0f && parsed <= 5000f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit quantity") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Enter grams. Only quantity can be edited.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Quantity (g)") },
                    supportingText = {
                        if (!isValid && text.isNotBlank()) Text("Please enter a valid number > 0")
                    },
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
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                onClick = { onSave(parsed!!) }
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
