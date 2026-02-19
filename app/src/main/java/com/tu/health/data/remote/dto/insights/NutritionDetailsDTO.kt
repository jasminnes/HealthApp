package com.tu.health.data.remote.dto.insights

import com.squareup.moshi.Json


data class NutritionDetailsDTO(
    val range: NutritionRangeDTO,
    val plan: NutritionPlanDTO,
    val summary: NutritionDetailsSummaryDTO,
    val points: List<NutritionDetailsPointDTO>
)


data class NutritionRangeDTO(
    val days: Int,
    @Json(name = "start_date") val startDate: String,
    @Json(name = "end_date") val endDate: String,
    val bucket: String
)


data class NutritionPlanDTO(
    val calories: Int?,
    @Json(name = "protein_g") val proteinG: Double?,
    @Json(name = "carbs_g") val carbsG: Double?,
    @Json(name = "fat_g") val fatG: Double?,
    @Json(name = "updated_at") val updatedAt: String?
)


data class NutritionDetailsSummaryDTO(
    @Json(name = "avg_calories") val avgCalories: Double,
    @Json(name = "avg_protein_g") val avgProteinG: Double,
    @Json(name = "avg_carbs_g") val avgCarbsG: Double,
    @Json(name = "avg_fat_g") val avgFatG: Double
)


data class NutritionDetailsPointDTO(
    val date: String,
    val calories: Double,
    @Json(name = "protein_g") val proteinG: Double,
    @Json(name = "carbs_g") val carbsG: Double,
    @Json(name = "fat_g") val fatG: Double
)
