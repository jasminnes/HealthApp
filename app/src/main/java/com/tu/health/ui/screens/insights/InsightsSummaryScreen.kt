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
import kotlin.math.roundToInt


private val DaysOptions = listOf(7, 30, 90, 365)

private enum class SummarySection { BODY, NUTRITION, HC, SCORES }

@Composable
private fun sectionColor(section: SummarySection): Color = when (section) {
    SummarySection.BODY -> MaterialTheme.colorScheme.primaryContainer
    SummarySection.NUTRITION -> MaterialTheme.colorScheme.secondary
    SummarySection.HC -> MaterialTheme.colorScheme.tertiary
    SummarySection.SCORES -> MaterialTheme.colorScheme.tertiaryContainer
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
    val accent = sectionColor(SummarySection.BODY)
    val s = data.bodyComposition

    SectionCard(
        title = "Body composition",
        subtitle = "Averages for selected period",
        onOpenDetails = onOpenDetails,
        accent = accent
    ) {
        MetricRow(
            items = listOf(
                MetricItem("Avg weight", s.avgWeight?.let { "${trim2(it)} kg" } ?: "—"),
                MetricItem("Avg waist", s.avgWaist?.let { "${trim2(it)} cm" } ?: "—"),
                MetricItem("Avg body fat", s.avgBfp?.let { "${trim2(it)} %" } ?: "—"),
                MetricItem("Avg lean mass", s.avgLbm?.let { "${trim2(it)} kg" } ?: "—"),
            ),
            accent = accent
        )
    }
}

@Composable
private fun NutritionSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    val accent = sectionColor(SummarySection.NUTRITION)
    val s = data.nutrition
    val plan = s.plan

    SectionCard(
        title = "Nutrition",
        subtitle = "Averages vs plan",
        accent = accent,
        onOpenDetails = onOpenDetails
    ) {
        MetricRow(
            items = listOf(
                MetricItem("Avg kcal", s.avgCalories?.let(::trim2) ?: "—"),
                MetricItem("Plan kcal", plan?.calories?.let { trim2(it.toDouble()) } ?: "—"),
                MetricItem("Avg protein", s.avgProtein?.let { "${trim2(it)} g" } ?: "—"),
                MetricItem("Avg carbs", s.avgCarbs?.let { "${trim2(it)} g" } ?: "—"),
            ),
            accent = accent
        )
    }
}

@Composable
private fun HealthConnectSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    val accent = sectionColor(SummarySection.HC)
    val s = data.healthConnect

    SectionCard(
        title = "Activity & recovery",
        subtitle = "Averages for selected period",
        onOpenDetails = onOpenDetails,
        accent = accent
    ) {
        MetricRow(
            items = listOf(
                MetricItem("Avg steps", formatIntOrDash(s.avgSteps)),
                MetricItem("Avg sleep", formatMinutesAsHrMinOrDash(s.avgSleepMinutes)),
                MetricItem("Avg exercise", formatMinutesAsHrMinOrDash(s.avgExerciseMinutes)),
                MetricItem("Avg workouts", s.avgWorkouts?.let(::trim2) ?: "—"),
            ),
            accent = accent
        )
    }
}

@Composable
private fun ScoresSection(
    data: InsightsSummaryDTO,
    onOpenDetails: () -> Unit
) {
    val accent = sectionColor(SummarySection.SCORES)
    val avg = data.scores

    SectionCard(
        title = "Health score",
        subtitle = "Average scores for selected period",
        onOpenDetails = onOpenDetails,
        accent = accent
    ) {
        MetricRow(
            items = listOf(
                MetricItem("Avg total", avg.total?.let(::trim2) ?: "—"),
                MetricItem("Avg activity", avg.activity?.let(::trim2) ?: "—"),
                MetricItem("Avg recovery", avg.recovery?.let(::trim2) ?: "—"),
                MetricItem("Avg nutrition", avg.nutrition?.let(::trim2) ?: "—"),
                MetricItem("Avg body comp", avg.bodyComposition?.let(::trim2) ?: "—"),
            ),
            accent = accent
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
    item: MetricItem,
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
                text = item.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.value,
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
                rowItems.forEach { item ->
                    MetricTile(
                        item = item,
                        accent = accent,
                        modifier = Modifier.weight(1f).heightIn(min = 64.dp)
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
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

private fun formatMinutesAsHrMin(minutes: Double): String {
    val total = minutes.roundToInt().coerceAtLeast(0)
    val h = total / 60
    val m = total % 60

    return when {
        h <= 0 -> "${m}m"
        m == 0 -> "${h}h"
        else -> "${h}h ${m}m"
    }
}

private fun formatMinutesAsHrMinOrDash(minutes: Double?): String {
    return minutes?.let(::formatMinutesAsHrMin) ?: "—"
}

private fun formatIntOrDash(value: Double?): String {
    return value?.roundToInt()?.toString() ?: "—"
}