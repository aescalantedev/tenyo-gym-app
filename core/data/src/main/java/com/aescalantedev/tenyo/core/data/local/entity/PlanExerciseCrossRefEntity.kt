package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

/**
 * Tabla intermedia relacional `workout_plan_exercises` que vincula un plan ([WorkoutPlanEntity])
 * con los ejercicios ([ExerciseEntity]) que lo componen, asociados a un día de la semana específico.
 */
@Entity(
    tableName = "workout_plan_exercises",
    primaryKeys = ["plan_id", "day_of_week", "exercise_id"],
    indices = [
        Index(value = ["plan_id"]),
        Index(value = ["exercise_id"]),
        Index(value = ["plan_id", "day_of_week"])
    ]
)
data class PlanExerciseCrossRefEntity(
    @ColumnInfo(name = "plan_id")
    val planId: String,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int,
    @ColumnInfo(name = "target_sets")
    val targetSets: Int,
    @ColumnInfo(name = "target_reps")
    val targetReps: String,
    @ColumnInfo(name = "rest_seconds")
    val restSeconds: Int,
    @ColumnInfo(name = "suggested_weight_kg")
    val suggestedWeightKg: Double,
    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: Int = 1,
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "set_weights")
    val setWeights: String? = null,
    @ColumnInfo(name = "set_reps")
    val setReps: String? = null
)
