package com.aescalantedev.tenyo.core.data.repository

import com.aescalantedev.tenyo.core.data.local.dao.ExerciseDao
import com.aescalantedev.tenyo.core.data.local.entity.ExerciseEntity
import com.aescalantedev.tenyo.core.data.mapper.TenyoEntityMappers.toDomain
import com.aescalantedev.tenyo.core.data.media.MediaUrlResolver
import com.aescalantedev.tenyo.core.data.seed.CatalogSeedManager
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.CategorySummary
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseFilter
import com.aescalantedev.tenyo.core.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Implementacion Offline-First de [ExerciseRepository].
 *
 * Utiliza Room ([ExerciseDao]) como Unica Fuente de Verdad (SSOT) y resuelve las URLs
 * de miniaturas e imagenes GIF mediante [MediaUrlResolver].
 */
class OfflineFirstExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val mediaUrlResolver: MediaUrlResolver,
    private val catalogSeedManager: CatalogSeedManager
) : ExerciseRepository {

    private val json = Json { encodeDefaults = true }

    override suspend fun ensureCatalogSeeded() {
        catalogSeedManager.ensureSeeded()
    }

    override fun observeCategorySummaries(): Flow<List<CategorySummary>> {
        return exerciseDao.observeCountsByBodyPart().map { projections ->
            val countsMap = projections.associate {
                BodyPartCategory.fromRawKey(it.bodyPart) to it.exerciseCount
            }
            BodyPartCategory.entries.map { category ->
                CategorySummary(
                    category = category,
                    exerciseCount = countsMap[category] ?: 0
                )
            }
        }
    }

    override fun observeExercises(filter: ExerciseFilter, limit: Int): Flow<List<Exercise>> {
        return exerciseDao.observeFilteredExercises(
            query = filter.query.trim(),
            bodyPart = filter.bodyPart?.rawKey,
            targetMuscle = filter.targetMuscle?.trim()?.takeIf { it.isNotBlank() },
            equipment = filter.equipment?.trim()?.takeIf { it.isNotBlank() },
            onlyFavorites = filter.onlyFavorites,
            limit = limit
        ).map { entities ->
            entities.map { it.toDomain(mediaUrlResolver) }
        }
    }

    override fun observeFeaturedExercises(limit: Int): Flow<List<Exercise>> {
        return exerciseDao.observeFeaturedExercises(limit = limit).map { entities ->
            entities.map { it.toDomain(mediaUrlResolver) }
        }
    }

    override fun observeExerciseById(exerciseId: String): Flow<Exercise?> {
        return exerciseDao.observeExerciseById(exerciseId).map { entity ->
            entity?.toDomain(mediaUrlResolver)
        }
    }

    override fun observeAvailableEquipment(bodyPart: BodyPartCategory?): Flow<List<String>> {
        return exerciseDao.observeDistinctEquipment(bodyPart?.rawKey)
    }

    override suspend fun toggleFavorite(exerciseId: String) {
        exerciseDao.toggleFavorite(exerciseId)
    }

    override suspend fun createCustomExercise(
        name: String,
        bodyPart: BodyPartCategory,
        equipment: String,
        targetMuscle: String,
        instructions: String,
        defaultSets: Int,
        defaultReps: String
    ): String {
        val customId = "custom_${UUID.randomUUID().toString().take(8)}"
        val steps = instructions
            .split("\n", ".")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .ifEmpty { listOf("Mantener control postural y tension mecanica constante durante todo el rango.") }

        val instructionsMap = mapOf("es" to instructions, "en" to instructions)
        val stepsMap = mapOf("es" to steps, "en" to steps)

        val entity = ExerciseEntity(
            id = customId,
            name = name,
            category = "strength",
            bodyPart = bodyPart.rawKey,
            equipment = equipment.lowercase(),
            targetMuscle = targetMuscle.lowercase(),
            muscleGroup = bodyPart.nameEs,
            secondaryMusclesJson = json.encodeToString(listOf("core", "stabilizers")),
            instructionsJson = json.encodeToString(instructionsMap),
            instructionStepsJson = json.encodeToString(stepsMap),
            imageRelativePath = "images/0025-c8bb588.jpg",
            gifRelativePath = "videos/0025-c8bb588.gif",
            defaultSets = defaultSets,
            defaultReps = defaultReps,
            defaultRestSeconds = 90,
            isFavorite = true,
            isCustom = true
        )
        exerciseDao.insertExercise(entity)
        return customId
    }
}
