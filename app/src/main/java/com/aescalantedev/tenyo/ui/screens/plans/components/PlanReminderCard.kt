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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.BellRing
import com.composables.icons.lucide.Lucide

/**
 * Tarjeta de configuración de recordatorio horario del plan.
 *
 * Responsabilidad única:
 * - Muestra el estado activo/inactivo del recordatorio.
 * - Muestra la hora programada en color dorado.
 * - Al tocar la hora, abre el selector de hora nativo (TimePicker / TimeInput).
 */
@Composable
fun PlanReminderCard(
    reminderEnabled: Boolean,
    reminderHour: Int,
    reminderMinute: Int,
    onToggleReminder: (Boolean) -> Unit,
    onOpenTimePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = run {
        val amPm = if (reminderHour >= 12) "PM" else "AM"
        val hour12 = if (reminderHour == 0) 12 else if (reminderHour > 12) reminderHour - 12 else reminderHour
        String.format("%02d:%02d %s", hour12, reminderMinute, amPm)
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = HtmlAppSurface,
        border = BorderStroke(1.dp, HtmlBorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenTimePicker)
            ) {
                Surface(
                    shape = CircleShape,
                    color = HtmlAppPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.BellRing,
                            contentDescription = null,
                            tint = HtmlAppPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Hora de Recordatorio",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TenyoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = HtmlAppGold
                    )
                }
            }

            Switch(
                checked = reminderEnabled,
                onCheckedChange = onToggleReminder,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TenyoOnJadePrimary,
                    checkedTrackColor = HtmlAppPrimary,
                    uncheckedThumbColor = TenyoTextSecondary,
                    uncheckedTrackColor = TenyoSurfaceContainerHigh
                )
            )
        }
    }
}
