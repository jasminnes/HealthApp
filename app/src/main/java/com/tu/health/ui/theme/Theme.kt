package com.tu.health.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = NavyCharcoal,
    primaryContainer = MutedTeal,
    onPrimaryContainer = White,

    secondary = MutedIndigo,
    onSecondary = White,
    secondaryContainer = MutedPurple,
    onSecondaryContainer = White,

    tertiary = MutedTeal,
    onTertiary = White,
    tertiaryContainer = MutedPurple,
    onTertiaryContainer = White,

    background = NavyCharcoal,
    onBackground = White,
    surface = Grey900,
    surfaceVariant = Grey700,
    surfaceDim = Grey1000,
    surfaceContainer = Grey800,
    surfaceContainerHighest = Grey800,
    onSurface = White,
    onSurfaceVariant = Grey500,

    error = Error,
    onError = White,
    onErrorContainer = Error,

    outline = Grey700,
    outlineVariant = Grey500
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryAccent,
    onPrimary = Grey900,
    primaryContainer = TealSoft,
    onPrimaryContainer = Grey900,

    secondary = IndigoSoft,
    onSecondary = White,
    secondaryContainer = PurpleSoft,
    onSecondaryContainer = Grey900,

    tertiary = TealSoft,
    onTertiary = Grey900,
    tertiaryContainer = PurpleSoft,
    onTertiaryContainer = White,

    background = WhiteLilac,
    onBackground = Grey900,
    surface = White,
    surfaceVariant = Grey200,
    surfaceDim = Grey400,
    surfaceContainer = Grey100,
    surfaceContainerHighest = Grey300,
    onSurface = Grey900,
    onSurfaceVariant = Grey700,

    error = Error,
    onError = White,
    onErrorContainer = Grey900,

    outline = Grey300,
    outlineVariant = Grey500
)

@Composable
fun HealthAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}