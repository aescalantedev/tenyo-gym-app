package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanDay
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import java.util.Calendar as JavaCalendar

/**
 * Selector horizontal semanal (Lunes a Domingo / Días 1 a 7).
 *
 * Responsabilidad única:
 * - Renderiza cada día con su inicial y día del mes.
 * - Distingue con rojo los días marcados como Descanso.
 * - Destaca el día actualmente seleccionado.
 */
@Composable
fun PlanWeeklyStrip(
    selectedDayOfWeek: Int,
    days: List<WorkoutPlanDay>,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = JavaCalendar.getInstance()
    calendar.firstDayOfWeek = JavaCalendar.MONDAY
    calendar.set(JavaCalendar.DAY_OF_WEEK, JavaCalendar.MONDAY)
    val startDayOfMonth = calendar.get(JavaCalendar.DAY_OF_MONTH)

    val dayHeaders = listOf(
        1 to "L",
        2 to "M",
        3 to "X",
        4 to "J",
        5 to "V",
        6 to "S",
        7 to "D"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = dayHeaders,
            key = { it.first }
        ) { (dayIndex, letter) ->
            val isSelected = selectedDayOfWeek == dayIndex
            val planDay = days.firstOrNull { it.dayOfWeek == dayIndex }
            val isRestDay = planDay?.isRestDay ?: false
            val dayNumber = startDayOfMonth + (dayIndex - 1)

            val targetContainerColor = when {
                isSelected && isRestDay -> RestRedAccent
                isSelected -> HtmlAppPrimary
                isRestDay -> RestRedBg
                else -> HtmlAppSurface
            }

            val bgAnimation by animateColorAsState(
                targetValue = targetContainerColor,
                label = "dayBgAnim"
            )
            val scaleAnimation by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1.0f,
                label = "dayScaleAnim"
            )

            val border = when {
                isSelected -> null
                isRestDay -> BorderStroke(1.dp, RestRedBorder)
                else -> BorderStroke(1.dp, HtmlBorderSubtle)
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = bgAnimation,
                border = border,
                modifier = Modifier
                    .width(62.dp)
                    .height(84.dp)
                    .scale(scaleAnimation)
                    .then(
                        if (isSelected) {
                            Modifier.shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = if (isRestDay) RestRedAccent else HtmlAppPrimary,
                                spotColor = if (isRestDay) RestRedAccent else HtmlAppPrimary
                            )
                        } else Modifier
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectDay(dayIndex) }
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            isSelected -> TenyoOnJadePrimary.copy(alpha = 0.9f)
                            isRestDay -> RestRedAccent
                            else -> TenyoTextSecondary
                        }
                    )

                    Text(
                        text = "$dayNumber",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = when {
                            isSelected -> TenyoOnJadePrimary
                            isRestDay -> Color.White
                            else -> TenyoTextPrimary
                        }
                    )

                    // Indicador inferior: si es descanso se muestra etiqueta/punto rojo
                    if (isRestDay) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) TenyoOnJadePrimary else RestRedAccent,
                            modifier = Modifier.size(6.dp)
                        ) {}
                    } else {
                        val exerciseCount = planDay?.exercises?.size ?: 0
                        Text(
                            text = if (exerciseCount > 0) "${exerciseCount}e" else "—",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isSelected) TenyoOnJadePrimary.copy(alpha = 0.7f) else TenyoTextTertiary
                        )
                    }
                }
            }
        }
    }
}
