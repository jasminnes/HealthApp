package com.tu.health.ui.navigation

sealed class Screen(val route: String) {

    // Authentication Module
    object Authentication : Screen("authentication")
    object SignUp : Screen("signup")
    object LogIn : Screen("login")

    // Profile Module
    object Profile : Screen("profile")
    object ChangePassword : Screen("change-password")
    object Onboarding : Screen("onboarding")
    object BodyMeasurements : Screen("measurements")
    object DietType : Screen("diet")
    object HealthConditions : Screen("conditions")
    object HealthConnectSetup : Screen("health-connect-setup")

    // Nutrition
    object Macros : Screen("macros")
    object FoodDetails : Screen("food-details/{id}")
    object FoodSearch : Screen("food-search")

    // Health
    object HealthScore : Screen("health-score")
    object RecommendationDetails : Screen("recommendation-details")

    // Insights
    object InsightsSummary : Screen("insights-summary")
    object InsightsNutrition : Screen("insights-nutrition")
    object InsightsHealthConnect : Screen("insights-healthconnect")
    object InsightsBodyComposition : Screen("insights-bodycomposition")
    object InsightsScores : Screen("insights-scores")
}
