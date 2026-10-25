package com.aescalantedev.tenyo.core.domain.usecase

import com.aescalantedev.tenyo.core.domain.model.ArchetypeProtocol
import com.aescalantedev.tenyo.core.domain.model.PlanDifficulty
import com.aescalantedev.tenyo.core.domain.model.PlanGoal
import com.aescalantedev.tenyo.core.domain.model.ProgressSummary
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import com.aescalantedev.tenyo.core.domain.repository.ProgressRepository
import com.aescalantedev.tenyo.core.domain.repository.UserPreferencesRepository
import com.aescalantedev.tenyo.core.domain.repository.WorkoutPlanRepository
import com.aescalantedev.tenyo.core.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observa los planes de entrenamiento filtrados por objetivo (Stitch 07).
 */
class ObserveWorkoutPlansUseCase(
    private val workoutPlanRepository: WorkoutPlanRepository
) {
    operator fun invoke(goalFilter: PlanGoal = PlanGoal.ALL): Flow<List<WorkoutPlan>> =
        workoutPlanRepository.observeAllPlans(goalFilter)
}

/**
 * Observa el plan activo actual para la tarjeta "Tu entrenamiento de hoy" en Home (Stitch 02).
 */
class ObserveActiveWorkoutPlanUseCase(
    private val workoutPlanRepository: WorkoutPlanRepository
) {
    operator fun invoke(): Flow<WorkoutPlan?> =
        workoutPlanRepository.observeActivePlan()
}

/**
 * Activa un plan de entrenamiento y actualiza la preferencia del usuario.
 */
class SelectActiveWorkoutPlanUseCase(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(planId: String) {
        workoutPlanRepository.setActivePlan(planId)
        userPreferencesRepository.updateActivePlanId(planId)
    }
}

/**
 * Añade un ejercicio a un plan de entrenamiento existente ("Añadir a rutina" en Stitch 05).
 */
class AddExerciseToPlanUseCase(
    private val workoutPlanRepository: WorkoutPlanRepository
) {
    suspend operator fun invoke(
        planId: String,
        exerciseId: String,
        targetSets: Int = 4,
        targetReps: String = "8-12",
        restSeconds: Int = 90,
        suggestedWeightKg: Double = 20.0
    ) {
        workoutPlanRepository.addExerciseToPlan(
            planId = planId,
            exerciseId = exerciseId,
            targetSets = targetSets,
            targetReps = targetReps,
            restSeconds = restSeconds,
            suggestedWeightKg = suggestedWeightKg
        )
    }
}

/**
 * Crea un plan de entrenamiento personalizado (Stitch 07).
 */
class CreateCustomWorkoutPlanUseCase(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(
        title: String,
        subtitle: String,
        description: String,
        goal: PlanGoal,
        difficulty: PlanDifficulty,
        daysPerWeek: Int,
        durationWeeks: Int,
        estimatedMinutesPerSession: Int,
        initialExerciseIds: List<String> = emptyList(),
        makeActive: Boolean = true
    ): String {
        val planId = workoutPlanRepository.createCustomPlan(
            title = title,
            subtitle = subtitle,
            description = description,
            goal = goal,
            difficulty = difficulty,
            daysPerWeek = daysPerWeek,
            durationWeeks = durationWeeks,
            estimatedMinutesPerSession = estimatedMinutesPerSession,
            initialExerciseIds = initialExerciseIds
        )
        if (makeActive) {
            workoutPlanRepository.setActivePlan(planId)
            userPreferencesRepository.updateActivePlanId(planId)
        }
        return planId
    }
}

/**
 * Observa un plan específico por su ID con todo el detalle de sus días y ejercicios.
 */
class ObservePlanByIdUseCase(
    private val workoutPlanRepository: WorkoutPlanRepository
) {
    operator fun invoke(planId: String): Flow<WorkoutPlan?> =
        workoutPlanRepository.observePlanById(planId)
}

/**
 * Gestiona la edición de rutinas diarias, ejercicios y recordatorios de un plan de entrenamiento.
 */
