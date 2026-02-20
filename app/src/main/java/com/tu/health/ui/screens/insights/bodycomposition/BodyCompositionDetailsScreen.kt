package com.tu.health.ui.screens.insights.bodycomposition

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
import com.tu.health.viewmodels.insights.bodycomposition.BodyCompositionDetailsEvent
import com.tu.health.viewmodels.insights.bodycomposition.BodyCompositionDetailsViewModel

private val DaysOptions = listOf(30, 90, 180, 365)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyCompositionDetailsScreen(
    navController: NavController,
    vm: BodyCompositionDetailsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }

    var metric by rememberSaveable { mutableStateOf(BodyMetric.COMBINED) }

    LaunchedEffect(Unit) { vm.onEvent(BodyCompositionDetailsEvent.Load) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Body composition", maxLines = 1, overflow = TextOverflow.Ellipsis) },
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

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DaysOptions.forEach { d ->
                            DropdownMenuItem(
                                text = { Text("$d days") },
                                onClick = {
                                    menuOpen = false
                                    vm.onEvent(BodyCompositionDetailsEvent.ChangeDays(d))
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = { vm.onEvent(BodyCompositionDetailsEvent.Refresh) },
                        enabled = !state.isLoading
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.errorMessage?.let { msg ->
                    ErrorBanner(
                        message = msg,
                        onDismiss = { vm.onEvent(BodyCompositionDetailsEvent.ClearError) }
                    )
                }

                val data = state.data
                if (data == null) {
                    ElevatedCard(shape = MaterialTheme.shapes.extraLarge) {
                        Column(Modifier.padding(16.dp)) {
                            Text("No data yet", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Add weight/waist measurements (and metabolic snapshots) to see trends here.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    BodyCompositionChartCard(
                        data = data,
                        metric = metric,
                        onMetricChange = { metric = it }
                    )

                    BodyCompositionSummaryCard(data = data)
                }

                Spacer(Modifier.height(24.dp))
            }

            if (state.isLoading) LoadingOverlay()
        }
    }
}