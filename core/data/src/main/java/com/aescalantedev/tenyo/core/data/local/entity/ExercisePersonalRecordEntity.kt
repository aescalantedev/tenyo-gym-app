package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `exercise_personal_records`.
 * Almacena los records personales (PRs) historicos alcanzados por cada ejercicio:
 * - Carga maxima (kg) y reps a esa carga.
 * - 1RM estimado maximo (Formula de Epley).
 * - Mejor volumen en una sola serie (kg x reps).
 */
@Entity(
    tableName = "exercise_personal_records",
    indices = [
        Index(value = ["exercise_id"], unique = true)
    ]
)
data class ExercisePersonalRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "max_weight_kg")
    val maxWeightKg: Double,
    @ColumnInfo(name = "reps_at_max_weight")
    val repsAtMaxWeight: Int,
    @ColumnInfo(name = "estimated_1rm_kg")
    val estimated1RmKg: Double,
    @ColumnInfo(name = "best_set_volume_kg")
    val bestSetVolumeKg: Double,
    @ColumnInfo(name = "achieved_at_epoch_millis")
    val achievedAtEpochMillis: Long
)
