package com.aescalantedev.tenyo.ui.screens.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.screens.progress.components.KpiBentoCard
import com.aescalantedev.tenyo.ui.screens.progress.components.MuscleDistributionCard
import com.aescalantedev.tenyo.ui.screens.progress.components.PersonalRecordRowCard
import com.aescalantedev.tenyo.ui.screens.progress.components.TenyoMuscleRecoverySection
import com.aescalantedev.tenyo.ui.screens.progress.components.TenyoRankEvolutionCard
import com.aescalantedev.tenyo.ui.screens.progress.components.TenyoWorkoutCalendarCard
import com.aescalantedev.tenyo.ui.screens.progress.components.WeeklyActivityChartCard
import com.aescalantedev.tenyo.ui.screens.progress.viewmodel.ProgressUiState
import com.aescalantedev.tenyo.ui.theme.TenyoAmberWarning
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Zap

/**
 * Pantalla de Progreso, Rango Fisico Tenyo, Calendario y Recuperacion Muscular.
 *
 * Estructurada en 4 vistas totalmente entendibles por cualquier persona:
 * - 0 = Resumen (Cuadros de Entrenamientos, Volumen kg, Duracion, Rango y Grafico de Semana)
 * - 1 = Calendario (Calendario mensual interactivo con dias reales entrenados)
 * - 2 = Recuperacion (Modelo Toji interactivo 3D + Porcentaje real de recuperacion con imagenes)
 * - 3 = Records PR (Marcas maximas alcanzadas en Room)
 */
@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onSelectSubTab: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectCalendarDay: (Long?) -> Unit,
    onToggleTojiSide: () -> Unit,
    onSelectExerciseRecord: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val summary = uiState.summary

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TenyoObsidianBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Surface(
                color = TenyoSurfaceContainerLow,
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mi Progreso",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TenyoTextPrimary
                        )
                        Text(
                            text = "Evolución física, actividad real y recuperación",
                            style = MaterialTheme.typography.bodySmall,
                            color = TenyoTextSecondary
                        )
                    }
                }
            }
        }
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(scaffoldPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = scaffoldPadding.calculateTopPadding() + 16.dp,
                bottom = maxOf(
                    scaffoldPadding.calculateBottomPadding(),
                    contentPadding.calculateBottomPadding()
                ) + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Selector de Pestanas Principales (Pills horizontales)
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TenyoSurfaceContainer,
                    border = BorderStroke(1.dp, TenyoOutlineSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        val subTabs = listOf("Resumen", "Calendario", "Recuperación", "Récords PR")
                        subTabs.forEachIndexed { index, label ->
                            val selected = uiState.selectedSubTabIndex == index
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectSubTab(index) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) TenyoJadePrimary else Color.Transparent
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 9.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                        ),
                                        color = if (selected) TenyoOnJadePrimary else TenyoTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (summary != null) {
                when (uiState.selectedSubTabIndex) {
                    // PESTAÑA 0: RESUMEN GENERAL (Cuadros de Entrenamientos, Volumen kg, Duracion, Rango y Grafico Semanal)
                    0 -> {
                        // 1. Los 3 cuadros clave pedidos: Entrenamientos, Volumen kg, Duracion
                        item {
                            val hours = summary.totalDurationMinutes / 60
                            val remMinutes = summary.totalDurationMinutes % 60
                            val volumeFormatted = formatVolume(summary.totalVolumeKg)

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // Fila 1: Entrenamientos y Volumen kg
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    KpiBentoCard(
                                        icon = Lucide.Dumbbell,
                                        iconColor = TenyoJadePrimary,
                                        value = "${summary.completedWorkoutsCount}",
                                        label = "Entrenamientos",
                                        badgeText = "Total sesiones",
                                        modifier = Modifier.weight(1f)
                                    )
                                    KpiBentoCard(
                                        icon = Lucide.Flame,
                                        iconColor = TenyoJadePrimary,
                                        value = "$volumeFormatted kg",
                                        label = "Volumen Total",
                                        badgeText = "Carga acumulada",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Fila 2: Duracion de los entrenamientos y Racha activa
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    KpiBentoCard(
                                        icon = Lucide.Clock,
                                        iconColor = TenyoVioletSecondary,
                                        value = "${hours}h ${remMinutes}m",
                                        label = "Duración Total",
                                        badgeText = "Bajo tensión",
                                        modifier = Modifier.weight(1f)
                                    )
                                    KpiBentoCard(
                                        icon = Lucide.Zap,
                                        iconColor = TenyoAmberWarning,
                                        value = "${summary.activeStreakWeeks} sem",
                                        label = "Racha Activa",
                                        badgeText = if (summary.activeStreakWeeks > 0) "Constancia" else "Iniciando",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // 2. Tarjeta de Rango Fisico Tenyo
                        item {
                            TenyoRankEvolutionCard(
                                currentRank = summary.currentRank,
                                nextRank = summary.nextRank,
                                workoutsToNext = summary.workoutsToNextRank,
                                progressFraction = summary.rankProgressFraction,
                                totalVolumeKg = summary.totalVolumeKg
                            )
                        }

                        // 3. Grafico de barras de actividad semanal real (Lunes a Domingo)
                        item {
                            WeeklyActivityChartCard(days = summary.weeklyActivity)
                        }

                        // 4. Distribucion real por grupo muscular
                        if (summary.muscleDistribution.isNotEmpty()) {
                            item {
                                MuscleDistributionCard(items = summary.muscleDistribution)
                            }
                        }
                    }

                    // PESTAÑA 1: CALENDARIO DE DIAS ENTRENADOS
                    1 -> {
                        item {
                            TenyoWorkoutCalendarCard(
                                currentMonth = uiState.currentCalendarMonth,
                                workoutDaysMap = summary.workoutDaysMap,
                                selectedDayEpoch = uiState.selectedDayEpoch,
                                onSelectDay = onSelectCalendarDay,
                                onPreviousMonth = onPreviousMonth,
                                onNextMonth = onNextMonth
                            )
                        }
                    }

                    // PESTAÑA 2: RECUPERACION MUSCULAR CON MODELO ANATÓMICO + IMAGENES DRAWABLES
                    2 -> {
                        item {
                            TenyoMuscleRecoverySection(
                                muscleRecoveries = summary.muscleRecoveries,
                                workedMuscleOptionIds = summary.workedMuscleOptionIds,
                                isTojiBackView = uiState.isTojiBackView,
                                onToggleTojiSide = onToggleTojiSide,
                                gender = uiState.gender
                            )
                        }
                    }

                    // PESTAÑA 3: RECORDS PERSONALES (PRs) REALES
                    3 -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Récords Personales (PRs)",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TenyoTextPrimary
                                    )
                                    Text(
                                        text = "Cargas máximas y mejores series superadas",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TenyoTextSecondary
                                    )
                                }
                                TenyoBadge(
                                    text = "${summary.personalRecords.size} PRs",
                                    tone = TenyoBadgeTone.CRIMSON
                                )
                            }
                        }

                        if (summary.personalRecords.isEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = TenyoSurfaceContainer,
                                    border = BorderStroke(1.dp, TenyoOutlineSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier.padding(28.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Aún no hay récords registrados. Completa una serie con peso en cualquier entrenamiento para fijar tu primer PR.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TenyoTextSecondary
                                        )
                                    }
                                }
                            }
                        } else {
                            items(
                                items = summary.personalRecords,
                                key = { it.exerciseId }
                            ) { record ->
                                PersonalRecordRowCard(
                                    record = record,
                                    onClick = { onSelectExerciseRecord(record.exerciseId) }
                                )
                            }
                        }
                    }
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
