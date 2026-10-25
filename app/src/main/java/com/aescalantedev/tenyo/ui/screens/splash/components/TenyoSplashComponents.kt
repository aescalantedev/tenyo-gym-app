package com.aescalantedev.tenyo.ui.screens.splash.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.ui.theme.TenyoAccentGold
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary

/**
 * Emblema oficial de Tenyo (Katana + Barra Olímpica + Silueta y Cabello de Toji Fushiguro).
 *
 * Reproduce fielmente el arte vectorial del imagotipo:
 * - Marco squircle estilizado con borde limpio y resplandor neón difuso que reacciona al tema activo.
 * - Katana diagonal con empuñadura (tsuka), guarda (tsuba) y filo de acero afilado.
 * - Barra olímpica horizontal cargada con múltiples discos simétricos.
 * - Espalda y torso en V hipertrofiado de Toji con cortes anatómicos en espacio negativo.
 * - Cabello característico de Toji Fushiguro con mechones manga/anime puntiagudos.
 */
@Composable
fun TenyoOfficialEmblemBadge(
    modifier: Modifier = Modifier,
    badgeSize: Dp = 186.dp,
    borderProgress: Float = 1f,
    katanaProgress: Float = 1f,
    barbellProgress: Float = 1f,
    figureProgress: Float = 1f,
    primaryColor: Color = TenyoJadePrimary,
    surfaceColor: Color = TenyoSurfaceContainerLow
) {
    val infiniteTransition = rememberInfiniteTransition(label = "badge_aura_pulse")
    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_glow"
    )

    Box(
        modifier = modifier.size(badgeSize),
        contentAlignment = Alignment.Center
    ) {
        // 1. Resplandor difuso ambiental neón vinculado al tema activo
        Box(
            modifier = Modifier
                .size(badgeSize * 1.30f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.35f * auraGlow * borderProgress),
                            primaryColor.copy(alpha = 0.12f * auraGlow * borderProgress),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Contenedor squircle con fondo de superficie profunda y borde pulido
        Box(
            modifier = Modifier
                .size(badgeSize)
                .clip(RoundedCornerShape(38.dp))
                .background(surfaceColor)
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.90f * borderProgress),
                            primaryColor.copy(alpha = 0.50f * borderProgress),
                            Color.White.copy(alpha = 0.20f * borderProgress)
                        )
                    ),
                    shape = RoundedCornerShape(38.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 3. Lienzo vectorial con las 3 capas: Katana, Barra, Torso con Cabello de Toji
            Canvas(
                modifier = Modifier
                    .size(badgeSize * 0.78f)
            ) {
                val scale = size.width / 120f

                // Capa 1: Katana diagonal (deslizamiento progresivo)
                if (katanaProgress > 0.01f) {
                    drawKatanaLayer(
                        scale = scale,
                        progress = katanaProgress,
                        katanaColor = Color.White,
                        cutColor = surfaceColor
                    )
                }

                // Capa 2: Barra olímpica horizontal con discos (expansión horizontal)
                if (barbellProgress > 0.01f) {
                    drawBarbellLayer(
                        scale = scale,
                        progress = barbellProgress,
                        barColor = Color.White
                    )
                }

                // Capa 3: Físico y Cabello de Toji Fushiguro (revelado frontal)
                if (figureProgress > 0.01f) {
                    drawTojiFigureLayer(
                        scale = scale,
                        progress = figureProgress,
                        figureColor = Color.White,
                        cutColor = surfaceColor
                    )
                }
            }
        }
    }
}

/**
 * Dibuja la Katana diagonal (espada de Toji) con tsuba, empuñadura y hoja.
 */
