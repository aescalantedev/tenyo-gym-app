package com.aescalantedev.tenyo.ui.screens.progress.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.WorkoutDayMarker
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Componente de Calendario de Entrenamientos mensual interactivo, reutilizable y limpio.
 *
 * Muestra los dias del mes actual o navegado, destacando visualmente aquellos en los que
 * el usuario completo sesiones reales. Al presionar cualquier dia, despliega el detalle de
 * esa jornada (rutinas, volumen y duracion).
 */
@Composable
fun TenyoWorkoutCalendarCard(
    currentMonth: YearMonth,
    workoutDaysMap: Map<Long, WorkoutDayMarker>,
    selectedDayEpoch: Long?,
    onSelectDay: (Long?) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthTitle = remember(currentMonth) {
        val formatter = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"))
        currentMonth.format(formatter).replaceFirstChar { it.uppercase() }
    }

    val today = remember { LocalDate.now() }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    // 0 = Lunes .. 6 = Domingo
    val startDayOffset = (firstDayOfMonth.dayOfWeek.value - 1)

    // Entrenamientos dentro del mes visualizado
    val workoutsInThisMonth = remember(currentMonth, workoutDaysMap) {
        val startEpoch = firstDayOfMonth.toEpochDay()
        val endEpoch = currentMonth.atEndOfMonth().toEpochDay()
        workoutDaysMap.filterKeys { it in startEpoch..endEpoch }
    }
    val totalTrainedDays = workoutsInThisMonth.size
    val totalMonthVolume = workoutsInThisMonth.values.sumOf { it.totalVolumeKg }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Cabecera del Calendario con Navegacion de Meses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Calendario de Entrenamientos",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                    Text(
                        text = "$totalTrainedDays días activos este mes",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = TenyoSurfaceContainerHigh,
                    border = BorderStroke(1.dp, TenyoOutlineSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onPreviousMonth)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.ChevronLeft,
                                    contentDescription = "Mes anterior",
                                    tint = TenyoTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = monthTitle,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TenyoTextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onNextMonth)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.ChevronRight,
                                    contentDescription = "Mes siguiente",
                                    tint = TenyoTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Fila de encabezado de dias de la semana (L, M, X, J, V, S, D)
            val weekDays = listOf("L", "M", "X", "J", "V", "S", "D")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekDays.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cuadricula de dias del mes
            val totalCells = startDayOffset + daysInMonth
            val totalRows = (totalCells + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (rowIndex in 0 until totalRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (colIndex in 0 until 7) {
                            val cellIndex = rowIndex * 7 + colIndex
                            val dayNumber = cellIndex - startDayOffset + 1

                            if (dayNumber in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayNumber)
                                val epochDay = date.toEpochDay()
                                val marker = workoutDaysMap[epochDay]
                                val hasWorkout = marker != null
                                val isToday = date == today
                                val isSelected = selectedDayEpoch == epochDay

                                DayCell(
                                    dayNumber = dayNumber,
                                    hasWorkout = hasWorkout,
                                    isToday = isToday,
                                    isSelected = isSelected,
                                    onClick = { onSelectDay(epochDay) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Detalle expandible del dia seleccionado
            val selectedMarker = selectedDayEpoch?.let { workoutDaysMap[it] }
            val selectedDate = selectedDayEpoch?.let { LocalDate.ofEpochDay(it) }

            AnimatedVisibility(
                visible = selectedDayEpoch != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (selectedDate != null) {
                    val dateFormatted = remember(selectedDate) {
                        val fmt = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("es-ES"))
                        selectedDate.format(fmt).replaceFirstChar { it.uppercase() }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = TenyoSurfaceContainerHigh,
                        border = BorderStroke(
                            1.dp,
                            if (selectedMarker != null) TenyoJadePrimary.copy(alpha = 0.5f) else TenyoOutlineSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dateFormatted,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TenyoTextPrimary
                                )

                                if (selectedMarker != null) {
                                    TenyoBadge(
                                        text = "${selectedMarker.workoutsCount} entreno${if (selectedMarker.workoutsCount > 1) "s" else ""}",
                                        tone = TenyoBadgeTone.JADE
                                    )
                                } else {
                                    Text(
                                        text = "Descanso",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TenyoTextTertiary
                                    )
                                }
                            }

                            if (selectedMarker != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Lucide.Clock,
                                            contentDescription = null,
                                            tint = TenyoJadePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${selectedMarker.durationMinutes} min",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = TenyoTextPrimary
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Lucide.Dumbbell,
                                            contentDescription = null,
                                            tint = TenyoJadePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        val volStr = if (selectedMarker.totalVolumeKg % 1.0 == 0.0) {
                                            selectedMarker.totalVolumeKg.toInt().toString()
                                        } else {
                                            "%.1f".format(selectedMarker.totalVolumeKg)
                                        }
                                        Text(
                                            text = "$volStr kg",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = TenyoTextPrimary
                                        )
                                    }
                                }

                                if (selectedMarker.sessionTitles.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = selectedMarker.sessionTitles.joinToString(", "),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TenyoTextSecondary
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No se registraron sesiones en este día. Buen momento de recuperación muscular.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TenyoTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    hasWorkout: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> TenyoJadePrimary
        hasWorkout -> TenyoJadePrimary.copy(alpha = 0.22f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> TenyoOnJadePrimary
        hasWorkout -> TenyoJadePrimary
        isToday -> TenyoTextPrimary
        else -> TenyoTextSecondary
    }

    val border = when {
        isToday && !isSelected -> BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.7f))
        hasWorkout && !isSelected -> BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.45f))
        else -> null
    }

    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .then(if (border != null) Modifier.clip(CircleShape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber.toString(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (hasWorkout || isToday || isSelected) FontWeight.ExtraBold else FontWeight.Normal
                ),
                color = textColor
            )

            if (hasWorkout && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(TenyoJadePrimary)
                )
            }
        }
    }
}
