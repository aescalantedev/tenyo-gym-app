package com.aescalantedev.tenyo.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Estado de una sesión de entrenamiento activa o finalizada.
 */
@Serializable
enum class SessionStatus {
    IN_PROGRESS,
    PAUSED,
    COMPLETED,
    ABANDONED
}

/**
 * Registro individual de una serie (`Set`) dentro de una sesión de entrenamiento.
 */
data class WorkoutSetLog(
    val id: Long = 0L,
    val sessionId: String,
    val exerciseId: String,
    val exerciseName: String = "",
    val bodyPart: BodyPartCategory = BodyPartCategory.CHEST,
    val setNumber: Int,
    val targetReps: Int = 10,
    val actualReps: Int,
    val weightKg: Double,
    val isCompleted: Boolean,
    val completedAtEpochMillis: Long? = null,
    val restSeconds: Int = 90,
    val note: String? = null,
    val previousSummary: String? = null,
    val imageUrl: String = "",
    val multiplier: Int = 1,
    val isPr: Boolean = false
) {
    val volumeKg: Double
        get() = if (isCompleted) actualReps * weightKg * multiplier else 0.0
}

/**
 * Sesión de entrenamiento completa (activa o histórica).
 * Refleja la tabla `workout_sessions` lista para sincronización futura con Supabase PostgreSQL.
 */
data class WorkoutSession(
    val id: String,
    val planId: String?,
    val dayOfWeek: Int? = null,
    val title: String,
    val status: SessionStatus,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long? = null,
    val elapsedSeconds: Long = 0L,
    val totalVolumeKg: Double = 0.0,
    val estimatedCaloriesKcal: Int = 0,
    val currentExerciseIndex: Int = 0,
    val setLogs: List<WorkoutSetLog> = emptyList(),
    val note: String? = null
) {
    val completedSetsCount: Int
        get() = setLogs.count { it.isCompleted }

    val totalSetsCount: Int
        get() = setLogs.size

    val completionFraction: Float
        get() = if (totalSetsCount == 0) 0f
        else (completedSetsCount.toFloat() / totalSetsCount.toFloat()).coerceIn(0f, 1f)
}
