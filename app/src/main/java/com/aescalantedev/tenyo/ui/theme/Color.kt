package com.aescalantedev.tenyo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.aescalantedev.tenyo.core.domain.model.AppThemeMode

/**
 * Definicion inmutable de una paleta de colores completa para Tenyo (天与).
 * Simplificado a 3 temas oficiales de diseno premium-minimal:
 * 01. Tenyo Dark (Principal · Minimal): `#0A0A0D`, `#111116`, `#17171E`, `#211C31`, `#9B83F5`
 * 02. Restriccion Esmeralda (Maki Focus): `#07090B`, `#0B0D10`, `#1A1F26`, `#00C896`
 * 03. Monocromo (Fuerza Pura): `#0A0A0A`, `#1C1C1E`, `#373737`, `#FFFFFF`
 */
@Immutable
data class TenyoColorPalette(
    val voidBlack: Color,
    val surface: Color,
    val obsidianBackground: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val surfaceBright: Color,
    val surfaceSelected: Color,
    val outline: Color,
    val outlineVariant: Color,
    val outlineSubtle: Color,
    val jadePrimary: Color,
    val jadePrimaryContainer: Color,
    val jadeContainer: Color,
    val onJadePrimary: Color,
    val onJadePrimaryContainer: Color,
    val violetSecondary: Color,
    val violetSecondaryContainer: Color,
    val violetContainer: Color,
    val onVioletSecondary: Color,
    val onVioletSecondaryContainer: Color,
    val crimsonTertiary: Color,
    val crimsonTertiaryContainer: Color,
    val crimsonContainer: Color,
    val onCrimsonTertiary: Color,
    val onCrimsonTertiaryContainer: Color,
    val amberWarning: Color,
    val accentGold: Color = Color(0xFFD9B37A),
    val accentGoldMuted: Color = Color(0xFF9B805A),
    val muscleSelected: Color = Color(0xFFB69AFF),
    val error: Color = Color(0xFFFF718B),
    val errorContainer: Color = Color(0xFF421C28),
    val onError: Color = Color(0xFF3B0008),
    val onErrorContainer: Color = Color(0xFFFFDAD6),
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val inverseSurface: Color = Color(0xFFE2E8F0),
    val inverseOnSurface: Color = Color(0xFF0F172A),
    val inversePrimary: Color = Color(0xFF8268E5),
    val swatchPreview: List<Color>
)

/**
 * 01. TENYO DARK · Minimal (Principal / Por Defecto)
 * Diseno premium-minimal con fondo ultra oscuro (#0A0A0D) y acento purpura celestial (#9B83F5).
 */
val Palette01TenyoDark = TenyoColorPalette(
    voidBlack = Color(0xFF070709),
    surface = Color(0xFF111116),
    obsidianBackground = Color(0xFF0A0A0D),
    surfaceContainerLowest = Color(0xFF0D0D11),
    surfaceContainerLow = Color(0xFF13131A),
    surfaceContainer = Color(0xFF17171E),
    surfaceContainerHigh = Color(0xFF1D1C24),
    surfaceContainerHighest = Color(0xFF24232E),
    surfaceBright = Color(0xFF2F2D3A),
    surfaceSelected = Color(0xFF211C31),
    outline = Color(0xFF302D3A),
    outlineVariant = Color(0xFF24232B),
    outlineSubtle = Color(0xFF24232B),
    jadePrimary = Color(0xFF9B83F5),
    jadePrimaryContainer = Color(0xFF30244E),
    jadeContainer = Color(0xFF211C31),
    onJadePrimary = Color(0xFF100C1B),
    onJadePrimaryContainer = Color(0xFFE9DFFF),
    violetSecondary = Color(0xFFB5A5F5),
    violetSecondaryContainer = Color(0xFF29233B),
    violetContainer = Color(0xFF29233B),
    onVioletSecondary = Color(0xFF171122),
    onVioletSecondaryContainer = Color(0xFFE9DFFF),
    crimsonTertiary = Color(0xFFFF718B),
    crimsonTertiaryContainer = Color(0xFF421C28),
    crimsonContainer = Color(0xFF421C28),
    onCrimsonTertiary = Color(0xFFFFF0F3),
    onCrimsonTertiaryContainer = Color(0xFFFFD9E0),
    amberWarning = Color(0xFFE8BE78),
    accentGold = Color(0xFFD9B37A),
    accentGoldMuted = Color(0xFF9B805A),
    muscleSelected = Color(0xFFB69AFF),
    onSurface = Color(0xFFF4F2FA),
    onSurfaceVariant = Color(0xFFAAA6B7),
    textPrimary = Color(0xFFF4F2FA),
    textSecondary = Color(0xFFAAA6B7),
    textTertiary = Color(0xFF777383),
    swatchPreview = listOf(
        Color(0xFF0A0A0D),
        Color(0xFF111116),
        Color(0xFF17171E),
        Color(0xFF211C31),
        Color(0xFF9B83F5),
        Color(0xFFB5A5F5)
    )
)

