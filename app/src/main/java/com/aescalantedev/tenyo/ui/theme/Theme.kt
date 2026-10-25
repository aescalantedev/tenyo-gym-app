package com.aescalantedev.tenyo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.aescalantedev.tenyo.core.domain.model.AppThemeMode

/**
 * Construye un [ColorScheme] de Material 3 a partir de la [TenyoColorPalette] activa.
 */
private fun TenyoColorPalette.toMaterialColorScheme(): ColorScheme = darkColorScheme(
    primary = jadePrimary,
    onPrimary = onJadePrimary,
    primaryContainer = jadePrimaryContainer,
    onPrimaryContainer = onJadePrimaryContainer,
    inversePrimary = inversePrimary,
    secondary = violetSecondary,
    onSecondary = onVioletSecondary,
    secondaryContainer = violetSecondaryContainer,
    onSecondaryContainer = onVioletSecondaryContainer,
    tertiary = crimsonTertiary,
    onTertiary = onCrimsonTertiary,
    tertiaryContainer = crimsonTertiaryContainer,
    onTertiaryContainer = onCrimsonTertiaryContainer,
    background = obsidianBackground,
    onBackground = onSurface,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceContainerHigh,
    onSurfaceVariant = onSurfaceVariant,
    surfaceTint = jadePrimary,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    outline = outline,
    outlineVariant = outlineVariant,
    surfaceBright = surfaceBright,
    surfaceDim = voidBlack,
    surfaceContainer = surfaceContainer,
    surfaceContainerHigh = surfaceContainerHigh,
    surfaceContainerHighest = surfaceContainerHighest,
    surfaceContainerLow = surfaceContainerLow,
    surfaceContainerLowest = surfaceContainerLowest
)

/**
 * Tema raiz de Tenyo que propaga [LocalTenyoPalette] y el [ColorScheme] de Material 3
 * de forma reactiva cuando el usuario cambia entre las 3 paletas oficiales.
 */
@Composable
fun TenyoTheme(
    themeMode: AppThemeMode = AppThemeMode.TENYO_DARK,
    content: @Composable () -> Unit
) {
    val activePalette = remember(themeMode) { paletteForThemeMode(themeMode) }
    val colorScheme = remember(activePalette) { activePalette.toMaterialColorScheme() }

    CompositionLocalProvider(LocalTenyoPalette provides activePalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}