package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo

/**
 * Proyeccion auxiliar para el conteo agrupado de ejercicios por categoria muscular (`body_part`).
 */
data class BodyPartCountProjection(
    @ColumnInfo(name = "body_part")
    val bodyPart: String,
    @ColumnInfo(name = "exercise_count")
    val exerciseCount: Int
)
