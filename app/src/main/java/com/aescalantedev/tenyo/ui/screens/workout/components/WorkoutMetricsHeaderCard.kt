package com.aescalantedev.tenyo.ui.screens.workout.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary

/**
 * Tarjeta superior horizontal minimalista de métricas de la sesión activa (Duración, Volumen, Series).
 * Actúa como contenedor compartido (Shared Bounds) enlazado con la mini-barra flotante de HomeScreen.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WorkoutMetricsHeaderCard(
    durationText: String,
    volumeKg: Double,
    completedSetsCount: Int,
    title: String = "ENTRENAMIENTO EN CURSO",
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier
) {
    val cardShape = remember { RoundedCornerShape(16.dp) }
    val boundsTransform = remember {
        BoundsTransform { _, _ ->
            tween(durationMillis = 350, easing = FastOutSlowInEasing)
        }
    }

    val transitionModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "active-workout-metrics-card"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                clipInOverlayDuringTransition = OverlayClip(cardShape),
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
            )
        }
    } else Modifier

    Surface(
        shape = cardShape,
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier
            .fillMaxWidth()
            .then(transitionModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Cabecera superior con indicador jade y titulo dinamico
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(TenyoJadePrimary)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title.ifBlank { "ENTRENAMIENTO EN CURSO" },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = TenyoJadePrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fila con las 3 metricas clave
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricColumn(
                    title = "Duración",
                    value = durationText,
                    isHighlight = true
                )
                MetricColumn(
                    title = "Volumen",
                    value = "${formatVolume(volumeKg)} kg",
                    isHighlight = false
                )
                MetricColumn(
                    title = "series",
                    value = "$completedSetsCount",
                    isHighlight = false
                )
            }
        }
    }
}

@Composable
private fun MetricColumn(
    title: String,
    value: String,
    isHighlight: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = TenyoTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isHighlight) TenyoJadePrimary else TenyoTextPrimary
        )
    }
}

private fun formatVolume(volumeKg: Double): String {
    return if (volumeKg % 1.0 == 0.0) {
        volumeKg.toInt().toString()
    } else {
        "%.1f".format(volumeKg)
    }
}
