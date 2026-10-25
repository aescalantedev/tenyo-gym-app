package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.aescalantedev.tenyo.ui.theme.TenyoAccentGold
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh

/**
 * Propiedades de color reactivas para las pantallas y componentes de Planes.
 * Delegadas en [com.aescalantedev.tenyo.ui.theme.LocalTenyoPalette] para reaccionar al instante
 * cuando el usuario cambia entre las 3 paletas oficiales (Tenyo Dark, Restricción Esmeralda, Monocromo).
 */
val HtmlAppBg: Color
    @Composable
    @ReadOnlyComposable
    get() = TenyoObsidianBackground

val HtmlAppSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = TenyoSurfaceContainer

val HtmlAppSurfaceHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = TenyoSurfaceContainerHigh

val HtmlAppIconBg: Color
    @Composable
    @ReadOnlyComposable
    get() = TenyoSurfaceContainerHigh

val HtmlAppPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = TenyoJadePrimary

val HtmlAppGold: Color
    @Composable
    @ReadOnlyComposable
    get() = TenyoAccentGold

val HtmlBorderSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = TenyoOutlineSubtle

// Distintivos visuales para días de descanso
val RestRedAccent = Color(0xFFFF5252)
val RestRedBg = Color(0x22FF5252)
val RestRedBorder = Color(0x44FF5252)
