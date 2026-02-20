package com.tu.health.ui.screens.insights.nutrition

import android.annotation.SuppressLint
import com.tu.health.data.remote.dto.insights.nutrition.MacrosPlanDTO
import com.tu.health.data.remote.dto.insights.nutrition.NutritionPointDTO
import kotlin.math.ceil
import kotlin.math.roundToInt

fun nutritionMetricValue(metric: MacroMetric, p: NutritionPointDTO): Double {
    return when (metric) {
        MacroMetric.CALORIES -> p.calories
        MacroMetric.PROTEIN -> p.protein
        MacroMetric.CARBS -> p.carbs
        MacroMetric.FAT -> p.fat
    }
}

fun nutritionTargetValue(metric: MacroMetric, plan: MacrosPlanDTO): Double? {
    return when (metric) {
        MacroMetric.CALORIES -> plan.calories?.toDouble()
        MacroMetric.PROTEIN -> plan.protein
        MacroMetric.CARBS -> plan.carbs
        MacroMetric.FAT -> plan.fat
    }
}

fun nutritionNiceMaxY(metric: MacroMetric, rawMax: Float): Float {
    val base = when (metric) {
        MacroMetric.CALORIES -> 250f
        else -> 10f
    }
    val padded = rawMax * 1.10f
    return (ceil(padded / base) * base).coerceAtLeast(base)
}

@SuppressLint("DefaultLocale")
fun nutritionFormatY(metric: MacroMetric, v: Float): String {
    return if (metric == MacroMetric.CALORIES) {
        if (v >= 1000f) String.format("%.1fk", v / 1000f) else v.roundToInt().toString()
    } else {
        v.roundToInt().toString()
    }
}

fun nutritionFormatValue(metric: MacroMetric, v: Double): String {
    val n = v.roundToInt()
    return if (metric == MacroMetric.CALORIES) "$n kcal" else "$n g"
}