private fun DrawScope.drawKatanaLayer(
    scale: Float,
    progress: Float,
    katanaColor: Color,
    cutColor: Color
) {
    val alpha = progress.coerceIn(0f, 1f)
    val slideOffset = (1f - progress) * (20f * scale)

    // Hoja principal de la katana
    val bladePath = Path().apply {
        moveTo((45f * scale) - slideOffset, (75f * scale) + slideOffset)
        lineTo((98f * scale) - slideOffset, (22f * scale) + slideOffset)
        lineTo((103.5f * scale) - slideOffset, (20.5f * scale) + slideOffset) // Punta afilada kissaki
        lineTo((102f * scale) - slideOffset, (25.5f * scale) + slideOffset)
        lineTo((50f * scale) - slideOffset, (78f * scale) + slideOffset)
        close()
    }
    drawPath(bladePath, color = katanaColor.copy(alpha = alpha))

    // Guarda de la espada (Tsuba)
    val tsubaPath = Path().apply {
        val cx = (48f * scale) - slideOffset
        val cy = (75.5f * scale) + slideOffset
        moveTo(cx - (8f * scale), cy - (8f * scale))
        lineTo(cx + (1.5f * scale), cy - (11f * scale))
        lineTo(cx + (8f * scale), cy + (8f * scale))
        lineTo(cx - (1.5f * scale), cy + (11f * scale))
        close()
    }
    drawPath(tsubaPath, color = katanaColor.copy(alpha = alpha))

    // Empuñadura (Tsuka)
    drawLine(
        color = katanaColor.copy(alpha = alpha),
        start = Offset((45f * scale) - slideOffset, (78.5f * scale) + slideOffset),
        end = Offset((25f * scale) - slideOffset, (98.5f * scale) + slideOffset),
        strokeWidth = 5.2f * scale,
        cap = StrokeCap.Round
    )

    // Cortes diagonales que simulan el trenzado tradicional de la tsuka
    val wrapStroke = 1.4f * scale
    drawLine(
        color = cutColor.copy(alpha = alpha),
        start = Offset((40f * scale) - slideOffset, (80.5f * scale) + slideOffset),
        end = Offset((35f * scale) - slideOffset, (85.5f * scale) + slideOffset),
        strokeWidth = wrapStroke
    )
    drawLine(
        color = cutColor.copy(alpha = alpha),
        start = Offset((34f * scale) - slideOffset, (86.5f * scale) + slideOffset),
        end = Offset((29f * scale) - slideOffset, (91.5f * scale) + slideOffset),
        strokeWidth = wrapStroke
    )
}

/**
 * Dibuja la Barra Olímpica horizontal con múltiples discos simétricos.
 */
private fun DrawScope.drawBarbellLayer(
    scale: Float,
    progress: Float,
    barColor: Color
) {
    val alpha = progress.coerceIn(0f, 1f)
    val cx = 60f * scale
    val totalHalfWidth = (48f * scale) * progress

    // Barra central
    drawLine(
        color = barColor.copy(alpha = alpha),
        start = Offset(cx - totalHalfWidth, 52f * scale),
        end = Offset(cx + totalHalfWidth, 52f * scale),
        strokeWidth = 3f * scale,
        cap = StrokeCap.Round
    )

    if (progress > 0.4f) {
        val plateAlpha = ((progress - 0.4f) / 0.6f).coerceIn(0f, 1f)

        // Discos Izquierda (Interior grueso, Medio, Exterior fino)
        drawRoundRect(
            color = barColor.copy(alpha = plateAlpha),
            topLeft = Offset(18f * scale, 38f * scale),
            size = Size(5.5f * scale, 28f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5f * scale)
        )
        drawRoundRect(
            color = barColor.copy(alpha = plateAlpha),
            topLeft = Offset(25f * scale, 42f * scale),
            size = Size(4f * scale, 20f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * scale)
        )
        drawRoundRect(
            color = barColor.copy(alpha = plateAlpha),
            topLeft = Offset(13.5f * scale, 45f * scale),
            size = Size(3f * scale, 14f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * scale)
        )

        // Discos Derecha (Medio, Interior grueso, Exterior fino)
        drawRoundRect(
            color = barColor.copy(alpha = plateAlpha),
            topLeft = Offset(91f * scale, 42f * scale),
            size = Size(4f * scale, 20f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * scale)
        )
        drawRoundRect(
            color = barColor.copy(alpha = plateAlpha),
            topLeft = Offset(97f * scale, 38f * scale),
            size = Size(5.5f * scale, 28f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5f * scale)
        )
        drawRoundRect(
            color = barColor.copy(alpha = plateAlpha),
            topLeft = Offset(103.5f * scale, 45f * scale),
            size = Size(3f * scale, 14f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * scale)
        )
    }
}

