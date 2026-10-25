package com.aescalantedev.tenyo.ui.screens.progress.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.MuscleDistributionItem
import com.aescalantedev.tenyo.core.domain.model.PersonalRecord
import com.aescalantedev.tenyo.core.domain.model.PhysicalRank
import com.aescalantedev.tenyo.core.domain.model.WeeklyActivityDay
import com.aescalantedev.tenyo.ui.components.CategoryVisuals
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletContainer
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trophy

/**
 * Tarjeta hero de evolucion de Rango Fisico Tenyo (Grado 4 a Restriccion Celestial).
 */
@Composable
fun TenyoRankEvolutionCard(
    currentRank: PhysicalRank,
    nextRank: PhysicalRank?,
    workoutsToNext: Int,
    progressFraction: Float,
    totalVolumeKg: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.5.dp, TenyoVioletSecondary.copy(alpha = 0.45f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TenyoBadge(
                    text = "EVOLUCION FISICA TENYO",
                    tone = TenyoBadgeTone.VIOLET
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TenyoVioletContainer,
                    border = BorderStroke(1.dp, TenyoVioletSecondary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = currentRank.kanjiBadge,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = TenyoVioletSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentRank.titleEs,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentRank.subtitleEs,
                style = MaterialTheme.typography.bodySmall,
                color = TenyoTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (nextRank != null) {
                        "Faltan $workoutsToNext sesiones para ${nextRank.titleEs}"
                    } else {
                        "Cumbre alcanzada: Restriccion Celestial"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = TenyoJadePrimary
                )
                Text(
                    text = "${(progressFraction * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TenyoJadePrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = TenyoJadePrimary,
                trackColor = TenyoSurfaceContainerHigh
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Volumen historico movilizado: ${totalVolumeKg.toInt()} kg",
                style = MaterialTheme.typography.labelSmall,
                color = TenyoTextTertiary
            )
        }
    }
}

/**
 * Tarjeta Bento de indicador clave de rendimiento (KPI) en la pantalla de Progreso.
 */
@Composable
fun KpiBentoCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                if (badgeText != null) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoJadePrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TenyoTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Tarjeta de grafico de barras de actividad semanal (Lunes a Domingo).
 */
@Composable
fun WeeklyActivityChartCard(
    days: List<WeeklyActivityDay>,
    modifier: Modifier = Modifier
) {
    val maxMinutes = (days.maxOfOrNull { it.durationMinutes } ?: 60).coerceAtLeast(30)
    val totalWeeklyMinutes = days.sumOf { it.durationMinutes }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Actividad Semanal",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                    Text(
                        text = "Minutos de entrenamiento por dia",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary
                    )
                }
                TenyoBadge(
                    text = "$totalWeeklyMinutes min / sem",
                    tone = TenyoBadgeTone.JADE
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    val ratio = (day.durationMinutes.toFloat() / maxMinutes.toFloat()).coerceIn(0.08f, 1f)
                    val barColor = when {
                        day.isToday -> TenyoJadePrimary
                        day.durationMinutes > 0 -> TenyoVioletSecondary
                        else -> TenyoSurfaceContainerHigh
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (day.durationMinutes > 0) {
                            Text(
                                text = "${day.durationMinutes}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (day.isToday) TenyoJadePrimary else TenyoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height((84.dp * ratio).coerceAtLeast(8.dp))
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                .background(barColor)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.Medium
                            ),
                            color = if (day.isToday) TenyoJadePrimary else TenyoTextTertiary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de distribucion porcentual de series efectivas por grupo muscular.
 */
@Composable
fun MuscleDistributionCard(
    items: List<MuscleDistributionItem>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Distribucion por Grupo Muscular",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Proporcion de series efectivas completadas",
                style = MaterialTheme.typography.bodySmall,
                color = TenyoTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            items.forEachIndexed { index, item ->
                val barColor = CategoryVisuals.accentColorFor(item.category)
                if (index > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.category.nameEs,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = TenyoTextPrimary
                        )
                        Text(
                            text = "${item.percentage}% (${item.completedSets} series)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = barColor
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (item.percentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(50)),
                        color = barColor,
                        trackColor = TenyoSurfaceContainerHigh
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de escalera completa de Grados Fisicos Tenyo.
 */
@Composable
fun TenyoAllRanksLadderCard(
    currentRank: PhysicalRank,
    completedWorkouts: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Jerarquia de Grados Fisicos Tenyo",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            PhysicalRank.entries.forEach { rank ->
                val unlocked = completedWorkouts >= rank.minWorkoutsRequired
                val isCurrent = rank == currentRank
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (unlocked) TenyoJadeContainer else TenyoSurfaceContainerHigh,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = rank.kanjiBadge.take(2),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (unlocked) TenyoJadePrimary else TenyoTextTertiary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = rank.titleEs,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isCurrent) TenyoJadePrimary else if (unlocked) TenyoTextPrimary else TenyoTextTertiary
                            )
                            Text(
                                text = "Requisito: ${rank.minWorkoutsRequired} entrenamientos",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoTextSecondary
                            )
                        }
                    }

                    if (isCurrent) {
                        TenyoBadge(text = "ACTUAL", tone = TenyoBadgeTone.JADE)
                    } else if (unlocked) {
                        TenyoBadge(text = "SUPERADO", tone = TenyoBadgeTone.VIOLET)
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de fila de Record Personal (PR) por ejercicio.
 */
@Composable
fun PersonalRecordRowCard(
    record: PersonalRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = TenyoCrimsonTertiary.copy(alpha = 0.16f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.Trophy,
                        contentDescription = null,
                        tint = TenyoCrimsonTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.exerciseName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${record.bodyPart.nameEs} · ${record.repsAtMaxWeight} repeticiones limpias",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${record.maxWeightKg.toInt()} kg",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TenyoJadePrimary
                )
                if (record.improvementKg > 0.0) {
                    Text(
                        text = "+${record.improvementKg.toInt()} kg PR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoCrimsonTertiary
                    )
                }
            }
        }
    }
}
