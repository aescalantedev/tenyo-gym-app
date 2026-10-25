package com.aescalantedev.tenyo.core.domain.model

/**
 * Record personal (PR) histórico alcanzado en un ejercicio específico.
 */
data class ExerciseRecord(
    val exerciseId: String,
    val maxWeightKg: Double,
    val repsAtMaxWeight: Int,
    val estimated1RmKg: Double,
    val bestSetVolumeKg: Double,
    val achievedAtEpochMillis: Long
)

/**
 * Registro de una sesión de entrenamiento completada en la que se ejecutó el ejercicio.
 */
data class ExerciseHistoryEntry(
    val sessionId: String,
    val sessionTitle: String,
    val dateEpochMillis: Long,
    val sets: List<ExerciseHistorySet>,
    val totalVolumeKg: Double,
    val maxWeightKg: Double
)

/**
 * Serie individual completada dentro del historial del ejercicio.
 */
data class ExerciseHistorySet(
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val isPr: Boolean
)
