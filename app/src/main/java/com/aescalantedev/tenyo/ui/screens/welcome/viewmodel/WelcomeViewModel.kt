package com.aescalantedev.tenyo.ui.screens.welcome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.ActivityLevel
import com.aescalantedev.tenyo.core.domain.model.ArchetypeProtocol
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.ExperienceLevel
import com.aescalantedev.tenyo.core.domain.model.OnboardingProfileData
import com.aescalantedev.tenyo.core.domain.model.TrainingGoal
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.core.domain.usecase.ManageUserPreferencesUseCase
import com.aescalantedev.tenyo.ui.screens.welcome.components.TojiMuscleOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado inmutable de la pantalla `WelcomeScreen` (Onboarding de 3 pasos de diseno premium-minimal).
 */
data class WelcomeUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 3,
    // Paso 1: Perfil
    val athleteName: String = "",
    val gender: UserGender = UserGender.MALE,
    val ageYears: Int = 26,
    val heightCm: Int = 170,
    val weightKg: Int = 72,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    // Paso 2: Objetivo y Cadencia
    val trainingGoal: TrainingGoal = TrainingGoal.BUILD_MUSCLE,
    val trainingDaysPerWeek: Int = 4,
    val sessionDurationMinutes: Int = 45,
    val selectedExperience: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    // Paso 3: Enfoque Muscular
    val selectedMuscles: Set<TojiMuscleOption> = setOf(TojiMuscleOption.CHEST, TojiMuscleOption.SHOULDERS),
    val isBackView: Boolean = false,
    val selectedBodyParts: List<BodyPartCategory> = listOf(BodyPartCategory.CHEST, BodyPartCategory.SHOULDERS),
    val selectedArchetype: ArchetypeProtocol = ArchetypeProtocol.entries.first(),
    val isActivatingProtocol: Boolean = false
) {
    val bmi: Float
        get() {
            val heightMeters = (heightCm / 100f).coerceAtLeast(1.2f)
            return weightKg / (heightMeters * heightMeters)
        }

    val bmiClassification: String
        get() = when {
            bmi < 18.5f -> "Estructura Ligera · Ideal para fase de masa magra"
            bmi < 25.0f -> "Rango Atletico · Base optima para Restriccion Celestial"
            bmi < 29.0f -> "Potencia Muscular / Recomposicion corporal"
            else -> "Alta Densidad · Prioridad en definicion y acondicionamiento"
        }
}

/**
 * ViewModel para `WelcomeScreen`.
 * Centraliza la seleccion de perfil, objetivos, cadencia semanal, seleccion anatomica y persistencia.
 */
