package com.aescalantedev.tenyo.core.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.aescalantedev.tenyo.core.notification.WorkoutNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Servicio en primer plano (Foreground Service) de Tenyo.
 *
 * Mantiene la ejecución ininterrumpida de las rutinas y temporizadores cuando la aplicación
 * pasa a segundo plano o la pantalla se bloquea, evitando que Android suspenda el proceso.
 */
class WorkoutTimerService : Service() {

    private val notificationManager: WorkoutNotificationManager by inject()
    private val coordinator: WorkoutTimerCoordinator by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var syncJob: Job? = null

    private val skipRestReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WorkoutNotificationManager.ACTION_SKIP_REST) {
                coordinator.cancelRestTimer()
            }
        }
    }

    companion object {
        const val ACTION_START = "com.aescalantedev.tenyo.ACTION_START_WORKOUT_SERVICE"
        const val ACTION_STOP = "com.aescalantedev.tenyo.ACTION_STOP_WORKOUT_SERVICE"

        fun startService(context: Context) {
            val intent = Intent(context, WorkoutTimerService::class.java).apply {
                action = ACTION_START
            }
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (_: Exception) {
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, WorkoutTimerService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(WorkoutNotificationManager.ACTION_SKIP_REST)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(skipRestReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(skipRestReceiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForegroundService()
            return START_NOT_STICKY
        }

        // Promover inmediatamente el servicio a Foreground
        val initialNotification = notificationManager.buildForegroundNotification(
            exerciseName = coordinator.activeRestExerciseName.value,
            restRemainingSeconds = coordinator.activeRestRemainingSeconds.value,
            elapsedSeconds = coordinator.workoutElapsedSeconds.value,
            restTargetEndEpochMillis = coordinator.restTargetEndEpochMillis,
            sessionStartedEpochMillis = coordinator.sessionStartedEpochMillis
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                WorkoutNotificationManager.WORKOUT_SERVICE_NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(
                WorkoutNotificationManager.WORKOUT_SERVICE_NOTIFICATION_ID,
                initialNotification
            )
        }

        startSyncLoop()
        return START_STICKY
    }

    private fun startSyncLoop() {
        syncJob?.cancel()
        syncJob = serviceScope.launch {
            while (isActive) {
                delay(1_000L)
                notificationManager.updateForegroundNotification(
                    exerciseName = coordinator.activeRestExerciseName.value,
                    restRemainingSeconds = coordinator.activeRestRemainingSeconds.value,
                    elapsedSeconds = coordinator.workoutElapsedSeconds.value,
                    restTargetEndEpochMillis = coordinator.restTargetEndEpochMillis,
                    sessionStartedEpochMillis = coordinator.sessionStartedEpochMillis
                )
            }
        }
    }

    private fun stopForegroundService() {
        syncJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        syncJob?.cancel()
        try {
            unregisterReceiver(skipRestReceiver)
        } catch (_: Exception) {
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
