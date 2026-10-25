package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary

/**
 * Diálogo modal de información rápida al presionar el icono "?" en una tarjeta de ejercicio.
 */
@Composable
fun ExerciseQuickInfoDialog(
    exercise: Exercise,
    onDismiss: () -> Unit,
    onOpenFullDetail: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TenyoSurfaceContainerHigh,
        title = {
            Text(
                text = exercise.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TenyoBadge(
                        text = exercise.localizedTargetMuscle(),
                        tone = TenyoBadgeTone.JADE
                    )
                    TenyoBadge(
                        text = exercise.localizedEquipment(),
                        tone = TenyoBadgeTone.VIOLET
                    )
                }

                Text(
                    text = "Instrucciones de ejecución:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )

                Text(
                    text = exercise.getInstructionsText(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenFullDetail,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TenyoJadePrimary,
                    contentColor = TenyoOnJadePrimary
                )
            ) {
                Text("Ver ficha completa", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TenyoTextSecondary)
            }
        }
    )
}
