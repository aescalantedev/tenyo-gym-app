package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `workout_set_logs`.
 * Almacena cada serie individual registrada dentro de una sesion de entrenamiento.
 */
@Entity(
    tableName = "workout_set_logs",
    indices = [
        Index(value = ["session_id"]),
        Index(value = ["exercise_id"]),
        Index(value = ["is_completed"])
    ]
)
data class WorkoutSetLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "exercise_name")
    val exerciseName: String,
    @ColumnInfo(name = "body_part")
    val bodyPart: String,
    @ColumnInfo(name = "set_number")
    val setNumber: Int,
    @ColumnInfo(name = "target_reps")
    val targetReps: Int,
    @ColumnInfo(name = "actual_reps")
    val actualReps: Int,
    @ColumnInfo(name = "weight_kg")
    val weightKg: Double,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "completed_at_epoch_millis")
    val completedAtEpochMillis: Long? = null,
    @ColumnInfo(name = "rest_seconds")
    val restSeconds: Int = 90,
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "previous_summary")
    val previousSummary: String? = null,
    @ColumnInfo(name = "image_url")
    val imageUrl: String = "",
    @ColumnInfo(name = "multiplier")
    val multiplier: Int = 1,
    @ColumnInfo(name = "is_pr")
    val isPr: Boolean = false
)
