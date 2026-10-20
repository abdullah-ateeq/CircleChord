package com.example.circlechord.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.circlechord.data.model.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = GeometryPrimaryDark,
    onPrimary = GeometryOnPrimaryDark,
    primaryContainer = GeometryPrimaryContainerDark,
    onPrimaryContainer = GeometryOnPrimaryContainerDark,
    secondary = GeometrySecondaryDark,
    onSecondary = GeometryOnSecondaryDark,
    secondaryContainer = GeometrySecondaryContainerDark,
    onSecondaryContainer = GeometryOnSecondaryContainerDark,
    tertiary = GeometryTertiaryDark,
    onTertiary = GeometryOnTertiaryDark,
    tertiaryContainer = GeometryTertiaryContainerDark,
    onTertiaryContainer = GeometryOnTertiaryContainerDark,
    background = GeometryBackgroundDark,
    onBackground = GeometryOnBackgroundDark,
    surface = GeometrySurfaceDark,
    onSurface = GeometryOnSurfaceDark,
    surfaceVariant = GeometrySurfaceVariantDark,
    onSurfaceVariant = GeometryOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = GeometryPrimaryLight,
    onPrimary = GeometryOnPrimaryLight,
    primaryContainer = GeometryPrimaryContainerLight,
    onPrimaryContainer = GeometryOnPrimaryContainerLight,
    secondary = GeometrySecondaryLight,
    onSecondary = GeometryOnSecondaryLight,
    secondaryContainer = GeometrySecondaryContainerLight,
    onSecondaryContainer = GeometryOnSecondaryContainerLight,
    tertiary = GeometryTertiaryLight,
    onTertiary = GeometryOnTertiaryLight,
    tertiaryContainer = GeometryTertiaryContainerLight,
    onTertiaryContainer = GeometryOnTertiaryContainerLight,
    background = GeometryBackgroundLight,
    onBackground = GeometryOnBackgroundLight,
    surface = GeometrySurfaceLight,
    onSurface = GeometryOnSurfaceLight,
    surfaceVariant = GeometrySurfaceVariantLight,
    onSurfaceVariant = GeometryOnSurfaceVariantLight
)

@Composable
fun CircleChordTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
