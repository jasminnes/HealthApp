package com.tu.health.data.remote.dto.insights

data class BodyCompositionPointDTO (
    val date: String,
    val weight: Double?,
    val waist: Double?,
    val bfp: Double?,
    val lbm: Double?
)