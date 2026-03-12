package com.tu.health.ui.screens.health

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.viewmodels.health.HealthUiEvent
import com.tu.health.viewmodels.health.HealthScoreViewModel
import com.tu.health.viewmodels.health.RecommendationStatus
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationDetailsScreen(
    navController: NavController
) {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry("health-score")
    }
    val viewModel: HealthScoreViewModel = hiltViewModel(parentEntry)

    val state by viewModel.uiState.collectAsState()
    val rec = state.selectedRecommendation
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { e ->
            when (e) {
                is HealthUiEvent.ShowMessage ->
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recommendation") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedRecommendation()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        if (rec == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No selected recommendation")
            }
            return@Scaffold
        }

        val statusText = when (rec.status) {
            RecommendationStatus.NEW -> "New"
            RecommendationStatus.DISMISSED -> "Dismissed"
            RecommendationStatus.COMPLETED -> "Completed"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            rec.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(10.dp))
                        AssistChip(onClick = { }, label = { Text(statusText) })
                    }

                    Text(
                        rec.category.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Text(rec.message, style = MaterialTheme.typography.bodyMedium)

                    if (rec.reason.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Why",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            formatKey(rec.reason),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )                    }
                }
            }

            if (!rec.evidence.isNullOrEmpty()) {
                EvidenceCard(rec.evidence)
            }

            if (rec.status == RecommendationStatus.NEW) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.dismissRecommendation(rec.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Dismiss")
                    }

                    Button(
                        onClick = { viewModel.markRecommendationCompleted(rec.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Done, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Complete")
                    }
                }
            } else {
                ElevatedCard(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Status",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            statusText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "This recommendation is $statusText and can’t be edited.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EvidenceCard(evidence: Map<String, Any>) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Evidence",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            evidence.entries.take(10).forEach { (k, v) ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatKey(k),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        formatKey(v.toString()),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun formatKey(key: String): String {
    return key
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }
}
