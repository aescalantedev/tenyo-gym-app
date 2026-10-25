package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `workout_plan_days`.
 * Representa cada uno de los días asignados dentro de un plan (Lunes a Domingo / Días 1 a 7),
 * permitiendo estructurar rutinas específicas por día (ej: Lunes = Pecho y Tríceps).
 */
@Entity(
    tableName = "workout_plan_days",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["plan_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["plan_id"]),
        Index(value = ["plan_id", "day_of_week"], unique = true)
    ]
)
data class WorkoutPlanDayEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "plan_id")
    val planId: String,
    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: Int, // 1 = Lunes, 2 = Martes, ..., 7 = Domingo
    @ColumnInfo(name = "name")
    val name: String, // Ej: "Pecho y Tríceps", "Descanso Activo"
    @ColumnInfo(name = "is_rest_day")
    val isRestDay: Boolean = false
)
