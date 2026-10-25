package com.aescalantedev.tenyo.ui.screens.plans.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import java.util.Calendar as JavaCalendar

/**
 * Hero Card compacto y minimalista idéntico a la maqueta de referencia.
 *
 * Responsabilidad única:
 * - Muestra en formato condensado el plan activo y la rutina correspondiente a hoy.
 * - Integra botón circular lila para iniciar el entrenamiento de hoy de forma directa.
 * - Al pulsar el resto de la tarjeta navega al detalle semanal.
 */
@Composable
fun ActivePlanHeroCard(
    plan: WorkoutPlan,
    onNavigateToDetail: () -> Unit,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayDayOfWeek = when (JavaCalendar.getInstance().get(JavaCalendar.DAY_OF_WEEK)) {
        JavaCalendar.MONDAY -> 1
        JavaCalendar.TUESDAY -> 2
        JavaCalendar.WEDNESDAY -> 3
        JavaCalendar.THURSDAY -> 4
        JavaCalendar.FRIDAY -> 5
        JavaCalendar.SATURDAY -> 6
        JavaCalendar.SUNDAY -> 7
        else -> 1
    }
    val todayDay = plan.getDayForDayOfWeek(todayDayOfWeek)
    val todayRoutineText = if (todayDay?.isRestDay == true) {
        "Hoy: Descanso y Recuperación"
    } else {
        "Hoy: ${todayDay?.name ?: "Rutina de Hoy"}"
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TenyoSurfaceContainerLow,
        border = BorderStroke(1.dp, HtmlAppPrimary.copy(alpha = 0.22f)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onNavigateToDetail)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(HtmlAppPrimary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "TU PLAN ACTIVO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = HtmlAppPrimary
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TenyoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = todayRoutineText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = TenyoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Botón circular Play en acento primario
            Surface(
                shape = CircleShape,
                color = HtmlAppPrimary,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onStartSession)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.Play,
                        contentDescription = "Entrenar rutina",
                        tint = TenyoOnJadePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
