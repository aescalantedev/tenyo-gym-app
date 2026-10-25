package com.aescalantedev.tenyo.ui.screens.workout

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.SessionStatus
import com.aescalantedev.tenyo.core.domain.model.WorkoutSetLog
import com.aescalantedev.tenyo.ui.components.TenyoAddExerciseBottomSheet
import com.aescalantedev.tenyo.ui.components.TenyoRestTimerPickerSheet
import com.aescalantedev.tenyo.ui.screens.workout.components.WorkoutExerciseCard
import com.aescalantedev.tenyo.ui.screens.workout.components.WorkoutMetricsHeaderCard
import com.aescalantedev.tenyo.ui.screens.workout.components.WorkoutRestTimerLiveBar
import com.aescalantedev.tenyo.ui.screens.workout.components.formatElapsedTime
import com.aescalantedev.tenyo.ui.screens.workout.viewmodel.ActiveWorkoutUiState
import com.aescalantedev.tenyo.ui.screens.workout.viewmodel.SessionExerciseGroup
import com.aescalantedev.tenyo.ui.theme.TenyoAmberWarning
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.EllipsisVertical
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Timer
import com.composables.icons.lucide.Trash2
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * Pantalla de Entrenamiento Activo Minimalista (Rutina Multi-Ejercicio Continua).
 * Diseno fiel a las capturas minimalistas (Hevy / Strong pattern).
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ActiveWorkoutScreen(
    uiState: ActiveWorkoutUiState,
    onBack: () -> Unit,
    onToggleSetCompleted: (WorkoutSetLog) -> Unit,
    onAdjustSetParameters: (WorkoutSetLog, newReps: Int, newWeightKg: Double) -> Unit,
    onDeleteSet: (WorkoutSetLog) -> Unit,
    onAddExtraSet: (exerciseId: String) -> Unit,
    onDeleteExercise: (exerciseId: String) -> Unit,
    onOpenReplaceExercise: (exerciseId: String) -> Unit,
    onNavigateToExerciseList: () -> Unit,
    onNavigateToExerciseDetail: (exerciseId: String) -> Unit,
    onOpenAddExerciseSheet: () -> Unit,
    onCloseAddExerciseSheet: () -> Unit,
    onUpdatePickerQuery: (String) -> Unit,
    onSelectPickerCategory: (BodyPartCategory?) -> Unit,
    onSelectExerciseToAdd: (Exercise) -> Unit,
    onOpenRestTimerPicker: (SessionExerciseGroup) -> Unit,
    onCloseRestTimerPicker: () -> Unit,
    onConfirmRestSeconds: (exerciseId: String, Int) -> Unit,
    onSaveExerciseNote: (exerciseId: String, String?) -> Unit,
    onCancelRestTimer: () -> Unit,
    onTogglePause: () -> Unit,
    onDiscardWorkout: () -> Unit,
    onFinishWorkout: (updatePlan: Boolean) -> Unit,
    onReorderExercises: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    // Solicitud en tiempo de ejecución de notificaciones en Android 13+ (Tiramisu / SDK 33+)
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { /* Permiso otorgado o denegado */ }
        )

        LaunchedEffect(Unit) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showTopMoreMenu by remember { mutableStateOf(false) }

    val isPaused = uiState.session?.status == SessionStatus.PAUSED
    val elapsedFormatted = formatElapsedTime(uiState.liveElapsedSeconds)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TenyoObsidianBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Surface(
                color = TenyoSurfaceContainerLow,
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Boton minimizar (flecha abajo) + Cronometro interactivo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TenyoSurfaceContainerHigh,
                            border = BorderStroke(1.dp, TenyoOutlineSubtle),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onBack)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = "Minimizar entrenamiento",
                                    tint = TenyoTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isPaused) TenyoAmberWarning.copy(alpha = 0.16f) else TenyoSurfaceContainerHigh,
                            border = BorderStroke(
                                1.dp,
                                if (isPaused) TenyoAmberWarning.copy(alpha = 0.5f) else TenyoOutlineSubtle
                            ),
                            modifier = Modifier.clickable(onClick = onTogglePause)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Lucide.Play else Lucide.Timer,
                                    contentDescription = null,
                                    tint = if (isPaused) TenyoAmberWarning else TenyoJadePrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPaused) "En Pausa" else elapsedFormatted,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isPaused) TenyoAmberWarning else TenyoTextPrimary
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Boton Finalizar en capsula: solo habilitado cuando se hayan completado los ejercicios con sus series
                        val canFinish = uiState.exerciseGroups.isNotEmpty() &&
                            uiState.totalSetsCount > 0 &&
                            uiState.completedSetsCount == uiState.totalSetsCount

                        Button(
                            onClick = { showFinishDialog = true },
                            enabled = canFinish,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TenyoJadePrimary,
                                contentColor = TenyoOnJadePrimary,
                                disabledContainerColor = TenyoSurfaceContainerHigh,
                                disabledContentColor = TenyoTextTertiary
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Finalizar",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }

                        // Boton 3 puntos de cabecera (Pausar / Descartar)
                        Box {
                            Surface(
                                shape = CircleShape,
                                color = TenyoSurfaceContainerHigh,
                                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .clickable { showTopMoreMenu = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.EllipsisVertical,
                                        contentDescription = "Opciones",
                                        tint = TenyoTextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showTopMoreMenu,
                                onDismissRequest = { showTopMoreMenu = false },
                                containerColor = TenyoSurfaceContainer
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (isPaused) "Reanudar entrenamiento" else "Pausar entrenamiento",
                                            color = TenyoTextPrimary
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (isPaused) Lucide.Play else Lucide.Pause,
                                            contentDescription = null,
                                            tint = TenyoJadePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    onClick = {
                                        showTopMoreMenu = false
                                        onTogglePause()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Descartar entrenamiento", color = TenyoCrimsonTertiary) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Lucide.Trash2,
                                            contentDescription = null,
                                            tint = TenyoCrimsonTertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    onClick = {
                                        showTopMoreMenu = false
                                        showDiscardDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { scaffoldPadding ->
        val coroutineScope = rememberCoroutineScope()
        val lazyListState = rememberLazyListState()
        val hasRestBar = uiState.activeRestSecondsRemaining != null && uiState.activeRestTotalSeconds != null
        val headerOffset = if (hasRestBar) 2 else 1

        val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
            val fromIndex = from.index - headerOffset
            val toIndex = to.index - headerOffset
            if (fromIndex in uiState.exerciseGroups.indices && toIndex in uiState.exerciseGroups.indices) {
                onReorderExercises(fromIndex, toIndex)
            }
        }

        val firstIncompleteIndex = uiState.exerciseGroups.indexOfFirst { group ->
            group.completedSetsCount < group.totalSetsCount
        }.let { if (it == -1) 0 else it }

        // Mantiene el ID del único ejercicio expandido (modo acordeón)
        var expandedExerciseId by rememberSaveable(uiState.session?.id) {
            mutableStateOf<String?>("__INITIAL__")
        }

        val effectiveExpandedId = if (expandedExerciseId == "__INITIAL__") {
            uiState.exerciseGroups.getOrNull(firstIncompleteIndex)?.exerciseId
        } else {
            expandedExerciseId
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(scaffoldPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = scaffoldPadding.calculateTopPadding() + 14.dp,
                bottom = maxOf(
                    scaffoldPadding.calculateBottomPadding(),
                    contentPadding.calculateBottomPadding()
                ) + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Tarjeta de metricas (Duracion, Volumen, Series)
            item {
                WorkoutMetricsHeaderCard(
                    title = uiState.session?.title?.ifBlank { "ENTRENAMIENTO EN CURSO" } ?: "ENTRENAMIENTO EN CURSO",
                    durationText = elapsedFormatted,
                    volumeKg = uiState.totalVolumeKg,
                    completedSetsCount = uiState.completedSetsCount,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }

            // 2. Banner de descanso en vivo si esta activo
            val restRemaining = uiState.activeRestSecondsRemaining
            val restTotal = uiState.activeRestTotalSeconds
            if (restRemaining != null && restTotal != null) {
                item {
                    WorkoutRestTimerLiveBar(
                        remainingSeconds = restRemaining,
                        totalSeconds = restTotal,
                        exerciseName = uiState.activeRestExerciseName.orEmpty(),
                        onSkip = onCancelRestTimer
                    )
                }
            }

            // 3. Tarjetas de ejercicios reordenables (Drag & Drop)
            itemsIndexed(
                items = uiState.exerciseGroups,
                key = { _, group -> group.exerciseId }
            ) { _, group ->
                ReorderableItem(
                    state = reorderableState,
                    key = group.exerciseId
                ) { isDragging ->
                    val elevation by animateDpAsState(
                        targetValue = if (isDragging) 8.dp else 0.dp,
                        label = "reorder_card_elevation"
                    )
                    WorkoutExerciseCard(
                        group = group,
                        isExpanded = (group.exerciseId == effectiveExpandedId),
                        onToggleExpand = {
                            expandedExerciseId = if (effectiveExpandedId == group.exerciseId) {
                                null
                            } else {
                                group.exerciseId
                            }
                        },
                        onToggleSetCompleted = { setLog ->
                            onToggleSetCompleted(setLog)
                            // Si se completa la última serie pendiente de este ejercicio, colapsarlo y expandir el siguiente
                            if (!setLog.isCompleted) {
                                val willBeComplete = (group.completedSetsCount + 1) >= group.totalSetsCount
                                if (willBeComplete && group.totalSetsCount > 0) {
                                    val currentIndex = uiState.exerciseGroups.indexOfFirst { it.exerciseId == group.exerciseId }
                                    if (currentIndex != -1) {
                                        val nextExercise = uiState.exerciseGroups.drop(currentIndex + 1).firstOrNull { it.completedSetsCount < it.totalSetsCount }
                                            ?: uiState.exerciseGroups.getOrNull(currentIndex + 1)
                                            ?: uiState.exerciseGroups.firstOrNull { it.exerciseId != group.exerciseId && it.completedSetsCount < it.totalSetsCount }

                                        expandedExerciseId = nextExercise?.exerciseId

                                        if (nextExercise != null) {
                                            val nextIndex = uiState.exerciseGroups.indexOfFirst { it.exerciseId == nextExercise.exerciseId }
                                            if (nextIndex != -1) {
                                                coroutineScope.launch {
                                                    lazyListState.animateScrollToItem(nextIndex + headerOffset)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        onAdjustSetParameters = onAdjustSetParameters,
                        onDeleteSet = onDeleteSet,
                        onAddExtraSet = { onAddExtraSet(group.exerciseId) },
                        onOpenRestTimerPicker = { onOpenRestTimerPicker(group) },
                        onSaveNote = { note -> onSaveExerciseNote(group.exerciseId, note) },
                        onReplaceExercise = {
                            onOpenReplaceExercise(group.exerciseId)
                            onNavigateToExerciseList()
                        },
                        onDeleteExercise = { onDeleteExercise(group.exerciseId) },
                        onNavigateToExerciseDetail = onNavigateToExerciseDetail,
                        dragHandleModifier = Modifier.longPressDraggableHandle(),
                        modifier = Modifier.shadow(elevation, RoundedCornerShape(18.dp))
                    )
                }
            }

            // 4. Botones de accion inferior (Capturas 1 y 2: Anadir ejercicios + Mas)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Boton blanco prominente "Añadir ejercicios" (navega al catalogo completo)
                    Button(
                        onClick = onNavigateToExerciseList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Añadir ejercicios",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Boton secundario "Más"
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable { showMoreMenu = true },
                            shape = RoundedCornerShape(50),
                            color = TenyoSurfaceContainerHigh,
                            border = BorderStroke(1.dp, TenyoOutlineSubtle)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Más",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = TenyoTextPrimary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            containerColor = TenyoSurfaceContainer
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isPaused) "Reanudar entrenamiento" else "Pausar entrenamiento",
                                        color = TenyoTextPrimary
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isPaused) Lucide.Play else Lucide.Pause,
                                        contentDescription = null,
                                        tint = TenyoJadePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    onTogglePause()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Descartar entrenamiento", color = TenyoCrimsonTertiary) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Lucide.Trash2,
                                        contentDescription = null,
                                        tint = TenyoCrimsonTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showDiscardDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal para anadir o reemplazar ejercicio
    if (uiState.isAddExerciseSheetVisible) {
        TenyoAddExerciseBottomSheet(
            exercises = uiState.availableExercises,
            searchQuery = uiState.exercisePickerQuery,
            onSearchQueryChange = onUpdatePickerQuery,
            selectedCategory = uiState.exercisePickerCategory,
            onSelectCategory = onSelectPickerCategory,
            onAddExercise = onSelectExerciseToAdd,
            onDismissRequest = onCloseAddExerciseSheet
        )
    }

    // Modal para configurar tiempo de descanso
    if (uiState.restTimerPickerExercise != null) {
        val group = uiState.restTimerPickerExercise
        TenyoRestTimerPickerSheet(
            initialSeconds = group.restSeconds,
            exerciseName = group.exerciseName,
            onDismissRequest = onCloseRestTimerPicker,
            onConfirm = { newSeconds ->
                onConfirmRestSeconds(group.exerciseId, newSeconds)
            }
        )
    }

    // Dialogo confirmacion de finalizacion
    if (showFinishDialog) {
        val isPlanWorkout = uiState.session?.planId != null
        var updatePlanValues by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            containerColor = TenyoObsidianBackground,
            title = {
                Text(
                    text = "Finalizar entrenamiento",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "¿Deseas concluir la sesión? Se guardarán tus series, cargas levantadas y volumen acumulado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TenyoTextSecondary
                    )

                    if (isPlanWorkout) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TenyoSurfaceContainerHigh,
                            border = BorderStroke(
                                1.dp,
                                if (updatePlanValues) TenyoJadePrimary.copy(alpha = 0.5f) else TenyoOutlineSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { updatePlanValues = !updatePlanValues }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = updatePlanValues,
                                    onCheckedChange = { updatePlanValues = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = TenyoJadePrimary,
                                        checkmarkColor = TenyoOnJadePrimary,
                                        uncheckedColor = TenyoTextTertiary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Actualizar mi rutina semanal",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = TenyoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Guardar estas cargas y repeticiones en tu plan para la próxima semana (Sobrecarga progresiva).",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TenyoTextTertiary
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
                        showFinishDialog = false
                        onFinishWorkout(if (isPlanWorkout) updatePlanValues else false)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TenyoJadePrimary,
                        contentColor = TenyoOnJadePrimary
                    )
                ) {
                    Text("Finalizar y Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Continuar entrenando", color = TenyoTextSecondary)
                }
            }
        )
    }

    // Dialogo confirmacion de descarte
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            containerColor = TenyoObsidianBackground,
            title = {
                Text(
                    text = "Descartar entrenamiento",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoCrimsonTertiary
                )
            },
            text = {
                Text(
                    text = "Se eliminara la sesion activa actual sin registrar marcas ni historial.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TenyoTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardDialog = false
                        onDiscardWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TenyoCrimsonTertiary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Volver", color = TenyoTextSecondary)
                }
            }
        )
    }
}
