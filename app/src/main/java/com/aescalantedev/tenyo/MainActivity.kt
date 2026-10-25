package com.aescalantedev.tenyo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aescalantedev.tenyo.core.notification.WorkoutNotificationManager
import com.aescalantedev.tenyo.navigation.NavViewModel
import com.aescalantedev.tenyo.navigation.TenyoNavGraph
import org.koin.android.ext.android.inject

/**
 * Actividad principal de Tenyo configurada con soporte completo Edge-to-Edge y barras del sistema
 * oscuras acordes con la paleta minimalista de Tenyo.
 *
 * La logica de navegacion y gestion de backstack se delega en [TenyoNavGraph] utilizando Jetpack Navigation 3.
 * Atiende intents de notificaciones para redirigir directamente al entrenamiento activo.
 */
class MainActivity : ComponentActivity() {

    private val navViewModel: NavViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        handleNotificationNavigation(intent)
        setContent {
            TenyoNavGraph()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationNavigation(intent)
    }

    private fun handleNotificationNavigation(incomingIntent: Intent?) {
        if (incomingIntent?.getBooleanExtra(WorkoutNotificationManager.ACTION_NAVIGATE_TO_ACTIVE_WORKOUT, false) == true) {
            navViewModel.navigateToActiveWorkout()
        }
        val planId = incomingIntent?.getStringExtra(WorkoutNotificationManager.EXTRA_PLAN_ID)
        if (!planId.isNullOrBlank()) {
            navViewModel.navigate(com.aescalantedev.tenyo.navigation.PlanDetailRoute(planId))
        }
    }
}

