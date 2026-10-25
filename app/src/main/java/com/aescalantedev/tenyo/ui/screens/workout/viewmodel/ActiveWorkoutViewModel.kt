package com.aescalantedev.tenyo.ui.screens.workout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseFilter
import com.aescalantedev.tenyo.core.domain.model.SessionStatus
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import com.aescalantedev.tenyo.core.domain.model.WorkoutSetLog
import com.aescalantedev.tenyo.core.domain.usecase.ManageActiveSessionUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExercisesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveWorkoutSessionUseCase
import com.aescalantedev.tenyo.core.domain.usecase.StartWorkoutSessionUseCase
import com.aescalantedev.tenyo.core.notification.WorkoutNotificationManager
import com.aescalantedev.tenyo.core.service.WorkoutTimerCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Agrupación de series asociadas a un ejercicio en la rutina en vivo.
 */
data class SessionExerciseGroup(
    val exerciseId: String,
    val exerciseName: String,
    val bodyPart: BodyPartCategory = BodyPartCategory.CHEST,
    val restSeconds: Int = 90,
    val note: String? = null,
    val imageUrl: String = "",
    val multiplier: Int = 1,
    val sets: List<WorkoutSetLog> = emptyList()
) {
    val isDumbbell: Boolean
        get() = multiplier == 2

    val completedSetsCount: Int
        get() = sets.count { it.isCompleted }

    val totalSetsCount: Int
        get() = sets.size

    val totalVolumeKg: Double
        get() = sets.sumOf { it.volumeKg }
}

/**
 * Estado completo de la pantalla de Entrenamiento Activo.
 */
data class ActiveWorkoutUiState(
    val session: WorkoutSession? = null,
    val exerciseGroups: List<SessionExerciseGroup> = emptyList(),
    val liveElapsedSeconds: Long = 0L,
    val totalVolumeKg: Double = 0.0,
    val completedSetsCount: Int = 0,
    val totalSetsCount: Int = 0,
    val activeRestSecondsRemaining: Int? = null,
    val activeRestTotalSeconds: Int? = null,
    val activeRestExerciseName: String? = null,
    val availableExercises: List<Exercise> = emptyList(),
    val isAddExerciseSheetVisible: Boolean = false,
    val exercisePickerQuery: String = "",
    val exercisePickerCategory: BodyPartCategory? = null,
    val restTimerPickerExercise: SessionExerciseGroup? = null,
    val replaceTargetExerciseId: String? = null,
    val isLoading: Boolean = true
)

