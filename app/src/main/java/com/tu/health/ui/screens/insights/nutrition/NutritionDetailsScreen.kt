package com.tu.health.ui.screens.insights.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.data.remote.dto.insights.NutritionDetailsDTO
import com.tu.health.ui.components.ErrorBanner
import com.tu.health.ui.components.LoadingOverlay
import com.tu.health.viewmodels.insights.nutrition.*

private val DaysOptions = listOf(7, 30, 90, 365)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionDetailsScreen(
    navController: NavController,
    vm: NutritionDetailsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }
    var metric by rememberSaveable { mutableStateOf(MacroMetric.CALORIES) }


    LaunchedEffect(Unit) { vm.onEvent(NutritionDetailsEvent.Load) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Nutrition", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Select days")
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DaysOptions.forEach { d ->
                            DropdownMenuItem(
                                text = { Text("$d days") },
                                onClick = {
                                    menuOpen = false
                                    vm.onEvent(NutritionDetailsEvent.ChangeDays(d))
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = { vm.onEvent(NutritionDetailsEvent.Refresh) },
                        enabled = !state.isLoading
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.errorMessage != null) {
                    ErrorBanner(
                        message = state.errorMessage.orEmpty(),
                        onDismiss = { vm.onEvent(NutritionDetailsEvent.ClearError) }
                    )
                }

                val data = state.data
                if (data == null) {
                    ElevatedCard(shape = MaterialTheme.shapes.extraLarge) {
                        Column(Modifier.padding(16.dp)) {
                            Text("No data yet", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Track some meals to see calories and macro trends here.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    NutritionMacrosChartCard(
                        data = data,
                        metric = metric,
                        onMetricChange = { metric = it }
                    )

                    NutritionSummaryCard(
                        data = data,
                        metric = metric
                    )

                }

                Spacer(Modifier.height(24.dp))
            }

            if (state.isLoading) LoadingOverlay()
        }
    }
}

@Composable
private fun NutritionSummaryCard(
    data: NutritionDetailsDTO,
    metric: MacroMetric
) {
    val avg = when (metric) {
        MacroMetric.CALORIES -> data.summary.avgCalories
        MacroMetric.PROTEIN -> data.summary.avgProteinG
        MacroMetric.CARBS -> data.summary.avgCarbsG
        MacroMetric.FAT -> data.summary.avgFatG
    }

    val target = when (metric) {
        MacroMetric.CALORIES -> data.plan.calories?.toDouble()
        MacroMetric.PROTEIN -> data.plan.proteinG
        MacroMetric.CARBS -> data.plan.carbsG
        MacroMetric.FAT -> data.plan.fatG
    }

    ElevatedCard(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Quick summary", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Avg ${metric.title.lowercase()}: ${formatValue(metric, avg)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Target: ${target?.let { formatValue(metric, it) } ?: "—"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