/**
 * Dibuja la silueta hipertrofiada de Toji Fushiguro:
 * - Cabello característico de Toji Fushiguro con mechones manga/anime desordenados y flequillo.
 * - Hombros 3D (deltoides), trapecios masivos y torso estético en V.
 * - Cortes anatómicos en espacio negativo (clavículas, esternón, pectorales, dorsales).
 */
private fun DrawScope.drawTojiFigureLayer(
    scale: Float,
    progress: Float,
    figureColor: Color,
    cutColor: Color
) {
    val alpha = progress.coerceIn(0f, 1f)
    val cx = 60f * scale

    // ========================================================================
    // 1. Cabeza y Cabello Estilo Anime Manga de Toji Fushiguro
    // ========================================================================
    // Silueta unificada del cabello desordenado con mechones manga
    val tojiHeadAndHairPath = Path().apply {
        // Barbilla
        moveTo(60f * scale, 36f * scale)
        // Mandíbula izquierda hacia lateral
        lineTo(55f * scale, 33f * scale)
        // Mechón lateral izquierdo bajo (patilla)
        lineTo(52f * scale, 33.5f * scale)
        lineTo(53f * scale, 29.5f * scale)
        // Mechón lateral medio izquierdo (pico hacia afuera)
        lineTo(49.5f * scale, 27f * scale)
        lineTo(53f * scale, 25f * scale)
        // Mechón superior izquierdo (pico desgreñado)
        lineTo(52f * scale, 21.5f * scale)
        lineTo(55.5f * scale, 22.5f * scale)
        // Mechón central de la coronilla de Toji
        lineTo(58.5f * scale, 19.5f * scale)
        lineTo(61f * scale, 22f * scale)
        // Mechón superior derecho
        lineTo(64.5f * scale, 20.5f * scale)
        lineTo(64.5f * scale, 24f * scale)
        // Mechón lateral derecho exterior
        lineTo(69f * scale, 26.5f * scale)
        lineTo(66.5f * scale, 28.5f * scale)
        // Mechón lateral derecho bajo (patilla)
        lineTo(68.5f * scale, 32.5f * scale)
        lineTo(65f * scale, 33f * scale)
        // Mandíbula derecha hacia barbilla
        lineTo(60f * scale, 36f * scale)
        close()
    }
    drawPath(tojiHeadAndHairPath, color = figureColor.copy(alpha = alpha))

    // Cortes en espacio negativo que definen el flequillo manga de Toji
    val hairCutStroke = 1.3f * scale
    val bangPath = Path().apply {
        moveTo(54.5f * scale, 27f * scale)
        lineTo(57f * scale, 30.5f * scale)
        lineTo(59.5f * scale, 27.5f * scale)
        lineTo(62f * scale, 31f * scale)
        lineTo(65f * scale, 28f * scale)
    }
    drawPath(
        path = bangPath,
        color = cutColor.copy(alpha = alpha),
        style = Stroke(width = hairCutStroke, cap = StrokeCap.Round)
    )

    // Mechón superior interno
    drawLine(
        color = cutColor.copy(alpha = alpha),
        start = Offset(57.5f * scale, 22f * scale),
        end = Offset(59f * scale, 25.5f * scale),
        strokeWidth = hairCutStroke,
        cap = StrokeCap.Round
    )
    drawLine(
        color = cutColor.copy(alpha = alpha),
        start = Offset(63f * scale, 21.5f * scale),
        end = Offset(61.5f * scale, 25.5f * scale),
        strokeWidth = hairCutStroke,
        cap = StrokeCap.Round
    )

    // ========================================================================
    // 2. Torso en V y Hombros Hipertrofiados de Toji (con cuello integrado)
    // ========================================================================
    val torsoPath = Path().apply {
        // Cuello izquierdo conectando directamente bajo la mandíbula
        moveTo(56.5f * scale, 34f * scale)
        lineTo(56.5f * scale, 38.5f * scale)
        // Trapecios y hombro izquierdo
        cubicTo(
            52f * scale, 39f * scale,
            46f * scale, 44f * scale,
            40f * scale, 47f * scale
        )
        // Cabeza lateral del deltoides izquierdo
        cubicTo(
            37.5f * scale, 48.5f * scale,
            36.5f * scale, 50.5f * scale,
            36.5f * scale, 52.5f * scale
        )
        cubicTo(
            36.5f * scale, 54f * scale,
            37.5f * scale, 55.5f * scale,
            39.5f * scale, 56.5f * scale
        )
        // Dorsal izquierdo hacia la cintura en V
        cubicTo(
            46.5f * scale, 58.5f * scale,
            53.5f * scale, 64.5f * scale,
            60f * scale, 73.5f * scale // Pico inferior de la cintura
        )
        // Dorsal derecho hacia arriba
        cubicTo(
            66.5f * scale, 64.5f * scale,
            73.5f * scale, 58.5f * scale,
            80.5f * scale, 56.5f * scale
        )
        // Cabeza lateral del deltoides derecho
        cubicTo(
            82.5f * scale, 55.5f * scale,
            83.5f * scale, 54f * scale,
            83.5f * scale, 52.5f * scale
        )
        cubicTo(
            83.5f * scale, 50.5f * scale,
            82.5f * scale, 48.5f * scale,
            80f * scale, 47f * scale
        )
        // Hombro y trapecio derecho
        cubicTo(
            74f * scale, 44f * scale,
            68f * scale, 39f * scale,
            63.5f * scale, 38.5f * scale
        )
        // Cuello derecho conectando con la mandíbula
        lineTo(63.5f * scale, 34f * scale)
        close()
    }
    drawPath(torsoPath, color = figureColor.copy(alpha = alpha))

    // ========================================================================
    // 3. Cortes anatómicos en espacio negativo (color de superficie)
    // ========================================================================
    val cutStrokeWidth = 1.6f * scale

    // Clavículas / hendidura superior del cuello
    val claviclePath = Path().apply {
        moveTo(54f * scale, 41.5f * scale)
        lineTo(60f * scale, 44.5f * scale)
        lineTo(66f * scale, 41.5f * scale)
    }
    drawPath(
        path = claviclePath,
        color = cutColor.copy(alpha = alpha),
        style = Stroke(width = cutStrokeWidth, cap = StrokeCap.Round)
    )

    // Esternón central
    drawLine(
        color = cutColor.copy(alpha = alpha),
        start = Offset(60f * scale, 44.5f * scale),
        end = Offset(60f * scale, 70.5f * scale),
        strokeWidth = cutStrokeWidth,
        cap = StrokeCap.Round
    )

    // Marcación de pectorales y trapecios / escápulas en V
    val chestPecPath = Path().apply {
        moveTo(49f * scale, 53.5f * scale)
        lineTo(60f * scale, 59.5f * scale)
        lineTo(71f * scale, 53.5f * scale)
    }
    drawPath(
        path = chestPecPath,
        color = cutColor.copy(alpha = alpha),
        style = Stroke(width = cutStrokeWidth, cap = StrokeCap.Round)
    )

    // Cortes horizontales de los dorsales / oblicuos
    drawLine(
        color = cutColor.copy(alpha = alpha),
        start = Offset(52f * scale, 64.5f * scale),
        end = Offset(57.5f * scale, 67.5f * scale),
        strokeWidth = cutStrokeWidth,
        cap = StrokeCap.Round
    )
    drawLine(
        color = cutColor.copy(alpha = alpha),
        start = Offset(68f * scale, 64.5f * scale),
        end = Offset(62.5f * scale, 67.5f * scale),
        strokeWidth = cutStrokeWidth,
        cap = StrokeCap.Round
    )
}