/**
 * ViewModel para la pantalla de Entrenamiento Activo minimalista y continua.
 * Coordina con [WorkoutTimerCoordinator] para mantener la precisión absoluta de tiempos
 * y notificaciones aun cuando la app pasa a segundo plano.
 *
 * Utiliza [Dispatchers.IO] para todas las operaciones pesadas de persistencia y base de datos,
 * asegurando una interfaz 100% fluida sin bloqueos en el hilo principal.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutViewModel(
    private val startWorkoutSessionUseCase: StartWorkoutSessionUseCase,
    private val observeWorkoutSessionUseCase: ObserveWorkoutSessionUseCase,
    private val manageActiveSessionUseCase: ManageActiveSessionUseCase,
    private val observeExercisesUseCase: ObserveExercisesUseCase,
    private val workoutNotificationManager: WorkoutNotificationManager,
    private val workoutTimerCoordinator: WorkoutTimerCoordinator
) : ViewModel() {

    private val activeSessionId = MutableStateFlow<String?>(null)
    private var lastInitializedKey: String? = null

    // Dialogos y hojas modales
    private val isAddExerciseSheetVisible = MutableStateFlow(false)
    private val exercisePickerQuery = MutableStateFlow("")
    private val exercisePickerCategory = MutableStateFlow<BodyPartCategory?>(null)
    private val restTimerPickerExercise = MutableStateFlow<SessionExerciseGroup?>(null)
    private val replaceTargetExerciseId = MutableStateFlow<String?>(null)
    private val customExerciseOrder = MutableStateFlow<List<String>>(emptyList())

    private val sessionFlow = activeSessionId.flatMapLatest { id ->
        if (id.isNullOrBlank()) {
            observeWorkoutSessionUseCase.active()
        } else {
            observeWorkoutSessionUseCase.byId(id)
        }
    }.onEach { session ->
        if (session != null && session.status == SessionStatus.IN_PROGRESS) {
            workoutTimerCoordinator.startWorkoutSession(session.startedAtEpochMillis)
        }
    }

    private val availableExercisesFlow = combine(
        exercisePickerQuery,
        exercisePickerCategory
    ) { query, category ->
        ExerciseFilter(
            query = query,
            bodyPart = category,
            onlyFavorites = false
        )
    }.flatMapLatest { filter ->
        observeExercisesUseCase(filter, limit = 80)
    }

    val uiState: StateFlow<ActiveWorkoutUiState> = combine(
        sessionFlow,
        workoutTimerCoordinator.workoutElapsedSeconds,
        workoutTimerCoordinator.activeRestRemainingSeconds,
        workoutTimerCoordinator.activeRestTotalSeconds,
        workoutTimerCoordinator.activeRestExerciseName,
        availableExercisesFlow,
        isAddExerciseSheetVisible,
        exercisePickerQuery,
        exercisePickerCategory,
        restTimerPickerExercise,
        replaceTargetExerciseId,
        customExerciseOrder
    ) { params ->
        val session = params[0] as? WorkoutSession
        val elapsed = params[1] as Long
        val restRemaining = params[2] as? Int
        val restTotal = params[3] as? Int
        val restExName = params[4] as? String
        @Suppress("UNCHECKED_CAST")
        val exercises = params[5] as List<Exercise>
        val isSheetVisible = params[6] as Boolean
        val pickerQuery = params[7] as String
        val pickerCategory = params[8] as? BodyPartCategory
        val timerPickerExercise = params[9] as? SessionExerciseGroup
        val replaceTargetId = params[10] as? String
        @Suppress("UNCHECKED_CAST")
        val exerciseOrder = params[11] as List<String>

        if (session == null) {
            ActiveWorkoutUiState(isLoading = true)
        } else {
            val rawGroups = session.setLogs
                .groupBy { it.exerciseId }
                .map { (exId, logs) ->
                    val firstLog = logs.firstOrNull()
                    SessionExerciseGroup(
                        exerciseId = exId,
                        exerciseName = firstLog?.exerciseName?.replaceFirstChar { it.uppercase() } ?: "Ejercicio",
                        bodyPart = firstLog?.bodyPart ?: BodyPartCategory.CHEST,
                        restSeconds = firstLog?.restSeconds ?: 90,
                        note = firstLog?.note,
                        imageUrl = firstLog?.imageUrl.orEmpty(),
                        multiplier = firstLog?.multiplier ?: 1,
                        sets = logs.sortedBy { it.setNumber }
                    )
                }

            val groups = if (exerciseOrder.isNotEmpty()) {
                val orderMap = exerciseOrder.withIndex().associate { it.value to it.index }
                rawGroups.sortedBy { orderMap[it.exerciseId] ?: Int.MAX_VALUE }
            } else {
                rawGroups
            }

            val totalVolume = session.setLogs.filter { it.isCompleted }.sumOf { it.volumeKg }
            val completedSets = session.setLogs.count { it.isCompleted }

            ActiveWorkoutUiState(
                session = session,
                exerciseGroups = groups,
                liveElapsedSeconds = elapsed.coerceAtLeast(session.elapsedSeconds),
                totalVolumeKg = totalVolume,
                completedSetsCount = completedSets,
                totalSetsCount = session.setLogs.size,
                activeRestSecondsRemaining = restRemaining,
                activeRestTotalSeconds = restTotal,
                activeRestExerciseName = restExName,
                availableExercises = exercises,
                isAddExerciseSheetVisible = isSheetVisible,
                exercisePickerQuery = pickerQuery,
                exercisePickerCategory = pickerCategory,
                restTimerPickerExercise = timerPickerExercise,
                replaceTargetExerciseId = replaceTargetId,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ActiveWorkoutUiState()
    )

    fun startOrAttachSession(planId: String?, singleExerciseId: String?, dayOfWeek: Int? = null) {
        val requestKey = "${planId.orEmpty()}_${singleExerciseId.orEmpty()}_${dayOfWeek ?: 0}"
        if (lastInitializedKey == requestKey && activeSessionId.value != null) {
            return
        }
        lastInitializedKey = requestKey
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = startWorkoutSessionUseCase(
                planId = planId,
                singleExerciseId = singleExerciseId,
                dayOfWeek = dayOfWeek
            )
            activeSessionId.value = sessionId
        }
    }

    fun toggleSetCompleted(setLog: WorkoutSetLog) {
        val willBeCompleted = !setLog.isCompleted
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.updateSet(
                setLogId = setLog.id,
                actualReps = setLog.actualReps,
                weightKg = setLog.weightKg,
                isCompleted = willBeCompleted
            )
        }

        // Si se marco como completada, iniciar la cuenta atras del descanso a traves del coordinador
        if (willBeCompleted) {
            startRestTimer(seconds = setLog.restSeconds, exerciseName = setLog.exerciseName)
        }
    }

    fun adjustSetParameters(
        setLog: WorkoutSetLog,
        newReps: Int,
        newWeightKg: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.updateSet(
                setLogId = setLog.id,
                actualReps = newReps,
                weightKg = newWeightKg,
                isCompleted = setLog.isCompleted
            )
        }
    }

    fun deleteSet(setLog: WorkoutSetLog) {
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.deleteSet(setLog.id)
        }
    }

    fun deleteExercise(exerciseId: String) {
        val sessionId = uiState.value.session?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.deleteExercise(sessionId, exerciseId)
        }
    }

    fun addExtraSet(exerciseId: String) {
        val sessionId = uiState.value.session?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.addExtraSet(sessionId, exerciseId)
        }
    }

    fun prepareReplaceExercise(targetExerciseId: String) {
        replaceTargetExerciseId.value = targetExerciseId
    }

    fun clearReplaceTarget() {
        replaceTargetExerciseId.value = null
    }

    fun addExerciseToSession(exerciseId: String) {
        val sessionId = uiState.value.session?.id ?: return
        val targetToReplace = replaceTargetExerciseId.value
        viewModelScope.launch(Dispatchers.IO) {
            if (targetToReplace != null) {
                manageActiveSessionUseCase.replaceExerciseInSession(
                    sessionId = sessionId,
                    oldExerciseId = targetToReplace,
                    newExerciseId = exerciseId
                )
                replaceTargetExerciseId.value = null
            } else {
                manageActiveSessionUseCase.addExerciseToSession(
                    sessionId = sessionId,
                    exerciseId = exerciseId
                )
            }
        }
    }

    fun addExercisesToSession(exerciseIds: List<String>) {
        val sessionId = uiState.value.session?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            exerciseIds.forEach { exId ->
                manageActiveSessionUseCase.addExerciseToSession(
                    sessionId = sessionId,
                    exerciseId = exId
                )
            }
        }
    }

    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        val currentGroups = uiState.value.exerciseGroups.toMutableList()
        if (fromIndex in currentGroups.indices && toIndex in currentGroups.indices) {
            val moved = currentGroups.removeAt(fromIndex)
            currentGroups.add(toIndex, moved)
            customExerciseOrder.value = currentGroups.map { it.exerciseId }
        }
    }

    fun openAddExerciseSheet() {
        replaceTargetExerciseId.value = null
        isAddExerciseSheetVisible.value = true
    }

    fun openReplaceExerciseSheet(targetExerciseId: String) {
        replaceTargetExerciseId.value = targetExerciseId
        isAddExerciseSheetVisible.value = true
    }

    fun closeAddExerciseSheet() {
        isAddExerciseSheetVisible.value = false
        replaceTargetExerciseId.value = null
    }

    fun updateExercisePickerQuery(query: String) {
        exercisePickerQuery.value = query
    }

    fun selectExercisePickerCategory(category: BodyPartCategory?) {
        exercisePickerCategory.value = category
    }

    fun onSelectExerciseFromPicker(exercise: Exercise) {
        val sessionId = uiState.value.session?.id ?: return
        val targetToReplace = replaceTargetExerciseId.value
        viewModelScope.launch(Dispatchers.IO) {
            if (targetToReplace != null) {
                manageActiveSessionUseCase.replaceExerciseInSession(
                    sessionId = sessionId,
                    oldExerciseId = targetToReplace,
                    newExerciseId = exercise.id
                )
            } else {
                manageActiveSessionUseCase.addExerciseToSession(
                    sessionId = sessionId,
                    exerciseId = exercise.id,
                    setsCount = exercise.defaultSets
                )
            }
            withContext(Dispatchers.Main) {
                closeAddExerciseSheet()
            }
        }
    }

    fun openRestTimerPicker(group: SessionExerciseGroup) {
        restTimerPickerExercise.value = group
    }

    fun closeRestTimerPicker() {
        restTimerPickerExercise.value = null
    }

    fun updateExerciseRestSeconds(exerciseId: String, newRestSeconds: Int) {
        val sessionId = uiState.value.session?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.updateExerciseRestSeconds(
                sessionId = sessionId,
                exerciseId = exerciseId,
                restSeconds = newRestSeconds
            )
            withContext(Dispatchers.Main) {
                closeRestTimerPicker()
            }
        }
    }

    fun updateExerciseNote(exerciseId: String, note: String?) {
        val sessionId = uiState.value.session?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.updateExerciseNote(sessionId, exerciseId, note)
        }
    }

    fun startRestTimer(seconds: Int, exerciseName: String) {
        val duration = seconds.coerceIn(10, 600)
        workoutTimerCoordinator.startRestTimer(duration, exerciseName)
    }

    fun cancelRestTimer() {
        workoutTimerCoordinator.cancelRestTimer()
    }

    fun togglePause() {
        val session = uiState.value.session ?: return
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.togglePause(
                sessionId = session.id,
                elapsedSeconds = uiState.value.liveElapsedSeconds
            )
        }
    }

    fun discardWorkout(onDiscarded: () -> Unit) {
        val targetSessionId = uiState.value.session?.id ?: activeSessionId.value
        workoutTimerCoordinator.stopWorkoutSession()
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.discardSession(targetSessionId)
            lastInitializedKey = null
            activeSessionId.value = null
            withContext(Dispatchers.Main) {
                onDiscarded()
            }
        }
    }

    fun finishWorkout(updatePlan: Boolean = false, onFinished: () -> Unit) {
        val session = uiState.value.session ?: return
        workoutTimerCoordinator.stopWorkoutSession()
        viewModelScope.launch(Dispatchers.IO) {
            manageActiveSessionUseCase.completeSession(
                sessionId = session.id,
                elapsedSeconds = uiState.value.liveElapsedSeconds,
                updatePlanValues = updatePlan
            )
            lastInitializedKey = null
            activeSessionId.value = null
            withContext(Dispatchers.Main) {
                onFinished()
            }
        }
    }
}