class ManagePlanRoutinesUseCase(
    private val workoutPlanRepository: WorkoutPlanRepository
) {
    suspend fun addExerciseToDay(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int = 4,
        targetReps: String = "8-12",
        restSeconds: Int = 90,
        suggestedWeightKg: Double = 20.0
    ) {
        workoutPlanRepository.addExerciseToPlanDay(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetSets = targetSets,
            targetReps = targetReps,
            restSeconds = restSeconds,
            suggestedWeightKg = suggestedWeightKg
        )
    }

    suspend fun addExercisesToDay(
        planId: String,
        dayOfWeek: Int,
        exerciseIds: List<String>
    ) {
        workoutPlanRepository.addExercisesToPlanDay(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseIds = exerciseIds
        )
    }

    suspend fun removeExerciseFromDay(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String
    ) {
        workoutPlanRepository.removeExerciseFromPlanDay(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId
        )
    }

    suspend fun updateReminders(
        planId: String,
        hour: Int,
        minute: Int,
        enabled: Boolean
    ) {
        workoutPlanRepository.updatePlanReminders(
            planId = planId,
            reminderHour = hour,
            reminderMinute = minute,
            reminderEnabled = enabled
        )
    }

    suspend fun updateDay(
        dayId: String,
        name: String,
        isRestDay: Boolean
    ) {
        workoutPlanRepository.updatePlanDay(
            dayId = dayId,
            name = name,
            isRestDay = isRestDay
        )
    }

    suspend fun updateExerciseConfig(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    ) {
        workoutPlanRepository.updatePlanExerciseConfig(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetSets = targetSets,
            targetReps = targetReps,
            restSeconds = restSeconds,
            suggestedWeightKg = suggestedWeightKg
        )
    }

    suspend fun updatePlanTitle(
        planId: String,
        title: String
    ) {
        workoutPlanRepository.updatePlanTitle(planId, title)
    }

    suspend fun updateExerciseNote(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        note: String?
    ) {
        workoutPlanRepository.updatePlanExerciseNote(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            note = note
        )
    }

    suspend fun updateExerciseSets(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int
    ) {
        workoutPlanRepository.updatePlanExerciseSets(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetSets = targetSets
        )
    }

    suspend fun updateExerciseRestSeconds(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        restSeconds: Int
    ) {
        workoutPlanRepository.updatePlanExerciseRestSeconds(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            restSeconds = restSeconds
        )
    }

    suspend fun updateExerciseParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetReps: String,
        suggestedWeightKg: Double
    ) {
        workoutPlanRepository.updatePlanExerciseParameters(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetReps = targetReps,
            suggestedWeightKg = suggestedWeightKg
        )
    }

    suspend fun updateExerciseSetParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int,
        targetReps: String,
        suggestedWeightKg: Double
    ) {
        workoutPlanRepository.updatePlanExerciseSetParameters(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            setNumber = setNumber,
            targetReps = targetReps,
            suggestedWeightKg = suggestedWeightKg
        )
    }

    suspend fun deleteExerciseSet(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int
    ) {
        workoutPlanRepository.deletePlanExerciseSet(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            setNumber = setNumber
        )
    }

    suspend fun reorderExercises(
        planId: String,
        dayOfWeek: Int,
        orderedExerciseIds: List<String>
    ) {
        workoutPlanRepository.reorderPlanExercises(planId, dayOfWeek, orderedExerciseIds)
    }

    suspend fun deletePlan(planId: String) {
        workoutPlanRepository.deletePlan(planId)
    }
}

/**
 * Inicia o recupera una sesión de entrenamiento activa (Stitch 06).
 */
class StartWorkoutSessionUseCase(
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(
        planId: String? = null,
        singleExerciseId: String? = null,
        dayOfWeek: Int? = null
    ): String = workoutSessionRepository.startSession(
        planId = planId,
        singleExerciseId = singleExerciseId,
        dayOfWeek = dayOfWeek
    )
}

/**
 * Observa una sesión de entrenamiento por `sessionId` o la sesión activa en curso.
 */
class ObserveWorkoutSessionUseCase(
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    fun byId(sessionId: String): Flow<WorkoutSession?> =
        workoutSessionRepository.observeSessionById(sessionId)

    fun active(): Flow<WorkoutSession?> =
        workoutSessionRepository.observeActiveSession()

    fun completed(limit: Int = 5): Flow<List<WorkoutSession>> =
        workoutSessionRepository.observeCompletedSessions(limit)
}

/**
 * Registra o actualiza una serie (`WorkoutSetLog`) y el estado de la sesión.
 */
