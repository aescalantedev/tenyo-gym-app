package com.aescalantedev.tenyo.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aescalantedev.tenyo.core.notification.WorkoutNotificationManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Receptor de alarmas exactas del sistema ([android.app.AlarmManager]).
 *
 * Se ejecuta de manera garantizada incluso si el dispositivo se encontraba en reposo profundo (Doze mode),
 * emitiendo la alerta sonora, vibración y notificación de fin de descanso.
 */
class RestTimerAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationManager: WorkoutNotificationManager by inject()
    private val coordinator: WorkoutTimerCoordinator by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == WorkoutTimerCoordinator.ACTION_ALARM_REST_EXPIRED) {
            val exerciseName = intent.getStringExtra(WorkoutTimerCoordinator.EXTRA_EXERCISE_NAME)
                ?: coordinator.activeRestExerciseName.value
                ?: "Ejercicio"

            // Notificación garantizada con sonido y vibración
            notificationManager.notifyRestCompleted(exerciseName)
            coordinator.cancelRestTimer()
        }
    }
}