/**
 * 02. RESTRICCION ESMERALDA · Maki Focus
 * Fondo oscuro (#0B0D10) con acento verde esmeralda (#00C896).
 */
val Palette02Emerald = TenyoColorPalette(
    voidBlack = Color(0xFF07090B),
    surface = Color(0xFF0B0D10),
    obsidianBackground = Color(0xFF0B0D10),
    surfaceContainerLowest = Color(0xFF090B0E),
    surfaceContainerLow = Color(0xFF13171C),
    surfaceContainer = Color(0xFF1A1F26),
    surfaceContainerHigh = Color(0xFF242A32),
    surfaceContainerHighest = Color(0xFF2E343B),
    surfaceBright = Color(0xFF3A424B),
    surfaceSelected = Color(0xFF0E2820),
    outline = Color(0xFF2A3A34),
    outlineVariant = Color(0xFF1F2B26),
    outlineSubtle = Color(0xFF1F2B26),
    jadePrimary = Color(0xFF00C896),
    jadePrimaryContainer = Color(0xFF0D3B2E),
    jadeContainer = Color(0xFF0E2820),
    onJadePrimary = Color(0xFF021F17),
    onJadePrimaryContainer = Color(0xFFDDF7EE),
    violetSecondary = Color(0xFF6B8F7A),
    violetSecondaryContainer = Color(0xFF1F382B),
    violetContainer = Color(0xFF1A3025),
    onVioletSecondary = Color(0xFF071A10),
    onVioletSecondaryContainer = Color(0xFFDDE7E1),
    crimsonTertiary = Color(0xFFFF4D6D),
    crimsonTertiaryContainer = Color(0xFF4C0F1D),
    crimsonContainer = Color(0xFF4C0F1D),
    onCrimsonTertiary = Color(0xFFFFF0F3),
    onCrimsonTertiaryContainer = Color(0xFFFFD9E0),
    amberWarning = Color(0xFFD9A066),
    accentGold = Color(0xFFD9A066),
    accentGoldMuted = Color(0xFF9E7448),
    muscleSelected = Color(0xFF00E5FF),
    onSurface = Color(0xFFF4F7FA),
    onSurfaceVariant = Color(0xFFA3B1B7),
    textPrimary = Color(0xFFF4F7FA),
    textSecondary = Color(0xFFA3B1B7),
    textTertiary = Color(0xFF7C8C8F),
    swatchPreview = listOf(
        Color(0xFF0B0D10),
        Color(0xFF1A1F26),
        Color(0xFF2E343B),
        Color(0xFF00C896),
        Color(0xFF6B8F7A),
        Color(0xFFD9A066)
    )
)

/**
 * 03. MONOCROMO · Fuerza Pura
 * Simple, limpia y poderosa: negro puro (#0A0A0A), escalas de acero y acento blanco (#FFFFFF).
 */
