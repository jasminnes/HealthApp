package com.tu.health.ui.screens.insights.nutrition

import com.patrykandpatrick.vico.core.entry.ChartEntry

private data class E(
    override val x: Float,
    override val y: Float
) : ChartEntry {

    override fun withY(y: Float): ChartEntry {
        return copy(y = y)
    }
}

