package com.aescalantedev.tenyo.core.data.mapper

import com.aescalantedev.tenyo.core.data.local.entity.ExerciseEntity
import com.aescalantedev.tenyo.core.data.local.entity.PlanExerciseCrossRefEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSessionEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSetLogEntity
import com.aescalantedev.tenyo.core.data.local.model.ExerciseSeedDto
import com.aescalantedev.tenyo.core.data.media.MediaUrlResolver
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.PlanDifficulty
import com.aescalantedev.tenyo.core.domain.model.PlanGoal
import com.aescalantedev.tenyo.core.domain.model.SessionStatus
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlanExercise
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import com.aescalantedev.tenyo.core.domain.model.WorkoutSetLog
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Funciones de mapeo entre DTOs de semilla JSON, Entidades de Room (`:core:data`)
 * y Modelos de Dominio puros (`:core:domain`).
 */
object TenyoEntityMappers {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Convierte un registro del archivo `exercises.json` ([ExerciseSeedDto]) en una entidad [ExerciseEntity]
     * lista para insertarse en SQLite.
     */
    fun ExerciseSeedDto.toEntity(): ExerciseEntity {
        val defaultSets = when (bodyPart.lowercase()) {
            "cardio" -> 3
            "chest", "back", "upper legs" -> 4
            else -> 3
        }
        val defaultReps = when (bodyPart.lowercase()) {
            "cardio" -> "45 seg"
            "waist" -> "15-20"
            "chest", "back", "upper legs" -> "8-10"
            else -> "10-12"
        }
        val defaultRest = when (bodyPart.lowercase()) {
            "chest", "back", "upper legs" -> 90
            else -> 60
        }
        return ExerciseEntity(
            id = id,
            name = name,
            category = category,
            bodyPart = bodyPart.lowercase().trim(),
            equipment = equipment.lowercase().trim(),
            targetMuscle = target.lowercase().trim(),
            muscleGroup = muscleGroup.ifBlank { bodyPart },
            secondaryMusclesJson = json.encodeToString(secondaryMuscles),
            instructionsJson = json.encodeToString(instructions),
            instructionStepsJson = json.encodeToString(instructionSteps),
            imageRelativePath = image,
            gifRelativePath = gifUrl,
            defaultSets = defaultSets,
            defaultReps = defaultReps,
            defaultRestSeconds = defaultRest,
            isFavorite = id in INITIAL_FAVORITE_IDS,
            isCustom = false
        )
    }

    /**
     * Convierte una entidad [ExerciseEntity] de Room en el modelo de dominio [Exercise],
     * resolviendo las URLs de imagen miniatura y GIF mediante [MediaUrlResolver].
     */
    fun ExerciseEntity.toDomain(mediaUrlResolver: MediaUrlResolver): Exercise {
        val secondaryList = runCatching {
            json.decodeFromString<List<String>>(secondaryMusclesJson)
        }.getOrDefault(emptyList())

        val instructionsMap = runCatching {
            json.decodeFromString<Map<String, String>>(instructionsJson)
        }.getOrDefault(emptyMap())

        val stepsMap = runCatching {
            json.decodeFromString<Map<String, List<String>>>(instructionStepsJson)
        }.getOrDefault(emptyMap())

        return Exercise(
            id = id,
            name = name,
            category = category,
            bodyPart = BodyPartCategory.fromRawKey(bodyPart),
            equipment = equipment,
            targetMuscle = targetMuscle,
            muscleGroup = muscleGroup,
            secondaryMuscles = secondaryList,
            instructionsByLang = instructionsMap,
            instructionStepsByLang = stepsMap,
            imageRelativePath = imageRelativePath,
            gifRelativePath = gifRelativePath,
            imageUrl = mediaUrlResolver.resolve(imageRelativePath),
            gifUrl = mediaUrlResolver.resolve(gifRelativePath),
            defaultSets = defaultSets,
            defaultReps = defaultReps,
            defaultRestSeconds = defaultRestSeconds,
            isFavorite = isFavorite,
            isCustom = isCustom
        )
    }

