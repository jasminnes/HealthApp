package com.tu.health.viewmodels.insights.summary


sealed interface InsightsSummaryEvent {
    data object Load : InsightsSummaryEvent
    data object Refresh : InsightsSummaryEvent
    data class ChangeDays(val days: Int) : InsightsSummaryEvent
    data object ClearError : InsightsSummaryEvent
}
