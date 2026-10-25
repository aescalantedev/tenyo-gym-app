package com.aescalantedev.tenyo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary

/**
 * Componentes reutilizables de progreso y carga del sistema de diseno Tenyo.
 */

/**
 * Barra de carga minimalista con indicador deslizante continuo (inspirada en `.loader` del Splash).
 *
 * @param modifier Modificador de layout externo.
 * @param width Ancho total de la barra (por defecto `120.dp`).
 * @param height Grosor de la barra (por defecto `2.dp`).
 * @param indicatorColor Color del segmento movil activo.
 * @param trackColor Color de fondo de la pista.
 */
@Composable
fun TenyoMinimalLoader(
    modifier: Modifier = Modifier,
    width: Dp = 120.dp,
    height: Dp = 2.dp,
    indicatorColor: Color = TenyoJadePrimary,
    trackColor: Color = TenyoTextPrimary.copy(alpha = 0.12f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tenyo_minimal_loader")
    val slideFraction by infiniteTransition.animateFloat(
        initialValue = -0.40f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loader_slide_fraction"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(height))
            .background(trackColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val indicatorWidth = size.width * 0.40f
            val startX = size.width * slideFraction
            drawRoundRect(
                color = indicatorColor,
                topLeft = Offset(startX, 0f),
                size = Size(indicatorWidth, size.height)
            )
        }
    }
}

/**
 * Barra de progreso segmentada lineal por pasos para flujos guiados como el Onboarding.
 */
@Composable
fun TenyoStepProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    height: Dp = 3.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            val isActive = i <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(height)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isActive) TenyoJadePrimary else TenyoOutlineSubtle)
            )
        }
    }
}

/**
 * Anillo de progreso circular reutilizable para mostrar porcentaje de completitud o series.
 */
@Composable
fun TenyoCircularProgressRing(
    progressFraction: Float,
    centerLabel: String,
    modifier: Modifier = Modifier,
    ringSize: Dp = 68.dp,
    strokeWidth: Dp = 6.dp,
    activeColor: Color = TenyoJadePrimary,
    trackColor: Color = TenyoOutline
) {
    val clampedProgress = progressFraction.coerceIn(0f, 1f)
    Box(
        modifier = modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = 360f * clampedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = centerLabel,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = activeColor
        )
    }
}