val Palette03Monochrome = TenyoColorPalette(
    voidBlack = Color(0xFF050505),
    surface = Color(0xFF0A0A0A),
    obsidianBackground = Color(0xFF0A0A0A),
    surfaceContainerLowest = Color(0xFF080808),
    surfaceContainerLow = Color(0xFF141414),
    surfaceContainer = Color(0xFF1C1C1E),
    surfaceContainerHigh = Color(0xFF28282A),
    surfaceContainerHighest = Color(0xFF373737),
    surfaceBright = Color(0xFF464648),
    surfaceSelected = Color(0xFF2A2A2E),
    outline = Color(0xFF38383C),
    outlineVariant = Color(0xFF2A2A2E),
    outlineSubtle = Color(0xFF2A2A2E),
    jadePrimary = Color(0xFFFFFFFF),
    jadePrimaryContainer = Color(0xFF373737),
    jadeContainer = Color(0xFF28282A),
    onJadePrimary = Color(0xFF0A0A0A),
    onJadePrimaryContainer = Color(0xFFFFFFFF),
    violetSecondary = Color(0xFFD6D6D6),
    violetSecondaryContainer = Color(0xFF2A2A2E),
    violetContainer = Color(0xFF202022),
    onVioletSecondary = Color(0xFF0A0A0A),
    onVioletSecondaryContainer = Color(0xFFFFFFFF),
    crimsonTertiary = Color(0xFFB0B0B0),
    crimsonTertiaryContainer = Color(0xFF373737),
    crimsonContainer = Color(0xFF28282A),
    onCrimsonTertiary = Color(0xFF0A0A0A),
    onCrimsonTertiaryContainer = Color(0xFFFFFFFF),
    amberWarning = Color(0xFFD6D6D6),
    accentGold = Color(0xFFE5E5E5),
    accentGoldMuted = Color(0xFF8A8A8A),
    muscleSelected = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFD6D6D6),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFB0B0B0),
    textTertiary = Color(0xFF8A8A8A),
    swatchPreview = listOf(
        Color(0xFF0A0A0A),
        Color(0xFF1C1C1E),
        Color(0xFF373737),
        Color(0xFF8A8A8A),
        Color(0xFFD6D6D6),
        Color(0xFFFFFFFF)
    )
)

/**
 * Devuelve la [TenyoColorPalette] correspondiente al [AppThemeMode] seleccionado.
 */
fun paletteForThemeMode(themeMode: AppThemeMode): TenyoColorPalette = when (themeMode) {
    AppThemeMode.TENYO_DARK -> Palette01TenyoDark
    AppThemeMode.EMERALD_RESTRICTION -> Palette02Emerald
    AppThemeMode.MONOCHROME -> Palette03Monochrome
}

/**
 * `CompositionLocal` que propaga la paleta activa de Tenyo a todos los componentes Compose.
 */
val LocalTenyoPalette = staticCompositionLocalOf { Palette01TenyoDark }

// ============================================================================
// Propiedades reactivas globales consumidas por todas las pantallas de Tenyo
// ============================================================================

val TenyoVoidBlack: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.voidBlack

val TenyoSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surface

val TenyoObsidianBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.obsidianBackground

val TenyoSurfaceContainerLowest: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surfaceContainerLowest

val TenyoSurfaceContainerLow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surfaceContainerLow

val TenyoSurfaceContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surfaceContainer

val TenyoSurfaceContainerHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surfaceContainerHigh

val TenyoSurfaceContainerHighest: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surfaceContainerHighest

val TenyoSurfaceBright: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surfaceBright

val TenyoSurfaceSelected: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.surfaceSelected

val TenyoOutline: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.outline

val TenyoOutlineVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.outlineVariant

val TenyoOutlineSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.outlineSubtle

val TenyoJadePrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.jadePrimary

val TenyoJadePrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.jadePrimaryContainer

val TenyoJadeContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.jadeContainer

val TenyoOnJadePrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onJadePrimary

val TenyoOnJadePrimaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onJadePrimaryContainer

val TenyoVioletSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.violetSecondary

val TenyoVioletSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.violetSecondaryContainer

val TenyoVioletContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.violetContainer

val TenyoOnVioletSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onVioletSecondary

val TenyoOnVioletSecondaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onVioletSecondaryContainer

val TenyoCrimsonTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.crimsonTertiary

val TenyoCrimsonTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.crimsonTertiaryContainer

val TenyoCrimsonContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.crimsonContainer

val TenyoOnCrimsonTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onCrimsonTertiary

val TenyoOnCrimsonTertiaryContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onCrimsonTertiaryContainer

val TenyoAmberWarning: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.amberWarning

val TenyoAccentGold: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.accentGold

val TenyoAccentGoldMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.accentGoldMuted

val TenyoMuscleSelected: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.muscleSelected

val TenyoError: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.error

val TenyoErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.errorContainer

val TenyoOnError: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onError

val TenyoOnErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onErrorContainer

val TenyoOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onSurface

val TenyoOnSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.onSurfaceVariant

val TenyoTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.textPrimary

val TenyoTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.textSecondary

val TenyoTextTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.textTertiary

val TenyoInverseSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.inverseSurface

val TenyoInverseOnSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.inverseOnSurface

val TenyoInversePrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalTenyoPalette.current.inversePrimary
