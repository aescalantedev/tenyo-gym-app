package com.aescalantedev.tenyo.ui.screens.workout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.aescalantedev.tenyo.core.domain.model.WorkoutSetLog
import com.aescalantedev.tenyo.ui.components.TenyoInlineNumericInput
import com.aescalantedev.tenyo.ui.components.TenyoSwipeToDismiss
import com.aescalantedev.tenyo.ui.theme.TenyoAccentGold
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trophy

/**
 * Fila interactiva para registrar una serie en la tabla de entrenamiento.
 * Incorpora soporte de deslizamiento lateral (SwipeToDismiss) para eliminar la serie.
 */
@Composable
fun WorkoutSetRowItem(
    setLog: WorkoutSetLog,
    onToggleCompleted: () -> Unit,
    onAdjustParameters: (newReps: Int, newWeight: Double) -> Unit,
    onDeleteSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    TenyoSwipeToDismiss(
        onDismiss = onDeleteSet,
        cornerRadius = 10.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        val isPrAchieved = setLog.isCompleted && setLog.isPr

        val containerColor = when {
            isPrAchieved -> TenyoAccentGold.copy(alpha = 0.12f)
            setLog.isCompleted -> TenyoJadeContainer.copy(alpha = 0.22f)
            else -> TenyoSurfaceContainer
        }
        val borderColor = when {
            isPrAchieved -> TenyoAccentGold.copy(alpha = 0.55f)
            setLog.isCompleted -> TenyoJadePrimary.copy(alpha = 0.45f)
            else -> TenyoOutlineSubtle
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = containerColor,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna 1: Numero de serie
                Box(
                    modifier = Modifier.width(36.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "${setLog.setNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            isPrAchieved -> TenyoAccentGold
                            setLog.isCompleted -> TenyoJadePrimary
                            else -> TenyoTextSecondary
                        }
                    )
                }

                // Columna 2: Registro Anterior y medalla de PR si aplica
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = setLog.previousSummary ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextTertiary,
                        maxLines = 1
                    )
                    if (isPrAchieved) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Trophy,
                                contentDescription = "Récord Personal",
                                tint = TenyoAccentGold,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "PR",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = TenyoAccentGold
                            )
                        }
                    }
                }

                // Columna 3: Carga en Kg con input numérico directo
                TenyoInlineNumericInput(
                    value = formatWeight(setLog.weightKg),
                    onValueChange = { inputStr ->
                        val newWeight = inputStr.toDoubleOrNull()
                        if (newWeight != null) {
                            onAdjustParameters(setLog.actualReps, newWeight)
                        }
                    },
                    isDecimal = true,
                    placeholder = "0",
                    modifier = Modifier.weight(1.3f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Columna 4: Repeticiones con input numérico directo
                TenyoInlineNumericInput(
                    value = if (setLog.actualReps > 0) "${setLog.actualReps}" else "",
                    onValueChange = { inputStr ->
                        val newReps = inputStr.toIntOrNull()
                        if (newReps != null) {
                            onAdjustParameters(newReps, setLog.weightKg)
                        }
                    },
                    isDecimal = false,
                    placeholder = "0",
                    modifier = Modifier.weight(1.1f)
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Columna 5: Checkmark circular de finalizacion / Trofeo si es PR
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onToggleCompleted),
                    shape = CircleShape,
                    color = when {
                        isPrAchieved -> TenyoAccentGold
                        setLog.isCompleted -> TenyoJadePrimary
                        else -> TenyoSurfaceContainerHigh
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            isPrAchieved -> TenyoAccentGold
                            setLog.isCompleted -> TenyoJadePrimary
                            else -> TenyoOutline
                        }
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (setLog.isCompleted) {
                            Icon(
                                imageVector = if (isPrAchieved) Lucide.Trophy else Lucide.Check,
                                contentDescription = if (isPrAchieved) "Récord Personal" else "Serie lista",
                                tint = if (isPrAchieved) Color.Black else TenyoOnJadePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
