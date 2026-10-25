package com.aescalantedev.tenyo.core.data.repository

import com.aescalantedev.tenyo.core.data.local.dao.ExerciseDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutPlanDao
import com.aescalantedev.tenyo.core.data.local.entity.PlanExerciseCrossRefEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanDayEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanEntity
import com.aescalantedev.tenyo.core.data.mapper.TenyoEntityMappers.toDomain
import com.aescalantedev.tenyo.core.data.media.MediaUrlResolver
import com.aescalantedev.tenyo.core.domain.model.PlanDifficulty
import com.aescalantedev.tenyo.core.domain.model.PlanGoal
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.core.domain.repository.WorkoutPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

/**
 * Implementación Offline-First de [WorkoutPlanRepository].
 *
 * Combina de forma reactiva los planes de entrenamiento almacenados en `workout_plans` con
 * sus días estructurados (`workout_plan_days`), su tabla relacional `workout_plan_exercises`
 * y los ejercicios correspondientes de `exercises`.
 */
class OfflineFirstWorkoutPlanRepository(
    private val workoutPlanDao: WorkoutPlanDao,
    private val exerciseDao: ExerciseDao,
    private val mediaUrlResolver: MediaUrlResolver
) : WorkoutPlanRepository {

    override fun observeAllPlans(goalFilter: PlanGoal): Flow<List<WorkoutPlan>> {
        return combine(
            workoutPlanDao.observeAllPlans(),
            workoutPlanDao.observeAllPlanExerciseRefs(),
            workoutPlanDao.observeAllPlanDays()
        ) { planEntities, allRefs, allDays ->
            val filteredPlans = if (goalFilter == PlanGoal.ALL) {
                planEntities
            } else {
                planEntities.filter { it.goal == goalFilter.name }
            }

            val neededExerciseIds = allRefs.map { it.exerciseId }.distinct()
            val exercisesById = if (neededExerciseIds.isEmpty()) {
                emptyMap()
            } else {
                exerciseDao.getExercisesByIds(neededExerciseIds)
                    .associate { it.id to it.toDomain(mediaUrlResolver) }
            }

            val refsByPlanId = allRefs.groupBy { it.planId }
            val daysByPlanId = allDays.groupBy { it.planId }

            filteredPlans.map { planEntity ->
                planEntity.toDomain(
                    crossRefs = refsByPlanId[planEntity.id].orEmpty(),
                    exercisesById = exercisesById,
                    daysEntities = daysByPlanId[planEntity.id].orEmpty()
                )
            }
        }
    }

    override fun observeActivePlan(): Flow<WorkoutPlan?> {
        return combine(
            workoutPlanDao.observeActivePlan(),
            workoutPlanDao.observeAllPlanExerciseRefs(),
            workoutPlanDao.observeAllPlanDays()
        ) { activePlanEntity, allRefs, allDays ->
            val entity = activePlanEntity ?: return@combine null
            val planRefs = allRefs.filter { it.planId == entity.id }
            val planDays = allDays.filter { it.planId == entity.id }
            val exerciseIds = planRefs.map { it.exerciseId }.distinct()
            val exercisesById = if (exerciseIds.isEmpty()) {
                emptyMap()
            } else {
                exerciseDao.getExercisesByIds(exerciseIds)
                    .associate { it.id to it.toDomain(mediaUrlResolver) }
            }
            entity.toDomain(
                crossRefs = planRefs,
                exercisesById = exercisesById,
                daysEntities = planDays
            )
        }
    }

    override fun observePlanById(planId: String): Flow<WorkoutPlan?> {
        return combine(
            workoutPlanDao.observePlanById(planId),
            workoutPlanDao.observePlanExerciseRefs(planId),
            workoutPlanDao.observePlanDays(planId)
        ) { planEntity, planRefs, planDays ->
            val entity = planEntity ?: return@combine null
            val exerciseIds = planRefs.map { it.exerciseId }.distinct()
            val exercisesById = if (exerciseIds.isEmpty()) {
                emptyMap()
            } else {
                exerciseDao.getExercisesByIds(exerciseIds)
                    .associate { it.id to it.toDomain(mediaUrlResolver) }
            }
            entity.toDomain(
                crossRefs = planRefs,
                exercisesById = exercisesById,
                daysEntities = planDays
            )
        }
    }

    override suspend fun setActivePlan(planId: String) {
        workoutPlanDao.setActivePlanTransaction(planId)
    }

    override suspend fun addExerciseToPlan(
        planId: String,
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    ) {
        addExerciseToPlanDay(
            planId = planId,
            dayOfWeek = 1,
            exerciseId = exerciseId,
            targetSets = targetSets,
            targetReps = targetReps,
            restSeconds = restSeconds,
            suggestedWeightKg = suggestedWeightKg
        )
    }

    override suspend fun addExerciseToPlanDay(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    ) {
        val currentRefs = workoutPlanDao.getPlanExerciseRefs(planId)
            .filter { it.dayOfWeek == dayOfWeek }
        val nextSortOrder = (currentRefs.maxOfOrNull { it.sortOrder } ?: 0) + 1
        workoutPlanDao.insertPlanExerciseCrossRef(
            PlanExerciseCrossRefEntity(
                planId = planId,
                exerciseId = exerciseId,
                sortOrder = nextSortOrder,
                targetSets = targetSets,
                targetReps = targetReps,
                restSeconds = restSeconds,
                suggestedWeightKg = suggestedWeightKg,
                dayOfWeek = dayOfWeek
            )
        )
    }

    override suspend fun addExercisesToPlanDay(
        planId: String,
        dayOfWeek: Int,
        exerciseIds: List<String>
    ) {
        val currentRefs = workoutPlanDao.getPlanExerciseRefs(planId)
            .filter { it.dayOfWeek == dayOfWeek }
        val startSortOrder = (currentRefs.maxOfOrNull { it.sortOrder } ?: 0) + 1
        val newRefs = exerciseIds.mapIndexed { index, exId ->
            PlanExerciseCrossRefEntity(
                planId = planId,
                exerciseId = exId,
                sortOrder = startSortOrder + index,
                targetSets = 4,
                targetReps = "8-12",
                restSeconds = 90,
                suggestedWeightKg = 20.0,
                dayOfWeek = dayOfWeek
            )
        }
        workoutPlanDao.insertPlanExerciseCrossRefs(newRefs)
    }

    override suspend fun removeExerciseFromPlanDay(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String
    ) {
        workoutPlanDao.deletePlanExerciseCrossRef(planId, dayOfWeek, exerciseId)
    }

    override suspend fun updatePlanReminders(
        planId: String,
        reminderHour: Int,
        reminderMinute: Int,
        reminderEnabled: Boolean
    ) {
        workoutPlanDao.updatePlanReminders(planId, reminderHour, reminderMinute, reminderEnabled)
    }

    override suspend fun updatePlanDay(
        dayId: String,
        name: String,
        isRestDay: Boolean
    ) {
        workoutPlanDao.updatePlanDay(dayId, name, isRestDay)
    }

    override suspend fun updatePlanExerciseConfig(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    ) {
        workoutPlanDao.updatePlanExerciseConfig(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetSets = targetSets,
            targetReps = targetReps,
            restSeconds = restSeconds,
            suggestedWeightKg = suggestedWeightKg
        )
    }

    override suspend fun updatePlanExerciseNote(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        note: String?
    ) {
        workoutPlanDao.updatePlanExerciseNote(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            note = note?.trim()?.ifBlank { null }
        )
    }

    override suspend fun updatePlanExerciseSets(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int
    ) {
        val safeSets = targetSets.coerceIn(1, 20)
        workoutPlanDao.updatePlanExerciseSets(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetSets = safeSets
        )
    }

    override suspend fun updatePlanExerciseRestSeconds(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        restSeconds: Int
    ) {
        val safeRest = restSeconds.coerceIn(0, 600)
        workoutPlanDao.updatePlanExerciseRestSeconds(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            restSeconds = safeRest
        )
    }

    override suspend fun updatePlanExerciseParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetReps: String,
        suggestedWeightKg: Double
    ) {
        workoutPlanDao.updatePlanExerciseParameters(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetReps = targetReps.trim(),
            suggestedWeightKg = suggestedWeightKg.coerceAtLeast(0.0)
        )
    }

    override suspend fun updatePlanExerciseSetParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int,
        targetReps: String,
        suggestedWeightKg: Double
    ) {
        workoutPlanDao.updateIndividualSetParameters(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            setNumber = setNumber,
            newReps = targetReps.trim(),
            newWeightKg = suggestedWeightKg.coerceAtLeast(0.0)
        )
    }

    override suspend fun deletePlanExerciseSet(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int
    ) {
        workoutPlanDao.deleteIndividualPlanSet(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            setNumber = setNumber
        )
    }

    override suspend fun reorderPlanExercises(
        planId: String,
        dayOfWeek: Int,
        orderedExerciseIds: List<String>
    ) {
        workoutPlanDao.reorderPlanExercises(planId, dayOfWeek, orderedExerciseIds)
    }

    override suspend fun updatePlanTitle(
        planId: String,
        title: String
    ) {
        workoutPlanDao.updatePlanTitle(planId, title.trim())
    }

    override suspend fun createCustomPlan(
        title: String,
        subtitle: String,
        description: String,
        goal: PlanGoal,
        difficulty: PlanDifficulty,
        daysPerWeek: Int,
        durationWeeks: Int,
        estimatedMinutesPerSession: Int,
        initialExerciseIds: List<String>
    ): String {
        val planId = "plan_custom_${UUID.randomUUID().toString().take(8)}"
        val safeDaysPerWeek = daysPerWeek.coerceIn(1, 7)
        val planEntity = WorkoutPlanEntity(
            id = planId,
            title = title.trim().ifBlank { "Mi Rutina Semanal" },
            subtitle = subtitle.trim().ifBlank { "$safeDaysPerWeek días/sem · ${difficulty.labelEs}" },
            description = description.trim().ifBlank { "Plan personalizado estructurado para evolución continua." },
            goal = if (goal == PlanGoal.ALL) PlanGoal.HEAVENLY_RESTRICTION.name else goal.name,
            difficulty = difficulty.name,
            daysPerWeek = safeDaysPerWeek,
            durationWeeks = durationWeeks.coerceAtLeast(1),
            estimatedMinutesPerSession = estimatedMinutesPerSession.coerceAtLeast(15),
            isUserPlan = true,
            isActivePlan = false,
            completedSessionsCount = 0,
            reminderHour = 18,
            reminderMinute = 0,
            reminderEnabled = false
        )
        workoutPlanDao.insertPlan(planEntity)

        val defaultDayNames = listOf(
            "Pecho y Tríceps",
            "Espalda y Bíceps",
            "Pierna Completa",
            "Hombros y Core",
            "Torso / Enfoque Fuerza",
            "Acondicionamiento",
            "Recuperación y Movilidad"
        )
        val daysList = (1..7).map { dayIndex ->
            val isRest = dayIndex > safeDaysPerWeek
            WorkoutPlanDayEntity(
                id = "${planId}_day_$dayIndex",
                planId = planId,
                dayOfWeek = dayIndex,
                name = if (isRest) "Descanso" else defaultDayNames[(dayIndex - 1) % defaultDayNames.size],
                isRestDay = isRest
            )
        }
        workoutPlanDao.insertPlanDays(daysList)

        // Los nuevos planes personalizados inician vacíos a menos que se indiquen ejercicios explícitos
        if (initialExerciseIds.isNotEmpty()) {
            val crossRefs = initialExerciseIds.mapIndexed { index, exId ->
                PlanExerciseCrossRefEntity(
                    planId = planId,
                    exerciseId = exId,
                    sortOrder = index + 1,
                    targetSets = 4,
                    targetReps = "8-12",
                    restSeconds = 90,
                    suggestedWeightKg = 20.0,
                    dayOfWeek = 1
                )
            }
            workoutPlanDao.insertPlanExerciseCrossRefs(crossRefs)
        }
        return planId
    }

    override suspend fun deletePlan(planId: String) {
        val plan = workoutPlanDao.getPlanById(planId)
        workoutPlanDao.deleteCompletePlan(planId)
        if (plan?.isActivePlan == true) {
            val remaining = workoutPlanDao.getAllPlans()
            val nextActive = remaining.firstOrNull()
            if (nextActive != null) {
                workoutPlanDao.setActivePlanTransaction(nextActive.id)
            }
        }
    }
}
