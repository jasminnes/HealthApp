package com.tu.health.ui.screens.health

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.tu.health.ui.components.EmptyRecommendationsCard
import com.tu.health.ui.components.RecFilter
import com.tu.health.ui.components.RecommendationCard
import com.tu.health.ui.components.RecommendationFilters
import com.tu.health.ui.components.RecommendationsHeader
import com.tu.health.ui.components.ScoreRing
import com.tu.health.viewmodels.health.HealthScoreUi
import com.tu.health.viewmodels.health.HealthUiEvent
import com.tu.health.viewmodels.health.HealthScoreViewModel
import com.tu.health.viewmodels.health.RecommendationStatus
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScoreScreen(
    navController: NavController,
    viewModel: HealthScoreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var filter by rememberSaveable { mutableStateOf(RecFilter.NEW) }

    val score = state.score
    val isLoading = state.isLoading

    val all = state.recommendations


    val recs = remember(all, filter) {
        when (filter) {
            RecFilter.NEW -> all.filter { it.status == RecommendationStatus.NEW }
            RecFilter.DISMISSED -> all.filter { it.status == RecommendationStatus.DISMISSED }
            RecFilter.COMPLETED -> all.filter { it.status == RecommendationStatus.COMPLETED }
            RecFilter.ALL -> all
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { e ->
            when (e) {
                is HealthUiEvent.ShowMessage ->
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.syncHealthOncePerDay()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (state.score == null || state.recommendations.isEmpty()) {
                    viewModel.loadHome()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { viewModel.loadHome() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
                .padding(start = 16.dp, top = 5.dp, end = 16.dp)
        ) {
            when {
                isLoading && score == null -> LoadingState()
                score == null -> EmptyState(onRetry = { viewModel.loadHome() })
                else -> HealthHomeContent(
                    score = score,
                    recommendations = recs,

                    filter = filter,
                    onFilterChange = { filter = it },

                    onRecommendationClick = { id ->
                        viewModel.selectRecommendation(id)
                        if (viewModel.uiState.value.selectedRecommendation != null) {
                            navController.navigate("recommendation-details")
                        }
                    }
                )
            }

            if (isLoading && score != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthHomeContent(
    score: HealthScoreUi,
    recommendations: List<com.tu.health.viewmodels.health.RecommendationUi>,
    filter: RecFilter,
    onFilterChange: (RecFilter) -> Unit,

    onRecommendationClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        HealthScoreCard(score = score)

        RecommendationsHeader(
            count = recommendations.size,
            isLoading = false
        )

        RecommendationFilters(
            selected = filter,
            onSelect = onFilterChange
        )

        if (recommendations.isEmpty()) {
            EmptyRecommendationsCard()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                recommendations.forEach { rec ->
                    RecommendationCard(
                        rec = rec,
                        onClick = { onRecommendationClick(rec.id) }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun HealthScoreCard(score: HealthScoreUi) {
    ElevatedCard(
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Health Score",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (score.isStale) AssistChip(onClick = {}, label = { Text("Stale") })
            }

            val total = score.total.coerceIn(0f, 100f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScoreRing(value = total, modifier = Modifier.size(112.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScoreBreakdownRow("Recovery", score.recovery)
                    ScoreBreakdownRow("Activity", score.activity)
                    ScoreBreakdownRow("Nutrition", score.nutrition)
                    ScoreBreakdownRow("Body", score.bodyComposition)
                }
            }

            val insight = scoreSummaryText(
                total = total,
                recovery = score.recovery,
                activity = score.activity,
                nutrition = score.nutrition,
                body = score.bodyComposition
            )

            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "Insight",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(insight, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ScoreBreakdownRow(label: String, value: Float) {
    val v = value.coerceIn(0f, 100f)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                "${v.roundToInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { v / 100f },
            modifier = Modifier.fillMaxWidth().height(10.dp)
        )
    }
}

private fun scoreSummaryText(
    total: Float,
    recovery: Float,
    activity: Float,
    nutrition: Float,
    body: Float
): String {
    val pillars = listOf(
        "recovery" to recovery,
        "activity" to activity,
        "nutrition" to nutrition,
        "body composition" to body
    )
    val weakest = pillars.minByOrNull { it.second }?.first ?: "recovery"

    return when {
        total >= 85 -> "Strong overall. Keep consistency and protect $weakest if it starts dipping."
        total >= 70 -> "Good base. Improve $weakest for the fastest gains."
        total >= 50 -> "Decent foundation. Focus on $weakest and keep daily habits simple."
        else -> "Start small: prioritize $weakest and build consistency day by day."
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Loading health score…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No data yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text("Try again.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
