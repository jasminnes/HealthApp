package com.tu.health.ui.screens.insights.healthconnect

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
import com.tu.health.ui.components.ErrorBanner
import com.tu.health.ui.components.LoadingOverlay
import com.tu.health.viewmodels.insights.healthconnect.HealthConnectDetailsEvent
import com.tu.health.viewmodels.insights.healthconnect.HealthConnectDetailsViewModel

private val DaysOptions = listOf(7, 30, 90, 365)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectDetailsScreen(
    navController: NavController,
    vm: HealthConnectDetailsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }
    var metric by rememberSaveable { mutableStateOf(HealthConnectMetric.STEPS) }

    LaunchedEffect(Unit) { vm.onEvent(HealthConnectDetailsEvent.Load) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Activity & Recovery", maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
                                    vm.onEvent(HealthConnectDetailsEvent.ChangeDays(d))
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = { vm.onEvent(HealthConnectDetailsEvent.Refresh) },
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
                        onDismiss = { vm.onEvent(HealthConnectDetailsEvent.ClearError) }
                    )
                }

                val hc = state.data

                if (hc != null) {
                    HealthConnectChartCard(
                        data = hc,
                        metric = metric,
                        onMetricChange = { metric = it }
                    )
                    HealthConnectSummaryCard(data = hc, metric = metric)

                    HealthConnectActivityLevelCard(state.data?.summary?.activityLevel)
                }

                Spacer(Modifier.height(24.dp))
            }

            if (state.isLoading) LoadingOverlay()
        }
    }
}