class WelcomeViewModel(
    private val manageUserPreferencesUseCase: ManageUserPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = manageUserPreferencesUseCase.preferences.first()
            _uiState.update { current ->
                current.copy(
                    athleteName = if (prefs.userName != "Atleta Tenyo") prefs.userName else "",
                    gender = prefs.gender,
                    ageYears = prefs.ageYears,
                    weightKg = prefs.weightKg.toInt().coerceIn(40, 150),
                    heightCm = prefs.heightCm.coerceIn(130, 220),
                    activityLevel = prefs.activityLevel,
                    trainingGoal = prefs.trainingGoal,
                    trainingDaysPerWeek = prefs.trainingDaysPerWeek,
                    sessionDurationMinutes = prefs.sessionDurationMinutes,
                    selectedExperience = prefs.experienceLevel,
                    selectedArchetype = prefs.archetypeProtocol
                )
            }
        }
    }

    fun updateAthleteName(name: String) {
        _uiState.update { it.copy(athleteName = name) }
    }

    fun selectGender(gender: UserGender) {
        _uiState.update {
            it.copy(
                gender = gender,
                selectedArchetype = if (gender == UserGender.FEMALE) {
                    ArchetypeProtocol.MAKI_DEMON_TWIN
                } else {
                    ArchetypeProtocol.TOJI_SORCERER_KILLER
                }
            )
        }
    }

    fun updateAge(age: Int) {
        _uiState.update { it.copy(ageYears = age.coerceIn(14, 90)) }
    }

    fun updateHeight(height: Int) {
        _uiState.update { it.copy(heightCm = height.coerceIn(130, 220)) }
    }

    fun updateWeight(weight: Int) {
        _uiState.update { it.copy(weightKg = weight.coerceIn(40, 150)) }
    }

    fun selectActivityLevel(level: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = level) }
    }

    fun selectTrainingGoal(goal: TrainingGoal) {
        _uiState.update { it.copy(trainingGoal = goal) }
    }

    fun selectTrainingDaysPerWeek(days: Int) {
        _uiState.update { it.copy(trainingDaysPerWeek = days.coerceIn(2, 7)) }
    }

    fun selectSessionDuration(minutes: Int) {
        _uiState.update { it.copy(sessionDurationMinutes = minutes) }
    }

    fun selectExperience(level: ExperienceLevel) {
        _uiState.update { it.copy(selectedExperience = level) }
    }

    /**
     * Marca o desmarca dinamicamente un musculo individual en el selector de Toji (`toji.riv`),
     * girando automaticamente el personaje al frente o espalda segun el musculo seleccionado.
     */
    fun toggleMuscle(option: TojiMuscleOption) {
        _uiState.update { state ->
            val updatedMuscles = state.selectedMuscles.toMutableSet()
            val isAdding = if (option in updatedMuscles) {
                updatedMuscles.remove(option)
                false
            } else {
                updatedMuscles.add(option)
                true
            }

            val anatomicalCategories = updatedMuscles.map { it.category }.distinct()
            val nonAnatomicalCategories = state.selectedBodyParts.filter { cat ->
                TojiMuscleOption.optionsForCategory(cat).isEmpty()
            }
            val updatedBodyParts = (anatomicalCategories + nonAnatomicalCategories).distinct()

            state.copy(
                selectedMuscles = updatedMuscles,
                isBackView = if (isAdding) option.isBackView else state.isBackView,
                selectedBodyParts = updatedBodyParts
            )
        }
    }

    fun toggleViewSide() {
        _uiState.update { it.copy(isBackView = !it.isBackView) }
    }

    fun toggleBodyPartCategory(category: BodyPartCategory) {
        _uiState.update { state ->
            val categoryOptions = TojiMuscleOption.optionsForCategory(category)
            val isCurrentlySelected = category in state.selectedBodyParts
            val updatedBodyParts = state.selectedBodyParts.toMutableList()
            val updatedMuscles = state.selectedMuscles.toMutableSet()

            var nextBackView = state.isBackView
            if (isCurrentlySelected) {
                updatedBodyParts.remove(category)
                updatedMuscles.removeAll(categoryOptions.toSet())
            } else {
                updatedBodyParts.add(category)
                updatedMuscles.addAll(categoryOptions)
                categoryOptions.firstOrNull()?.let { firstOption ->
                    nextBackView = firstOption.isBackView
                }
            }

            state.copy(
                selectedBodyParts = updatedBodyParts,
                selectedMuscles = updatedMuscles,
                isBackView = nextBackView
            )
        }
    }

    fun selectArchetype(protocol: ArchetypeProtocol) {
        _uiState.update { it.copy(selectedArchetype = protocol) }
    }

    fun nextStep() {
        _uiState.update { state ->
            if (state.currentStep < state.totalSteps - 1) {
                state.copy(currentStep = state.currentStep + 1)
            } else {
                state
            }
        }
    }

    fun previousStep() {
        _uiState.update { state ->
            state.copy(currentStep = (state.currentStep - 1).coerceAtLeast(0))
        }
    }

    fun completeOnboarding(
        openCategoriesFirst: Boolean,
        onCompleted: (openCategoriesFirst: Boolean) -> Unit
    ) {
        val currentState = _uiState.value
        if (currentState.isActivatingProtocol) return

        viewModelScope.launch {
            _uiState.update { it.copy(isActivatingProtocol = true) }

            val focusList = currentState.selectedBodyParts.ifEmpty {
                listOf(BodyPartCategory.BACK, BodyPartCategory.SHOULDERS, BodyPartCategory.CHEST)
            }
            val profile = OnboardingProfileData(
                userName = currentState.athleteName.trim().ifBlank { "Atleta Tenyo" },
                gender = currentState.gender,
                ageYears = currentState.ageYears,
                weightKg = currentState.weightKg.toDouble(),
                heightCm = currentState.heightCm,
                activityLevel = currentState.activityLevel,
                trainingGoal = currentState.trainingGoal,
                experienceLevel = currentState.selectedExperience,
                trainingDaysPerWeek = currentState.trainingDaysPerWeek,
                sessionDurationMinutes = currentState.sessionDurationMinutes,
                focusBodyParts = focusList,
                archetypeProtocol = currentState.selectedArchetype
            )

            manageUserPreferencesUseCase.completeOnboarding(profile = profile)
            delay(PROTOCOL_ACTIVATION_DELAY_MILLIS)
            _uiState.update { it.copy(isActivatingProtocol = false) }
            onCompleted(openCategoriesFirst)
        }
    }

    companion object {
        private const val PROTOCOL_ACTIVATION_DELAY_MILLIS = 450L
    }
}
