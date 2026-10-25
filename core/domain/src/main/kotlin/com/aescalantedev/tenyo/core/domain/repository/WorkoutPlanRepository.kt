package com.aescalantedev.tenyo.core.domain.repository

import com.aescalantedev.tenyo.core.domain.model.PlanDifficulty
import com.aescalantedev.tenyo.core.domain.model.PlanGoal
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para los Planes de Entrenamiento (Stitch 02 y Stitch 07).
 */
interface WorkoutPlanRepository {
    /**
     * Observa todos los planes de entrenamiento (tanto planes del usuario como del catálogo).
     */
    fun observeAllPlans(goalFilter: PlanGoal = PlanGoal.ALL): Flow<List<WorkoutPlan>>

    /**
     * Observa el plan activo seleccionado actualmente por el usuario para el Dashboard de Inicio.
     */
    fun observeActivePlan(): Flow<WorkoutPlan?>

    /**
     * Observa un plan específico con la lista completa de sus ejercicios asociados y distribución semanal.
     */
    fun observePlanById(planId: String): Flow<WorkoutPlan?>

    /**
     * Establece un plan como el plan activo del usuario y lo vincula a "Mis planes".
     */
    suspend fun setActivePlan(planId: String)

    /**
     * Añade un ejercicio existente a un plan de entrenamiento.
     */
    suspend fun addExerciseToPlan(
        planId: String,
        exerciseId: String,
        targetSets: Int = 4,
        targetReps: String = "8-12",
        restSeconds: Int = 90,
        suggestedWeightKg: Double = 20.0
    )

    /**
     * Añade un ejercicio específico a un día de la semana concreto dentro de un plan.
     */
    suspend fun addExerciseToPlanDay(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int = 4,
        targetReps: String = "8-12",
        restSeconds: Int = 90,
        suggestedWeightKg: Double = 20.0
    )

    /**
     * Añade múltiples ejercicios a un día específico del plan.
     */
    suspend fun addExercisesToPlanDay(
        planId: String,
        dayOfWeek: Int,
        exerciseIds: List<String>
    )

    /**
     * Elimina un ejercicio de un día específico del plan.
     */
    suspend fun removeExerciseFromPlanDay(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String
    )

    /**
     * Actualiza la configuración de recordatorios para un plan.
     */
    suspend fun updatePlanReminders(
        planId: String,
        reminderHour: Int,
        reminderMinute: Int,
        reminderEnabled: Boolean
    )

    /**
     * Actualiza el nombre o estado de descanso de un día del plan.
     */
    suspend fun updatePlanDay(
        dayId: String,
        name: String,
        isRestDay: Boolean
    )

    /**
     * Actualiza las series, repeticiones, descanso y peso de un ejercicio dentro de un día del plan.
     */
    suspend fun updatePlanExerciseConfig(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    )

    /**
     * Actualiza la nota personalizada asociada a un ejercicio de un día del plan.
     */
    suspend fun updatePlanExerciseNote(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        note: String?
    )

    /**
     * Actualiza el total de series de un ejercicio del plan (+ / - serie).
     */
    suspend fun updatePlanExerciseSets(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int
    )

    /**
     * Actualiza el temporizador de descanso entre series para un ejercicio del plan.
     */
    suspend fun updatePlanExerciseRestSeconds(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        restSeconds: Int
    )

    /**
     * Actualiza repeticiones y peso sugerido de un ejercicio del plan a nivel global.
     */
    suspend fun updatePlanExerciseParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetReps: String,
        suggestedWeightKg: Double
    )

    /**
     * Actualiza repeticiones y peso de una serie individual específica dentro de un ejercicio del plan.
     */
    suspend fun updatePlanExerciseSetParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int,
        targetReps: String,
        suggestedWeightKg: Double
    )

    /**
     * Elimina una serie individual específica (por deslizamiento) dentro de un ejercicio del plan.
     */
    suspend fun deletePlanExerciseSet(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int
    )

    /**
     * Reordena los ejercicios asignados a un día específico del plan según el orden de IDs proporcionado.
     */
    suspend fun reorderPlanExercises(
        planId: String,
        dayOfWeek: Int,
        orderedExerciseIds: List<String>
    )

    /**
     * Actualiza el título del plan.
     */
    suspend fun updatePlanTitle(
        planId: String,
        title: String
    )

    /**
     * Crea un nuevo plan de entrenamiento personalizado por el usuario (Stitch 07).
     */
    suspend fun createCustomPlan(
        title: String,
        subtitle: String,
        description: String,
        goal: PlanGoal,
        difficulty: PlanDifficulty,
        daysPerWeek: Int,
        durationWeeks: Int,
        estimatedMinutesPerSession: Int,
        initialExerciseIds: List<String> = emptyList()
    ): String

    /**
     * Elimina completamente un plan de entrenamiento con todos sus días y ejercicios programados.
     */
    suspend fun deletePlan(planId: String)
}
