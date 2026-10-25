package com.aescalantedev.tenyo.ui.screens.plans.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.PlanDifficulty
import com.aescalantedev.tenyo.core.domain.model.PlanGoal
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.core.domain.usecase.CreateCustomWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ManagePlanRoutinesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveWorkoutPlansUseCase
import com.aescalantedev.tenyo.core.domain.usecase.SelectActiveWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.notification.WorkoutPlanAlarmScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de Planes de Entrenamiento (Stitch 07).
 */
data class WorkoutPlansUiState(
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0, // 0 = Mis planes, 1 = Explorar catálogo
    val selectedGoal: PlanGoal = PlanGoal.ALL,
    val myPlans: List<WorkoutPlan> = emptyList(),
    val catalogPlans: List<WorkoutPlan> = emptyList(),
    val isCreatePlanModalVisible: Boolean = false
) {
    val activePlan: WorkoutPlan?
        get() = catalogPlans.firstOrNull { it.isActivePlan } ?: myPlans.firstOrNull { it.isActivePlan }
}

/**
 * ViewModel para la pantalla de Planes de Entrenamiento (Stitch 07).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutPlansViewModel(
    private val observeWorkoutPlansUseCase: ObserveWorkoutPlansUseCase,
    private val selectActiveWorkoutPlanUseCase: SelectActiveWorkoutPlanUseCase,
    private val createCustomWorkoutPlanUseCase: CreateCustomWorkoutPlanUseCase,
    private val managePlanRoutinesUseCase: ManagePlanRoutinesUseCase,
    private val alarmScheduler: WorkoutPlanAlarmScheduler
) : ViewModel() {

    private val selectedTabIndex = MutableStateFlow(0)
    private val selectedGoal = MutableStateFlow(PlanGoal.ALL)
    private val isCreatePlanModalVisible = MutableStateFlow(false)

    private val plansFlow = selectedGoal.flatMapLatest { goal ->
        observeWorkoutPlansUseCase(goalFilter = goal)
    }

    val uiState: StateFlow<WorkoutPlansUiState> = combine(
        selectedTabIndex,
        selectedGoal,
        plansFlow,
        isCreatePlanModalVisible
    ) { tabIndex, goal, plans, isModalVisible ->
        WorkoutPlansUiState(
            isLoading = false,
            selectedTabIndex = tabIndex,
            selectedGoal = goal,
            myPlans = plans.filter { it.isUserPlan || it.isActivePlan },
            catalogPlans = plans,
            isCreatePlanModalVisible = isModalVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WorkoutPlansUiState(isLoading = true)
    )

    fun selectTab(index: Int) {
        selectedTabIndex.value = index.coerceIn(0, 1)
    }

    fun selectGoal(goal: PlanGoal) {
        selectedGoal.value = goal
    }

    fun openCreatePlanModal() {
        isCreatePlanModalVisible.value = true
    }

    fun closeCreatePlanModal() {
        isCreatePlanModalVisible.value = false
    }

    fun activatePlan(planId: String) {
        viewModelScope.launch {
            selectActiveWorkoutPlanUseCase(planId)
            val activatedPlan = uiState.value.catalogPlans.firstOrNull { it.id == planId }
            if (activatedPlan != null) {
                if (activatedPlan.reminderEnabled) {
                    alarmScheduler.schedulePlanReminders(activatedPlan)
                } else {
                    alarmScheduler.cancelPlanReminders(planId)
                }
            }
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            managePlanRoutinesUseCase.deletePlan(planId)
            alarmScheduler.cancelPlanReminders(planId)
        }
    }

    fun createNewEmptyPlan(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val customCount = uiState.value.myPlans.size + 1
            val planId = createCustomWorkoutPlanUseCase(
                title = "Mi Rutina Semanal $customCount",
                subtitle = "5 días/sem · Personalizada",
                description = "Programa semanal estructurado con días de entrenamiento y descanso.",
                goal = PlanGoal.HYPERTROPHY,
                difficulty = PlanDifficulty.INTERMEDIATE,
                daysPerWeek = 5,
                durationWeeks = 8,
                estimatedMinutesPerSession = 50,
                initialExerciseIds = emptyList(),
                makeActive = false
            )
            selectedTabIndex.value = 0
            onCreated(planId)
        }
    }

    fun createCustomPlan(
        title: String,
        subtitle: String,
        description: String,
        goal: PlanGoal,
        difficulty: PlanDifficulty,
        daysPerWeek: Int,
        durationWeeks: Int,
        estimatedMinutes: Int,
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val planId = createCustomWorkoutPlanUseCase(
                title = title,
                subtitle = subtitle,
                description = description,
                goal = goal,
                difficulty = difficulty,
                daysPerWeek = daysPerWeek,
                durationWeeks = durationWeeks,
                estimatedMinutesPerSession = estimatedMinutes,
                makeActive = true
            )
            isCreatePlanModalVisible.value = false
            selectedTabIndex.value = 0
            onCreated(planId)
        }
    }
}
