package com.aescalantedev.tenyo.ui.screens.plans

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanExercise
import com.aescalantedev.tenyo.navigation.ProvideFab
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.screens.plans.components.HtmlAppBg
import com.aescalantedev.tenyo.ui.screens.plans.components.HtmlAppPrimary
import com.aescalantedev.tenyo.ui.screens.plans.components.HtmlAppSurface
import com.aescalantedev.tenyo.ui.screens.plans.components.HtmlBorderSubtle
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanDaySummaryCard
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanDetailSkeleton
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanExerciseCard
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanExerciseSeriesDialog
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanFloatingAddButton
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanReminderCard
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanTimePickerDialog
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanTopHeader
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanWeeklyStrip
import com.aescalantedev.tenyo.ui.screens.plans.components.RestRedAccent
import com.aescalantedev.tenyo.ui.screens.plans.viewmodel.PlanDetailUiState
import com.composables.icons.lucide.Bed
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Plus
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * Pantalla de Detalle y Editor del Plan Semanal.
 *
 * Se renderiza directamente dentro del Scaffold raíz (sin Scaffolds anidados).
 * Utiliza [ProvideFab] para registrar el botón flotante '+' en el Scaffold raíz,
 * garantizando que sea visible y no se solape con otros contenedores.
 */
@Composable
fun PlanDetailScreen(
    uiState: PlanDetailUiState,
    onBack: () -> Unit,
    onSelectDay: (Int) -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    onUpdateReminderTime: (hour: Int, minute: Int) -> Unit,
    onActivatePlan: () -> Unit,
    onToggleRestDay: () -> Unit,
    onOpenEditExerciseConfig: (WorkoutPlanExercise) -> Unit,
    onCloseEditExerciseConfig: () -> Unit,
    onUpdateExerciseConfig: (
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    ) -> Unit,
    onRemoveExerciseFromCurrentDay: (exerciseId: String) -> Unit,
    onStartWorkoutForDay: (planId: String, dayOfWeek: Int) -> Unit,
    onOpenExerciseSelectionList: () -> Unit,
    onOpenTimePicker: () -> Unit,
    onCloseTimePicker: () -> Unit,
    onOpenRenameDayDialog: () -> Unit,
    onCloseRenameDayDialog: () -> Unit,
    onUpdateDayDetails: (name: String, isRestDay: Boolean) -> Unit,
    onOpenRenameTitleDialog: () -> Unit,
    onCloseRenameTitleDialog: () -> Unit,
    onUpdatePlanTitle: (newTitle: String) -> Unit,
    onSaveExerciseNote: (exerciseId: String, note: String?) -> Unit = { _, _ -> },
    onAddExtraSet: (exerciseId: String, currentSets: Int) -> Unit = { _, _ -> },
    onDeleteExerciseSet: (exerciseId: String, setNumber: Int) -> Unit = { _, _ -> },
    onUpdateExerciseRestSeconds: (exerciseId: String, restSeconds: Int) -> Unit = { _, _ -> },
    onAdjustExerciseSetParameters: (exerciseId: String, setNumber: Int, targetReps: String, suggestedWeightKg: Double) -> Unit = { _, _, _, _ -> },
    onNavigateToExerciseDetail: (exerciseId: String) -> Unit = {},
    onReorderExercises: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val plan = uiState.plan
    val selectedDay = uiState.selectedDay

    if (uiState.isLoading || plan == null) {
        PlanDetailSkeleton(
            contentPadding = contentPadding,
            modifier = modifier
        )
        return
    }

    // Registro del FAB en el Scaffold raíz solo si el día no es de descanso
    if (selectedDay?.isRestDay == false) {
        ProvideFab(position = FabPosition.End) {
            PlanFloatingAddButton(
                onClick = onOpenExerciseSelectionList
            )
        }
    }

    // 1. Diálogo de TimePicker oficial Material 3
    if (uiState.isTimePickerVisible && plan != null) {
        PlanTimePickerDialog(
            initialHour = plan.reminderHour,
            initialMinute = plan.reminderMinute,
            onConfirm = { h, m ->
                onUpdateReminderTime(h, m)
            },
            onDismiss = onCloseTimePicker
        )
    }

    // 2. Diálogo para configurar series y cargas de un ejercicio
    uiState.editingExerciseConfig?.let { exerciseToEdit ->
        PlanExerciseSeriesDialog(
            planExercise = exerciseToEdit,
            onConfirm = { sets, reps, restSec, weight ->
                onUpdateExerciseConfig(
                    exerciseToEdit.exercise.id,
                    sets,
                    reps,
                    restSec,
                    weight
                )
            },
            onDismiss = onCloseEditExerciseConfig
        )
    }

    // 3. Diálogo para renombrar el título del plan
    if (uiState.isRenameTitleDialogVisible && plan != null) {
        var tempTitle by rememberSaveable { mutableStateOf(plan.title) }
        AlertDialog(
            onDismissRequest = onCloseRenameTitleDialog,
            containerColor = HtmlAppSurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Renombrar Rutina",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    label = { Text("Nombre del plan semanal") },
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
            },
            confirmButton = {
                Button(
                    onClick = { onUpdatePlanTitle(tempTitle) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HtmlAppPrimary,
                        contentColor = TenyoOnJadePrimary
                    )
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseRenameTitleDialog) {
                    Text("Cancelar", color = TenyoTextSecondary)
                }
            }
        )
    }

    // 4. Diálogo para editar nombre del día
    if (uiState.isRenameDayDialogVisible && selectedDay != null) {
        var tempDayName by rememberSaveable { mutableStateOf(selectedDay.name) }
        AlertDialog(
            onDismissRequest = onCloseRenameDayDialog,
            containerColor = HtmlAppSurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Enfoque del ${selectedDay.dayNameEs}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = tempDayName,
                    onValueChange = { tempDayName = it },
                    label = { Text("Ej: Pecho y Tríceps, Hipertrofia") },
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
            },
            confirmButton = {
                Button(
                    onClick = { onUpdateDayDetails(tempDayName, selectedDay.isRestDay) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HtmlAppPrimary,
                        contentColor = TenyoOnJadePrimary
                    )
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseRenameDayDialog) {
                    Text("Cancelar", color = TenyoTextSecondary)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HtmlAppBg)
    ) {
        if (uiState.isLoading || plan == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HtmlAppPrimary)
            }
        } else {
            val exercisesForDay = uiState.exercisesForSelectedDay
            val isRestDay = selectedDay?.isRestDay ?: false
            val lazyListState = rememberLazyListState()
            val headerOffset = 5

            // Control de expansión acordeón (por defecto colapsados como en ActiveWorkoutScreen)
            var expandedExerciseId by rememberSaveable(plan.id, uiState.selectedDayOfWeek) {
                mutableStateOf<String?>(null)
            }

            val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
                val fromIndex = from.index - headerOffset
                val toIndex = to.index - headerOffset
                if (fromIndex in exercisesForDay.indices && toIndex in exercisesForDay.indices) {
                    onReorderExercises(fromIndex, toIndex)
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = contentPadding.calculateTopPadding() + 8.dp,
                    bottom = contentPadding.calculateBottomPadding() + 68.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Cabecera principal con botón Atrás y título editable
                item {
                    PlanTopHeader(
                        planTitle = plan.title,
                        isActivePlan = plan.isActivePlan,
                        onBack = onBack,
                        onRenameClick = onOpenRenameTitleDialog,
                        onActivatePlan = onActivatePlan,
                        modifier = Modifier.statusBarsPadding()
                    )
                }

                // 2. Selector de días con distinción de descanso en rojo
                item {
                    PlanWeeklyStrip(
                        selectedDayOfWeek = uiState.selectedDayOfWeek,
                        days = plan.days,
                        onSelectDay = onSelectDay
                    )
                }

                // 3. Tarjeta de resumen del día seleccionado (con toggle descanso/entreno)
                item {
                    PlanDaySummaryCard(
                        day = selectedDay,
                        onToggleRestDay = onToggleRestDay,
                        onRenameDayClick = onOpenRenameDayDialog
                    )
                }

                // 4. Tarjeta de recordatorio con Material 3 TimePicker
                item {
                    PlanReminderCard(
                        reminderEnabled = plan.reminderEnabled,
                        reminderHour = plan.reminderHour,
                        reminderMinute = plan.reminderMinute,
                        onToggleReminder = onToggleReminder,
                        onOpenTimePicker = onOpenTimePicker
                    )
                }

                // 5. Encabezado de la lista de ejercicios del día
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRestDay) "Día de Recuperación" else "Ejercicios Programados",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color.White
                        )

                        if (!isRestDay) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HtmlAppPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${exercisesForDay.size} Ejercicios",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = HtmlAppPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 6. Lista de ejercicios o estado vacío
                if (isRestDay) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = HtmlAppSurface,
                            border = BorderStroke(1.dp, HtmlBorderSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Lucide.Bed,
                                    contentDescription = null,
                                    tint = RestRedAccent,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Sin fatiga acumulada hoy",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Prioriza hidratación, nutrición y descanso para maximizar tu rendimiento el siguiente día.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TenyoTextSecondary
                                )
                            }
                        }
                    }
                } else if (exercisesForDay.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = HtmlAppSurface,
                            border = BorderStroke(1.dp, HtmlBorderSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Sin ejercicios para este día",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TenyoTextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Toca el botón '+' abajo a la derecha para seleccionar ejercicios del catálogo.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TenyoTextSecondary
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = onOpenExerciseSelectionList,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HtmlAppPrimary,
                                        contentColor = TenyoOnJadePrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Lucide.Plus,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text("Añadir Ejercicios", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        items = exercisesForDay,
                        key = { _, it -> "${uiState.selectedDayOfWeek}_${it.exercise.id}" }
                    ) { _, exerciseItem ->
                        ReorderableItem(
                            state = reorderableState,
                            key = "${uiState.selectedDayOfWeek}_${exerciseItem.exercise.id}"
                        ) { isDragging ->
                            val elevation by animateDpAsState(
                                targetValue = if (isDragging) 8.dp else 0.dp,
                                label = "plan_exercise_elevation"
                            )
                            PlanExerciseCard(
                                planExercise = exerciseItem,
                                isExpanded = (exerciseItem.exercise.id == expandedExerciseId),
                                onToggleExpand = {
                                    expandedExerciseId = if (expandedExerciseId == exerciseItem.exercise.id) {
                                        null
                                    } else {
                                        exerciseItem.exercise.id
                                    }
                                },
                                onSaveNote = { note ->
                                    onSaveExerciseNote(exerciseItem.exercise.id, note)
                                },
                                onUpdateRestSeconds = { restSec ->
                                    onUpdateExerciseRestSeconds(exerciseItem.exercise.id, restSec)
                                },
                                onAddExtraSet = {
                                    onAddExtraSet(exerciseItem.exercise.id, exerciseItem.targetSets)
                                },
                                onDeleteSet = { setNumber ->
                                    onDeleteExerciseSet(exerciseItem.exercise.id, setNumber)
                                },
                                onAdjustSetParameters = { setNumber, newReps, newWeight ->
                                    onAdjustExerciseSetParameters(exerciseItem.exercise.id, setNumber, newReps, newWeight)
                                },
                                onConfigureSeries = { onOpenEditExerciseConfig(exerciseItem) },
                                onDelete = { onRemoveExerciseFromCurrentDay(exerciseItem.exercise.id) },
                                onNavigateToExerciseDetail = onNavigateToExerciseDetail,
                                dragHandleModifier = Modifier.longPressDraggableHandle(),
                                modifier = Modifier.shadow(elevation, RoundedCornerShape(18.dp))
                            )
                        }
                    }

                    // Botón para entrenar esta rutina ahora
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Button(
                                onClick = { onStartWorkoutForDay(plan.id, uiState.selectedDayOfWeek) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HtmlAppSurface,
                                    contentColor = HtmlAppPrimary
                                ),
                                border = BorderStroke(1.dp, HtmlAppPrimary.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Lucide.Play,
                                    contentDescription = null,
                                    tint = HtmlAppPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Entrenar esta rutina ahora",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
