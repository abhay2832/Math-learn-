package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    secondary = CyanSecondary,
    tertiary = VioletTertiary,
    background = CosmicDarkBg,
    surface = CosmicDarkCard,
    error = RoseError,
    onPrimary = CosmicDarkBg,
    onSecondary = CosmicDarkBg,
    onTertiary = CosmicDarkBg,
    onBackground = CosmicDarkTextPrimary,
    onSurface = CosmicDarkTextPrimary,
    surfaceVariant = CosmicDarkCard,
    onSurfaceVariant = CosmicDarkTextSecondary,
    outline = CosmicDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = CyanSecondary,
    tertiary = VioletTertiary,
    background = SlateLightBg,
    surface = SlateLightCard,
    error = RoseError,
    onPrimary = SlateLightBg,
    onSecondary = SlateLightBg,
    onTertiary = SlateLightBg,
    onBackground = SlateLightTextPrimary,
    onSurface = SlateLightTextPrimary,
    surfaceVariant = SlateLightCard,
    onSurfaceVariant = SlateLightTextSecondary,
    outline = SlateLightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
