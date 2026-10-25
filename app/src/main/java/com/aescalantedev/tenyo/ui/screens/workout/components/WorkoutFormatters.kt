package com.aescalantedev.tenyo.ui.screens.workout.components

/**
 * Formatea los segundos transcurridos al formato de cronometro `MM:SS`.
 */
fun formatElapsedTime(elapsedSeconds: Long): String {
    val minutes = elapsedSeconds / 60L
    val seconds = elapsedSeconds % 60L
    return "%02d:%02d".format(minutes, seconds)
}

/**
 * Formatea un peso en kilogramos omitiendo decimales cuando es un valor entero.
 */
fun formatWeight(weightKg: Double): String {
    return if (weightKg % 1.0 == 0.0) {
        weightKg.toInt().toString()
    } else {
        "%.1f".format(weightKg)
    }
}
