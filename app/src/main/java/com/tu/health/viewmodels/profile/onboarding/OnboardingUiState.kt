package com.tu.health.viewmodels.profile.onboarding

import com.tu.health.data.remote.dto.ActivityDTO
import com.tu.health.data.remote.dto.ConditionDTO
import com.tu.health.data.remote.dto.DietTypeDTO

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.HEIGHT,
    val isLoading: Boolean = false,

    val height: Float = 170f,
    val weight: Float = 70f,
    val waist: Float = 75f,
    val neck: Float = 34f,
    val weightGoal: String = "Maintain Weight",

    val selectedDietTypeId: Int? = null,
    val selectedActivityLevelId: Int? = null,
    val selectedConditionIds: Set<Int> = emptySet(),

    val allDietTypes: List<DietTypeDTO> = emptyList(),
    val allActivityLevels: List<ActivityDTO> = emptyList(),
    val allConditions: List<ConditionDTO> = emptyList(),
)
