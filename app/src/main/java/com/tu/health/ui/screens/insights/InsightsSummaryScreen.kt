package com.tu.health.ui.screens.insights

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.data.remote.dto.insights.InsightsSummaryDTO
import com.tu.health.ui.components.ErrorBanner
import com.tu.health.ui.components.LoadingOverlay
import com.tu.health.viewmodels.insights.summary.InsightsSummaryEvent
import com.tu.health.viewmodels.insights.summary.InsightsSummaryViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets


private val DaysOptions = listOf(7, 30, 90, 365)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsSummaryScreen(
    navController: NavController,
    vm: InsightsSummaryViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { vm.onEvent(InsightsSummaryEvent.Load) }

    Scaffold(
        topBar = {
            InsightsTopBar(
                selectedDays = state.selectedDays,
                isLoading = state.isLoading,
                onDaysSelected = { vm.onEvent(InsightsSummaryEvent.ChangeDays(it)) },
                onRefresh = { vm.onEvent(InsightsSummaryEvent.Refresh) }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = state.errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ErrorBanner(
                            message = state.errorMessage.orEmpty(),
                            onDismiss = { vm.onEvent(InsightsSummaryEvent.ClearError) }
                        )
                    }
                }

                item {
                    HeaderSummary(
                        days = state.selectedDays,
                        rangeText = state.data?.range?.let { "${it.startDate} → ${it.endDate}" }
                    )
                }

                if (state.data == null) {
                    item {
                        EmptyStateCard(
                            isLoading = state.isLoading,
                            onRetry = { vm.onEvent(InsightsSummaryEvent.Refresh) }
                        )
                    }
                } else {
                    item {
                        BodyCompositionSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-body_composition") }
                        )
                    }
                    item {
                        NutritionSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-nutrition") }
                        )
                    }
                    item {
                        MetabolicSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-metabolic") }
                        )
                    }
                    item {
                        HealthConnectSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-health_connect") }
                        )
                    }
                    item {
                        ScoresSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-scores") }
                        )
                    }
                    item {
                        RecommendationsSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-recommendations") }
                        )
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }

            if (state.isLoading) LoadingOverlay()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InsightsTopBar(
    selectedDays: Int,
    isLoading: Boolean,
    onDaysSelected: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "Insights",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        actions = {
            IconButton(onClick = { menuOpen = true }) {
                Icon(Icons.Filled.CalendarToday, contentDescription = "Select range")
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
                            onDaysSelected(d)
                        },
                        trailingIcon = {
                            if (d == selectedDays) {
                                Icon(Icons.Filled.Info, contentDescription = null)
                            }
                        }
                    )
                }
            }

            IconButton(onClick = onRefresh, enabled = !isLoading) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
            }
        }
    )

}


@Composable
private fun HeaderSummary(days: Int, rangeText: String?) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Your snapshot",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = rangeText ?: "Showing last $days days",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text("Quick view") }
                )
                AssistChip(
                    onClick = { },
                    label = { Text("Trends") }
                )
                AssistChip(
                    onClick = { },
                    label = { Text("Consistency") }
                )
            }
        }
    }
}


@Composable
private fun BodyCompositionSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    SectionCard(
        title = "Body composition",
        subtitle = "RMR (Resting Metabolic Rate) • TDEE (Total Daily Energy Expenditure)",
        onOpenDetails = onOpenDetails
    ) {
        val latest = data.bodyComposition.summary.latest

        MetricRow(
            items = listOf(
                MetricItem("Weight", latest?.weight?.let { "${trim2(it)} kg" } ?: "—"),
                MetricItem("Waist", latest?.waist?.let { "${trim2(it)} cm" } ?: "—"),
                MetricItem("Body Fat Percentage", latest?.bfp?.let { "${trim2(it)} %" } ?: "—"),
                MetricItem("Lean Body Mass", latest?.lbm?.let { "${trim2(it)} kg" } ?: "—"),
            )
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest points",
            points = data.bodyComposition.points.map {
                "${it.date}: W ${it.weight?.let(::trim2) ?: "—"} • " +
                        "Wa ${it.waist?.let(::trim2) ?: "—"} • " +
                        "BFP ${it.bfp?.let(::trim2) ?: "—"} • " +
                        "LBM ${it.lbm?.let(::trim2) ?: "—"}"
            }
        )
    }
}

@Composable
private fun NutritionSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    SectionCard(
        title = "Nutrition",
        subtitle = "Consumed vs plan",
        onOpenDetails = onOpenDetails
    ) {
        val s = data.nutrition.summary
        val plan = s.plan

        MetricRow(
            items = listOf(
                MetricItem("Avg kcal", trim2(s.avgCalories ?: 0.0)),
                MetricItem("Plan kcal", plan?.calories?.toString() ?: "—"),
                MetricItem("Avg protein", "${trim2(s.avgProtein ?: 0.0)} g"),
                MetricItem("Avg carbs", "${trim2(s.avgCarbs ?: 0.0)} g"),
            )
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest days",
            points = data.nutrition.points.map {
                "${it.date}: ${trim2(it.calories)} kcal • " +
                        "P ${trim2(it.protein)} • C ${trim2(it.carbs)} • F ${trim2(it.fat)}"
            }
        )
    }
}

