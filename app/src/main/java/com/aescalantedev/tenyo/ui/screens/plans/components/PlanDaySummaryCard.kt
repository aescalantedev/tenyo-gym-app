package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanDay
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Bed
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil

/**
 * Tarjeta de resumen y configuración del día seleccionado.
 *
 * Responsabilidad única:
 * - Muestra el nombre del día y enfoque muscular (editable).
 * - Permite alternar con un toque entre Día de Entrenamiento y Día de Descanso (resaltado en rojo).
 * - Muestra mensaje instructivo si es día de recuperación.
 */
@Composable
fun PlanDaySummaryCard(
    day: WorkoutPlanDay?,
    onToggleRestDay: () -> Unit,
    onRenameDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (day == null) return

    val isRest = day.isRestDay

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isRest) RestRedBg else HtmlAppSurface,
            border = BorderStroke(1.dp, if (isRest) RestRedBorder else HtmlBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onRenameDayClick)
                    ) {
                        Text(
                            text = day.dayNameEs.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = if (isRest) RestRedAccent else HtmlAppPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = day.name.ifBlank { if (isRest) "Descanso" else "Rutina Día ${day.dayOfWeek}" },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Lucide.Pencil,
                                contentDescription = "Editar nombre del día",
                                tint = TenyoTextTertiary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // Botón para alternar Descanso vs Entrenamiento
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isRest) RestRedAccent else HtmlAppSurfaceHigh,
                        border = BorderStroke(1.dp, if (isRest) RestRedAccent else HtmlBorderSubtle),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onToggleRestDay)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isRest) Lucide.Bed else Lucide.Dumbbell,
                                contentDescription = null,
                                tint = if (isRest) Color.White else HtmlAppGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRest) "Descanso" else "Entreno",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                if (isRest) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Día programado para recuperación biológica y supercompensación neuromuscular. No es obligatorio realizar ejercicios.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary
                    )
                }
            }
        }
    }
}
