package com.tu.health.ui.screens.nutrition

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt
import com.tu.health.data.remote.dto.TrackedFoodDTO
import com.tu.health.viewmodels.nutrition.MacrosViewModel
import com.tu.health.viewmodels.nutrition.MacrosUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacrosScreen(
    navController: NavController,
    viewModel: MacrosViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.syncHealthOncePerDay()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is MacrosUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (state.macroPlan == null) viewModel.getMacroPlan()
                viewModel.getTodayFood()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val dailyMacros = state.dailySummary
    val trackedFoods = state.trackedFoods
    val isLoading = state.isLoading

    // --- everything below stays the same UI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macros") },
                actions = {
                    IconButton(onClick = { navController.navigate("food-search") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add food")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
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
                isLoading && dailyMacros == null -> LoadingState()
                dailyMacros == null -> EmptyState(onRetry = {
                    viewModel.getMacroPlan()
                    viewModel.getTodayFood()
                })
                else -> DailyMacrosContent(
                    caloriesConsumed = dailyMacros.caloriesConsumed,
                    caloriesTarget = dailyMacros.caloriesTarget,
                    proteinConsumed = dailyMacros.proteinConsumed,
                    proteinTarget = dailyMacros.proteinTarget,
                    carbsConsumed = dailyMacros.carbsConsumed,
                    carbsTarget = dailyMacros.carbsTarget,
                    fatConsumed = dailyMacros.fatConsumed,
                    fatTarget = dailyMacros.fatTarget,
                    trackedFoods = trackedFoods,
                    viewModel = viewModel,
                    navController = navController
                )
            }

            if (isLoading && dailyMacros != null) {
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
private fun DailyMacrosContent(
    caloriesConsumed: Float,
    caloriesTarget: Float,
    proteinConsumed: Float,
    proteinTarget: Float,
    carbsConsumed: Float,
    carbsTarget: Float,
    fatConsumed: Float,
    fatTarget: Float,
    trackedFoods: List<TrackedFoodDTO>,
    viewModel: MacrosViewModel,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

    CaloriesCard(
            consumed = caloriesConsumed,
            target = caloriesTarget,
        )

        ElevatedCard(
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Macros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                MacroRow(
                    label = "Protein",
                    consumed = proteinConsumed,
                    target = proteinTarget
                )
                MacroRow(
                    label = "Carbs",
                    consumed = carbsConsumed,
                    target = carbsTarget
                )
                MacroRow(
                    label = "Fat",
                    consumed = fatConsumed,
                    target = fatTarget
                )
            }
        }

        TipsCard(
            caloriesConsumed = caloriesConsumed,
            caloriesTarget = caloriesTarget,
            proteinConsumed = proteinConsumed,
            proteinTarget = proteinTarget,
            carbsConsumed = carbsConsumed,
            carbsTarget = carbsTarget
        )

        TrackedFoodsCard(foods = trackedFoods, viewModel = viewModel, navController = navController)
    }
}

@Composable
private fun CaloriesCard(
    consumed: Float,
    target: Float,
) {
    val progressRaw = if (target <= 0f) 0f else (consumed / target)
    val progress = progressRaw.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "calProgress"
    )

    ElevatedCard(
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(88.dp),
                    strokeWidth = 10.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${consumed.roundToInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Calories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))

                val targetText = if (target > 0f)
                    "${target.roundToInt()} kcal target" else "No target set"
                Text(
                    text = "$targetText • ${((progressRaw * 100f)
                        .coerceAtLeast(0f))
                        .roundToInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AssistChip(
                        onClick = { },
                        label = {
                            val left = (target - consumed).roundToInt()
                            Text(
                                if (target <= 0f) "Set target"
                            else if (left >= 0) "$left kcal left"
                            else "${-left} over"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroRow(
    label: String,
    consumed: Float,
    target: Float
) {
    val progressRaw = if (target <= 0f) 0f else (consumed / target)
    val progress = progressRaw.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = progress,
        label = "${label}Progress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            val right = if (target > 0f) {
                "${consumed.pretty1()} / ${target.pretty1()} g"
            } else {
                "${consumed.pretty1()} g"
            }
            Text(
                text = right,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun TipsCard(
    caloriesConsumed: Float,
    caloriesTarget: Float,
    proteinConsumed: Float,
    proteinTarget: Float,
    carbsConsumed: Float,
    carbsTarget: Float
) {
    val msg = when {
        proteinConsumed <= proteinTarget / 2 -> "You need to eat more protein today."
        caloriesConsumed <= caloriesTarget -> "You’re on track. Keep building consistency."
        carbsConsumed > carbsTarget -> "You're above carbs target. Try being more active today."
        else -> "You’re above target today. If you want: lighter dinner + extra steps can balance it."
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "Insight",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TrackedFoodsCard(
    foods: List<TrackedFoodDTO>,
    modifier: Modifier = Modifier,
    viewModel: MacrosViewModel,
    navController: NavController
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth().padding(bottom = 15.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "Today foods",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (foods.isEmpty()) {
                Text(
                    text = "No foods logged today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    foods.forEach { f ->
                        TrackedFoodRow(
                            food = f,
                            onClick = {
                                viewModel.onSelectedIdChange(f.id)
                                navController.navigate("food-details/${f.id}")
                            }
                        )

                    }
                }
            }
        }
    }
}

@Composable
private fun TrackedFoodRow(
    food: TrackedFoodDTO,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${food.quantity.pretty1()} g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${food.calories.roundToInt()} kcal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        Text("Loading daily macros…", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Text(
            "Pull to refresh or try again.",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

fun Float.pretty1(): String {
    val rounded = (this * 10f).roundToInt() / 10f
    return if (rounded == rounded.roundToInt().toFloat()) {
        rounded.roundToInt().toString()
    } else {
        rounded.toString()
    }
}
