package com.aescalantedev.tenyo.ui.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.AppThemeMode
import com.aescalantedev.tenyo.core.domain.model.ArchetypeProtocol
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.ExperienceLevel
import com.aescalantedev.tenyo.core.domain.model.OnboardingProfileData
import com.aescalantedev.tenyo.core.domain.model.ProgressSummary
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.model.WeightUnit
import com.aescalantedev.tenyo.core.domain.usecase.ManageUserPreferencesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveProgressSummaryUseCase
import com.aescalantedev.tenyo.ui.screens.welcome.components.TojiMuscleOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pestañas principales de configuración en la pantalla de Perfil.
 */
enum class ProfileTab(val titleEs: String) {
    BIOMETRICS_AND_MUSCLES("Físico & Músculos"),
    PROTOCOL("Protocolo"),
    SETTINGS_AND_THEME("Ajustes & Tema")
}

/**
 * Estado inmutable de la pantalla de Perfil y Ajustes de Tenyo.
 */
data class ProfileUiState(
    val preferences: UserPreferences = UserPreferences(),
    val progressSummary: ProgressSummary? = null,
    val statusMessage: String? = null,
    val selectedTab: ProfileTab = ProfileTab.BIOMETRICS_AND_MUSCLES,

    // Datos del formulario de edición del atleta
    val athleteName: String = "",
    val ageText: String = "24",
    val weightKg: Float = 75f,
    val heightCm: Float = 175f,
    val selectedExperience: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val trainingDays: Int = 4,
    val selectedArchetype: ArchetypeProtocol = ArchetypeProtocol.TOJI_SORCERER_KILLER,
    val selectedMuscles: Set<TojiMuscleOption> = setOf(TojiMuscleOption.CHEST, TojiMuscleOption.BACK_LATS),
    val isBackView: Boolean = false,
    val selectedBodyParts: List<BodyPartCategory> = listOf(BodyPartCategory.CHEST, BodyPartCategory.BACK),
    val isSaving: Boolean = false
) {
    val liveHeightMeters: Float
        get() = (heightCm / 100f).coerceAtLeast(1.2f)

    val liveBmi: Float
        get() = weightKg / (liveHeightMeters * liveHeightMeters)
}

/**
 * ViewModel para la Pantalla de Perfil de Tenyo.
 *
 * Centraliza la lógica de negocio, edición biométrica, visor interactivo Rive de Toji,
 * selección anatómica, protocolo maestro y personalización visual (temas, idioma, unidades).
 */
