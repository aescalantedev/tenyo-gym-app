package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `workout_sessions`.
 * Almacena las sesiones de entrenamiento activas y finalizadas.
 */
@Entity(
    tableName = "workout_sessions",
    indices = [
        Index(value = ["status"]),
        Index(value = ["started_at_epoch_millis"])
    ]
)
data class WorkoutSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "plan_id")
    val planId: String?,
    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: Int? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "started_at_epoch_millis")
    val startedAtEpochMillis: Long,
    @ColumnInfo(name = "ended_at_epoch_millis")
    val endedAtEpochMillis: Long? = null,
    @ColumnInfo(name = "elapsed_seconds")
    val elapsedSeconds: Long = 0L,
    @ColumnInfo(name = "total_volume_kg")
    val totalVolumeKg: Double = 0.0,
    @ColumnInfo(name = "estimated_calories_kcal")
    val estimatedCaloriesKcal: Int = 0,
    @ColumnInfo(name = "current_exercise_index")
    val currentExerciseIndex: Int = 0,
    @ColumnInfo(name = "note")
    val note: String? = null
)
