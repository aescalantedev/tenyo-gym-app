package com.aescalantedev.tenyo.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aescalantedev.tenyo.core.domain.usecase.ObserveActiveWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.notification.WorkoutPlanAlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Receptor del evento [Intent.ACTION_BOOT_COMPLETED].
 *
 * Cuando el dispositivo se reinicia o enciende, reprograma automáticamente las alarmas
 * de entrenamiento del plan activo para que las notificaciones sigan activas.
 */
class BootCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val observeActiveWorkoutPlanUseCase: ObserveActiveWorkoutPlanUseCase by inject()
    private val alarmScheduler: WorkoutPlanAlarmScheduler by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == "android.intent.action.QUICKBOOT_POWERON") {
            CoroutineScope(Dispatchers.IO).launch {
                val activePlan = observeActiveWorkoutPlanUseCase().firstOrNull()
                if (activePlan != null && activePlan.reminderEnabled) {
                    alarmScheduler.schedulePlanReminders(activePlan)
                }
            }
        }
    }
}