/**
 * Logotipo estilizado `T E N Y O` con la 'Y' resaltada en el color primario del tema activo.
 */
@Composable
fun TenyoBrandTitle(
    modifier: Modifier = Modifier,
    primaryColor: Color = TenyoJadePrimary,
    textColor: Color = Color.White
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // T
        Text(
            text = "T",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 38.sp,
                letterSpacing = 1.sp
            ),
            color = textColor
        )

        // E estilizada (3 barras paralelas)
        TenyoCustomE(
            color = textColor,
            width = 22.dp,
            height = 24.dp
        )

        // N
        Text(
            text = "N",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 38.sp,
                letterSpacing = 1.sp
            ),
            color = textColor
        )

        // Y (destacada en el color primario reactivo del tema)
        Text(
            text = "Y",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 38.sp,
                letterSpacing = 1.sp
            ),
            color = primaryColor
        )

        // O (hexagonal / geométrica)
        TenyoGeometricO(
            color = textColor,
            size = 25.dp
        )
    }
}

/**
 * Letra 'E' estilizada con 3 barras horizontales redondeadas.
 */
@Composable
fun TenyoCustomE(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    width: Dp = 22.dp,
    height: Dp = 24.dp
) {
    Column(
        modifier = modifier
            .width(width)
            .height(height)
            .padding(vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(3.2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(color)
        )
        Box(
            modifier = Modifier
                .width(width * 0.88f)
                .height(3.2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(color)
        )
        Box(
            modifier = Modifier
                .width(width)
                .height(3.2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(color)
        )
    }
}

/**
 * Letra 'O' geométrica con cortes achaflanados hexagonales a juego con el imagotipo.
 */
@Composable
fun TenyoGeometricO(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    size: Dp = 25.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = 3.6.dp.toPx()
        val w = this.size.width
        val h = this.size.height
        val chamfer = 6.dp.toPx()

        val oPath = Path().apply {
            moveTo(chamfer, 0f)
            lineTo(w - chamfer, 0f)
            lineTo(w, chamfer)
            lineTo(w, h - chamfer)
            lineTo(w - chamfer, h)
            lineTo(chamfer, h)
            lineTo(0f, h - chamfer)
            lineTo(0f, chamfer)
            close()
        }
        drawPath(
            path = oPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

/**
 * Lema oficial de Tenyo con líneas divisorias doradas: `— DISCIPLINA • FUERZA • ESFUERZO —`.
 */
@Composable
fun TenyoBrandSloganRow(
    modifier: Modifier = Modifier,
    accentColor: Color = TenyoAccentGold
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(26.dp)
                .height(1.dp)
                .background(accentColor.copy(alpha = 0.5f))
        )
        Text(
            text = "DISCIPLINA · FUERZA · ESFUERZO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.5.sp,
                letterSpacing = 2.8.sp
            ),
            color = accentColor
        )
        Box(
            modifier = Modifier
                .width(26.dp)
                .height(1.dp)
                .background(accentColor.copy(alpha = 0.5f))
        )
    }
}

/**
 * Barra de carga inferior minimalista con texto informativo dinámico.
 */
@Composable
fun TenyoModernSplashLoader(
    isSeedingCatalog: Boolean,
    modifier: Modifier = Modifier,
    primaryColor: Color = TenyoJadePrimary,
    trackColor: Color = TenyoSurfaceContainerHigh
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader_shimmer")
    val slideFraction by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "slide_fraction"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Barra lineal
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.20f))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val indicatorWidth = size.width * 0.44f
                val startX = size.width * slideFraction

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.8f),
                            Color.White,
                            primaryColor.copy(alpha = 0.8f),
                            Color.Transparent
                        ),
                        startX = startX,
                        endX = startX + indicatorWidth
                    ),
                    topLeft = Offset(startX, 0f),
                    size = Size(indicatorWidth, size.height)
                )
            }
        }

        // Texto informativo de estado real
        Text(
            text = if (isSeedingCatalog) {
                "CARGANDO CATÁLOGO DE EJERCICIOS..."
            } else {
                "INICIANDO PROTOCOLO..."
            },
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White.copy(alpha = 0.78f)
        )
    }
}
