package com.aescalantedev.tenyo.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.core.service.WorkoutPlanReminderReceiver
import java.util.Calendar

/**
 * Programador de alarmas exactas del sistema ([AlarmManager]) para recordatorios de entrenamiento.
 *
 * Funciona de manera confiable con la aplicación cerrada, en segundo plano o con el teléfono
 * en reposo profundo (Doze Mode).
 */
class WorkoutPlanAlarmScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Programa o reprograma los recordatorios para los días de entrenamiento activos del plan.
     */
    fun schedulePlanReminders(plan: WorkoutPlan) {
        if (!plan.reminderEnabled) {
            cancelPlanReminders(plan.id)
            return
        }

        plan.days.forEach { day ->
            if (day.isRestDay) {
                cancelDayReminder(plan.id, day.dayOfWeek)
            } else {
                scheduleDayReminder(
                    planId = plan.id,
                    planTitle = plan.title,
                    routineName = day.name.ifBlank { "Rutina del ${day.dayNameEs}" },
                    dayOfWeek = day.dayOfWeek,
                    hour = plan.reminderHour,
                    minute = plan.reminderMinute
                )
            }
        }
    }

    /**
     * Programa la alarma para un día específico de la semana.
     */
    fun scheduleDayReminder(
        planId: String,
        planTitle: String,
        routineName: String,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ) {
        val calendarDayOfWeek = when (dayOfWeek) {
            1 -> Calendar.MONDAY
            2 -> Calendar.TUESDAY
            3 -> Calendar.WEDNESDAY
            4 -> Calendar.THURSDAY
            5 -> Calendar.FRIDAY
            6 -> Calendar.SATURDAY
            7 -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }

        val targetCalendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, calendarDayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si la hora ya pasó en la semana actual, programar para la siguiente semana
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 7)
            }
        }

        val intent = Intent(context, WorkoutPlanReminderReceiver::class.java).apply {
            action = WorkoutPlanReminderReceiver.ACTION_PLAN_REMINDER
            putExtra(WorkoutPlanReminderReceiver.EXTRA_PLAN_ID, planId)
            putExtra(WorkoutPlanReminderReceiver.EXTRA_PLAN_TITLE, planTitle)
            putExtra(WorkoutPlanReminderReceiver.EXTRA_ROUTINE_NAME, routineName)
            putExtra(WorkoutPlanReminderReceiver.EXTRA_DAY_OF_WEEK, dayOfWeek)
            putExtra(WorkoutPlanReminderReceiver.EXTRA_HOUR, hour)
            putExtra(WorkoutPlanReminderReceiver.EXTRA_MINUTE, minute)
        }

        val requestCode = (planId.hashCode() * 31) + dayOfWeek
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = targetCalendar.timeInMillis
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancela la alarma de un día específico.
     */
    fun cancelDayReminder(planId: String, dayOfWeek: Int) {
        val intent = Intent(context, WorkoutPlanReminderReceiver::class.java)
        val requestCode = (planId.hashCode() * 31) + dayOfWeek
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Cancela todas las alarmas para los 7 días de un plan.
     */
    fun cancelPlanReminders(planId: String) {
        for (day in 1..7) {
            cancelDayReminder(planId, day)
        }
    }
}
