package com.tu.health.ui.navigation

sealed class Screen(val route: String) {
    object Profile : Screen("profile")

    // Authentication Module
    object Authentication : Screen("authentication")
    object SignUp : Screen("signup")
    object LogIn : Screen("login")

    // Profile Module
    object ChangePassword : Screen("change-password")
    object Onboarding : Screen("onboarding")
    object BodyMeasurements : Screen("measurements")
    object DietType : Screen("diet")
    object HealthConditions : Screen("conditions")

    // HealthConnect
    object HealthConnectSetup : Screen("health-connect-setup")

    // Nutrition
    object Macros : Screen("macros")
    object FoodDetails : Screen("food-details/{id}")
    object FoodSearch : Screen("food-search")
}
