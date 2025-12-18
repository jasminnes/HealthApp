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
import com.tu.health.ui.screens.profile.ProfileScreen
import com.tu.health.viewmodels.StartViewModel

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startViewModel: StartViewModel = hiltViewModel()
) {
    val isLoggedIn by startViewModel.isLoggedIn.collectAsState()
    val isLoading by startViewModel.isLoading.collectAsState()

    if (!isLoading) {
        val bottomBarState = rememberSaveable { mutableStateOf(true) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navOffScreens = listOf(
            Screen.Authentication.route,
            Screen.SignUp.route,
            Screen.LogIn.route
        )

        bottomBarState.value = currentRoute !in navOffScreens

        // Navigate to auth screen if session expired or missing
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
                startDestination = if (isLoggedIn) "profile" else "authentication",
                modifier = modifier.padding(paddingValues)
            ) {
                // Main screens
                composable(Screen.Profile.route) { ProfileScreen(navController) }

                // Authentication
                composable(Screen.Authentication.route) { AuthenticationScreen(navController) }
                composable(Screen.SignUp.route) { SignUpScreen(navController) }
                composable(Screen.LogIn.route) { LogInScreen(navController) }
            }
        }
    }
}
