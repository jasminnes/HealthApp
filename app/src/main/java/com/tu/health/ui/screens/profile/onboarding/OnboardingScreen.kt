package com.tu.health.ui.screens.profile.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.viewmodels.profile.onboarding.OnboardingStep
import com.tu.health.viewmodels.profile.onboarding.OnboardingViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val step = uiState.step
    val height = uiState.height
    val activityLevelId = uiState.selectedActivityLevelId
    val dietTypeId = uiState.selectedDietTypeId
    val weight = uiState.weight
    val waist = uiState.waist
    val neck = uiState.neck
    val weightGoal = uiState.weightGoal
    val isLoading = uiState.isLoading

    val activityLevels = uiState.allActivityLevels
    val dietTypes = uiState.allDietTypes
    val conditions = uiState.allConditions
    val selectedConditions = uiState.selectedConditionIds

    LaunchedEffect(step) {
        if (step == OnboardingStep.COMPLETE) {
            navController.navigate("health-connect-setup") {
                popUpTo("onboarding") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        LinearProgressIndicator(
            progress = {
                when (step) {
                    OnboardingStep.HEIGHT -> 1f / 8f
                    OnboardingStep.ACTIVITY_LEVEL -> 2f / 8f
                    OnboardingStep.DIET_TYPE -> 3f / 8f
                    OnboardingStep.CONDITIONS -> 4f / 6f
                    OnboardingStep.BODY_MEASUREMENTS -> 5f / 8f
                    OnboardingStep.WEIGHT_GOAL -> 6f / 8f
                    OnboardingStep.RECOMMENDED_DIETS -> 7f / 8f
                    OnboardingStep.SETUP_COMPLETE -> 8f / 8f
                    OnboardingStep.COMPLETE -> 8f / 8f
                }
            },
            gapSize = (-15).dp,
            drawStopIndicator = {}
        )

        Spacer(Modifier.height(24.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (step) {
                OnboardingStep.HEIGHT ->
                    HeightStep(
                        height = height,
                        onHeightChange = viewModel::onHeightChange
                    )

                OnboardingStep.ACTIVITY_LEVEL ->
                    ActivityLevelStep(
                        items = activityLevels,
                        selectedId = activityLevelId,
                        onSelect = viewModel::onActivityLevelSelected
                    )

                OnboardingStep.DIET_TYPE ->
                    DietTypeStep(
                        items = dietTypes,
                        selectedId = dietTypeId,
                        onSelect = viewModel::onDietTypeSelected
                    )

                OnboardingStep.CONDITIONS ->
                    ConditionsStep(
                        items = conditions,
                        selectedIds = selectedConditions,
                        onToggle = viewModel::toggleCondition
                    )

                OnboardingStep.BODY_MEASUREMENTS ->
                    BodyMeasurementsStep(
                        weight = weight,
                        waist = waist,
                        neck = neck,
                        onWeightChange = viewModel::onWeightChange,
                        onWaistChange = viewModel::onWaistChange,
                        onNeckChange = viewModel::onNeckChange
                    )

                OnboardingStep.WEIGHT_GOAL ->
                    WeightGoalStep(
                        goal = weightGoal,
                        onGoalChange = viewModel::onWeightGoalChange
                    )

                OnboardingStep.RECOMMENDED_DIETS ->
                    RecommendedDietsStep(
                        items = uiState.allDietTypes,
                        selectedId = uiState.selectedDietTypeId,
                        onSelect = viewModel::onDietTypeSelected
                    )

                OnboardingStep.SETUP_COMPLETE ->
                    SetupCompleteStep()

                OnboardingStep.COMPLETE -> Unit
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step != OnboardingStep.HEIGHT && step != OnboardingStep.SETUP_COMPLETE) {
                TextButton(
                    onClick = viewModel::previousStep,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Back") }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            if (step == OnboardingStep.RECOMMENDED_DIETS) {
                TextButton(
                    onClick = { viewModel.nextStep() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) { Text("Skip") }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    when (step) {
                        OnboardingStep.WEIGHT_GOAL -> {
                            viewModel.onboardUser { success ->
                                if (success) {
                                    viewModel.getRecommendedDiets()
                                }
                            }
                        }


                        OnboardingStep.RECOMMENDED_DIETS -> {
                            val chosenId = uiState.selectedDietTypeId
                            if (chosenId == null) {
                                viewModel.skipRecommendedDiets()
                            } else {
                                viewModel.applyRecommendedDiet {
                                    viewModel.nextStep()
                                }
                            }
                        }

                        OnboardingStep.SETUP_COMPLETE -> {
                            viewModel.nextStep()
                        }

                        else -> viewModel.nextStep()
                    }
                },
                enabled = when (step) {
                    OnboardingStep.RECOMMENDED_DIETS -> uiState.selectedDietTypeId != null && !isLoading

                    else -> isStepValid(
                        step = step,
                        height = height,
                        activityLevelId = activityLevelId,
                        dietTypeId = dietTypeId,
                        weight = weight,
                        waist = waist,
                        neck = neck,
                        weightGoal = weightGoal
                    ) && !isLoading
                }
            ) {
                Text(
                    when (step) {
                        OnboardingStep.WEIGHT_GOAL -> "Continue"
                        OnboardingStep.RECOMMENDED_DIETS -> "Finish"
                        OnboardingStep.SETUP_COMPLETE -> "Go to Profile"
                        else -> "Next"
                    }
                )
            }
        }

    }
}

private fun isStepValid(
    step: OnboardingStep,
    height: Float,
    activityLevelId: Int?,
    dietTypeId: Int?,
    weight: Float,
    waist: Float,
    neck: Float,
    weightGoal: String,
): Boolean = when (step) {
    OnboardingStep.HEIGHT -> height in 50f..250f
    OnboardingStep.ACTIVITY_LEVEL -> activityLevelId != null
    OnboardingStep.DIET_TYPE -> dietTypeId != null
    OnboardingStep.CONDITIONS -> true
    OnboardingStep.BODY_MEASUREMENTS -> weight > 0f && waist > 0f && neck > 0f
    OnboardingStep.WEIGHT_GOAL -> weightGoal.isNotBlank()
    OnboardingStep.RECOMMENDED_DIETS -> true
    OnboardingStep.SETUP_COMPLETE -> true
    OnboardingStep.COMPLETE -> true
}
