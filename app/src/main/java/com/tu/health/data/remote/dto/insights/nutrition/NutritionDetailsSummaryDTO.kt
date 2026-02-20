package com.tu.health.data.remote.dto.insights.nutrition

import com.squareup.moshi.Json

data class NutritionDetailsSummaryDTO(
    @Json(name = "avg_calories") val avgCalories: Double,
    @Json(name = "avg_protein_g") val avgProteinG: Double,
    @Json(name = "avg_carbs_g") val avgCarbsG: Double,
    @Json(name = "avg_fat_g") val avgFatG: Double,
    @Json(name = "latest_rmr") val latestRmr: Double? = null,
    @Json(name = "latest_tdee") val latestTdee: Double? = null,
    @Json(name = "latest_metabolic_date") val latestMetabolicDate: String? = null,
    @Json(name = "avg_rmr") val avgRmr: Double? = null,
    @Json(name = "avg_tdee") val avgTdee: Double? = null
)