package com.aescalantedev.tenyo.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import com.aescalantedev.tenyo.ui.components.CategoryVisuals
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.theme.TenyoAccentGold
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Award
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trophy
import com.composables.icons.lucide.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tarjeta de Historial de Entrenamiento Reciente (Inspirada en el Feed de Lyfta).
 * Muestra el usuario, hora de sesion, estadisticas clave (duracion, volumen y records)
 * y una cuadricula visual de los ejercicios realizados.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeRecentWorkoutsCard(
    session: WorkoutSession,
    userName: String,
    modifier: Modifier = Modifier
) {
    val durationMinutes = (session.elapsedSeconds / 60L).coerceAtLeast(1L)
    val hours = durationMinutes / 60
    val mins = durationMinutes % 60
    val durationText = if (hours > 0) "${hours}h ${mins}m" else "${mins} min"
    val volumeFormatted = String.format(Locale.US, "%,d", session.totalVolumeKg.toInt()).replace(',', ' ')

    val dateFormatted = runCatching {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        "Hoy a las ${sdf.format(Date(session.startedAtEpochMillis))}"
    }.getOrDefault("Hoy")

    // Agrupar sets por ejercicio para la cuadricula estilo Lyfta
    val exerciseGroups = session.setLogs.groupBy { it.exerciseId to it.exerciseName }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Fila de Usuario / Avatar y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = TenyoSurfaceContainerHigh,
                    border = BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.User,
                            contentDescription = null,
                            tint = TenyoJadePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Titulo de la sesion
            Text(
                text = session.title.ifBlank { "Entrenamiento de Protocolo" },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TenyoTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metricas de la sesion: Duracion, Volumen y Records
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Duracion
                Column {
                    Text(
                        text = "Duración",
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextTertiary
                    )
                    Text(
                        text = durationText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                }

                // Volumen
                Column {
                    Text(
                        text = "Volumen",
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextTertiary
                    )
                    Text(
                        text = "$volumeFormatted kg",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Records personales o series completadas
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(TenyoSurfaceContainerHigh)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Trophy,
                        contentDescription = null,
                        tint = TenyoAccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${session.completedSetsCount} series",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoAccentGold
                    )
                }
            }

            // Cuadricula / chips de los ejercicios realizados
            if (exerciseGroups.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    exerciseGroups.entries.take(6).forEach { entry ->
                        val (_, exerciseName) = entry.key
                        val sets = entry.value
                        val completedSets = sets.count { it.isCompleted }
                        val firstLog = sets.firstOrNull()
                        val icon = if (firstLog != null) CategoryVisuals.iconFor(firstLog.bodyPart) else Lucide.Dumbbell

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TenyoSurfaceContainerHigh,
                            border = BorderStroke(1.dp, TenyoOutlineSubtle),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = TenyoJadePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${completedSets.coerceAtLeast(1)} x ${exerciseName.replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TenyoTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
