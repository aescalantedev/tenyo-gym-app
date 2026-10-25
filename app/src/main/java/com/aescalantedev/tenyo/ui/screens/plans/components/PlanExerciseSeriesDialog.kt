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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanExercise
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Plus

/**
 * Diálogo modal para ajustar la prescripción de series, repeticiones y descanso de un ejercicio del plan.
 *
 * Responsabilidad única:
 * - Proporciona controles interactivos (+ / -) para definir series objetivo.
 * - Campo para especificar el rango de repeticiones (ej: "8-12", "10").
 * - Selector rápido de tiempo de descanso (60s, 90s, 120s, 180s).
 * - Campo para peso sugerido en kg.
 */
@Composable
fun PlanExerciseSeriesDialog(
    planExercise: WorkoutPlanExercise,
    onConfirm: (
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var sets by rememberSaveable { mutableIntStateOf(planExercise.targetSets.coerceIn(1, 12)) }
    var repsText by rememberSaveable { mutableStateOf(planExercise.getRepsForSet(1)) }
    var restSec by rememberSaveable { mutableIntStateOf(planExercise.restSeconds.coerceIn(15, 600)) }
    var weightKg by rememberSaveable { mutableDoubleStateOf(planExercise.suggestedWeightKg) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HtmlAppSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = "Configurar Series y Cargas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = planExercise.exercise.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = HtmlAppPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Selector de cantidad de series (+ / -)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Series objetivo",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TenyoTextPrimary
                        )
                        Text(
                            text = "Rondas de trabajo",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = HtmlAppSurfaceHigh,
                            border = BorderStroke(1.dp, HtmlBorderSubtle),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { if (sets > 1) sets-- }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Minus,
                                    contentDescription = "Menos series",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = "$sets",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = HtmlAppPrimary,
                            modifier = Modifier.width(28.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Surface(
                            shape = CircleShape,
                            color = HtmlAppSurfaceHigh,
                            border = BorderStroke(1.dp, HtmlBorderSubtle),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { if (sets < 12) sets++ }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Plus,
                                    contentDescription = "Más series",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Repeticiones objetivo
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { input -> repsText = input.filter { it.isDigit() } },
                    label = { Text("Repeticiones objetivo por serie") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TenyoTextPrimary,
                        unfocusedTextColor = TenyoTextPrimary,
                        focusedBorderColor = HtmlAppPrimary,
                        unfocusedBorderColor = HtmlBorderSubtle,
                        focusedLabelColor = HtmlAppPrimary,
                        unfocusedLabelColor = TenyoTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Descanso entre series
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Descanso entre series: ${restSec}s",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TenyoTextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(45, 60, 90, 120, 180).forEach { sec ->
                            val isSelected = restSec == sec
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) HtmlAppPrimary else HtmlAppSurfaceHigh,
                                border = if (isSelected) null else BorderStroke(1.dp, HtmlBorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { restSec = sec }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "${sec}s",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) TenyoOnJadePrimary else TenyoTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Peso sugerido
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (planExercise.isDumbbell) "Peso sugerido (2x)" else "Peso sugerido",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TenyoTextPrimary
                        )
                        Text(
                            text = if (planExercise.isDumbbell) "Carga por cada mancuerna" else "Carga de trabajo inicial",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = HtmlAppSurfaceHigh,
                            border = BorderStroke(1.dp, HtmlBorderSubtle),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { if (weightKg >= 2.5) weightKg -= 2.5 }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Minus,
                                    contentDescription = "Menos peso",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        val weightFormatted = if (weightKg % 1.0 == 0.0) "${weightKg.toInt()}" else "$weightKg"
                        Text(
                            text = "$weightFormatted kg${if (planExercise.isDumbbell) " (2x)" else ""}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = HtmlAppGold,
                            modifier = Modifier.width(if (planExercise.isDumbbell) 86.dp else 60.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Surface(
                            shape = CircleShape,
                            color = HtmlAppSurfaceHigh,
                            border = BorderStroke(1.dp, HtmlBorderSubtle),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { weightKg += 2.5 }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Plus,
                                    contentDescription = "Más peso",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        sets,
                        repsText.trim().ifBlank { "8-12" },
                        restSec,
                        weightKg
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HtmlAppPrimary,
                    contentColor = TenyoOnJadePrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TenyoTextSecondary)
            }
        }
    )
}
