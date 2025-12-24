package com.tu.health.ui.screens.profile.goals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.viewmodels.profile.GoalsStep
import com.tu.health.viewmodels.profile.WeightGoalViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightGoalsScreen(
    navController: NavController,
    viewModel: WeightGoalViewModel = hiltViewModel()
) {
    val goals by viewModel.goals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var mode by remember { mutableStateOf(GoalsStep.LIST) }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        mode == GoalsStep.LIST -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Goals") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                viewModel.onSelectedIdChange(0)
                                viewModel.onNameChange("")
                                viewModel.onGoalWeightChange(0f)
                                viewModel.onStartingWeightChange(0f)
                                viewModel.onFinalDateChange("")
                                mode = GoalsStep.EDIT
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add goal",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        windowInsets = WindowInsets(0)
                    )

                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(goals) { goal ->
                        GoalItem(
                            goalName = goal.name,
                            finalDate = goal.finalDate,
                            onClick = {
                                viewModel.onSelectedIdChange(goal.id)
                                viewModel.onNameChange(goal.name)
                                viewModel.onGoalWeightChange(goal.goalWeight)
                                viewModel.onFinalDateChange(goal.finalDate)
                                viewModel.onStartingWeightChange(goal.startingWeight)
                                mode = GoalsStep.DETAILS
                            }
                        )
                    }
                }
            }
        }

        mode == GoalsStep.DETAILS -> {
            WeightGoalDetails(
                viewModel = viewModel,
                onEdit = { mode = GoalsStep.EDIT },
                onDelete = {
                    viewModel.deleteGoalWeight { success, _ ->
                        if (success) {
                            viewModel.refreshGoals()
                            mode = GoalsStep.LIST
                        }
                    }
                },
                onBack = { mode = GoalsStep.LIST }
            )
        }

        mode == GoalsStep.EDIT -> {
            WeightGoalEdit(
                viewModel = viewModel,
                onSave = {
                    viewModel.refreshGoals()
                    mode = GoalsStep.LIST
                },
                onCancel = { mode = GoalsStep.LIST }
            )
        }
    }
}

@Composable
fun GoalItem(goalName: String, finalDate: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = goalName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Final date: ${formatDate(finalDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatDate(date: String): String {
    return try {
        LocalDate.parse(date.take(10))
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: Exception) {
        date
    }
}
