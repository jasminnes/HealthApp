package com.tu.health.data.healthconnect.dto

data class HeartRateDaySummary(
    val minBpm: Int?,
    val avgBpm: Int?,
    val maxBpm: Int?,
    val latestBpm: Int?
)