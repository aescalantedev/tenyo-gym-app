package com.aescalantedev.tenyo.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.screens.splash.components.TenyoBrandSloganRow
import com.aescalantedev.tenyo.ui.screens.splash.components.TenyoBrandTitle
import com.aescalantedev.tenyo.ui.screens.splash.components.TenyoModernSplashLoader
import com.aescalantedev.tenyo.ui.screens.splash.components.TenyoOfficialEmblemBadge
import com.aescalantedev.tenyo.ui.theme.TenyoAccentGold
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLowest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Pantalla de Arranque (SplashScreen) Oficial de Tenyo (天与).
 *
 * Diseno premium-minimalista de alto impacto:
 * - Imagotipo Oficial: Insignia squircle con Katana afilada, barra olímpica con discos,
 *   y silueta anatómica de Toji Fushiguro con cabello manga característico.
 * - Tipografía de marca geométrica (`T E N Y O`) con la 'E' de 3 barras y la 'Y'
 *   reaccionando dinámicamente al color primario del tema activo.
 * - Lema con divisores dorados: `— DISCIPLINA · FUERZA · ESFUERZO —`.
 * - Barra de carga minimalista con shimmer continuo e indicador de estado de seeding.
 * - Totalmente reactiva al tema ([TenyoTheme]): Tenyo Dark (Púrpura), Maki Focus (Esmeralda), Monocromo (Blanco).
 * - Animaciones fluidas ejecutadas en capas gráficas directas del GPU (graphicsLayer).
 */
@Composable
fun SplashScreen(
    isSeedingCatalog: Boolean,
    modifier: Modifier = Modifier
) {
    // Animaciones escalonadas de entrada
    val badgeScale = remember { Animatable(0.85f) }
    val borderProgress = remember { Animatable(0f) }
    val barbellProgress = remember { Animatable(0f) }
    val figureProgress = remember { Animatable(0f) }

    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffsetY = remember { Animatable(18f) }

    val sloganAlpha = remember { Animatable(0f) }
    val sloganOffsetY = remember { Animatable(14f) }

    val loaderAlpha = remember { Animatable(0f) }

    // Colores dinámicos vinculados a la paleta activa
    val primaryColor = TenyoJadePrimary
    val surfaceLow = TenyoSurfaceContainerLow
    val surfaceHigh = TenyoSurfaceContainerHigh
    val accentGold = TenyoAccentGold
    val backgroundColor = TenyoObsidianBackground
    val backgroundLowest = TenyoSurfaceContainerLowest

    LaunchedEffect(Unit) {
        // Fase 1: Entrada y expansión del marco del emblema
        launch {
            badgeScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        }
        launch {
            borderProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
            )
        }

        // Fase 2: Despliegue de la Barra Olímpica (100% Gym & Fitness)
        launch {
            delay(200)
            barbellProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }

        // Fase 3: Silueta V-Taper y Cabello Manga de Toji
        launch {
            delay(380)
            figureProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
            )
        }

        // Fase 5: T E N Y O Wordmark
        launch {
            delay(750.milliseconds)
            wordmarkAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }
        launch {
            delay(750.milliseconds)
            wordmarkOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }

        // Fase 6: Lema oficial dorada
        launch {
            delay(1_000.milliseconds)
            sloganAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }
        launch {
            delay(1_000.milliseconds)
            sloganOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }

        // Fase 7: Barra de carga y estado
        launch {
            delay(1_200.milliseconds)
            loaderAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundLowest,
                        backgroundColor,
                        backgroundLowest
                    )
                )
            ),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            // Contenedor Central: Insignia Toji + Katana + Barra + Marca + Slogan
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emblema Oficial Tenyo (Puro Gym & Musculación)
                TenyoOfficialEmblemBadge(
                    badgeSize = 186.dp,
                    borderProgress = borderProgress.value,
                    katanaProgress = 0f,
                    barbellProgress = barbellProgress.value,
                    figureProgress = figureProgress.value,
                    primaryColor = primaryColor,
                    surfaceColor = surfaceLow,
                    modifier = Modifier.graphicsLayer {
                        scaleX = badgeScale.value
                        scaleY = badgeScale.value
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Logotipo de la marca: T E N Y O
                TenyoBrandTitle(
                    primaryColor = primaryColor,
                    textColor = Color.White,
                    modifier = Modifier.graphicsLayer {
                        alpha = wordmarkAlpha.value
                        translationY = wordmarkOffsetY.value
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Slogan oficial con barras de acento
                TenyoBrandSloganRow(
                    accentColor = accentGold,
                    modifier = Modifier.graphicsLayer {
                        alpha = sloganAlpha.value
                        translationY = sloganOffsetY.value
                    }
                )
            }

            // Barra de carga minimalista en la parte inferior
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 48.dp)
                    .graphicsLayer {
                        alpha = loaderAlpha.value
                    }
            ) {
                TenyoModernSplashLoader(
                    isSeedingCatalog = isSeedingCatalog,
                    primaryColor = primaryColor,
                    trackColor = surfaceHigh
                )
            }
        }
    }
}
