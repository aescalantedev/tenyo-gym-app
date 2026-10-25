package com.aescalantedev.tenyo.ui.screens.exercises.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseHistoryEntry
import com.aescalantedev.tenyo.core.domain.model.ExerciseRecord
import com.aescalantedev.tenyo.core.domain.usecase.AddExerciseToPlanUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ManageUserPreferencesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExerciseDetailUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExercisePerformanceUseCase
import com.aescalantedev.tenyo.core.domain.usecase.StartWorkoutSessionUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ToggleFavoriteExerciseUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de Detalle del Ejercicio (Stitch 05).
 */
data class ExerciseDetailUiState(
    val isLoading: Boolean = true,
    val exercise: Exercise? = null,
    val personalRecord: ExerciseRecord? = null,
    val history: List<ExerciseHistoryEntry> = emptyList(),
    val selectedTabIndex: Int = 0, // 0 = Técnica y Guía, 1 = Historial y PRs
    val languageCode: String = "es",
    val activePlanId: String = "plan_toji_heavenly_restriction",
    val statusMessage: String? = null
)

/**
 * ViewModel para la Ficha Técnica / Detalle del Ejercicio (Stitch 05).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseDetailViewModel(
    private val observeExerciseDetailUseCase: ObserveExerciseDetailUseCase,
    private val observeExercisePerformanceUseCase: ObserveExercisePerformanceUseCase,
    manageUserPreferencesUseCase: ManageUserPreferencesUseCase,
    private val toggleFavoriteExerciseUseCase: ToggleFavoriteExerciseUseCase,
    private val addExerciseToPlanUseCase: AddExerciseToPlanUseCase,
    private val startWorkoutSessionUseCase: StartWorkoutSessionUseCase
) : ViewModel() {

    private val currentExerciseId = MutableStateFlow("")
    private val selectedTabIndex = MutableStateFlow(0)
    private val statusMessage = MutableStateFlow<String?>(null)

    private val exerciseFlow = currentExerciseId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(null) else observeExerciseDetailUseCase(id)
    }

    private val recordFlow = currentExerciseId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(null) else observeExercisePerformanceUseCase.observeRecord(id)
    }

    private val historyFlow = currentExerciseId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(emptyList()) else observeExercisePerformanceUseCase.observeHistory(id)
    }

    private val performanceFlow = combine(
        recordFlow,
        historyFlow,
        selectedTabIndex
    ) { record, history, tabIndex ->
        Triple(record, history, tabIndex)
    }

    val uiState: StateFlow<ExerciseDetailUiState> = combine(
        exerciseFlow,
        performanceFlow,
        manageUserPreferencesUseCase.preferences,
        statusMessage
    ) { exercise, performance, prefs, message ->
        val (record, history, tabIndex) = performance
        ExerciseDetailUiState(
            isLoading = exercise == null,
            exercise = exercise,
            personalRecord = record,
            history = history,
            selectedTabIndex = tabIndex,
            languageCode = prefs.languageCode,
            activePlanId = prefs.activePlanId,
            statusMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExerciseDetailUiState(isLoading = true)
    )

    fun loadExercise(exerciseId: String) {
        if (currentExerciseId.value != exerciseId) {
            currentExerciseId.value = exerciseId
            statusMessage.value = null
        }
    }

    fun selectTab(index: Int) {
        selectedTabIndex.value = index
    }

    fun toggleFavorite() {
        val id = currentExerciseId.value.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch {
            toggleFavoriteExerciseUseCase(id)
        }
    }

    fun addToActivePlan() {
        val exercise = uiState.value.exercise ?: return
        val planId = uiState.value.activePlanId
        viewModelScope.launch {
            addExerciseToPlanUseCase(
                planId = planId,
                exerciseId = exercise.id,
                targetSets = exercise.defaultSets,
                targetReps = exercise.defaultReps,
                restSeconds = exercise.defaultRestSeconds
            )
            statusMessage.value = "Añadido a tu plan activo"
        }
    }

    fun clearStatusMessage() {
        statusMessage.value = null
    }

    fun startSingleExerciseWorkout(onSessionStarted: () -> Unit) {
        val exercise = uiState.value.exercise ?: return
        viewModelScope.launch {
            startWorkoutSessionUseCase(
                planId = null,
                singleExerciseId = exercise.id
            )
            onSessionStarted()
        }
    }
}
