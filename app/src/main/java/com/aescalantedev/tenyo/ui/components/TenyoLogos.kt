package com.aescalantedev.tenyo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.LocalTenyoPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Componente reutilizable del Emblema Lineal de Tenyo (`viewBox 512x512`).
 *
 * Representa la Naginata de Maki Zenin cruzada con la Lanza Invertida del Cielo de Toji Fushiguro,
 * con animacion de trazado progresivo (`d1..d4`) y pulso de acento (`accent`).
 *
 * Puede utilizarse tanto en el `SplashScreen` como en cabeceras o tarjetas de marca.
 */
@Composable
fun TenyoAnimatedLineLogo(
    contentDescriptionText: String,
    modifier: Modifier = Modifier,
    logoSize: Dp = 216.dp,
    animateDraw: Boolean = true
) {
    val palette = LocalTenyoPalette.current
    val lineColor = palette.textPrimary
    val accentColor = palette.jadePrimary

    val drawD1 = remember { Animatable(if (animateDraw) 0f else 1f) }
    val drawD2 = remember { Animatable(if (animateDraw) 0f else 1f) }
    val drawD3 = remember { Animatable(if (animateDraw) 0f else 1f) }
    val drawD4 = remember { Animatable(if (animateDraw) 0f else 1f) }

    LaunchedEffect(animateDraw) {
        if (animateDraw) {
            launch {
                delay(150)
                drawD1.animateTo(1f, tween(durationMillis = 1_100, easing = FastOutSlowInEasing))
            }
            launch {
                delay(450)
                drawD2.animateTo(1f, tween(durationMillis = 1_100, easing = FastOutSlowInEasing))
            }
            launch {
                delay(750)
                drawD3.animateTo(1f, tween(durationMillis = 1_050, easing = FastOutSlowInEasing))
            }
            launch {
                delay(1_000)
                drawD4.animateTo(1f, tween(durationMillis = 950, easing = FastOutSlowInEasing))
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "tenyo_logo_accent_pulse")
    val accentAlpha by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "accent_pulse"
    )

    Canvas(
        modifier = modifier
            .size(logoSize)
            .semantics { contentDescription = contentDescriptionText }
    ) {
        val scale = size.minDimension / 512f
        fun sx(v: Float): Float = v * scale
        fun sy(v: Float): Float = v * scale
        fun sw(v: Float): Float = v * scale

        // 1. Circulo base exterior (d1): M256,18 a238,238 0 1,1 -0.1,0
        if (drawD1.value > 0f) {
            drawArc(
                color = lineColor.copy(alpha = 0.35f),
                startAngle = -90f,
                sweepAngle = 360f * drawD1.value,
                useCenter = false,
                topLeft = Offset(sx(18f), sy(18f)),
                size = Size(sx(476f), sy(476f)),
                style = Stroke(width = sw(5f), cap = StrokeCap.Round)
            )
        }

        // Arco de acento Jade (accent): M256,18 A238,238 0 0 1 488,236
        drawArc(
            color = accentColor.copy(alpha = accentAlpha * drawD1.value),
            startAngle = -90f,
            sweepAngle = 78f * drawD1.value,
            useCenter = false,
            topLeft = Offset(sx(18f), sy(18f)),
            size = Size(sx(476f), sy(476f)),
            style = Stroke(width = sw(5f), cap = StrokeCap.Round)
        )

        // 2. Naginata de Maki (d2)
        val naginataShaft = Path().apply {
            moveTo(sx(150f), sy(388f))
            lineTo(sx(348f), sy(166f))
        }
        drawPartialPath(
            fullPath = naginataShaft,
            progress = drawD2.value,
            color = lineColor,
            strokeWidth = sw(7f)
        )

        val naginataBlade = Path().apply {
            moveTo(sx(332f), sy(152f))
            quadraticTo(sx(392f), sy(92f), sx(410f), sy(90f))
            quadraticTo(sx(394f), sy(128f), sx(350f), sy(168f))
            lineTo(sx(332f), sy(152f))
        }
        drawPartialPath(
            fullPath = naginataBlade,
            progress = drawD2.value,
            color = lineColor,
            strokeWidth = sw(7f)
        )

        // Acento en el collar de la Naginata: M318,176 L336,158
        if (drawD2.value > 0.2f) {
            drawLine(
                color = accentColor.copy(alpha = accentAlpha),
                start = Offset(sx(318f), sy(176f)),
                end = Offset(sx(336f), sy(158f)),
                strokeWidth = sw(9f),
                cap = StrokeCap.Round
            )
        }

        // Cadena de Toji (accent): M186,158 q26,6 34,30 q8,22 2,44
        if (drawD2.value > 0.3f) {
            val chainPath = Path().apply {
                moveTo(sx(186f), sy(158f))
                quadraticTo(sx(212f), sy(164f), sx(220f), sy(188f))
                quadraticTo(sx(228f), sy(210f), sx(222f), sy(232f))
            }
            drawPath(
                path = chainPath,
                color = accentColor.copy(alpha = accentAlpha),
                style = Stroke(
                    width = sw(5f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(sw(10f), sw(9f)),
                        phase = 0f
                    )
                )
            )
        }

        // 3. Lanza Invertida del Cielo (d3)
        val spearBody = Path().apply {
            moveTo(sx(168f), sy(136f))
            lineTo(sx(358f), sy(350f))
            lineTo(sx(346f), sy(362f))
            lineTo(sx(156f), sy(148f))
            close()
        }
        drawPartialPath(
            fullPath = spearBody,
            progress = drawD3.value,
            color = lineColor,
            strokeWidth = sw(6f)
        )

        val spearGuard = Path().apply {
            moveTo(sx(342f), sy(336f))
            lineTo(sx(362f), sy(358f))
        }
        drawPartialPath(
            fullPath = spearGuard,
            progress = drawD3.value,
            color = lineColor,
            strokeWidth = sw(9f)
        )

        // 4. Anillos del pomo y marcas de corte (d4)
        if (drawD4.value > 0f) {
            val pommelCenter = Offset(sx(378f), sy(382f))
            drawArc(
                color = lineColor,
                startAngle = -90f,
                sweepAngle = 360f * drawD4.value,
                useCenter = false,
                topLeft = Offset(pommelCenter.x - sx(20f), pommelCenter.y - sy(20f)),
                size = Size(sx(40f), sy(40f)),
                style = Stroke(width = sw(7f), cap = StrokeCap.Round)
            )

            drawCircle(
                color = accentColor.copy(alpha = accentAlpha * drawD4.value),
                radius = sx(7f),
                center = pommelCenter,
                style = Stroke(width = sw(5f))
            )

            val slash1 = Path().apply {
                moveTo(sx(118f), sy(262f))
                lineTo(sx(172f), sy(262f))
            }
            drawPartialPath(
                fullPath = slash1,
                progress = drawD4.value,
                color = accentColor,
                strokeWidth = sw(6f)
            )

            val slash2 = Path().apply {
                moveTo(sx(132f), sy(288f))
                lineTo(sx(178f), sy(288f))
            }
            drawPartialPath(
                fullPath = slash2,
                progress = drawD4.value,
                color = lineColor.copy(alpha = 0.55f),
                strokeWidth = sw(5f)
            )

            val slash3 = Path().apply {
                moveTo(sx(148f), sy(314f))
                lineTo(sx(184f), sy(314f))
            }
            drawPartialPath(
                fullPath = slash3,
                progress = drawD4.value,
                color = lineColor.copy(alpha = 0.30f),
                strokeWidth = sw(4f)
            )
        }
    }
}

/**
 * Dibuja un segmento parcial (`0f..1f`) de un [Path] utilizando [PathMeasure],
 * replicando la animacion SVG `stroke-dasharray` / `stroke-dashoffset`.
 */
private fun DrawScope.drawPartialPath(
    fullPath: Path,
    progress: Float,
    color: Color,
    strokeWidth: Float
) {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0f) return
    val measure = PathMeasure()
    measure.setPath(fullPath, false)
    val segmentPath = Path()
    measure.getSegment(
        startDistance = 0f,
        stopDistance = measure.length * clamped,
        destination = segmentPath,
        startWithMoveTo = true
    )
    drawPath(
        path = segmentPath,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
