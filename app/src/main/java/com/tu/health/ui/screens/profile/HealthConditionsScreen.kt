package com.tu.health.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.data.remote.dto.ConditionDTO
import com.tu.health.viewmodels.profile.ProfileUiEvent
import com.tu.health.viewmodels.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConditionsScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()

    val isLoading = uiState.isLoading
    val allConditions = uiState.allConditions
    val selectedIds = uiState.selectedConditionIds

    var initialSelected by remember { mutableStateOf<Set<Int>>(emptySet()) }

    LaunchedEffect(Unit) {
        viewModel.loadHealthConditions()
    }

    LaunchedEffect(allConditions) {
        if (allConditions.isNotEmpty() && initialSelected.isEmpty()) {
            initialSelected = selectedIds
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val hasChanges by remember(selectedIds, initialSelected) {
        derivedStateOf { initialSelected.isNotEmpty() && selectedIds != initialSelected }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Health conditions") },
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
                ),
                actions = {
                    TextButton(
                        enabled = hasChanges && !isLoading,
                        onClick = {
                            viewModel.updateUserConditions { success ->
                                if (success) {
                                    initialSelected = selectedIds
                                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Failed to save", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Save")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && allConditions.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Loading conditions…",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                allConditions.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No conditions available.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { viewModel.loadHealthConditions() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Select any conditions that apply to you.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(12.dp))

                        ElevatedCard(shape = MaterialTheme.shapes.extraLarge) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                items(allConditions, key = { it.id }) { condition ->
                                    ConditionRow(
                                        condition = condition,
                                        checked = selectedIds.contains(condition.id),
                                        onToggle = { viewModel.toggleCondition(condition.id) }
                                    )
                                }
                            }
                        }

                        if (isLoading) {
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConditionRow(
    condition: ConditionDTO,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = condition.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
