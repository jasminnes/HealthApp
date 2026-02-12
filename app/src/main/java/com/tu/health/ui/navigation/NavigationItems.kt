package com.tu.health.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(val title: String, val icon: ImageVector, val route: String)

val navigationItems = listOf(
    NavigationItem("Home", Icons.Default.Home, Screen.HealthScore.route),
    NavigationItem("Macros", Icons.Default.Restaurant, Screen.Macros.route),
    NavigationItem("Profile", Icons.Default.Person, Screen.Profile.route),
)
