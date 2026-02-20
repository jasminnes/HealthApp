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
import androidx.compose.ui.graphics.Color


private val DaysOptions = listOf(7, 30, 90, 365)

@Composable
private fun sectionColor(key: String): Color {
    return when (key) {
        "body" -> MaterialTheme.colorScheme.primaryContainer
        "nutrition" -> MaterialTheme.colorScheme.secondary
        "hc" -> MaterialTheme.colorScheme.tertiary
        "scores" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primary
    }
}


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
                            onOpenDetails = { navController.navigate("insights-bodycomposition") }
                        )
                    }
                    item {
                        NutritionSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-nutrition") }
                        )
                    }
                    item {
                        HealthConnectSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-healthconnect") }
                        )
                    }
                    item {
                        ScoresSection(
                            data = state.data!!,
                            onOpenDetails = { navController.navigate("insights-scores") }
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
    val fg = MaterialTheme.colorScheme.onPrimaryContainer

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Your snapshot",
                style = MaterialTheme.typography.titleLarge,
                color = fg
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = rangeText ?: "Showing last $days days",
                style = MaterialTheme.typography.bodyMedium,
                color = fg.copy(alpha = 0.85f)
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
                    label = { Text("Quick view", color = fg) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                        labelColor = fg
                    )
                )
                AssistChip(
                    onClick = { },
                    label = { Text("Trends", color = fg) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                        labelColor = fg
                    )
                )
                AssistChip(
                    onClick = { },
                    label = { Text("Consistency", color = fg) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                        labelColor = fg
                    )
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
        onOpenDetails = onOpenDetails,
        accent = sectionColor("body"),
        ) {
        val latest = data.bodyComposition.summary.latest

        MetricRow(
            items = listOf(
                MetricItem("Weight", latest?.weight?.let { "${trim2(it)} kg" } ?: "—"),
                MetricItem("Waist", latest?.waist?.let { "${trim2(it)} cm" } ?: "—"),
                MetricItem("Body Fat Percentage", latest?.bfp?.let { "${trim2(it)} %" } ?: "—"),
                MetricItem("Lean Body Mass", latest?.lbm?.let { "${trim2(it)} kg" } ?: "—"),
            ),
            accent = sectionColor("body")
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest points",
            points = data.bodyComposition.points.map {
                "${it.date}: W ${it.weight?.let(::trim2) ?: "—"} • " +
                        "Wa ${it.waist?.let(::trim2) ?: "—"} • " +
                        "BFP ${it.bfp?.let(::trim2) ?: "—"} • " +
                        "LBM ${it.lbm?.let(::trim2) ?: "—"}"
            },
            accent = sectionColor("body")
        )
    }
}

@Composable
private fun NutritionSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    val accent = sectionColor("nutrition")

    SectionCard(
        title = "Nutrition",
        subtitle = "Consumed vs plan",
        accent = accent,
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
            ),
            accent = accent
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest days",
            points = data.nutrition.points.map {
                "${it.date}: ${trim2(it.calories)} kcal • P ${trim2(it.protein)} • C ${trim2(it.carbs)}"
            },
            accent = accent
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
        onOpenDetails = onOpenDetails,
        accent = sectionColor("hc"),
        ) {
        val s = data.healthConnect.summary

        MetricRow(
            items = listOf(
                MetricItem("Avg steps", trim2(s.avgSteps ?: 0.0)),
                MetricItem("Avg sleep", "${trim2(s.avgSleepMinutes ?: 0.0)} min"),
                MetricItem("Avg exercise", "${trim2(s.avgExerciseMinutes ?: 0.0)} min"),
                MetricItem("Avg workouts", trim2(s.avgWorkouts ?: 0.0)),
            ),
            accent = sectionColor("hc")

        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest days",
            points = data.healthConnect.points.map {
                "${it.date}: ${it.steps ?: 0} steps • " +
                        "sleep ${it.sleepMin ?: 0}m • "
            },
            accent = sectionColor("hc")
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
        onOpenDetails = onOpenDetails,
        accent = sectionColor("scores"),
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
            ),
            accent = sectionColor("scores")
        )

        Spacer(Modifier.height(10.dp))

        PointsChips(
            title = "Latest scores",
            points = data.scores.points.map {
                "${it.date}: total ${it.total?.let(::trim2) ?: "—"}"
            },
            accent = sectionColor("scores")
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    accent: Color,
    onOpenDetails: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        onClick = onOpenDetails
    ) {
        Row(Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .padding(vertical = 14.dp)
            ) {
                Surface(
                    color = accent,
                    shape = RoundedCornerShape(99.dp),
                    modifier = Modifier.fillMaxSize()
                ) {}
            }

            Column(Modifier.padding(16.dp).weight(1f)) {
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
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = accent
                    )
                }

                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

data class MetricItem(val label: String, val value: String)

@Composable
private fun MetricTile(
    item: MetricItem?,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val container = accent.copy(alpha = 0.12f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = container)
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
private fun MetricRow(items: List<MetricItem>, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    item = rowItems.getOrNull(0),
                    accent = accent,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 64.dp)
                )

                MetricTile(
                    item = rowItems.getOrNull(1),
                    accent = accent,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 64.dp)
                )
            }
        }
    }
}

@Composable
private fun PointsChips(title: String, points: List<String>, accent: Color) {
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
                    onClick = { },
                    label = {
                        Text(
                            text = line,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = accent.copy(alpha = 0.14f)
                    )
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
