package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanExercise
import com.aescalantedev.tenyo.ui.components.TenyoExerciseMedia
import com.aescalantedev.tenyo.ui.components.TenyoInlineNumericInput
import com.aescalantedev.tenyo.ui.components.TenyoSwipeToDismiss
import com.aescalantedev.tenyo.ui.screens.workout.components.formatWeight
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.EllipsisVertical
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.SlidersVertical
import com.composables.icons.lucide.Timer
import com.composables.icons.lucide.Trash2

/**
 * Tarjeta de ejercicio asignado a la rutina semanal.
 * Replica de manera exacta el diseño, ergonomía y funcionalidades de [WorkoutExerciseCard]:
 * - Tarjeta colapsable (AnimatedVisibility) para mantener una rutina limpia.
 * - Miniatura multimedia real del ejercicio con navegación a ficha técnica al tocarla.
 * - Fila de notas configurable (se transfiere a la sesión en vivo).
 * - Temporizador de descanso editable.
 * - Tabla de series con ajustes rápidos (-/+) de peso y repeticiones.
 * - Botón "+ Añadir serie" y eliminación individual de series.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanExerciseCard(
    planExercise: WorkoutPlanExercise,
    onSaveNote: (String?) -> Unit,
    onUpdateRestSeconds: (Int) -> Unit,
    onAddExtraSet: () -> Unit,
    onDeleteSet: (setNumber: Int) -> Unit,
    onAdjustSetParameters: (setNumber: Int, newReps: String, newWeight: Double) -> Unit,
    onConfigureSeries: () -> Unit,
    onDelete: () -> Unit,
    onNavigateToExerciseDetail: (String) -> Unit,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    dragHandleModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var isEditingNote by remember { mutableStateOf(false) }
    var noteText by remember(planExercise.note) { mutableStateOf(planExercise.note.orEmpty()) }
    var isEditingRest by remember { mutableStateOf(false) }
    var selectedRestSeconds by remember(planExercise.restSeconds) { mutableIntStateOf(planExercise.restSeconds) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "plan_exercise_chevron_rotation"
    )

    TenyoSwipeToDismiss(
        onDismiss = onDelete,
        onSecondaryAction = onConfigureSeries,
        cornerRadius = 18.dp,
        enableDismissFromStartToEnd = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Cabecera del ejercicio: Miniatura + Nombre/Subtítulo + Chevron + Menú 3 puntos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(dragHandleModifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                // Miniatura interactiva
                TenyoExerciseMedia(
                    mediaUrl = planExercise.exercise.imageUrl,
                    contentDescription = planExercise.exercise.name,
                    fallbackIcon = Lucide.Dumbbell,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onNavigateToExerciseDetail(planExercise.exercise.id) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Título y conteo de series (al tocar conmuta expandir/contraer)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onToggleExpand)
                ) {
                    Text(
                        text = planExercise.exercise.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${planExercise.targetSets} series · ${planExercise.getRepsForSet(1)} reps · ${planExercise.exercise.bodyPart.nameEs}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextSecondary
                    )
                }

                // Flecha indicadora de colapso animada
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onToggleExpand),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.ChevronDown,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir",
                        tint = TenyoTextTertiary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(chevronRotation)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Menú 3 puntos (Configurar objetivo / Eliminar ejercicio)
                Box {
                    Surface(
                        shape = CircleShape,
                        color = TenyoSurfaceContainerHigh,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable { showMenu = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.EllipsisVertical,
                                contentDescription = "Opciones",
                                tint = TenyoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = TenyoSurfaceContainer
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configurar objetivo completo", color = TenyoTextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Lucide.SlidersVertical,
                                    contentDescription = null,
                                    tint = TenyoJadePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onConfigureSeries()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar ejercicio", color = TenyoCrimsonTertiary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Lucide.Trash2,
                                    contentDescription = null,
                                    tint = TenyoCrimsonTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // Cuerpo colapsable: Notas, temporizador, tabla de series y botón añadir serie
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Fila de nota
                    Text(
                        text = if (planExercise.note.isNullOrBlank()) "Añadir nota..." else planExercise.note.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (planExercise.note.isNullOrBlank()) TenyoTextTertiary else TenyoTextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isEditingNote = true }
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 2. Fila del temporizador de descanso
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isEditingRest = true }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Timer,
                            contentDescription = null,
                            tint = TenyoJadePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val minutes = planExercise.restSeconds / 60
                        val seconds = planExercise.restSeconds % 60
                        val restFormatted = if (minutes > 0 && seconds > 0) {
                            "${minutes}m ${seconds}s"
                        } else if (minutes > 0) {
                            "${minutes}m"
                        } else {
                            "${seconds}s"
                        }
                        Text(
                            text = "Temporizador de descanso: $restFormatted",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TenyoJadePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Cabecera de la tabla de series
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Serie",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextTertiary,
                            modifier = Modifier.width(36.dp)
                        )
                        Row(
                            modifier = Modifier.weight(1.3f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Dumbbell,
                                contentDescription = null,
                                tint = if (planExercise.isDumbbell) TenyoJadePrimary else TenyoTextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (planExercise.isDumbbell) "Kg (2x)" else "Kg",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (planExercise.isDumbbell) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (planExercise.isDumbbell) TenyoJadePrimary else TenyoTextTertiary
                            )
                        }
                        Text(
                            text = "Reps",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextTertiary,
                            modifier = Modifier.weight(1.2f)
                        )
                        Box(
                            modifier = Modifier.width(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = null,
                                tint = TenyoTextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 4. Filas de series interactivas (deslizar fila para eliminar serie)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (setIndex in 1..planExercise.targetSets) {
                            val currentSetWeight = planExercise.getWeightForSet(setIndex)
                            val currentSetReps = planExercise.getRepsForSet(setIndex)

                            TenyoSwipeToDismiss(
                                onDismiss = {
                                    if (planExercise.targetSets > 1) {
                                        onDeleteSet(setIndex)
                                    }
                                },
                                cornerRadius = 10.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = TenyoSurfaceContainer,
                                    border = BorderStroke(1.dp, TenyoOutlineSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Columna 1: Número de serie
                                        Box(
                                            modifier = Modifier.width(36.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = "$setIndex",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TenyoJadePrimary
                                            )
                                        }

                                        // Columna 2: Carga en Kg con input numérico directo
                                        TenyoInlineNumericInput(
                                            value = formatWeight(currentSetWeight),
                                            onValueChange = { inputStr ->
                                                val newWeight = inputStr.toDoubleOrNull()
                                                if (newWeight != null) {
                                                    onAdjustSetParameters(setIndex, currentSetReps, newWeight)
                                                }
                                            },
                                            isDecimal = true,
                                            placeholder = "0",
                                            modifier = Modifier.weight(1.3f)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Columna 3: Repeticiones con input numérico directo
                                        TenyoInlineNumericInput(
                                            value = currentSetReps,
                                            onValueChange = { inputStr ->
                                                if (inputStr.isNotBlank()) {
                                                    onAdjustSetParameters(setIndex, inputStr, currentSetWeight)
                                                }
                                            },
                                            isDecimal = false,
                                            placeholder = "0",
                                            modifier = Modifier.weight(1.1f)
                                        )

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Columna 4: Indicador discreto de serie lista
                                        Box(
                                            modifier = Modifier.width(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Lucide.Check,
                                                contentDescription = null,
                                                tint = TenyoTextTertiary.copy(alpha = 0.45f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Botón "+ Añadir serie"
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onAddExtraSet),
                        shape = RoundedCornerShape(10.dp),
                        color = TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Plus,
                                contentDescription = null,
                                tint = TenyoTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Añadir serie",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TenyoTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
    }

    // Diálogo para editar nota
    if (isEditingNote) {
        AlertDialog(
            onDismissRequest = { isEditingNote = false },
            containerColor = TenyoObsidianBackground,
            title = {
                Text(
                    text = "Nota para ${planExercise.exercise.name}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Añadir sensaciones, agarre o técnica...", color = TenyoTextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TenyoSurfaceContainer,
                        unfocusedContainerColor = TenyoSurfaceContainer,
                        focusedBorderColor = TenyoJadePrimary,
                        unfocusedBorderColor = TenyoOutlineSubtle,
                        focusedTextColor = TenyoTextPrimary,
                        unfocusedTextColor = TenyoTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveNote(noteText.ifBlank { null })
                        isEditingNote = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TenyoJadePrimary,
                        contentColor = TenyoOnJadePrimary
                    )
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingNote = false }) {
                    Text("Cancelar", color = TenyoTextSecondary)
                }
            }
        )
    }

    // Diálogo rápido para configurar temporizador de descanso
    if (isEditingRest) {
        val quickRestOptions = listOf(30, 45, 60, 90, 120, 180)
        AlertDialog(
            onDismissRequest = { isEditingRest = false },
            containerColor = TenyoObsidianBackground,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Timer,
                        contentDescription = null,
                        tint = TenyoJadePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Descanso entre series",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Selecciona el tiempo de recuperación recomendado para ${planExercise.exercise.name}:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickRestOptions.forEach { seconds ->
                            val isSelected = selectedRestSeconds == seconds
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRestSeconds = seconds },
                                label = {
                                    Text(
                                        text = if (seconds >= 60) "${seconds / 60}m ${if (seconds % 60 > 0) "${seconds % 60}s" else ""}".trim() else "${seconds}s",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TenyoJadePrimary,
                                    selectedLabelColor = TenyoOnJadePrimary,
                                    containerColor = TenyoSurfaceContainer,
                                    labelColor = TenyoTextSecondary
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateRestSeconds(selectedRestSeconds)
                        isEditingRest = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TenyoJadePrimary,
                        contentColor = TenyoOnJadePrimary
                    )
                ) {
                    Text("Aplicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingRest = false }) {
                    Text("Cancelar", color = TenyoTextSecondary)
                }
            }
        )
    }
}