@Composable
private fun MetabolicSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    SectionCard(
        title = "Metabolic",
        subtitle = "RMR • TDEE • Activity level",
        onOpenDetails = onOpenDetails
    ) {
        val s = data.metabolic.summary

        MetricRow(
            items = listOf(
                MetricItem("RMR", s.latestRmr?.let(::trim2) ?: "—"),
                MetricItem("TDEE", s.latestTdee?.let(::trim2) ?: "—"),
                MetricItem("Activity Level", s.currentActivityLevel ?: "—"),
                MetricItem("Last change", s.lastChange?.let { "${it.from}→${it.to}" } ?: "—"),
            )
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest snapshots",
            points = data.metabolic.points.map {
                "${it.date}: RMR ${it.rmr?.let(::trim2) ?: "—"} • " +
                        "TDEE ${it.tdee?.let(::trim2) ?: "—"} • " +
                        (it.activityLevel ?: "—")
            }
        )
    }
}

@Composable
private fun HealthConnectSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    SectionCard(
        title = "Activity & recovery",
        subtitle = "Steps • Sleep • Workouts",
        onOpenDetails = onOpenDetails
    ) {
        val s = data.healthConnect.summary

        MetricRow(
            items = listOf(
                MetricItem("Avg steps", trim2(s.avgSteps ?: 0.0)),
                MetricItem("Avg sleep", "${trim2(s.avgSleepMinutes ?: 0.0)} min"),
                MetricItem("Avg exercise", "${trim2(s.avgExerciseMinutes ?: 0.0)} min"),
                MetricItem("Avg workouts", trim2(s.avgWorkouts ?: 0.0)),
            )
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest days",
            points = data.healthConnect.points.map {
                "${it.date}: ${it.steps ?: 0} steps • " +
                        "sleep ${it.sleepMin ?: 0}m • "
            }
        )
    }
}

@Composable
private fun ScoresSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    SectionCard(
        title = "Health score",
        subtitle = "Total + subscores",
        onOpenDetails = onOpenDetails
    ) {
        val latest = data.scores.summary.latest

        MetricRow(
            items = listOf(
                MetricItem("Latest", latest?.total?.let(::trim2) ?: "—"),
                MetricItem("Avg", trim2(data.scores.summary.avgTotal ?: 0.0)),
                MetricItem("Activity", latest?.activity?.let(::trim2) ?: "—"),
                MetricItem("Nutrition", latest?.nutrition?.let(::trim2) ?: "—"),
                MetricItem("Body Composition", latest?.bodyComposition?.let(::trim2) ?: "—"),
                MetricItem("Recovery", latest?.recovery?.let(::trim2) ?: "—"),
            )
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest scores",
            points = data.scores.points.map {
                "${it.date}: total ${it.total?.let(::trim2) ?: "—"}"
            }
        )
    }
}

@Composable
private fun RecommendationsSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    SectionCard(
        title = "Recommendations",
        subtitle = "Completion rate",
        onOpenDetails = onOpenDetails
    ) {
        val s = data.recommendations.summary

        MetricRow(
            items = listOf(
                MetricItem("Completed", s.completed.toString()),
                MetricItem("New", s.new.toString()),
                MetricItem("Dismissed", s.dismissed.toString()),
                MetricItem("Rate", "${trim2(s.completionRate * 100)}%"),
            )
        )

        if (data.recommendations.points.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            PointsChips(
                title = "Latest periods",
                points = data.recommendations.points.map {
                    "${it.periodStart}: ${trim2(it.completionRate * 100)}% " +
                            "(${it.completedCount} done / ${it.newCount} new)"
                }
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    onOpenDetails: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        onClick = onOpenDetails
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

data class MetricItem(val label: String, val value: String)

@Composable
private fun MetricRow(items: List<MetricItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(rowItems.getOrNull(0))
                MetricTile(rowItems.getOrNull(1))

                // keep grid aligned if odd count
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
private fun RowScope.MetricTile(item: MetricItem?) {
    ElevatedCard(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = item?.label ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item?.value ?: "—",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PointsChips(title: String, points: List<String>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            points.takeLast(4).forEach { line ->
                AssistChip(
                    onClick = { /* later: open details at that date */ },
                    label = {
                        Text(
                            text = line,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(isLoading: Boolean, onRetry: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("No data yet", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Start logging nutrition, measurements, and syncing Health Connect to see your insights here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onRetry, enabled = !isLoading) { Text("Retry") }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun trim2(v: Double): String {
    val s = String.format("%.2f", v)
    return s.trimEnd('0').trimEnd('.')
}
