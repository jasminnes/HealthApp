package com.tu.health.data.remote.request

import com.squareup.moshi.Json


data class OnboardingRequest(
    @Json(name = "height") val height: Float,
    @Json(name = "activity_level") val activityLevel: Int,
    @Json(name = "diet_type") val dietType: Int,
    @Json(name = "body_measurements") val bodyMeasurements: BodyMeasurementRequest,
    @Json(name = "conditions") val conditions: List<Int>,
)

data class ProfileRequest(
    @Json(name = "height")
    val height: Float?,

    @Json(name = "activity_level")
    val activityLevel: Int,

    @Json(name = "diet_type")
    val dietType: Int,

    @Json(name = "conditions")
    val conditions: List<Int>,

    @Json(name = "weight_goal")
    val weightGoal: String
)

data class BodyMeasurementRequest(
    @Json(name = "weight") val weight: Float,
    @Json(name = "neck") val neck: Float,
    @Json(name = "waist") val waist: Float,
)

data class UpdateUserHeightRequest(
    @Json(name = "height") val height: Float
)

data class UpdateUserWeightGoalRequest(
    @Json(name = "weight_goal") val goal: String
)

data class UpdateUserDietTypeRequest(
    @Json(name = "diet_type") val dietType: Int?
)

data class UpdateUserConditionsRequest(
    @Json(name = "conditions") val conditions: List<Int>
)
