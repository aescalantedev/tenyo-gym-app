package com.aescalantedev.tenyo.ui.screens.workout.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.WorkoutSetLog
import com.aescalantedev.tenyo.ui.components.TenyoExerciseMedia
import com.aescalantedev.tenyo.ui.components.TenyoSwipeToDismiss
import com.aescalantedev.tenyo.ui.screens.workout.viewmodel.SessionExerciseGroup
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
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Timer
import com.composables.icons.lucide.Trash2

/**
 * Tarjeta de ejercicio que contiene la cabecera, notas, temporizador configurable,
 * tabla de series y boton de anadir serie.
 *
 * Diseno minimalista exacto a las capturas de referencia:
 * - Tarjeta colapsable (AnimatedVisibility) para mantener una rutina limpia.
 * - Miniatura multimedia real del ejercicio con navegacion a ficha tecnica al tocarla.
 */
@Composable
fun WorkoutExerciseCard(
    group: SessionExerciseGroup,
    onToggleSetCompleted: (WorkoutSetLog) -> Unit,
    onAdjustSetParameters: (WorkoutSetLog, newReps: Int, newWeight: Double) -> Unit,
    onDeleteSet: (WorkoutSetLog) -> Unit,
    onAddExtraSet: () -> Unit,
    onOpenRestTimerPicker: () -> Unit,
    onSaveNote: (String?) -> Unit,
    onReplaceExercise: () -> Unit,
    onDeleteExercise: () -> Unit,
    onNavigateToExerciseDetail: (String) -> Unit,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    dragHandleModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var isEditingNote by remember { mutableStateOf(false) }
    var noteText by remember(group.note) { mutableStateOf(group.note.orEmpty()) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "exercise_chevron_rotation"
    )

    TenyoSwipeToDismiss(
        onDismiss = onDeleteExercise,
        onSecondaryAction = onReplaceExercise,
        cornerRadius = 18.dp,
        enableDismissFromStartToEnd = true,
        modifier = modifier.fillMaxWidth()
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
                // Cabecera del ejercicio: Miniatura multimedia + Titulo/Conteo + Chevron colapso + Menu contextual
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(dragHandleModifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Miniatura real del ejercicio (tap navega al detalle/guia tecnica)
                    TenyoExerciseMedia(
                        mediaUrl = group.imageUrl,
                        contentDescription = group.exerciseName,
                        fallbackIcon = Lucide.Dumbbell,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onNavigateToExerciseDetail(group.exerciseId) }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Titulo y subtitulo (tap conmuta colapso/expansion)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onToggleExpand)
                    ) {
                        Text(
                            text = group.exerciseName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TenyoTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${group.completedSetsCount}/${group.totalSetsCount} completado",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary
                        )
                    }

                    // Indicador flecha de expansion/colapso animada
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

                    // Menu 3 puntos (Reemplazar / Eliminar)
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
                                text = { Text("Reemplazar ejercicio", color = TenyoTextPrimary) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Lucide.RefreshCw,
                                        contentDescription = null,
                                        tint = TenyoJadePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onReplaceExercise()
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
                                    onDeleteExercise()
                                }
                            )
                        }
                    }
                }

                // Cuerpo colapsable: Notas, descanso, tabla de series y boton anadir serie
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Fila de nota
                        Text(
                            text = if (group.note.isNullOrBlank()) "Añadir nota..." else group.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (group.note.isNullOrBlank()) TenyoTextTertiary else TenyoTextSecondary,
                            modifier = Modifier
                                .clickable { isEditingNote = true }
                                .padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Fila del temporizador de descanso
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onOpenRestTimerPicker)
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
                            val minutes = group.restSeconds / 60
                            val seconds = group.restSeconds % 60
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

                        // Cabecera de la tabla de series
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
                            Text(
                                text = "Anterior",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoTextTertiary,
                                modifier = Modifier.weight(1.2f)
                            )
                            Row(
                                modifier = Modifier.weight(1.3f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Dumbbell,
                                    contentDescription = null,
                                    tint = if (group.isDumbbell) TenyoJadePrimary else TenyoTextTertiary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (group.isDumbbell) "Kg (2x)" else "Kg",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (group.isDumbbell) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = if (group.isDumbbell) TenyoJadePrimary else TenyoTextTertiary
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

                        // Filas de series
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            group.sets.forEach { setLog ->
                                WorkoutSetRowItem(
                                    setLog = setLog,
                                    onToggleCompleted = { onToggleSetCompleted(setLog) },
                                    onAdjustParameters = { reps, weight ->
                                        onAdjustSetParameters(setLog, reps, weight)
                                    },
                                    onDeleteSet = { onDeleteSet(setLog) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Boton "+ Añadir serie"
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

    // Dialogo para editar nota
    if (isEditingNote) {
        AlertDialog(
            onDismissRequest = { isEditingNote = false },
            containerColor = TenyoObsidianBackground,
            title = {
                Text(
                    text = "Nota para ${group.exerciseName}",
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
}
