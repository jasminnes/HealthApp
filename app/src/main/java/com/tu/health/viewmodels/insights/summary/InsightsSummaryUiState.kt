package com.tu.health.viewmodels.insights.summary

import com.tu.health.data.remote.dto.insights.InsightsSummaryDTO

data class InsightsSummaryUiState(
    val selectedDays: Int = 30,
    val isLoading: Boolean = false,
    val data: InsightsSummaryDTO? = null,
    val errorMessage: String? = null,
    val showStaleDataWhileLoading: Boolean = true
)
