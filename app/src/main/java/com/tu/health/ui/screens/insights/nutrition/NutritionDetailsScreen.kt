package com.tu.health.ui.screens.insights.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.ui.components.ErrorBanner
import com.tu.health.ui.components.LoadingOverlay
import com.tu.health.viewmodels.insights.nutrition.NutritionDetailsEvent
import com.tu.health.viewmodels.insights.nutrition.NutritionDetailsViewModel

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

    val data = state.data
    val hasEnergy = remember(data) {
        if (data == null) false
        else data.points.any { it.rmr != null || it.tdee != null } ||
                (data.summary.latestRmr != null || data.summary.latestTdee != null)
    }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Select days")
                    }

                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
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
                    val overlayAvailableNow = metric == MacroMetric.CALORIES
                    val canToggleOverlay = hasEnergy && overlayAvailableNow

                    val isOn = state.showEnergyOverlay

                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        elevation = CardDefaults.cardElevation(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isOn)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .alpha(if (canToggleOverlay) 1f else 0.55f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.large,
                                    color = if (isOn)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Bolt,
                                        contentDescription = null,
                                        modifier = Modifier.padding(8.dp),
                                        tint = if (isOn) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column {
                                    Text("Energy overlay", style = MaterialTheme.typography.titleSmall)

                                    val subtitle = when {
                                        !hasEnergy -> "No RMR/TDEE data available"
                                        !overlayAvailableNow -> "Available on Calories only"
                                        else -> "Show RMR & TDEE on calories chart"
                                    }

                                    Text(
                                        subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isOn,
                                onCheckedChange = { vm.onEvent(NutritionDetailsEvent.ToggleEnergyOverlay(it)) },
                                enabled = canToggleOverlay,
                                thumbContent = {
                                    Icon(
                                        imageVector = if (isOn) Icons.Filled.Check else Icons.Filled.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                        tint = if (isOn) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                    checkedBorderColor = Color.Transparent,

                                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    uncheckedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),

                                    disabledCheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f),
                                    disabledCheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
                                    disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f),
                                    disabledUncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                )
                            )
                        }
                    }

                    NutritionMacrosChartCard(
                        data = data,
                        metric = metric,
                        onMetricChange = { metric = it },
                        showEnergyOverlay = state.showEnergyOverlay
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