class ManageActiveSessionUseCase(
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    suspend fun updateSet(
        setLogId: Long,
        actualReps: Int,
        weightKg: Double,
        isCompleted: Boolean
    ) {
        workoutSessionRepository.updateSetLog(
            setLogId = setLogId,
            actualReps = actualReps.coerceAtLeast(1),
            weightKg = weightKg.coerceAtLeast(0.0),
            isCompleted = isCompleted
        )
    }

    suspend fun deleteSet(setLogId: Long) {
        workoutSessionRepository.deleteSet(setLogId)
    }

    suspend fun deleteExercise(sessionId: String, exerciseId: String) {
        workoutSessionRepository.deleteExercise(sessionId, exerciseId)
    }

    suspend fun addExerciseToSession(sessionId: String, exerciseId: String, setsCount: Int = 3) {
        workoutSessionRepository.addExerciseToSession(sessionId, exerciseId, setsCount)
    }

    suspend fun replaceExerciseInSession(sessionId: String, oldExerciseId: String, newExerciseId: String) {
        workoutSessionRepository.replaceExerciseInSession(sessionId, oldExerciseId, newExerciseId)
    }

    suspend fun updateExerciseRestSeconds(sessionId: String, exerciseId: String, restSeconds: Int) {
        workoutSessionRepository.updateExerciseRestSeconds(sessionId, exerciseId, restSeconds)
    }

    suspend fun updateExerciseNote(sessionId: String, exerciseId: String, note: String?) {
        workoutSessionRepository.updateExerciseNote(sessionId, exerciseId, note)
    }

    suspend fun addExtraSet(sessionId: String, exerciseId: String) {
        workoutSessionRepository.addExtraSet(sessionId, exerciseId)
    }

    suspend fun selectExerciseIndex(sessionId: String, index: Int) {
        workoutSessionRepository.updateCurrentExerciseIndex(sessionId, index.coerceAtLeast(0))
    }

    suspend fun togglePause(sessionId: String, elapsedSeconds: Long) {
        workoutSessionRepository.togglePauseSession(sessionId, elapsedSeconds)
    }

    suspend fun discardSession(sessionId: String? = null) {
        if (sessionId.isNullOrBlank()) {
            workoutSessionRepository.discardActiveSession()
        } else {
            workoutSessionRepository.discardSession(sessionId)
        }
    }

    suspend fun completeSession(
        sessionId: String,
        elapsedSeconds: Long,
        updatePlanValues: Boolean = false
    ) {
        workoutSessionRepository.completeSession(sessionId, elapsedSeconds, updatePlanValues)
    }
}

/**
 * Observa el resumen de evolución física, PRs y Rango Tenyo (Stitch 08).
 */
class ObserveProgressSummaryUseCase(
    private val progressRepository: ProgressRepository
) {
    operator fun invoke(): Flow<ProgressSummary> =
        progressRepository.observeProgressSummary()
}

/**
 * Gestiona las preferencias del usuario, el flujo de Onboarding completo (Stitch 01)
 * y la pantalla de Perfil / Ajustes de la aplicacion.
 */
class ManageUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val workoutPlanRepository: WorkoutPlanRepository
) {
    val preferences: Flow<UserPreferences> = userPreferencesRepository.userPreferences

    suspend fun completeOnboarding(profile: com.aescalantedev.tenyo.core.domain.model.OnboardingProfileData) {
        val cleanedProfile = profile.copy(
            userName = profile.userName.trim().ifBlank { "Atleta Tenyo" },
            ageYears = profile.ageYears.coerceIn(12, 95),
            weightKg = profile.weightKg.coerceIn(30.0, 250.0),
            heightCm = profile.heightCm.coerceIn(120, 230),
            trainingDaysPerWeek = profile.trainingDaysPerWeek.coerceIn(2, 7)
        )
        userPreferencesRepository.completeOnboarding(cleanedProfile)
        workoutPlanRepository.setActivePlan(cleanedProfile.archetypeProtocol.defaultPlanId)
    }

    suspend fun updateLanguage(languageCode: String) {
        userPreferencesRepository.updateLanguage(languageCode.trim().lowercase().ifBlank { "es" })
    }

    suspend fun updateThemeMode(themeMode: com.aescalantedev.tenyo.core.domain.model.AppThemeMode) {
        userPreferencesRepository.updateThemeMode(themeMode)
    }

    suspend fun updateWeightUnit(weightUnit: com.aescalantedev.tenyo.core.domain.model.WeightUnit) {
        userPreferencesRepository.updateWeightUnit(weightUnit)
    }
}

