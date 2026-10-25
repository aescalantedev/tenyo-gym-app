package com.aescalantedev.tenyo.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.CategorySummary
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ProgressSummary
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import com.aescalantedev.tenyo.core.domain.usecase.ManageUserPreferencesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveActiveWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveCategorySummariesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveFeaturedExercisesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveProgressSummaryUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveWorkoutSessionUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ToggleFavoriteExerciseUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de Inicio (Stitch 02).
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val preferences: UserPreferences = UserPreferences(),
    val categories: List<CategorySummary> = emptyList(),
    val activePlan: WorkoutPlan? = null,
    val featuredExercises: List<Exercise> = emptyList(),
    val progressSummary: ProgressSummary? = null,
    val activeSession: WorkoutSession? = null,
    val completedSessions: List<WorkoutSession> = emptyList()
)

private data class BaseDashboardData(
    val preferences: UserPreferences,
    val categories: List<CategorySummary>,
    val activePlan: WorkoutPlan?,
    val featuredExercises: List<Exercise>,
    val progressSummary: ProgressSummary?
)

/**
 * ViewModel para el Dashboard de Inicio (Stitch 02).
 * Expone de forma reactiva el saludo personalizado, el rango fisico actual, las categorias con su
 * conteo real, el plan de entrenamiento activo de hoy, la sesion en curso, el historial reciente
 * y los ejercicios populares.
 */
class HomeViewModel(
    manageUserPreferencesUseCase: ManageUserPreferencesUseCase,
    observeCategorySummariesUseCase: ObserveCategorySummariesUseCase,
    observeActiveWorkoutPlanUseCase: ObserveActiveWorkoutPlanUseCase,
    observeFeaturedExercisesUseCase: ObserveFeaturedExercisesUseCase,
    observeProgressSummaryUseCase: ObserveProgressSummaryUseCase,
    observeWorkoutSessionUseCase: ObserveWorkoutSessionUseCase,
    private val toggleFavoriteExerciseUseCase: ToggleFavoriteExerciseUseCase
) : ViewModel() {

    private val baseDashboardFlow = combine(
        manageUserPreferencesUseCase.preferences,
        observeCategorySummariesUseCase(),
        observeActiveWorkoutPlanUseCase(),
        observeFeaturedExercisesUseCase(limit = 6),
        observeProgressSummaryUseCase()
    ) { prefs, categories, activePlan, featured, progress ->
        BaseDashboardData(prefs, categories, activePlan, featured, progress)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        baseDashboardFlow,
        observeWorkoutSessionUseCase.active(),
        observeWorkoutSessionUseCase.completed(limit = 3)
    ) { base, activeSession, completedSessions ->
        HomeUiState(
            isLoading = base.categories.isEmpty() && base.featuredExercises.isEmpty(),
            preferences = base.preferences,
            categories = base.categories,
            activePlan = base.activePlan,
            featuredExercises = base.featuredExercises,
            progressSummary = base.progressSummary,
            activeSession = activeSession,
            completedSessions = completedSessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomeUiState(isLoading = true)
    )

    fun toggleFavorite(exerciseId: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            runCatching {
                toggleFavoriteExerciseUseCase(exerciseId)
            }
        }
    }
}
