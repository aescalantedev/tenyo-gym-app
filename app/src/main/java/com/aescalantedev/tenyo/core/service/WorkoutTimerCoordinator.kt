package com.aescalantedev.tenyo.core.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.aescalantedev.tenyo.core.notification.WorkoutNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Coordinador central para el seguimiento de tiempos de entrenamiento y descansos.
 *
 * Arquitectura Offline-First y Resiliente a suspensión de CPU (Doze Mode):
 * - Los cálculos de tiempo se basan en marcas de tiempo reales (`System.currentTimeMillis()`),
 *   lo que garantiza exactitud absoluta aunque el dispositivo suspenda la app o el usuario cambie de pantalla.
 * - Ejecuta las rutinas en segundo plano usando `Dispatchers.Default` y `Dispatchers.IO`.
 * - Despacha alertas sonoras en los últimos 3 segundos (3, 2, 1) y alerta final en 0.
 * - Programa alarmas exactas con [AlarmManager.setExactAndAllowWhileIdle] como salvaguarda en reposo profundo.
 * - Inicia y detiene el [WorkoutTimerService] en Foreground.
 */
class WorkoutTimerCoordinator(
    private val context: Context,
    private val notificationManager: WorkoutNotificationManager
) {
    private val coordinatorScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Estado del Temporizador de Descanso
    private val _activeRestRemainingSeconds = MutableStateFlow<Int?>(null)
    val activeRestRemainingSeconds: StateFlow<Int?> = _activeRestRemainingSeconds.asStateFlow()

    private val _activeRestTotalSeconds = MutableStateFlow<Int?>(null)
    val activeRestTotalSeconds: StateFlow<Int?> = _activeRestTotalSeconds.asStateFlow()

    private val _activeRestExerciseName = MutableStateFlow<String?>(null)
    val activeRestExerciseName: StateFlow<String?> = _activeRestExerciseName.asStateFlow()

    // Estado del Cronómetro de la Sesión Activa
    private val _workoutElapsedSeconds = MutableStateFlow(0L)
    val workoutElapsedSeconds: StateFlow<Long> = _workoutElapsedSeconds.asStateFlow()

    var sessionStartedEpochMillis: Long? = null
        private set

    var restTargetEndEpochMillis: Long? = null
        private set

    private var restExerciseName: String? = null
    private var lastWarningSecondPlayed: Int? = null

    private var timerLoopJob: Job? = null
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    companion object {
        const val ACTION_ALARM_REST_EXPIRED = "com.aescalantedev.tenyo.ACTION_ALARM_REST_EXPIRED"
        const val EXTRA_EXERCISE_NAME = "EXTRA_EXERCISE_NAME"
        private const val ALARM_REQUEST_CODE = 3001
    }

    init {
        startTimerLoop()
    }

    /**
     * Inicia o sincroniza el cronómetro de la sesión activa de entrenamiento.
     */
    fun startWorkoutSession(startedAtEpochMillis: Long) {
        sessionStartedEpochMillis = startedAtEpochMillis
        WorkoutTimerService.startService(context)
    }

    /**
     * Detiene el seguimiento del entrenamiento y apaga el Foreground Service.
     */
    fun stopWorkoutSession() {
        sessionStartedEpochMillis = null
        cancelRestTimer()
        _workoutElapsedSeconds.value = 0L
        WorkoutTimerService.stopService(context)
    }

    /**
     * Inicia la cuenta regresiva del temporizador de descanso entre series.
     */
    fun startRestTimer(seconds: Int, exerciseName: String) {
        if (seconds <= 0) return

        // Cancelar cualquier notificación previa de descanso completado
        notificationManager.cancelRestNotification()

        val now = System.currentTimeMillis()
        val targetMillis = now + (seconds * 1000L)
        restTargetEndEpochMillis = targetMillis
        restExerciseName = exerciseName
        lastWarningSecondPlayed = null

        _activeRestTotalSeconds.value = seconds
        _activeRestRemainingSeconds.value = seconds
        _activeRestExerciseName.value = exerciseName

        // Asegurar que el Foreground Service esté activo y actualice su notificación
        WorkoutTimerService.startService(context)
        notificationManager.updateForegroundNotification(
            exerciseName = exerciseName,
            restRemainingSeconds = seconds,
            elapsedSeconds = _workoutElapsedSeconds.value,
            restTargetEndEpochMillis = targetMillis,
            sessionStartedEpochMillis = sessionStartedEpochMillis
        )

        // Programar alarma exacta como failsafe en caso de suspensión profunda del sistema
        scheduleExactRestAlarm(targetMillis, exerciseName)
    }

    /**
     * Cancela el temporizador de descanso activo.
     */
    fun cancelRestTimer() {
        cancelExactRestAlarm()
        restTargetEndEpochMillis = null
        restExerciseName = null
        lastWarningSecondPlayed = null

        _activeRestRemainingSeconds.value = null
        _activeRestTotalSeconds.value = null
        _activeRestExerciseName.value = null

        notificationManager.cancelRestNotification()
        notificationManager.updateForegroundNotification(
            exerciseName = null,
            restRemainingSeconds = null,
            elapsedSeconds = _workoutElapsedSeconds.value,
            restTargetEndEpochMillis = null,
            sessionStartedEpochMillis = sessionStartedEpochMillis
        )
    }

    private fun startTimerLoop() {
        timerLoopJob?.cancel()
        timerLoopJob = coordinatorScope.launch {
            while (isActive) {
                delay(500L) // Muestreo a 500ms para precisión sin consumo de batería
                val now = System.currentTimeMillis()

                // 1. Cálculo del tiempo de sesión
                val sessionStart = sessionStartedEpochMillis
                if (sessionStart != null && sessionStart > 0) {
                    val elapsed = (now - sessionStart) / 1000L
                    if (elapsed >= 0) {
                        _workoutElapsedSeconds.value = elapsed
                    }
                }

                // 2. Cálculo del tiempo de descanso restante
                val target = restTargetEndEpochMillis
                val exName = restExerciseName
                if (target != null && exName != null) {
                    val diff = target - now
                    val remaining = if (diff > 0) ((diff + 999) / 1000L).toInt() else 0

                    if (_activeRestRemainingSeconds.value != remaining) {
                        _activeRestRemainingSeconds.value = remaining
                        // Actualizar notificación en vivo segundo a segundo
                        notificationManager.updateForegroundNotification(
                            exerciseName = exName,
                            restRemainingSeconds = remaining,
                            elapsedSeconds = _workoutElapsedSeconds.value,
                            restTargetEndEpochMillis = target,
                            sessionStartedEpochMillis = sessionStart
                        )
                    }

                    // Beeping de advertencia en los últimos 3 segundos (3, 2, 1)
                    if (remaining in 1..3 && remaining != lastWarningSecondPlayed) {
                        lastWarningSecondPlayed = remaining
                        notificationManager.playWarningTick()
                    }

                    // Finalización del descanso en 0
                    if (remaining <= 0) {
                        cancelExactRestAlarm()
                        restTargetEndEpochMillis = null
                        restExerciseName = null
                        lastWarningSecondPlayed = null

                        _activeRestRemainingSeconds.value = null
                        _activeRestTotalSeconds.value = null
                        _activeRestExerciseName.value = null

                        // Notificación de alta prioridad con sonido y vibración
                        notificationManager.notifyRestCompleted(exName)

                        // Volver a actualizar la notificación Foreground al cronómetro normal
                        notificationManager.updateForegroundNotification(
                            exerciseName = null,
                            restRemainingSeconds = null,
                            elapsedSeconds = _workoutElapsedSeconds.value,
                            restTargetEndEpochMillis = null,
                            sessionStartedEpochMillis = sessionStart
                        )
                    }
                }
            }
        }
    }

    private fun scheduleExactRestAlarm(triggerAtMillis: Long, exerciseName: String) {
        val intent = Intent(context, RestTimerAlarmReceiver::class.java).apply {
            action = ACTION_ALARM_REST_EXPIRED
            putExtra(EXTRA_EXERCISE_NAME, exerciseName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager?.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Manejo defensivo en dispositivos con restricciones de alarmas exactas
        }
    }

    private fun cancelExactRestAlarm() {
        val intent = Intent(context, RestTimerAlarmReceiver::class.java).apply {
            action = ACTION_ALARM_REST_EXPIRED
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingIntent)
    }
}
