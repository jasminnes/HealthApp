package com.tu.health.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tu.health.ui.screens.authentication.AuthenticationScreen
import com.tu.health.ui.screens.authentication.LogInScreen
import com.tu.health.ui.screens.authentication.SignUpScreen
import com.tu.health.ui.screens.health.HealthScoreScreen
import com.tu.health.ui.screens.health.RecommendationDetailsScreen
import com.tu.health.ui.screens.insights.InsightsSummaryScreen
import com.tu.health.ui.screens.insights.bodycomposition.BodyCompositionDetailsScreen
import com.tu.health.ui.screens.insights.healthconnect.HealthConnectDetailsScreen
import com.tu.health.ui.screens.insights.nutrition.NutritionDetailsScreen
import com.tu.health.ui.screens.insights.scores.HealthScoresDetailsScreen
import com.tu.health.ui.screens.profile.onboarding.HealthConnectScreen
import com.tu.health.ui.screens.nutrition.FoodDetailsScreen
import com.tu.health.ui.screens.nutrition.FoodSearchScreen
import com.tu.health.ui.screens.nutrition.MacrosScreen
import com.tu.health.ui.screens.profile.ChangePasswordScreen
import com.tu.health.ui.screens.profile.onboarding.OnboardingScreen
import com.tu.health.ui.screens.profile.ProfileScreen
import com.tu.health.ui.screens.profile.BodyMeasurementsScreen
import com.tu.health.ui.screens.profile.DietTypeScreen
import com.tu.health.ui.screens.profile.HealthConditionsScreen
import com.tu.health.viewmodels.StartViewModel

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startViewModel: StartViewModel = hiltViewModel()
) {
    val isLoading by startViewModel.isLoading.collectAsState()
    val showAuth by startViewModel.showAuth.collectAsState()

    val startDestination = if (showAuth) {
        Screen.Authentication.route
    } else {
        Screen.HealthScore.route
    }

    if (!isLoading) {
        val bottomBarState = rememberSaveable { mutableStateOf(true) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navOffScreens = listOf(
            Screen.Authentication.route,
            Screen.SignUp.route,
            Screen.LogIn.route,
            Screen.Onboarding.route,
            Screen.HealthConnectSetup.route
        )

        bottomBarState.value = currentRoute !in navOffScreens

        LaunchedEffect(showAuth) {
            if (showAuth) {
                navController.navigate(Screen.Authentication.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    navController = navController,
                    bottomBarState = bottomBarState
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier.padding(paddingValues)
            ) {
                // Main screens
                composable(Screen.HealthScore.route) { HealthScoreScreen(navController) }
                composable(Screen.Macros.route) { MacrosScreen(navController) }
                composable(Screen.Profile.route) { ProfileScreen(navController) }

                // Authentication
                composable(Screen.Authentication.route) { AuthenticationScreen(navController) }
                composable(Screen.SignUp.route) { SignUpScreen(navController) }
                composable(Screen.LogIn.route) { LogInScreen(navController) }

                // Profile screens
                composable(Screen.ChangePassword.route) { ChangePasswordScreen(navController) }
                composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
                composable(Screen.BodyMeasurements.route) { BodyMeasurementsScreen(navController) }
                composable(Screen.DietType.route) { DietTypeScreen(navController) }
                composable(Screen.HealthConditions.route) { HealthConditionsScreen(navController) }
                composable(Screen.HealthConnectSetup.route) { HealthConnectScreen(navController) }

                // Health
                composable(Screen.RecommendationDetails.route) { RecommendationDetailsScreen(navController) }

                // Nutrition
                composable(Screen.FoodDetails.route) { FoodDetailsScreen(navController) }
                composable(Screen.FoodSearch.route) { FoodSearchScreen(navController) }

                // Insights
                composable(Screen.InsightsSummary.route) { InsightsSummaryScreen(navController) }
                composable(Screen.InsightsNutrition.route) { NutritionDetailsScreen(navController) }
                composable(Screen.InsightsHealthConnect.route) { HealthConnectDetailsScreen(navController) }
                composable(Screen.InsightsBodyComposition.route) { BodyCompositionDetailsScreen(navController) }
                composable(Screen.InsightsScores.route) { HealthScoresDetailsScreen(navController) }
            }
        }
    }
}
