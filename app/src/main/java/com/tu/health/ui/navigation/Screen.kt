package com.tu.health.ui.navigation

sealed class Screen(val route: String) {
    object Profile : Screen("profile")

    // Authentication Module
    object Authentication : Screen("authentication")
    object SignUp : Screen("signup")
    object LogIn : Screen("login")

    // Profile Module
    object ChangePassword : Screen("change-password")
}
