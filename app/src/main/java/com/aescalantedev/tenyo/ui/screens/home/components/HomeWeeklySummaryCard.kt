package com.aescalantedev.tenyo.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.ProgressSummary
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import java.util.Locale

/**
 * Tarjeta de Resumen Semanal (Inspirada en Lyfta y optimizada al diseno minimal de Tenyo).
 * Muestra el conteo de entrenamientos, duracion acumulada y volumen total en kg de la semana.
 */
@Composable
fun HomeWeeklySummaryCard(
    progressSummary: ProgressSummary?,
    onViewMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val workouts = progressSummary?.completedWorkoutsCount ?: 0
    val totalMinutes = progressSummary?.totalDurationMinutes ?: 0
    val hours = totalMinutes / 60
    val remainingMinutes = totalMinutes % 60
    val durationText = if (hours > 0) "${hours}h ${remainingMinutes}m" else "${remainingMinutes}m"
    val volumeKg = progressSummary?.totalVolumeKg?.toInt() ?: 0
    val volumeFormatted = String.format(Locale.US, "%,d", volumeKg).replace(',', ' ')

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera de la seccion: Titulo a la izquierda, "Ver mas" a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tu resumen semanal",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )

                Text(
                    text = "Ver más",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TenyoJadePrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onViewMore)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Columnas de metricas semanales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metrica 1: Entrenamientos
                WeeklyMetricColumn(
                    value = "$workouts",
                    label = "Entrenamientos",
                    modifier = Modifier.weight(1f)
                )

                // Metrica 2: Duracion
                WeeklyMetricColumn(
                    value = durationText,
                    label = "Duración",
                    modifier = Modifier.weight(1f)
                )

                // Metrica 3: Volumen
                WeeklyMetricColumn(
                    value = "$volumeFormatted kg",
                    label = "Volumen",
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}

@Composable
private fun WeeklyMetricColumn(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = TenyoTextPrimary
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TenyoTextSecondary
        )
    }
}