class ProfileViewModel(
    private val manageUserPreferencesUseCase: ManageUserPreferencesUseCase,
    private val observeProgressSummaryUseCase: ObserveProgressSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var isInitialized = false

    init {
        viewModelScope.launch {
            manageUserPreferencesUseCase.preferences.collectLatest { prefs ->
                _uiState.update { current ->
                    if (!isInitialized) {
                        isInitialized = true
                        val muscles = TojiMuscleOption.entries.filter { it.category in prefs.focusBodyParts }.toSet()
                        current.copy(
                            preferences = prefs,
                            athleteName = prefs.userName,
                            ageText = prefs.ageYears.toString(),
                            weightKg = prefs.weightKg.toFloat(),
                            heightCm = prefs.heightCm.toFloat(),
                            selectedExperience = prefs.experienceLevel,
                            trainingDays = prefs.trainingDaysPerWeek,
                            selectedArchetype = prefs.archetypeProtocol,
                            selectedMuscles = if (muscles.isNotEmpty()) muscles else setOf(
                                TojiMuscleOption.CHEST,
                                TojiMuscleOption.BACK_LATS
                            ),
                            selectedBodyParts = if (prefs.focusBodyParts.isNotEmpty()) {
                                prefs.focusBodyParts
                            } else {
                                listOf(BodyPartCategory.CHEST, BodyPartCategory.BACK)
                            }
                        )
                    } else {
                        current.copy(preferences = prefs)
                    }
                }
            }
        }

        viewModelScope.launch {
            observeProgressSummaryUseCase().collectLatest { progress ->
                _uiState.update { it.copy(progressSummary = progress) }
            }
        }
    }

    fun selectTab(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateAthleteName(name: String) {
        _uiState.update { it.copy(athleteName = name) }
    }

    fun updateAgeText(age: String) {
        if (age.length <= 2 && age.all { it.isDigit() }) {
            _uiState.update { it.copy(ageText = age) }
        }
    }

    fun updateWeightKg(weight: Float) {
        _uiState.update { it.copy(weightKg = (weight * 2).toInt() / 2f) }
    }

    fun updateHeightCm(height: Float) {
        _uiState.update { it.copy(heightCm = height.toInt().toFloat()) }
    }

    /**
     * Alterna la selección de un músculo específico al tocar el modelo Rive de Toji.
     */
    fun toggleMuscle(option: TojiMuscleOption) {
        _uiState.update { state ->
            val updatedMuscles = state.selectedMuscles.toMutableSet()
            val isAdding = if (option in updatedMuscles) {
                if (updatedMuscles.size > 1) {
                    updatedMuscles.remove(option)
                    false
                } else true
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

    /**
     * Alterna una categoría anatómica completa (ej. Pantorrillas, Pecho, etc.).
     */
    fun toggleBodyPartCategory(category: BodyPartCategory) {
        _uiState.update { state ->
            val categoryOptions = TojiMuscleOption.optionsForCategory(category)
            val isCurrentlySelected = category in state.selectedBodyParts
            val updatedBodyParts = state.selectedBodyParts.toMutableList()
            val updatedMuscles = state.selectedMuscles.toMutableSet()

            var nextBackView = state.isBackView
            if (isCurrentlySelected) {
                if (updatedBodyParts.size > 1) {
                    updatedBodyParts.remove(category)
                    updatedMuscles.removeAll(categoryOptions.toSet())
                }
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

    fun selectExperience(level: ExperienceLevel) {
        _uiState.update { it.copy(selectedExperience = level) }
    }

    fun selectTrainingDays(days: Int) {
        _uiState.update { it.copy(trainingDays = days) }
    }

    fun selectArchetype(protocol: ArchetypeProtocol) {
        _uiState.update { it.copy(selectedArchetype = protocol) }
    }

    fun saveProfile() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val parsedAge = state.ageText.toIntOrNull()?.coerceIn(14, 90) ?: state.preferences.ageYears
            val profile = OnboardingProfileData(
                userName = state.athleteName.trim().ifBlank { "Atleta Tenyo" },
                gender = state.preferences.gender,
                ageYears = parsedAge,
                weightKg = state.weightKg.toDouble(),
                heightCm = state.heightCm.toInt(),
                activityLevel = state.preferences.activityLevel,
                trainingGoal = state.preferences.trainingGoal,
                experienceLevel = state.selectedExperience,
                trainingDaysPerWeek = state.trainingDays,
                sessionDurationMinutes = state.preferences.sessionDurationMinutes,
                focusBodyParts = state.selectedBodyParts.ifEmpty {
                    listOf(BodyPartCategory.CHEST, BodyPartCategory.BACK)
                },
                archetypeProtocol = state.selectedArchetype
            )
            manageUserPreferencesUseCase.completeOnboarding(profile)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    statusMessage = "Perfil y configuración guardados correctamente"
                )
            }
        }
    }

    fun selectLanguage(languageCode: String) {
        if (languageCode.equals("es", ignoreCase = true)) {
            viewModelScope.launch {
                manageUserPreferencesUseCase.updateLanguage("es")
                _uiState.update { it.copy(statusMessage = "Idioma: Español (Actual)") }
            }
        } else {
            _uiState.update {
                it.copy(statusMessage = "Próximamente: Soporte para ${languageCode.uppercase()} en una próxima actualización")
            }
        }
    }

    fun selectThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch {
            manageUserPreferencesUseCase.updateThemeMode(themeMode)
            _uiState.update { it.copy(statusMessage = "Tema aplicado: ${themeMode.titleEs}") }
        }
    }

    fun selectWeightUnit(weightUnit: WeightUnit) {
        viewModelScope.launch {
            manageUserPreferencesUseCase.updateWeightUnit(weightUnit)
            val msg = if (weightUnit == WeightUnit.LBS) {
                "Próximamente: Soporte para Libras (lb) en una próxima actualización"
            } else {
                "Unidad: Kilogramos (kg)"
            }
            _uiState.update { it.copy(statusMessage = msg) }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
