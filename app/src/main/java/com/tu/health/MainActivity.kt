package com.tu.health

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.tu.health.ui.navigation.NavigationGraph
import com.tu.health.ui.theme.HealthAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            HealthAppTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val view = LocalView.current
    val window = (view.context as Activity).window
    val barColor = MaterialTheme.colorScheme.surfaceVariant

    val controller = remember(view) {
        WindowInsetsControllerCompat(window, view)
    }

    SideEffect {
        val useDarkIcons = barColor.luminance() > 0.5f

        window.statusBarColor = barColor.toArgb()
        controller.isAppearanceLightStatusBars = useDarkIcons
    }

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavigationGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}