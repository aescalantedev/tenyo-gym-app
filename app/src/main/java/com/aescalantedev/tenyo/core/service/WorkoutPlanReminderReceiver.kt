package com.aescalantedev.tenyo.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aescalantedev.tenyo.core.notification.WorkoutNotificationManager
import com.aescalantedev.tenyo.core.notification.WorkoutPlanAlarmScheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Receptor de alarmas programadas de entrenamiento del sistema ([android.app.AlarmManager]).
 *
 * Se ejecuta de manera garantizada con la aplicación cerrada, en segundo plano o con el teléfono
 * en reposo profundo (Doze mode), emitiendo la notificación sonora y háptica para la rutina del día.
 */
class WorkoutPlanReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationManager: WorkoutNotificationManager by inject()
    private val alarmScheduler: WorkoutPlanAlarmScheduler by inject()

    companion object {
        const val ACTION_PLAN_REMINDER = "com.aescalantedev.tenyo.ACTION_PLAN_REMINDER"
        const val EXTRA_PLAN_ID = "EXTRA_PLAN_ID"
        const val EXTRA_PLAN_TITLE = "EXTRA_PLAN_TITLE"
        const val EXTRA_ROUTINE_NAME = "EXTRA_ROUTINE_NAME"
        const val EXTRA_DAY_OF_WEEK = "EXTRA_DAY_OF_WEEK"
        const val EXTRA_HOUR = "EXTRA_HOUR"
        const val EXTRA_MINUTE = "EXTRA_MINUTE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_PLAN_REMINDER) {
            val planId = intent.getStringExtra(EXTRA_PLAN_ID) ?: return
            val planTitle = intent.getStringExtra(EXTRA_PLAN_TITLE) ?: "Protocolo Tenyo"
            val routineName = intent.getStringExtra(EXTRA_ROUTINE_NAME) ?: "Rutina de hoy"
            val dayOfWeek = intent.getIntExtra(EXTRA_DAY_OF_WEEK, 1)
            val hour = intent.getIntExtra(EXTRA_HOUR, 18)
            val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

            // 1. Mostrar notificación de alta prioridad
            notificationManager.notifyPlanWorkoutReminder(
                planId = planId,
                planTitle = planTitle,
                routineName = routineName
            )

            // 2. Reprogramar automáticamente para la siguiente semana
            alarmScheduler.scheduleDayReminder(
                planId = planId,
                planTitle = planTitle,
                routineName = routineName,
                dayOfWeek = dayOfWeek,
                hour = hour,
                minute = minute
            )
        }
    }
}
