package com.tu.health.viewmodels.health


data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,

    val score: HealthScoreUi? = null,
    val recommendations: List<RecommendationUi> = emptyList(),

    val selectedRecommendation: RecommendationUi? = null,

    val errorMessage: String? = null,
)


data class HealthScoreUi(
    val total: Float,
    val activity: Float,
    val recovery: Float,
    val nutrition: Float,
    val bodyComposition: Float,
    val isStale: Boolean,
    val status: String,
    val requestedDate: String,
)

enum class RecommendationStatus { NEW, DISMISSED, COMPLETED }

data class RecommendationUi(
    val id: Int,
    val date: String,
    val category: String,
    val title: String,
    val message: String,
    val reason: String,
    val priority: Int,
    val status: RecommendationStatus,
    val evidence: Map<String, Any>?,
)
