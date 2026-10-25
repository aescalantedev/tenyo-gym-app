package com.aescalantedev.tenyo.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aescalantedev.tenyo.core.data.local.dao.ExerciseDao
import com.aescalantedev.tenyo.core.data.local.dao.ExercisePersonalRecordDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutPlanDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutSessionDao
import com.aescalantedev.tenyo.core.data.local.entity.ExerciseEntity
import com.aescalantedev.tenyo.core.data.local.entity.ExercisePersonalRecordEntity
import com.aescalantedev.tenyo.core.data.local.entity.PlanExerciseCrossRefEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanDayEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSessionEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSetLogEntity

/**
 * Base de datos relacional principal de Tenyo construida con Room 2.8.5 (KMP-ready).
 *
 * Actúa como la Única Fuente de Verdad (Single Source of Truth) en la arquitectura Offline-First.
 * Exporta su esquema JSON en `core/data/schemas` para auditoría de migraciones y compatibilidad
 * con el futuro esquema PostgreSQL en Supabase.
 */
@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutPlanEntity::class,
        WorkoutPlanDayEntity::class,
        PlanExerciseCrossRefEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetLogEntity::class,
        ExercisePersonalRecordEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class TenyoDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun exercisePersonalRecordDao(): ExercisePersonalRecordDao

    companion object {
        const val DATABASE_NAME = "tenyo_workout.db"
    }
}
