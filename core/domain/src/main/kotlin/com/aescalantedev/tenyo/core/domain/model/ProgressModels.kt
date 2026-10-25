package com.aescalantedev.tenyo.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Sistema de Evolución Física "Tenyo" inspirado en Jujutsu Kaisen:
 * El usuario evoluciona desde Grado 4 hasta alcanzar la Restricción Celestial completa
 * (Cero Energía Maldita — Cumbre Física de Toji Fushiguro y Maki Zenin) según sus sesiones
 * completadas y volumen total levantado.
 */
@Serializable
enum class PhysicalRank(
    val titleEs: String,
    val kanjiBadge: String,
    val subtitleEs: String,
    val minWorkoutsRequired: Int
) {
    GRADE_4(
        titleEs = "Nivel 1 · Iniciación",
        kanjiBadge = "Nivel 1",
        subtitleEs = "Primeros pasos en tu disciplina física.",
        minWorkoutsRequired = 0
    ),
    GRADE_3(
        titleEs = "Nivel 2 · Constancia",
        kanjiBadge = "Nivel 2",
        subtitleEs = "Construyendo el hábito y la resistencia muscular.",
        minWorkoutsRequired = 4
    ),
    GRADE_2(
        titleEs = "Nivel 3 · Fuerza",
        kanjiBadge = "Nivel 3",
        subtitleEs = "Incremento notable de fuerza y potencia corporal.",
        minWorkoutsRequired = 10
    ),
    GRADE_1(
        titleEs = "Nivel 4 · Avanzado",
        kanjiBadge = "Nivel 4",
        subtitleEs = "Dominio de cargas y técnica sólida en cada sesión.",
        minWorkoutsRequired = 20
    ),
    SPECIAL_GRADE(
        titleEs = "Nivel 5 · Élite",
        kanjiBadge = "Nivel 5",
        subtitleEs = "Rendimiento y disciplina en el más alto nivel.",
        minWorkoutsRequired = 35
    ),
    HEAVENLY_RESTRICTION(
        titleEs = "Nivel Máximo · Cumbre Física",
        kanjiBadge = "Cumbre",
        subtitleEs = "Fuerza y acondicionamiento físico extraordinario.",
        minWorkoutsRequired = 50
    );

    companion object {
        fun fromWorkoutsCount(completedWorkouts: Int): PhysicalRank =
            entries.lastOrNull { completedWorkouts >= it.minWorkoutsRequired } ?: GRADE_4

        fun nextRank(current: PhysicalRank): PhysicalRank? {
            val nextIndex = current.ordinal + 1
            return entries.getOrNull(nextIndex)
        }
    }
}

/**
 * Actividad de un día de la semana (Lunes a Domingo) para el gráfico de barras de Stitch 08.
 */
data class WeeklyActivityDay(
    val dayLabel: String, // "L", "M", "X", "J", "V", "S", "D"
    val durationMinutes: Int,
    val volumeKg: Double,
    val isToday: Boolean = false
)

/**
 * Porcentaje de distribución de trabajo por grupo muscular (Stitch 08).
 */
data class MuscleDistributionItem(
    val category: BodyPartCategory,
    val completedSets: Int,
    val percentage: Int
)

/**
 * Récord Personal (PR) histórico alcanzado en un ejercicio específico (Stitch 08).
 */
data class PersonalRecord(
    val exerciseId: String,
    val exerciseName: String,
    val bodyPart: BodyPartCategory,
    val maxWeightKg: Double,
    val repsAtMaxWeight: Int,
    val previousWeightKg: Double,
    val achievedAtEpochMillis: Long
) {
    val improvementKg: Double
        get() = (maxWeightKg - previousWeightKg).coerceAtLeast(0.0)
}

/**
 * Estado y porcentaje de recuperación muscular (0% - 100%) según el tiempo transcurrido
 * desde el último estímulo de entrenamiento.
 */
data class MuscleRecoveryItem(
    val category: BodyPartCategory,
    val muscleNameEs: String,
    val recoveryPercentage: Int, // 0..100%
    val statusText: String, // "100% Recuperado", "En recuperación", "Fatiga alta"
    val hoursSinceLastTrained: Long?, // null si no ha sido entrenado
    val lastTrainedDateFormatted: String?,
    val setsTrainedLast48h: Int,
    val tojiMuscleOptionId: String,
    val drawableAssetName: String // e.g. "cat_muscle_chest"
)

/**
 * Registro de entrenamientos en un día específico del calendario.
 */
data class WorkoutDayMarker(
    val epochDay: Long, // LocalDate.toEpochDay()
    val workoutsCount: Int,
    val totalVolumeKg: Double,
    val durationMinutes: Int,
    val sessionTitles: List<String>
)

/**
 * Resumen integral de progreso y evolución física del usuario (Stitch 08 + Home).
 */
data class ProgressSummary(
    val completedWorkoutsCount: Int,
    val totalDurationMinutes: Int,
    val totalCaloriesBurned: Int,
    val totalVolumeKg: Double,
    val activeStreakWeeks: Int,
    val currentRank: PhysicalRank,
    val nextRank: PhysicalRank?,
    val workoutsToNextRank: Int,
    val rankProgressFraction: Float,
    val weeklyActivity: List<WeeklyActivityDay>,
    val muscleDistribution: List<MuscleDistributionItem>,
    val muscleRecoveries: List<MuscleRecoveryItem> = emptyList(),
    val workoutDaysMap: Map<Long, WorkoutDayMarker> = emptyMap(),
    val personalRecords: List<PersonalRecord> = emptyList(),
    val workedMuscleOptionIds: Set<String> = emptySet()
)
