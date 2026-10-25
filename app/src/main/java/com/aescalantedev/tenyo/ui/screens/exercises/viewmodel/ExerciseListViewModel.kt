package com.aescalantedev.tenyo.ui.screens.exercises.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseFilter
import com.aescalantedev.tenyo.core.domain.repository.ExerciseRepository
import com.aescalantedev.tenyo.core.domain.usecase.CreateCustomExerciseUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExercisesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ToggleFavoriteExerciseUseCase
import com.aescalantedev.tenyo.ui.screens.exercises.components.CAROUSEL_MUSCLE_OPTIONS
import com.aescalantedev.tenyo.ui.screens.exercises.components.CarouselMuscleOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Criterio de ordenamiento para el catálogo de ejercicios.
 */
enum class ExerciseSortOrder(val labelEs: String) {
    RECENT("Recién Realizado"),
    NAME_ASC("Nombre (A-Z)"),
    POPULAR("Más populares")
}

/**
 * Estado de la pantalla de Lista de Ejercicios (Stitch 04).
 */
data class ExerciseListUiState(
    val isLoading: Boolean = false,
    val selectedBodyPart: BodyPartCategory? = null,
    val selectedMuscleOptionId: String? = null,
    val selectedEquipment: String? = null,
    val searchQuery: String = "",
    val onlyFavorites: Boolean = false,
    val sortOrder: ExerciseSortOrder = ExerciseSortOrder.RECENT,
    val isGridView: Boolean = true,
    val availableEquipment: List<String> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val scrollPositionIndex: Int = 0,
    val scrollPositionOffset: Int = 0
) {
    /**
     * Conteo de filtros activos aplicados (para la insignia del botón de filtro).
     */
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedEquipment != null) count++
            if (onlyFavorites) count++
            if (searchQuery.isNotBlank()) count++
            return count
        }
}

