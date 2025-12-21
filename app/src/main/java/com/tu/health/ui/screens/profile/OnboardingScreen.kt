package com.tu.health.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.ui.screens.profile.steps.ActivityLevelStep
import com.tu.health.ui.screens.profile.steps.BodyMeasurementsStep
import com.tu.health.ui.screens.profile.steps.ConditionsStep
import com.tu.health.ui.screens.profile.steps.DietTypeStep
import com.tu.health.ui.screens.profile.steps.HeightStep
import com.tu.health.viewmodels.profile.OnboardingStep
import com.tu.health.viewmodels.profile.OnboardingViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val step by viewModel.step.collectAsState()
    val height by viewModel.height.collectAsState()
    val activityLevelId by viewModel.selectedActivityLevelId.collectAsState()
    val dietTypeId by viewModel.selectedDietTypeId.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val waist by viewModel.waist.collectAsState()
    val neck by viewModel.neck.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val activityLevels by viewModel.allActivityLevels.collectAsState()
    val dietTypes by viewModel.allDietTypes.collectAsState()
    val conditions by viewModel.allConditions.collectAsState()
    val selectedConditions by viewModel.selectedConditionIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        LinearProgressIndicator(
            progress = {
                when (step) {
                    OnboardingStep.HEIGHT -> 0.2f
                    OnboardingStep.ACTIVITY_LEVEL -> 0.4f
                    OnboardingStep.DIET_TYPE -> 0.6f
                    OnboardingStep.CONDITIONS -> 0.8f
                    OnboardingStep.BODY_MEASUREMENTS -> 1f
                    else -> 1f
                }
            },
            gapSize = (-15).dp,
            drawStopIndicator = {}
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            when (step) {

                OnboardingStep.HEIGHT ->
                    HeightStep(
                        height = height, onHeightChange = { viewModel.onHeightChange(it) }
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

                OnboardingStep.COMPLETE -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("profile") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
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
                    if (step == OnboardingStep.BODY_MEASUREMENTS) {
                        viewModel.onboardUser { success, _ ->
                            if (success) viewModel.complete()
                        }
                    } else {
                        viewModel.nextStep()
                    }
                },
                enabled = isStepValid(
                    step,
                    height,
                    activityLevelId,
                    dietTypeId,
                    weight,
                    waist,
                    neck
                ) && !isLoading
            ) {
                Text(if (step == OnboardingStep.BODY_MEASUREMENTS) "Finish" else "Next")
            }
        }
    }
}

private fun isStepValid(
    step: OnboardingStep,
    height: Float?,
    activityLevelId: Int?,
    dietTypeId: Int?,
    weight: Float?,
    waist: Float?,
    neck: Float?
): Boolean =
    when (step) {
        OnboardingStep.HEIGHT -> height != null
        OnboardingStep.ACTIVITY_LEVEL -> activityLevelId != null
        OnboardingStep.DIET_TYPE -> dietTypeId != null
        OnboardingStep.CONDITIONS -> true
        OnboardingStep.BODY_MEASUREMENTS ->
            weight != null && waist != null && neck != null
        else -> true
    }