    /**
     * Convierte una entidad [WorkoutPlanEntity] junto con sus referencias de ejercicios
     * y días estructurados en el modelo de dominio [WorkoutPlan].
     */
    fun WorkoutPlanEntity.toDomain(
        crossRefs: List<PlanExerciseCrossRefEntity>,
        exercisesById: Map<String, Exercise>,
        daysEntities: List<com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanDayEntity> = emptyList()
    ): WorkoutPlan {
        val planExercises = crossRefs
            .sortedBy { it.sortOrder }
            .mapNotNull { ref ->
                val domainExercise = exercisesById[ref.exerciseId] ?: return@mapNotNull null
                WorkoutPlanExercise(
                    exercise = domainExercise,
                    sortOrder = ref.sortOrder,
                    targetSets = ref.targetSets,
                    targetReps = ref.targetReps,
                    restSeconds = ref.restSeconds,
                    suggestedWeightKg = ref.suggestedWeightKg,
                    dayOfWeek = ref.dayOfWeek,
                    note = ref.note,
                    setWeights = ref.setWeights,
                    setReps = ref.setReps
                )
            }

        val domainDays = if (daysEntities.isNotEmpty()) {
            daysEntities.sortedBy { it.dayOfWeek }.map { dayEntity ->
                val dayExercises = planExercises.filter { it.dayOfWeek == dayEntity.dayOfWeek }
                com.aescalantedev.tenyo.core.domain.model.WorkoutPlanDay(
                    id = dayEntity.id,
                    planId = dayEntity.planId,
                    dayOfWeek = dayEntity.dayOfWeek,
                    name = dayEntity.name,
                    isRestDay = dayEntity.isRestDay,
                    exercises = dayExercises
                )
            }
        } else {
            (1..7).map { dayIndex ->
                val dayExercises = planExercises.filter { it.dayOfWeek == dayIndex }
                val isRest = dayIndex > daysPerWeek && dayExercises.isEmpty()
                com.aescalantedev.tenyo.core.domain.model.WorkoutPlanDay(
                    id = "${id}_day_$dayIndex",
                    planId = id,
                    dayOfWeek = dayIndex,
                    name = if (isRest) "Descanso" else "Rutina Día $dayIndex",
                    isRestDay = isRest,
                    exercises = dayExercises
                )
            }
        }

        val parsedGoal = PlanGoal.entries.firstOrNull { it.name == goal }
            ?: PlanGoal.HEAVENLY_RESTRICTION
        val parsedDifficulty = PlanDifficulty.entries.firstOrNull { it.name == difficulty }
            ?: PlanDifficulty.HEAVENLY_RESTRICTION

        return WorkoutPlan(
            id = id,
            title = title,
            subtitle = subtitle,
            description = description,
            goal = parsedGoal,
            difficulty = parsedDifficulty,
            daysPerWeek = daysPerWeek,
            durationWeeks = durationWeeks,
            estimatedMinutesPerSession = estimatedMinutesPerSession,
            isUserPlan = isUserPlan,
            isActivePlan = isActivePlan,
            completedSessionsCount = completedSessionsCount,
            totalPlannedSessions = (daysPerWeek * durationWeeks).coerceAtLeast(1),
            reminderHour = reminderHour,
            reminderMinute = reminderMinute,
            reminderEnabled = reminderEnabled,
            days = domainDays,
            exercises = planExercises
        )
    }

    /**
     * Convierte una entidad [WorkoutSetLogEntity] en el modelo de dominio [WorkoutSetLog].
     */
    fun WorkoutSetLogEntity.toDomain(): WorkoutSetLog =
        WorkoutSetLog(
            id = id,
            sessionId = sessionId,
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            bodyPart = BodyPartCategory.fromRawKey(bodyPart),
            setNumber = setNumber,
            targetReps = targetReps,
            actualReps = actualReps,
            weightKg = weightKg,
            isCompleted = isCompleted,
            completedAtEpochMillis = completedAtEpochMillis,
            restSeconds = restSeconds,
            note = note,
            previousSummary = previousSummary,
            imageUrl = imageUrl,
            multiplier = multiplier,
            isPr = isPr
        )

    /**
     * Convierte una entidad [WorkoutSessionEntity] y sus series registradas en el modelo de dominio [WorkoutSession].
     */
    fun WorkoutSessionEntity.toDomain(setLogs: List<WorkoutSetLogEntity>): WorkoutSession {
        val parsedStatus = SessionStatus.entries.firstOrNull { it.name == status }
            ?: SessionStatus.IN_PROGRESS
        return WorkoutSession(
            id = id,
            planId = planId,
            dayOfWeek = dayOfWeek,
            title = title,
            status = parsedStatus,
            startedAtEpochMillis = startedAtEpochMillis,
            endedAtEpochMillis = endedAtEpochMillis,
            elapsedSeconds = elapsedSeconds,
            totalVolumeKg = totalVolumeKg,
            estimatedCaloriesKcal = estimatedCaloriesKcal,
            currentExerciseIndex = currentExerciseIndex,
            setLogs = setLogs.map { it.toDomain() },
            note = note
        )
    }

    private val INITIAL_FAVORITE_IDS = setOf("0025", "0032", "0043", "0027")
}
