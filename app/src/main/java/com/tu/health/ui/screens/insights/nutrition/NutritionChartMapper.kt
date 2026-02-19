package com.tu.health.ui.screens.insights.nutrition

import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.tu.health.data.remote.dto.insights.NutritionPointDTO

private data class E(
    override val x: Float,
    override val y: Float
) : ChartEntry {

    override fun withY(y: Float): ChartEntry {
        return copy(y = y)
    }
}

fun buildCaloriesSeries(
    points: List<NutritionPointDTO>
): Pair<List<String>, List<FloatEntry>> {

    val labels = points.map { it.date }

    val entries = points.mapIndexed { index, point ->
        FloatEntry(
            x = index.toFloat(),
            y = point.calories.toFloat()
        )
    }

    return labels to entries
}

fun buildPlanSeries(
    points: List<NutritionPointDTO>,
    planCalories: Int
): List<FloatEntry> {
    return points.mapIndexed { index, _ ->
        FloatEntry(
            x = index.toFloat(),
            y = planCalories.toFloat()
        )
    }
}


