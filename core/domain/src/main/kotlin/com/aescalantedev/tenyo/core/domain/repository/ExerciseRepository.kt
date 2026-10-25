package com.aescalantedev.tenyo.core.domain.repository

import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.CategorySummary
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseFilter
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para el catálogo de ejercicios (Offline-First con Room como SSOT).
 */
interface ExerciseRepository {
    /**
     * Garantiza que los 1,324 ejercicios del dataset y los planes iniciales estén cargados en Room.
     */
    suspend fun ensureCatalogSeeded()

    /**
     * Observa el resumen de categorías musculares junto con su conteo real de ejercicios.
     */
    fun observeCategorySummaries(): Flow<List<CategorySummary>>

    /**
     * Observa la lista de ejercicios filtrada por búsqueda, categoría muscular, equipo o favoritos.
     */
    fun observeExercises(filter: ExerciseFilter, limit: Int = 120): Flow<List<Exercise>>

    /**
     * Observa los ejercicios destacados/populares para mostrar en el Dashboard de Inicio (Stitch 02).
     */
    fun observeFeaturedExercises(limit: Int = 8): Flow<List<Exercise>>

    /**
     * Observa el detalle reactivo de un ejercicio por su `id`.
     */
    fun observeExerciseById(exerciseId: String): Flow<Exercise?>

    /**
     * Obtiene la lista de equipamientos distintos disponibles para una categoría muscular.
     */
    fun observeAvailableEquipment(bodyPart: BodyPartCategory?): Flow<List<String>>

    /**
     * Alterna el estado de favorito de un ejercicio.
     */
    suspend fun toggleFavorite(exerciseId: String)

    /**
     * Crea un ejercicio personalizado por el usuario (Stitch 04).
     */
    suspend fun createCustomExercise(
        name: String,
        bodyPart: BodyPartCategory,
        equipment: String,
        targetMuscle: String,
        instructions: String,
        defaultSets: Int = 4,
        defaultReps: String = "8-12"
    ): String
}
