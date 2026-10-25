package com.aescalantedev.tenyo.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Objetivo principal de un plan de entrenamiento (usado en los chips de filtro de Stitch 07).
 */
@Serializable
enum class PlanGoal(val labelEs: String, val badgeColorKey: String) {
    ALL("Todos", "jade"),
    HEAVENLY_RESTRICTION("Restricción Celestial", "violet"),
    HYPERTROPHY("Ganar músculo", "jade"),
    STRENGTH("Fuerza máxima", "crimson"),
    CONDITIONING("Agilidad y Definición", "amber")
}

/**
 * Nivel de exigencia física del plan.
 */
@Serializable
enum class PlanDifficulty(val labelEs: String) {
    BEGINNER("Iniciado · Grado 3-4"),
    INTERMEDIATE("Intermedio · Grado 1-2"),
    ADVANCED("Avanzado · Grado Especial"),
    HEAVENLY_RESTRICTION("Restricción Celestial · Toji / Maki")
}

/**
 * Ejercicio asignado dentro de un [WorkoutPlan], con prescripción de series, repeticiones,
 * descanso y peso sugerido.
 */
data class WorkoutPlanExercise(
    val exercise: Exercise,
    val sortOrder: Int,
    val targetSets: Int,
    val targetReps: String,
    val restSeconds: Int,
    val suggestedWeightKg: Double = 0.0,
    val dayOfWeek: Int = 1,
    val note: String? = null,
    val setWeights: String? = null,
    val setReps: String? = null
) {
    val isDumbbell: Boolean
        get() = exercise.isDumbbell

    fun getWeightForSet(setNumber: Int): Double {
        val weights = setWeights?.split(",")?.mapNotNull { it.trim().toDoubleOrNull() }.orEmpty()
        return weights.getOrNull(setNumber - 1) ?: suggestedWeightKg
    }

    fun getRepsForSet(setNumber: Int): String {
        val repsList = setReps?.split(",")?.map { it.trim() }.orEmpty()
        val raw = repsList.getOrNull(setNumber - 1)?.ifBlank { targetReps } ?: targetReps
        val numbers = Regex("\\d+").findAll(raw).mapNotNull { it.value.toIntOrNull() }.toList()
        return (numbers.firstOrNull() ?: 10).toString()
    }
}

/**
 * Día estructurado dentro de un [WorkoutPlan] semanal (Lunes a Domingo o Día 1 a 7).
 */
data class WorkoutPlanDay(
    val id: String,
    val planId: String,
    val dayOfWeek: Int, // 1 = Lunes, 2 = Martes, ..., 7 = Domingo
    val name: String, // Ej: "Pecho y Tríceps", "Descanso Activo"
    val isRestDay: Boolean = false,
    val exercises: List<WorkoutPlanExercise> = emptyList()
) {
    val dayNameEs: String
        get() = when (dayOfWeek) {
            1 -> "Lunes"
            2 -> "Martes"
            3 -> "Miércoles"
            4 -> "Jueves"
            5 -> "Viernes"
            6 -> "Sábado"
            7 -> "Domingo"
            else -> "Día $dayOfWeek"
        }

    val dayShortNameEs: String
        get() = when (dayOfWeek) {
            1 -> "L"
            2 -> "M"
            3 -> "X"
            4 -> "J"
            5 -> "V"
            6 -> "S"
            7 -> "D"
            else -> "$dayOfWeek"
        }
}

/**
 * Plan de entrenamiento completo (predefinido del catálogo como Protocolo Toji / Protocolo Maki
 * o personalizado por el usuario) con distribución semanal y recordatorios.
 */
data class WorkoutPlan(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val goal: PlanGoal,
    val difficulty: PlanDifficulty,
    val daysPerWeek: Int,
    val durationWeeks: Int,
    val estimatedMinutesPerSession: Int,
    val isUserPlan: Boolean,
    val isActivePlan: Boolean = false,
    val completedSessionsCount: Int = 0,
    val totalPlannedSessions: Int = daysPerWeek * durationWeeks,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
    val reminderEnabled: Boolean = false,
    val days: List<WorkoutPlanDay> = emptyList(),
    val exercises: List<WorkoutPlanExercise> = days.flatMap { it.exercises }
) {
    val progressFraction: Float
        get() = if (totalPlannedSessions <= 0) 0f
        else (completedSessionsCount.toFloat() / totalPlannedSessions.toFloat()).coerceIn(0f, 1f)

    /**
     * Retorna la rutina configurada para hoy según el día de la semana actual (1 = Lunes .. 7 = Domingo).
     */
    fun getDayForDayOfWeek(dayOfWeek: Int): WorkoutPlanDay? {
        return days.firstOrNull { it.dayOfWeek == dayOfWeek }
    }
}
