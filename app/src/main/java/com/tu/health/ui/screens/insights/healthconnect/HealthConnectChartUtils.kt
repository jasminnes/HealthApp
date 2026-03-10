package com.tu.health.ui.screens.insights.healthconnect

import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectDetailsDTO
import com.tu.health.data.remote.dto.insights.healthconnect.HealthConnectPointDTO
import kotlin.math.ceil
import kotlin.math.roundToInt

fun hcMetricValue(metric: HealthConnectMetric, p: HealthConnectPointDTO): Double {
    return when (metric) {
        HealthConnectMetric.STEPS -> (p.steps ?: 0).toDouble()
        HealthConnectMetric.SLEEP_MIN -> (p.sleepMin ?: 0).toDouble()
        HealthConnectMetric.EXERCISE_MIN -> (p.exerciseMinutes ?: 0).toDouble()
        HealthConnectMetric.ACTIVE_KCAL -> (p.activeKcal ?: 0.0)
        HealthConnectMetric.WORKOUTS -> (p.workouts ?: 0).toDouble()
    }
}

fun hcAvgValue(metric: HealthConnectMetric, data: HealthConnectDetailsDTO): Double {
    val a = data.summary.avg
    return when (metric) {
        HealthConnectMetric.STEPS -> a.steps ?: 0.0
        HealthConnectMetric.SLEEP_MIN -> a.sleepMin ?: 0.0
        HealthConnectMetric.EXERCISE_MIN -> a.exerciseMin ?: 0.0
        HealthConnectMetric.ACTIVE_KCAL -> a.activeKcal ?: 0.0
        HealthConnectMetric.WORKOUTS -> a.workouts ?: 0.0
    }
}

fun hcNiceMaxY(metric: HealthConnectMetric, rawMax: Float): Float {
    val base = when (metric) {
        HealthConnectMetric.STEPS -> 2000f
        HealthConnectMetric.SLEEP_MIN -> 60f
        HealthConnectMetric.EXERCISE_MIN -> 20f
        HealthConnectMetric.ACTIVE_KCAL -> 100f
        HealthConnectMetric.WORKOUTS -> 1f
    }
    val padded = rawMax * 1.12f
    return (ceil(padded / base) * base).coerceAtLeast(base)
}

fun computeLabelStep(size: Int): Int {
    if (size <= 8) return 1
    val desired = 7
    return ceil(size / desired.toFloat()).roundToInt().coerceAtLeast(1)
}

fun hcFormatY(metric: HealthConnectMetric, v: Float): String {
    return when (metric) {
        HealthConnectMetric.STEPS -> {
            if (v >= 10_000f) "${(v / 1000f).roundToInt()}k"
            else v.roundToInt().toString()
        }

        HealthConnectMetric.SLEEP_MIN -> {
            val total = v.roundToInt()
            val h = total / 60
            "${h}h"
        }

        else -> v.roundToInt().toString()
    }
}

fun hcFormatValue(metric: HealthConnectMetric, v: Double): String {
    return when (metric) {
        HealthConnectMetric.STEPS -> "${v.roundToInt()} steps"

        HealthConnectMetric.SLEEP_MIN -> {
            val totalMin = v.roundToInt()
            val hours = totalMin / 60
            val minutes = totalMin % 60
            "${hours}h ${minutes}m"
        }

        HealthConnectMetric.EXERCISE_MIN -> "${v.roundToInt()} min"
        HealthConnectMetric.ACTIVE_KCAL -> "${v.roundToInt()} kcal"
        HealthConnectMetric.WORKOUTS -> "${v.roundToInt()} sessions"
    }
}

