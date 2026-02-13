package com.tu.health.data.remote.dto

data class RecommendationsDTO (
    val id: Int,
    val date: String,
    val category: String,
    val title: String,
    val message: String,
    val reason: String,
    val priority: Int,
    val status: String,
    val evidence: Map<String, Any>?,
)
