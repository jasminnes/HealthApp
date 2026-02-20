package com.tu.health.ui.screens.insights.bodycomposition

import android.annotation.SuppressLint
import com.tu.health.data.remote.dto.insights.bodycomposition.BodyCompositionDetailsDTO
import kotlin.math.ceil
import kotlin.math.roundToInt

fun bodyUnitWeight(data: BodyCompositionDetailsDTO): String =
    data.chartHint?.units?.weight ?: "kg"

fun bodyUnitWaist(data: BodyCompositionDetailsDTO): String =
    data.chartHint?.units?.waist ?: "cm"

fun bodyUnitLbm(data: BodyCompositionDetailsDTO): String =
    data.chartHint?.units?.lbm ?: "kg"

fun bodyUnitBfp(data: BodyCompositionDetailsDTO): String =
    data.chartHint?.units?.bfp ?: "%"

fun bodyScaleBfpToKgAxis(bfpPercent: Double, maxKgAxis: Float): Float {
    val clamped = bfpPercent.coerceIn(0.0, 40.0)
    return ((clamped / 40.0) * maxKgAxis).toFloat()
}

fun bodyNiceMaxKg(rawMax: Float): Float {
    val base = 2.5f
    val padded = rawMax * 1.08f
    return (ceil(padded / base) * base).coerceAtLeast(base)
}

fun bodyNiceMaxWaist(rawMax: Float): Float {
    val base = 2.0f
    val padded = rawMax * 1.08f
    return (ceil(padded / base) * base).coerceAtLeast(base)
}

@SuppressLint("DefaultLocale")
fun bodyFormatKgAxis(v: Float, unit: String): String {
    return "${v.roundToInt()}$unit"
}

@SuppressLint("DefaultLocale")
fun bodyFmtValue(v: Double, unit: String): String =
    String.format("%.1f %s", v, unit)

@SuppressLint("DefaultLocale")
fun bodyFmtPercent(v: Double, unit: String): String =
    String.format("%.1f%s", v, unit)

@SuppressLint("DefaultLocale")
fun bodyFmtDelta(v: Double, unit: String): String {
    val sign = if (v > 0) "+" else ""
    return String.format("%s%.1f %s", sign, v, unit)
}

@SuppressLint("DefaultLocale")
fun bodyFmtDeltaPercent(v: Double, unit: String): String {
    val sign = if (v > 0) "+" else ""
    return String.format("%s%.1f%s", sign, v, unit)
}