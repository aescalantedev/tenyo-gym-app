package com.aescalantedev.tenyo.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `workout_plans`.
 * Representa tanto planes del catálogo inicial (Protocolo Toji, Protocolo Maki, etc.)
 * como planes creados por el usuario, incluyendo configuración de recordatorios semanales.
 */
@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "subtitle")
    val subtitle: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "goal")
    val goal: String,
    @ColumnInfo(name = "difficulty")
    val difficulty: String,
    @ColumnInfo(name = "days_per_week")
    val daysPerWeek: Int,
    @ColumnInfo(name = "duration_weeks")
    val durationWeeks: Int,
    @ColumnInfo(name = "estimated_minutes_per_session")
    val estimatedMinutesPerSession: Int,
    @ColumnInfo(name = "is_user_plan")
    val isUserPlan: Boolean,
    @ColumnInfo(name = "is_active_plan")
    val isActivePlan: Boolean,
    @ColumnInfo(name = "completed_sessions_count")
    val completedSessionsCount: Int = 0,
    @ColumnInfo(name = "reminder_hour")
    val reminderHour: Int = 18,
    @ColumnInfo(name = "reminder_minute")
    val reminderMinute: Int = 0,
    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean = false
)
