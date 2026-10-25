package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary

/**
 * Diálogo modal para selección de hora usando el componente oficial de Material 3 [TimePicker].
 *
 * Responsabilidad única:
 * - Proporciona el selector horario estándar de Android con reloj circular analógico y alternancia AM/PM.
 * - Devuelve la hora y minuto seleccionados al confirmar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("Confirmar", color = HtmlAppPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TenyoTextSecondary)
            }
        },
        title = {
            Text(
                text = "Hora de Recordatorio",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = TenyoSurfaceContainerHigh,
                        selectorColor = HtmlAppPrimary,
                        containerColor = HtmlAppSurface,
                        periodSelectorBorderColor = HtmlBorderSubtle,
                        periodSelectorSelectedContainerColor = HtmlAppPrimary,
                        periodSelectorSelectedContentColor = TenyoOnJadePrimary,
                        periodSelectorUnselectedContainerColor = TenyoSurfaceContainerHigh,
                        periodSelectorUnselectedContentColor = TenyoTextPrimary,
                        timeSelectorSelectedContainerColor = HtmlAppPrimary.copy(alpha = 0.2f),
                        timeSelectorSelectedContentColor = HtmlAppPrimary,
                        timeSelectorUnselectedContainerColor = TenyoSurfaceContainerHigh,
                        timeSelectorUnselectedContentColor = TenyoTextPrimary
                    )
                )
            }
        },
        containerColor = HtmlAppSurface,
        shape = RoundedCornerShape(24.dp)
    )
}
