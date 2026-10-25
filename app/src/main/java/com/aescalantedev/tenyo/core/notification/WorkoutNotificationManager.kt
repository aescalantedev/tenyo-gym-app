package com.aescalantedev.tenyo.core.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.aescalantedev.tenyo.MainActivity
import com.aescalantedev.tenyo.R

/**
 * Gestor especializado en notificaciones, alertas sonoras, vibración háptica
 * y notificaciones persistentes para el servicio Foreground de entrenamiento.
 *
 * Cumple con el Principio de Responsabilidad Única (SRP):
 * - Registra los canales de notificación necesarios (Fin de Descanso y Servicio Foreground).
 * - Despacha alertas sonoras y de vibración háptica al hardware físico del dispositivo.
 * - Construye las notificaciones interactivas con PendingIntent hacia [MainActivity].
 */
class WorkoutNotificationManager(
    private val context: Context
) {
    companion object {
        const val REST_TIMER_CHANNEL_ID = "tenyo_rest_timer_channel"
        const val REST_TIMER_NOTIFICATION_ID = 2001

        const val WORKOUT_SERVICE_CHANNEL_ID = "tenyo_workout_service_channel"
        const val WORKOUT_SERVICE_NOTIFICATION_ID = 2002

        const val PLAN_REMINDER_CHANNEL_ID = "tenyo_plan_reminder_channel"
        const val PLAN_REMINDER_NOTIFICATION_ID = 2003

        const val ACTION_SKIP_REST = "com.aescalantedev.tenyo.ACTION_SKIP_REST"
        const val ACTION_NAVIGATE_TO_ACTIVE_WORKOUT = "EXTRA_NAVIGATE_TO_ACTIVE_WORKOUT"
        const val ACTION_OPEN_PLAN_DETAIL = "com.aescalantedev.tenyo.ACTION_OPEN_PLAN_DETAIL"
        const val EXTRA_PLAN_ID = "EXTRA_PLAN_ID"

        private val REST_VIBRATION_PATTERN = longArrayOf(0, 350, 150, 350)
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            // 1. Canal de alta prioridad para aviso de fin de descanso
            val restChannel = NotificationChannel(
                REST_TIMER_CHANNEL_ID,
                "Fin de Descanso",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones al finalizar el tiempo de descanso entre series"
                enableVibration(true)
                vibrationPattern = REST_VIBRATION_PATTERN
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 2. Canal de servicio en ejecución para el cronómetro de fondo
            val serviceChannel = NotificationChannel(
                WORKOUT_SERVICE_CHANNEL_ID,
                "Entrenamiento en Curso",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Estado continuo del entrenamiento y temporizador activo"
                enableVibration(false)
                setSound(null, null)
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            // 3. Canal de alta prioridad para recordatorios semanales del plan
            val planChannel = NotificationChannel(
                PLAN_REMINDER_CHANNEL_ID,
                "Recordatorios de Entrenamiento",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones programadas según los días de tu plan de entrenamiento"
                enableVibration(true)
                vibrationPattern = REST_VIBRATION_PATTERN
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(restChannel)
            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(planChannel)
        }
    }

    /**
     * Emite una notificación de alta prioridad cuando llega la hora programada de entrenar.
     */
    fun notifyPlanWorkoutReminder(planId: String, planTitle: String, routineName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_PLAN_ID, planId)
            action = ACTION_OPEN_PLAN_DETAIL
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            planId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PLAN_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tenyo · Es hora de entrenar")
            .setContentText("Hoy toca $routineName · $planTitle")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Hoy toca $routineName ($planTitle). Mantén la disciplina y ejecuta tu sesión programada.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(PLAN_REMINDER_NOTIFICATION_ID, notification)
        triggerCompletionHaptics()
    }

    /**
     * Emite un tono corto de aviso (beeping countdown) en los últimos segundos (3, 2, 1).
     */
    fun playWarningTick() {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (_: Exception) {
            // Manejo silencioso en dispositivos sin soporte de audio notification stream
        }
    }

    /**
     * Emite el tono sonoro completo de finalización de descanso.
     */
    fun playCompletionChime() {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 350)
        } catch (_: Exception) {
            // Failsafe mediante RingtoneManager del sistema
            try {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(context, soundUri)
                ringtone?.play()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Emite una vibración física en el dispositivo usando el hardware disponible.
     */
    fun triggerCompletionHaptics() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        REST_VIBRATION_PATTERN,
                        intArrayOf(0, 255, 0, 255),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(REST_VIBRATION_PATTERN, -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(REST_VIBRATION_PATTERN, -1)
                }
            }
        } catch (_: Exception) {
            // Manejo silencioso en dispositivos sin hardware de vibración
        }
    }

    /**
     * Construye un PendingIntent hacia MainActivity para abrir la pantalla de entrenamiento activo.
     */
    private fun createContentIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ACTION_NAVIGATE_TO_ACTIVE_WORKOUT, true)
        }
        return PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Construye la notificación persistente para el Foreground Service.
     * Incorpora soporte de Chronometer nativo de Android (count up para sesión y countdown para descanso).
     */
    fun buildForegroundNotification(
        exerciseName: String?,
        restRemainingSeconds: Int?,
        elapsedSeconds: Long,
        restTargetEndEpochMillis: Long? = null,
        sessionStartedEpochMillis: Long? = null
    ): Notification {
        val contentIntent = createContentIntent()
        val now = System.currentTimeMillis()

        val isResting = (restTargetEndEpochMillis != null && restTargetEndEpochMillis > now) ||
                (restRemainingSeconds != null && restRemainingSeconds > 0)

        val secondsLeft = restRemainingSeconds
            ?: if (restTargetEndEpochMillis != null) {
                val diff = restTargetEndEpochMillis - now
                if (diff > 0) ((diff + 999) / 1000L).toInt() else 0
            } else 0

        val builder = NotificationCompat.Builder(context, WORKOUT_SERVICE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setShowWhen(true)
            .setUsesChronometer(true)

        if (isResting) {
            val minutes = secondsLeft / 60
            val seconds = secondsLeft % 60
            val formattedRest = String.format("%02d:%02d", minutes, seconds)

            builder.setContentTitle("Descanso: $formattedRest")
            builder.setContentText("Siguiente: ${exerciseName ?: "Ejercicio"}")
            builder.setSubText("Descanso")
            builder.setChronometerCountDown(true)

            val target = restTargetEndEpochMillis ?: (now + (secondsLeft * 1000L))
            builder.setWhen(target)

            val skipIntent = Intent(ACTION_SKIP_REST).apply {
                setPackage(context.packageName)
            }
            val skipPendingIntent = PendingIntent.getBroadcast(
                context,
                1002,
                skipIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                R.mipmap.ic_launcher,
                "Saltar descanso",
                skipPendingIntent
            )
        } else {
            val elMinutes = elapsedSeconds / 60
            val elSeconds = elapsedSeconds % 60
            val formattedElapsed = String.format("%02d:%02d", elMinutes, elSeconds)

            builder.setContentTitle("Entrenamiento en curso")
            builder.setContentText("Tiempo transcurrido: $formattedElapsed")
            builder.setSubText("Tenyo")
            builder.setChronometerCountDown(false)

            val startWhen = sessionStartedEpochMillis ?: (now - (elapsedSeconds * 1000L))
            builder.setWhen(startWhen)
        }

        return builder.build()
    }

    /**
     * Muestra una notificación de alta prioridad al completarse el tiempo de descanso.
     */
    fun notifyRestCompleted(exerciseName: String) {
        triggerCompletionHaptics()
        playCompletionChime()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return
        }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val contentIntent = createContentIntent()

        val builder = NotificationCompat.Builder(context, REST_TIMER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Descanso completado")
            .setContentText("Es momento de continuar con: $exerciseName")
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(REST_VIBRATION_PATTERN)
            .setSound(soundUri)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(REST_TIMER_NOTIFICATION_ID, builder.build())
    }

    /**
     * Cancela la notificación de descanso completado.
     */
    fun cancelRestNotification() {
        NotificationManagerCompat.from(context).cancel(REST_TIMER_NOTIFICATION_ID)
    }

    /**
     * Actualiza la notificación del Foreground Service en vivo.
     */
    fun updateForegroundNotification(
        exerciseName: String?,
        restRemainingSeconds: Int?,
        elapsedSeconds: Long,
        restTargetEndEpochMillis: Long? = null,
        sessionStartedEpochMillis: Long? = null
    ) {
        val notification = buildForegroundNotification(
            exerciseName = exerciseName,
            restRemainingSeconds = restRemainingSeconds,
            elapsedSeconds = elapsedSeconds,
            restTargetEndEpochMillis = restTargetEndEpochMillis,
            sessionStartedEpochMillis = sessionStartedEpochMillis
        )
        NotificationManagerCompat.from(context).notify(WORKOUT_SERVICE_NOTIFICATION_ID, notification)
    }
}
