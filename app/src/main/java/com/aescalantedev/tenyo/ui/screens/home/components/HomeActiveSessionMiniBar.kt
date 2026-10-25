package com.aescalantedev.tenyo.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play

/**
 * Barra flotante interactiva y minimalista de sesión activa en HomeScreen (Estilo Cápsula Dynamic Island).
 *
 * Muestra el estado del entrenamiento en curso en una sola fila compacta (~54dp de altura):
 * - Indicador de pulso neón parpadeante en el color primario del tema.
 * - Título del entrenamiento y métricas en vivo (tiempo transcurrido, series completadas y volumen).
 * - Acceso opcional a ver el detalle del plan (`onViewPlanDetail`) antes de reanudar.
 * - Botón compacto "Continuar" para ingresar a la sesión activa.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeActiveSessionMiniBar(
    session: WorkoutSession,
    onResume: () -> Unit,
    onViewPlanDetail: ((planId: String) -> Unit)? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val cardShape = remember { RoundedCornerShape(20.dp) }
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

    val durationMinutes = (session.elapsedSeconds / 60L).coerceAtLeast(0L)
    val durationSecs = session.elapsedSeconds % 60L
    val durationFormatted = String.format("%02d:%02d", durationMinutes, durationSecs)

    Surface(
        shape = cardShape,
        color = TenyoSurfaceContainerHigh,
        border = BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.40f)),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .then(transitionModifier)
            .clip(cardShape)
            .clickable(onClick = onResume)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Punto de actividad con pulso neón
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(TenyoJadePrimary)
                    .alpha(pulseAlpha)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // 2. Información del entrenamiento: Título + Métricas en una columna compacta
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = session.title.ifBlank { "Entrenamiento en curso" },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = TenyoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Clock,
                        contentDescription = null,
                        tint = TenyoJadePrimary,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = durationFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoJadePrimary,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextTertiary
                    )

                    Text(
                        text = "${session.completedSetsCount} series",
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextSecondary,
                        fontSize = 11.sp
                    )

                    if (session.totalVolumeKg > 0.0) {
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextTertiary
                        )
                        Text(
                            text = "${formatVolume(session.totalVolumeKg)} kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 3. Botón para ver detalle del plan (si la sesión está asociada a un plan)
            val currentPlanId = session.planId
            if (!currentPlanId.isNullOrBlank() && onViewPlanDetail != null) {
                Surface(
                    shape = CircleShape,
                    color = TenyoSurfaceContainer,
                    border = BorderStroke(1.dp, TenyoOutlineSubtle),
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable { onViewPlanDetail(currentPlanId) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.Info,
                            contentDescription = "Ver detalle del plan",
                            tint = TenyoTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // 4. Botón de acción principal "Continuar"
            Surface(
                shape = RoundedCornerShape(50),
                color = TenyoJadePrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onResume)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continuar",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoOnJadePrimary,
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Lucide.Play,
                        contentDescription = null,
                        tint = TenyoOnJadePrimary,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

private fun formatVolume(volumeKg: Double): String {
    return if (volumeKg % 1.0 == 0.0) {
        volumeKg.toInt().toString()
    } else {
        "%.1f".format(volumeKg)
    }
}
