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
import com.tu.health.ui.screens.profile.ChangePasswordScreen
import com.tu.health.ui.screens.profile.onboarding.OnboardingScreen
import com.tu.health.ui.screens.profile.ProfileScreen
import com.tu.health.ui.screens.profile.goals.WeightGoalsScreen
import com.tu.health.viewmodels.StartViewModel

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startViewModel: StartViewModel = hiltViewModel()
) {
    val isLoading by startViewModel.isLoading.collectAsState()
    val isLoggedIn by startViewModel.isLoggedInFlow.collectAsState()

    val startDestination = when {
        !isLoggedIn -> Screen.Authentication.route
        else -> Screen.Profile.route
    }

    if (!isLoading) {
        val bottomBarState = rememberSaveable { mutableStateOf(true) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navOffScreens = listOf(
            Screen.Authentication.route,
            Screen.SignUp.route,
            Screen.LogIn.route,
            Screen.Onboarding.route
        )

        bottomBarState.value = currentRoute !in navOffScreens

        if (!isLoggedIn) {
            LaunchedEffect(Unit) {
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
                composable(Screen.Profile.route) { ProfileScreen(navController) }

                // Authentication
                composable(Screen.Authentication.route) { AuthenticationScreen(navController) }
                composable(Screen.SignUp.route) { SignUpScreen(navController) }
                composable(Screen.LogIn.route) { LogInScreen(navController) }

                // Profile screens
                composable(Screen.ChangePassword.route) { ChangePasswordScreen(navController) }
                composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
                composable(Screen.Goals.route) { WeightGoalsScreen(navController) }
            }
        }
    }
}
