package com.aescalantedev.tenyo.ui.screens.plans.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseFilter
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanDay
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanExercise
import com.aescalantedev.tenyo.core.domain.usecase.ManagePlanRoutinesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExercisesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObservePlanByIdUseCase
import com.aescalantedev.tenyo.core.domain.usecase.SelectActiveWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.notification.WorkoutPlanAlarmScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class PlanDetailUiState(
    val isLoading: Boolean = true,
    val plan: WorkoutPlan? = null,
    val selectedDayOfWeek: Int = 1,
    val isAddExerciseSheetVisible: Boolean = false,
    val isTimePickerVisible: Boolean = false,
    val isRenameDayDialogVisible: Boolean = false,
    val isRenameTitleDialogVisible: Boolean = false,
    val editingExerciseConfig: WorkoutPlanExercise? = null,
    val availableExercises: List<Exercise> = emptyList(),
    val exercisePickerQuery: String = "",
    val exercisePickerCategory: BodyPartCategory? = null,
    val customExerciseOrder: List<String> = emptyList()
) {
    val selectedDay: WorkoutPlanDay?
        get() = plan?.days?.firstOrNull { it.dayOfWeek == selectedDayOfWeek }

    val exercisesForSelectedDay: List<WorkoutPlanExercise>
        get() {
            val raw = selectedDay?.exercises.orEmpty()
            if (customExerciseOrder.isEmpty()) return raw
            val orderMap = customExerciseOrder.withIndex().associate { it.value to it.index }
            return raw.sortedBy { orderMap[it.exercise.id] ?: Int.MAX_VALUE }
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlanDetailViewModel(
    private val observePlanByIdUseCase: ObservePlanByIdUseCase,
    private val managePlanRoutinesUseCase: ManagePlanRoutinesUseCase,
    private val selectActiveWorkoutPlanUseCase: SelectActiveWorkoutPlanUseCase,
    private val observeExercisesUseCase: ObserveExercisesUseCase,
    private val alarmScheduler: WorkoutPlanAlarmScheduler
) : ViewModel() {

    private val planIdFlow = MutableStateFlow<String?>(null)
    private val selectedDayOfWeekFlow = MutableStateFlow(
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    )

    private val isAddExerciseSheetVisible = MutableStateFlow(false)
    private val isTimePickerVisible = MutableStateFlow(false)
    private val isRenameDayDialogVisible = MutableStateFlow(false)
    private val isRenameTitleDialogVisible = MutableStateFlow(false)
    private val editingExerciseConfig = MutableStateFlow<WorkoutPlanExercise?>(null)
    private val exercisePickerQuery = MutableStateFlow("")
    private val exercisePickerCategory = MutableStateFlow<BodyPartCategory?>(null)
    private val customExerciseOrder = MutableStateFlow<List<String>>(emptyList())

    private val planFlow = planIdFlow.flatMapLatest { id ->
        if (id.isNullOrBlank()) flowOf(null)
        else observePlanByIdUseCase(id)
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
        observeExercisesUseCase(filter, limit = 60)
    }

    val uiState: StateFlow<PlanDetailUiState> = combine(
        planFlow,
        selectedDayOfWeekFlow,
        isAddExerciseSheetVisible,
        isTimePickerVisible,
        isRenameDayDialogVisible,
        isRenameTitleDialogVisible,
        editingExerciseConfig,
        availableExercisesFlow,
        customExerciseOrder
    ) { params ->
        val plan = params[0] as WorkoutPlan?
        val selectedDay = params[1] as Int
        val sheetVisible = params[2] as Boolean
        val timePickerVisible = params[3] as Boolean
        val renameVisible = params[4] as Boolean
        val renameTitleVisible = params[5] as Boolean
        val editingConfig = params[6] as WorkoutPlanExercise?
        @Suppress("UNCHECKED_CAST")
        val exercises = params[7] as List<Exercise>
        @Suppress("UNCHECKED_CAST")
        val order = params[8] as List<String>

        PlanDetailUiState(
            isLoading = plan == null,
            plan = plan,
            selectedDayOfWeek = selectedDay,
            isAddExerciseSheetVisible = sheetVisible,
            isTimePickerVisible = timePickerVisible,
            isRenameDayDialogVisible = renameVisible,
            isRenameTitleDialogVisible = renameTitleVisible,
            editingExerciseConfig = editingConfig,
            availableExercises = exercises,
            exercisePickerQuery = exercisePickerQuery.value,
            exercisePickerCategory = exercisePickerCategory.value,
            customExerciseOrder = order
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlanDetailUiState()
    )

    fun init(planId: String) {
        if (planIdFlow.value != planId) {
            planIdFlow.value = planId
        }
    }

    fun selectDay(dayOfWeek: Int) {
        selectedDayOfWeekFlow.value = dayOfWeek.coerceIn(1, 7)
        customExerciseOrder.value = emptyList()
    }

    fun toggleReminders(enabled: Boolean) {
        val currentPlan = uiState.value.plan ?: return
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateReminders(
                planId = currentPlan.id,
                hour = currentPlan.reminderHour,
                minute = currentPlan.reminderMinute,
                enabled = enabled
            )
            val updatedPlan = currentPlan.copy(reminderEnabled = enabled)
            if (enabled && updatedPlan.isActivePlan) {
                alarmScheduler.schedulePlanReminders(updatedPlan)
            } else {
                alarmScheduler.cancelPlanReminders(currentPlan.id)
            }
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        val currentPlan = uiState.value.plan ?: return
        isTimePickerVisible.value = false
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateReminders(
                planId = currentPlan.id,
                hour = hour,
                minute = minute,
                enabled = true
            )
            val updatedPlan = currentPlan.copy(
                reminderHour = hour,
                reminderMinute = minute,
                reminderEnabled = true
            )
            if (updatedPlan.isActivePlan) {
                alarmScheduler.schedulePlanReminders(updatedPlan)
            }
        }
    }

    fun activatePlan() {
        val currentPlan = uiState.value.plan ?: return
        viewModelScope.launch {
            selectActiveWorkoutPlanUseCase(currentPlan.id)
            if (currentPlan.reminderEnabled) {
                alarmScheduler.schedulePlanReminders(currentPlan)
            }
        }
    }

    fun updateDayDetails(name: String, isRestDay: Boolean) {
        val currentPlan = uiState.value.plan ?: return
        val currentDay = uiState.value.selectedDay ?: return
        isRenameDayDialogVisible.value = false
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateDay(
                dayId = currentDay.id,
                name = name.trim().ifBlank { if (isRestDay) "Descanso" else "Rutina Día ${currentDay.dayOfWeek}" },
                isRestDay = isRestDay
            )
            if (currentPlan.isActivePlan && currentPlan.reminderEnabled) {
                alarmScheduler.schedulePlanReminders(currentPlan)
            }
        }
    }

    fun addExerciseToCurrentDay(exerciseId: String) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        isAddExerciseSheetVisible.value = false
        viewModelScope.launch {
            managePlanRoutinesUseCase.addExerciseToDay(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId
            )
        }
    }

    fun addSingleExercise(planId: String, dayOfWeek: Int, exerciseId: String) {
        viewModelScope.launch {
            managePlanRoutinesUseCase.addExerciseToDay(
                planId = planId,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId
            )
        }
    }

    fun addExercises(planId: String, dayOfWeek: Int, exerciseIds: List<String>) {
        viewModelScope.launch {
            managePlanRoutinesUseCase.addExercisesToDay(
                planId = planId,
                dayOfWeek = dayOfWeek,
                exerciseIds = exerciseIds
            )
        }
    }

    fun removeExerciseFromCurrentDay(exerciseId: String) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        viewModelScope.launch {
            managePlanRoutinesUseCase.removeExerciseFromDay(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId
            )
        }
    }

    fun openAddExerciseSheet() {
        exercisePickerQuery.value = ""
        exercisePickerCategory.value = null
        isAddExerciseSheetVisible.value = true
    }

    fun closeAddExerciseSheet() {
        isAddExerciseSheetVisible.value = false
    }

    fun openTimePicker() {
        isTimePickerVisible.value = true
    }

    fun closeTimePicker() {
        isTimePickerVisible.value = false
    }

    fun openRenameDayDialog() {
        isRenameDayDialogVisible.value = true
    }

    fun closeRenameDayDialog() {
        isRenameDayDialogVisible.value = false
    }

    fun updateExercisePickerQuery(query: String) {
        exercisePickerQuery.value = query
    }

    fun toggleRestDayForCurrentDay() {
        val currentPlan = uiState.value.plan ?: return
        val currentDay = uiState.value.selectedDay ?: return
        val newIsRest = !currentDay.isRestDay
        val newName = if (newIsRest) "Descanso" else "Rutina Día ${currentDay.dayOfWeek}"
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateDay(
                dayId = currentDay.id,
                name = newName,
                isRestDay = newIsRest
            )
            if (currentPlan.isActivePlan && currentPlan.reminderEnabled) {
                alarmScheduler.schedulePlanReminders(currentPlan)
            }
        }
    }

    fun openEditExerciseConfig(exercise: WorkoutPlanExercise) {
        editingExerciseConfig.value = exercise
    }

    fun closeEditExerciseConfig() {
        editingExerciseConfig.value = null
    }

    fun updateExerciseConfig(
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    ) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        closeEditExerciseConfig()
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateExerciseConfig(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                targetSets = targetSets,
                targetReps = targetReps,
                restSeconds = restSeconds,
                suggestedWeightKg = suggestedWeightKg
            )
        }
    }

    fun openRenameTitleDialog() {
        isRenameTitleDialogVisible.value = true
    }

    fun closeRenameTitleDialog() {
        isRenameTitleDialogVisible.value = false
    }

    fun updatePlanTitle(newTitle: String) {
        val currentPlan = uiState.value.plan ?: return
        closeRenameTitleDialog()
        viewModelScope.launch {
            managePlanRoutinesUseCase.updatePlanTitle(
                planId = currentPlan.id,
                title = newTitle
            )
        }
    }

    fun selectExercisePickerCategory(category: BodyPartCategory?) {
        exercisePickerCategory.value = category
    }

    fun saveExerciseNote(exerciseId: String, note: String?) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateExerciseNote(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                note = note
            )
        }
    }

    fun addExtraSet(exerciseId: String, currentSets: Int) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateExerciseSets(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                targetSets = currentSets + 1
            )
        }
    }

    fun deleteSet(exerciseId: String, currentSets: Int) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        if (currentSets <= 1) return
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateExerciseSets(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                targetSets = currentSets - 1
            )
        }
    }

    fun updateExerciseRestSeconds(exerciseId: String, restSeconds: Int) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateExerciseRestSeconds(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                restSeconds = restSeconds
            )
        }
    }

    fun adjustExerciseParameters(exerciseId: String, targetReps: String, suggestedWeightKg: Double) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateExerciseParameters(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                targetReps = targetReps,
                suggestedWeightKg = suggestedWeightKg
            )
        }
    }

    fun updateExerciseSetParameters(
        exerciseId: String,
        setNumber: Int,
        targetReps: String,
        suggestedWeightKg: Double
    ) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        viewModelScope.launch {
            managePlanRoutinesUseCase.updateExerciseSetParameters(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                setNumber = setNumber,
                targetReps = targetReps,
                suggestedWeightKg = suggestedWeightKg
            )
        }
    }

    fun deleteExerciseSet(exerciseId: String, setNumber: Int) {
        val currentPlan = uiState.value.plan ?: return
        val dayOfWeek = uiState.value.selectedDayOfWeek
        viewModelScope.launch {
            managePlanRoutinesUseCase.deleteExerciseSet(
                planId = currentPlan.id,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                setNumber = setNumber
            )
        }
    }

    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        val currentPlan = uiState.value.plan ?: return
        val currentDay = uiState.value.selectedDayOfWeek
        val exercises = uiState.value.exercisesForSelectedDay.toMutableList()
        if (fromIndex in exercises.indices && toIndex in exercises.indices) {
            val moved = exercises.removeAt(fromIndex)
            exercises.add(toIndex, moved)
            val newIds = exercises.map { it.exercise.id }
            customExerciseOrder.value = newIds

            viewModelScope.launch {
                managePlanRoutinesUseCase.reorderExercises(
                    planId = currentPlan.id,
                    dayOfWeek = currentDay,
                    orderedExerciseIds = newIds
                )
            }
        }
    }
}
