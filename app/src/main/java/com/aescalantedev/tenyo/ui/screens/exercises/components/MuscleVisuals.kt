package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.annotation.DrawableRes
import com.aescalantedev.tenyo.R

/**
 * Resuelve la imagen anatómica de `res/drawable` correspondiente al músculo o grupo muscular indicado.
 */
object MuscleVisuals {

    @DrawableRes
    fun drawableForMuscle(muscleOrBodyPart: String): Int {
        val normalized = muscleOrBodyPart.lowercase().trim()
        return when {
            // Pecho / Pectorales
            normalized.contains("chest") || normalized.contains("pecho") || normalized.contains("pector") ->
                R.drawable.cat_muscle_chest

            // Espalda / Dorsales / Trapecio / Lumbar
            normalized.contains("back") || normalized.contains("espalda") || normalized.contains("lat") ||
                normalized.contains("trap") || normalized.contains("rhomb") || normalized.contains("romboid") ||
                normalized.contains("spine") || normalized.contains("lumbar") ->
                R.drawable.cat_muscle_back

            // Bíceps / Braquial
            normalized.contains("bicep") || normalized.contains("bícep") ||
                normalized.contains("brachial") || normalized.contains("braquial") ->
                R.drawable.cat_muscle_biceps

            // Tríceps
            normalized.contains("tricep") || normalized.contains("trícep") ->
                R.drawable.cat_muscle_triceps

            // Hombros / Deltoides
            normalized.contains("shoulder") || normalized.contains("hombro") || normalized.contains("delt") ->
                R.drawable.cat_muscle_shoulders

            // Cuádriceps / Piernas
            normalized.contains("quad") || normalized.contains("cuádricep") || normalized.contains("cuadricep") ->
                R.drawable.cat_muscle_quads

            // Isquiotibiales / Femorales
            normalized.contains("hamstring") || normalized.contains("isquio") || normalized.contains("femoral") ->
                R.drawable.cat_muscle_hamstrings

            // Glúteos
            normalized.contains("glute") || normalized.contains("glúteo") ->
                R.drawable.cat_muscle_glutes

            // Pantorrillas / Gemelos / Sóleo
            normalized.contains("calf") || normalized.contains("calves") || normalized.contains("pantorrilla") ||
                normalized.contains("gemelo") || normalized.contains("gastrocnem") ||
                normalized.contains("soleo") || normalized.contains("sóleo") ->
                R.drawable.cat_muscle_calves

            // Abdomen / Core / Oblicuos
            normalized.contains("abs") || normalized.contains("abdom") || normalized.contains("core") ||
                normalized.contains("oblique") || normalized.contains("oblicuo") || normalized.contains("waist") ->
                R.drawable.cat_muscle_abs

            // Antebrazos / Flexores / Muñeca
            normalized.contains("forearm") || normalized.contains("antebrazo") ||
                normalized.contains("wrist") || normalized.contains("muñeca") || normalized.contains("grip") ->
                R.drawable.cat_muscle_forearms

            // Cuello / Trapecio Superior
            normalized.contains("neck") || normalized.contains("cuello") || normalized.contains("cervical") ->
                R.drawable.cat_muscle_neck

            // Fallback por grupos anatómicos generales
            normalized.contains("upper arms") || normalized.contains("brazo") ->
                R.drawable.cat_muscle_biceps
            normalized.contains("upper legs") || normalized.contains("pierna") ->
                R.drawable.cat_muscle_quads
            normalized.contains("lower legs") ->
                R.drawable.cat_muscle_calves
            normalized.contains("lower arms") ->
                R.drawable.cat_muscle_forearms

            else -> R.drawable.cat_muscle_chest
        }
    }
}
