package com.aescalantedev.tenyo.core.domain.usecase

import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.CategoryRegion
import com.aescalantedev.tenyo.core.domain.model.CategorySummary
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseFilter
import com.aescalantedev.tenyo.core.domain.model.ExerciseHistoryEntry
import com.aescalantedev.tenyo.core.domain.model.ExerciseRecord
import com.aescalantedev.tenyo.core.domain.repository.ExerciseRepository
import com.aescalantedev.tenyo.core.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Inicializa el catálogo de ejercicios (seed de los 1,324 ejercicios del dataset en Room).
 */
class EnsureCatalogSeededUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke() {
        exerciseRepository.ensureCatalogSeeded()
    }
}

/**
 * Observa las categorías musculares filtradas opcionalmente por región anatómica y búsqueda (Stitch 02 y 03).
 */
class ObserveCategorySummariesUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(
        region: CategoryRegion = CategoryRegion.ALL,
        searchQuery: String = ""
    ): Flow<List<CategorySummary>> {
        return exerciseRepository.observeCategorySummaries().map { summaries ->
            summaries.filter { item ->
                val matchesRegion = region == CategoryRegion.ALL || item.category.region == region
                val matchesQuery = searchQuery.isBlank() ||
                    item.category.nameEs.contains(searchQuery, ignoreCase = true) ||
                    item.category.nameEn.contains(searchQuery, ignoreCase = true) ||
                    item.category.subtitleEs.contains(searchQuery, ignoreCase = true)
                matchesRegion && matchesQuery
            }
        }
    }
}

/**
 * Observa la lista de ejercicios aplicando filtros de categoría, búsqueda, equipo o favoritos (Stitch 04).
 */
class ObserveExercisesUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(filter: ExerciseFilter, limit: Int = 120): Flow<List<Exercise>> =
        exerciseRepository.observeExercises(filter = filter, limit = limit)
}

/**
 * Observa ejercicios populares/destacados para el Dashboard de Inicio (Stitch 02).
 */
class ObserveFeaturedExercisesUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(limit: Int = 8): Flow<List<Exercise>> =
        exerciseRepository.observeFeaturedExercises(limit = limit)
}

/**
 * Observa el detalle completo de un ejercicio por su `id` (Stitch 05).
 */
class ObserveExerciseDetailUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(exerciseId: String): Flow<Exercise?> =
        exerciseRepository.observeExerciseById(exerciseId)
}

/**
 * Alterna el estado de favorito de un ejercicio.
 */
class ToggleFavoriteExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exerciseId: String) {
        exerciseRepository.toggleFavorite(exerciseId)
    }
}

/**
 * Crea un ejercicio personalizado en el catálogo local (Stitch 04).
 */
class CreateCustomExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(
        name: String,
        bodyPart: BodyPartCategory,
        equipment: String,
        targetMuscle: String,
        instructions: String,
        defaultSets: Int = 4,
        defaultReps: String = "8-12"
    ): String = exerciseRepository.createCustomExercise(
        name = name.trim(),
        bodyPart = bodyPart,
        equipment = equipment.trim().ifBlank { "body weight" },
        targetMuscle = targetMuscle.trim().ifBlank { bodyPart.nameEs },
        instructions = instructions.trim(),
        defaultSets = defaultSets,
        defaultReps = defaultReps
    )
}

/**
 * Observa el récord personal (PR) y el historial de sesiones completadas para un ejercicio (Stitch 05).
 */
class ObserveExercisePerformanceUseCase(
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    fun observeRecord(exerciseId: String): Flow<ExerciseRecord?> =
        workoutSessionRepository.observeExerciseRecord(exerciseId)

    fun observeHistory(exerciseId: String): Flow<List<ExerciseHistoryEntry>> =
        workoutSessionRepository.observeExerciseHistory(exerciseId)
}
