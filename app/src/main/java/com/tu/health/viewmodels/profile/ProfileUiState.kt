package com.tu.health.viewmodels.profile

import com.tu.health.data.remote.dto.ActivityDTO
import com.tu.health.data.remote.dto.ConditionDTO
import com.tu.health.data.remote.dto.DietTypeDTO

data class ProfileUiState(
    val isLoading: Boolean = false,

    val height: Float = 0f,
    val weightGoal: String = "",

    val selectedDietTypeId: Int? = null,
    val selectedActivityLevelId: Int? = null,
    val selectedConditionIds: Set<Int> = emptySet(),

    val allDietTypes: List<DietTypeDTO> = emptyList(),
    val allActivityLevels: List<ActivityDTO> = emptyList(),
    val allConditions: List<ConditionDTO> = emptyList(),
)
