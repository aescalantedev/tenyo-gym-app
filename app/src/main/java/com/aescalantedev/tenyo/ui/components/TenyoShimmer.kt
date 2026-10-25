package com.aescalantedev.tenyo.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh

/**
 * Modificador de efecto Shimmer (resplandor de carga animado) para vistas Skeleton.
 * Aplica un degradado suave y sutil sobre fondos oscuros conforme a la paleta minimalista de Tenyo.
 */
fun Modifier.shimmerEffect(
    shape: Shape = RoundedCornerShape(12.dp),
    baseColor: Color? = null,
    highlightColor: Color? = null
): Modifier = composed {
    val actualBaseColor = baseColor ?: TenyoSurfaceContainer
    val actualHighlightColor = highlightColor ?: TenyoSurfaceContainerHigh.copy(alpha = 0.55f)

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim by transition.animateFloat(
        initialValue = -350f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    val shimmerColors = listOf(
        actualBaseColor,
        actualHighlightColor,
        actualBaseColor
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 350f, y = translateAnim - 350f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    this
        .clip(shape)
        .background(brush)
}

/**
 * Componente bloque Skeleton con forma y efecto shimmer integrados.
 */
@Composable
fun TenyoSkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Box(
        modifier = modifier.shimmerEffect(shape = shape)
    )
}