/**
 * ViewModel para la pantalla de Lista de Ejercicios por Categoria / Rutina (Stitch 04).
 * Soporta filtrado por grupo muscular, equipamiento, favoritos, busqueda por texto y creacion
 * de ejercicios personalizados.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseListViewModel(
    private val observeExercisesUseCase: ObserveExercisesUseCase,
    private val exerciseRepository: ExerciseRepository,
    private val toggleFavoriteExerciseUseCase: ToggleFavoriteExerciseUseCase,
    private val createCustomExerciseUseCase: CreateCustomExerciseUseCase
) : ViewModel() {

    private val filterState = MutableStateFlow(ExerciseFilter())
    private val selectedMuscleOptionIdState = MutableStateFlow<String?>(null)
    private val sortOrderState = MutableStateFlow(ExerciseSortOrder.RECENT)
    private val isGridViewState = MutableStateFlow(true)
    private val scrollPositionState = MutableStateFlow(0 to 0)

    private var lastAppliedArguments: Triple<String?, String, String?>? = null

    private val availableEquipmentFlow = filterState.flatMapLatest { filter ->
        exerciseRepository.observeAvailableEquipment(filter.bodyPart)
    }

    private val exercisesFlow = filterState.flatMapLatest { filter ->
        observeExercisesUseCase(filter = filter, limit = 150)
    }

    private val filterCombinedFlow = combine(
        filterState,
        selectedMuscleOptionIdState
    ) { filter, optionId ->
        filter to optionId
    }

    private val viewPreferencesFlow = combine(
        sortOrderState,
        isGridViewState,
        scrollPositionState
    ) { sortOrder, isGridView, scrollPos ->
        Triple(sortOrder, isGridView, scrollPos)
    }

    val uiState: StateFlow<ExerciseListUiState> = combine(
        filterCombinedFlow,
        availableEquipmentFlow,
        exercisesFlow,
        viewPreferencesFlow
    ) { (filter, selectedOptionId), equipmentList, rawExercises, viewPrefs ->
        val (sortOrder, isGridView, scrollPos) = viewPrefs
        val sortedExercises = when (sortOrder) {
            ExerciseSortOrder.NAME_ASC -> rawExercises.sortedBy { it.name.lowercase() }
            ExerciseSortOrder.RECENT -> rawExercises
            ExerciseSortOrder.POPULAR -> rawExercises.sortedByDescending { it.isFavorite }
        }
        ExerciseListUiState(
            isLoading = false,
            selectedBodyPart = filter.bodyPart,
            selectedMuscleOptionId = selectedOptionId,
            selectedEquipment = filter.equipment,
            searchQuery = filter.query,
            onlyFavorites = filter.onlyFavorites,
            sortOrder = sortOrder,
            isGridView = isGridView,
            availableEquipment = equipmentList,
            exercises = sortedExercises,
            scrollPositionIndex = scrollPos.first,
            scrollPositionOffset = scrollPos.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExerciseListUiState(isLoading = true)
    )

    fun applyInitialArguments(
        bodyPartRawKey: String?,
        initialSearchQuery: String,
        equipment: String? = null
    ) {
        val currentArgs = Triple(bodyPartRawKey, initialSearchQuery, equipment)
        if (lastAppliedArguments == currentArgs) {
            // Si la ruta no cambió (por ejemplo, al volver atrás desde el detalle),
            // NO sobreescribir los filtros ni la búsqueda que el usuario modificó manualmente.
            return
        }
        lastAppliedArguments = currentArgs
        scrollPositionState.value = 0 to 0

        val category = bodyPartRawKey?.takeIf { it.isNotBlank() }?.let {
            BodyPartCategory.fromRawKey(it)
        }
        val matchingMuscleOption = CAROUSEL_MUSCLE_OPTIONS.firstOrNull { option ->
            (category != null && option.category == category && option.id.equals(initialSearchQuery, ignoreCase = true)) ||
            (option.targetMuscle != null && option.targetMuscle.equals(initialSearchQuery, ignoreCase = true)) ||
            (category != null && option.category == category && option.targetMuscle == null && initialSearchQuery.isBlank())
        }

        val selectedOptionId = matchingMuscleOption?.id
        val targetMuscle = matchingMuscleOption?.targetMuscle
        val effectiveQuery = if (matchingMuscleOption != null && initialSearchQuery.equals(matchingMuscleOption.id, ignoreCase = true)) {
            ""
        } else {
            initialSearchQuery
        }

        selectedMuscleOptionIdState.value = selectedOptionId
        filterState.value = ExerciseFilter(
            query = effectiveQuery,
            bodyPart = category,
            targetMuscle = targetMuscle,
            equipment = equipment,
            onlyFavorites = false
        )
    }

    fun updateScrollPosition(index: Int, offset: Int) {
        if (scrollPositionState.value.first != index || scrollPositionState.value.second != offset) {
            scrollPositionState.value = index to offset
        }
    }

    fun selectMuscleOption(option: CarouselMuscleOption?) {
        if (option == null) {
            selectedMuscleOptionIdState.value = null
            filterState.value = filterState.value.copy(
                bodyPart = null,
                targetMuscle = null,
                equipment = null
            )
        } else {
            selectedMuscleOptionIdState.value = option.id
            filterState.value = filterState.value.copy(
                bodyPart = option.category,
                targetMuscle = option.targetMuscle,
                equipment = null
            )
        }
    }

    fun selectBodyPart(category: BodyPartCategory?) {
        val matchingOption = CAROUSEL_MUSCLE_OPTIONS.firstOrNull { it.category == category && it.targetMuscle == null }
            ?: CAROUSEL_MUSCLE_OPTIONS.firstOrNull { it.category == category }
        selectedMuscleOptionIdState.value = matchingOption?.id
        filterState.value = filterState.value.copy(
            bodyPart = category,
            targetMuscle = matchingOption?.targetMuscle,
            equipment = null
        )
    }

    fun selectEquipment(equipment: String?) {
        filterState.value = filterState.value.copy(equipment = equipment)
    }

    fun updateSearchQuery(query: String) {
        filterState.value = filterState.value.copy(query = query)
    }

    fun toggleOnlyFavorites() {
        filterState.value = filterState.value.copy(
            onlyFavorites = !filterState.value.onlyFavorites
        )
    }

    fun setSortOrder(order: ExerciseSortOrder) {
        sortOrderState.value = order
    }

    fun toggleViewMode() {
        isGridViewState.value = !isGridViewState.value
    }

    fun clearFilters() {
        selectedMuscleOptionIdState.value = null
        filterState.value = filterState.value.copy(
            bodyPart = null,
            targetMuscle = null,
            equipment = null,
            onlyFavorites = false,
            query = ""
        )
    }

    fun toggleFavorite(exerciseId: String) {
        viewModelScope.launch {
            toggleFavoriteExerciseUseCase(exerciseId)
        }
    }

    fun createCustomExercise(
        name: String,
        bodyPart: BodyPartCategory,
        equipment: String,
        targetMuscle: String,
        instructions: String,
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val newId = createCustomExerciseUseCase(
                name = name,
                bodyPart = bodyPart,
                equipment = equipment,
                targetMuscle = targetMuscle,
                instructions = instructions
            )
            onCreated(newId)
        }
    }
}
