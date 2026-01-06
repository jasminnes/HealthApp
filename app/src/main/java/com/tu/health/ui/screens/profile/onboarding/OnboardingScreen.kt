package com.tu.health.ui.screens.profile.onboarding

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.viewmodels.profile.OnboardingStep
import com.tu.health.viewmodels.profile.OnboardingViewModel
import com.tu.health.viewmodels.profile.ProfileUiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(step) {
        if (step == OnboardingStep.COMPLETE) {
            navController.navigate("profile") {
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
                    OnboardingStep.HEIGHT -> 1f / 6f
                    OnboardingStep.ACTIVITY_LEVEL -> 2f / 6f
                    OnboardingStep.DIET_TYPE -> 3f / 6f
                    OnboardingStep.CONDITIONS -> 4f / 6f
                    OnboardingStep.BODY_MEASUREMENTS -> 5f / 6f
                    OnboardingStep.WEIGHT_GOAL -> 6f / 6f
                    OnboardingStep.COMPLETE -> 1f
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

                OnboardingStep.COMPLETE -> Unit
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step != OnboardingStep.HEIGHT) {
                TextButton(
                    onClick = viewModel::previousStep,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Back")
                }
            } else {
                Spacer(Modifier)
            }

            Button(
                onClick = {
                    if (step == OnboardingStep.WEIGHT_GOAL) {
                        viewModel.onboardUser { success ->
                            if (success) viewModel.complete()
                        }
                    } else {
                        viewModel.nextStep()
                    }
                },
                enabled = isStepValid(
                    step = step,
                    height = height,
                    activityLevelId = activityLevelId,
                    dietTypeId = dietTypeId,
                    weight = weight,
                    waist = waist,
                    neck = neck,
                    weightGoal = weightGoal
                ) && !isLoading
            ) {
                Text(if (step == OnboardingStep.WEIGHT_GOAL) "Finish" else "Next")
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
    weightGoal: String
): Boolean =
    when (step) {
        OnboardingStep.HEIGHT -> height in 50f..250f
        OnboardingStep.ACTIVITY_LEVEL -> activityLevelId != null
        OnboardingStep.DIET_TYPE -> dietTypeId != null
        OnboardingStep.CONDITIONS -> true
        OnboardingStep.BODY_MEASUREMENTS -> weight > 0f && waist > 0f && neck > 0f
        OnboardingStep.WEIGHT_GOAL -> weightGoal in listOf("Gain Muscle", "Lose Weight", "Maintain Weight")
        OnboardingStep.COMPLETE -> true
    }
