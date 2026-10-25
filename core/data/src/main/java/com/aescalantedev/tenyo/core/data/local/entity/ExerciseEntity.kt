package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `exercises`.
 *
 * Su estructura relacional refleja la tabla `exercises` en Supabase (PostgreSQL).
 * Los mapas multilenguaje (`instructions_json`, `instruction_steps_json`) y la lista de musculos
 * secundarios (`secondary_muscles_json`) se almacenan serializados en formato JSON compatible
 * con columnas `JSONB` de PostgreSQL.
 */
@Entity(
    tableName = "exercises",
    indices = [
        Index(value = ["body_part"]),
        Index(value = ["equipment"]),
        Index(value = ["is_favorite"]),
        Index(value = ["name"])
    ]
)
data class ExerciseEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "body_part")
    val bodyPart: String,
    @ColumnInfo(name = "equipment")
    val equipment: String,
    @ColumnInfo(name = "target_muscle")
    val targetMuscle: String,
    @ColumnInfo(name = "muscle_group")
    val muscleGroup: String,
    @ColumnInfo(name = "secondary_muscles_json")
    val secondaryMusclesJson: String,
    @ColumnInfo(name = "instructions_json")
    val instructionsJson: String,
    @ColumnInfo(name = "instruction_steps_json")
    val instructionStepsJson: String,
    @ColumnInfo(name = "image_relative_path")
    val imageRelativePath: String,
    @ColumnInfo(name = "gif_relative_path")
    val gifRelativePath: String,
    @ColumnInfo(name = "default_sets")
    val defaultSets: Int = 4,
    @ColumnInfo(name = "default_reps")
    val defaultReps: String = "8-12",
    @ColumnInfo(name = "default_rest_seconds")
    val defaultRestSeconds: Int = 90,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false
)
