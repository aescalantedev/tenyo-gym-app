package com.aescalantedev.tenyo.core.domain.model

/**
 * Modelo de dominio puro para un Ejercicio de Tenyo.
 * Combina la estructura del dataset `hasaneyldrm/exercises-dataset` (1,324 ejercicios)
 * con soporte multilenguaje (10 idiomas), URLs resueltas (listas para GitHub Raw o futuro S3/Supabase)
 * y banderas de usuario (`isFavorite`, `isCustom`).
 */
data class Exercise(
    val id: String,
    val name: String,
    val category: String,
    val bodyPart: BodyPartCategory,
    val equipment: String,
    val targetMuscle: String,
    val muscleGroup: String,
    val secondaryMuscles: List<String>,
    val instructionsByLang: Map<String, String>,
    val instructionStepsByLang: Map<String, List<String>>,
    val imageRelativePath: String,
    val gifRelativePath: String,
    val imageUrl: String,
    val gifUrl: String,
    val defaultSets: Int = 4,
    val defaultReps: String = "8-12",
    val defaultRestSeconds: Int = 90,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
) {
    /**
     * Indica si el ejercicio se realiza con mancuernas (requiere multiplicar la carga por 2 en volumen/cálculo).
     */
    val isDumbbell: Boolean
        get() = equipment.contains("dumbbell", ignoreCase = true) ||
                name.contains("dumbbell", ignoreCase = true) ||
                equipment.contains("mancuerna", ignoreCase = true) ||
                name.contains("mancuerna", ignoreCase = true)
    /**
     * Obtiene los pasos de ejecución en el idioma solicitado (por defecto `"es"`),
     * con fallback automático a inglés (`"en"`) o al primer idioma disponible.
     */
    fun getInstructionSteps(languageCode: String = "es"): List<String> {
        val code = languageCode.lowercase().take(2)
        return instructionStepsByLang[code]?.takeIf { it.isNotEmpty() }
            ?: instructionStepsByLang["es"]?.takeIf { it.isNotEmpty() }
            ?: instructionStepsByLang["en"]?.takeIf { it.isNotEmpty() }
            ?: instructionStepsByLang.values.firstOrNull().orEmpty()
    }

    /**
     * Obtiene la descripción continua en el idioma solicitado con fallback a `"es"` y `"en"`.
     */
    fun getInstructionsText(languageCode: String = "es"): String {
        val code = languageCode.lowercase().take(2)
        return instructionsByLang[code]?.takeIf { it.isNotBlank() }
            ?: instructionsByLang["es"]?.takeIf { it.isNotBlank() }
            ?: instructionsByLang["en"]?.takeIf { it.isNotBlank() }
            ?: instructionsByLang.values.firstOrNull().orEmpty()
    }

    /**
     * Traducción amigable del equipamiento común del dataset para mostrar en la UI en español.
     */
    fun localizedEquipment(languageCode: String = "es"): String {
        if (languageCode.lowercase().startsWith("en")) {
            return equipment.replaceFirstChar { it.uppercase() }
        }
        return when (equipment.lowercase().trim()) {
            "barbell" -> "Barra olímpica"
            "dumbbell" -> "Mancuernas"
            "body weight" -> "Peso corporal"
            "cable" -> "Polea / Cable"
            "machine", "leverage machine" -> "Máquina"
            "kettlebell" -> "Pesa rusa (Kettlebell)"
            "ez barbell" -> "Barra Z"
            "smith machine" -> "Máquina Smith"
            "band", "resistance band" -> "Banda elástica"
            "stability ball" -> "Balón suizo"
            "medicine ball" -> "Balón medicinal"
            "weighted" -> "Lastre"
            "assisted" -> "Asistido"
            else -> equipment.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Traducción amigable del músculo objetivo (`targetMuscle`) para la UI en español.
     */
    fun localizedTargetMuscle(languageCode: String = "es"): String {
        if (languageCode.lowercase().startsWith("en")) {
            return targetMuscle.replaceFirstChar { it.uppercase() }
        }
        return when (targetMuscle.lowercase().trim()) {
            "pectorals" -> "Pectorales"
            "lats" -> "Dorsales"
            "upper back" -> "Espalda alta"
            "traps" -> "Trapecios"
            "delts" -> "Deltoides"
            "biceps" -> "Bíceps"
            "triceps" -> "Tríceps"
            "forearms" -> "Antebrazos"
            "abs" -> "Abdominales"
            "quads" -> "Cuádriceps"
            "hamstrings" -> "Isquiotibiales"
            "glutes" -> "Glúteos"
            "calves" -> "Pantorrillas"
            "cardiovascular system" -> "Sistema cardiovascular"
            "spine" -> "Erectores espinales"
            "serratus anterior" -> "Serrato anterior"
            "adductors" -> "Aductores"
            "abductors" -> "Abductores"
            "levator scapulae" -> "Elevador de la escápula"
            else -> targetMuscle.replaceFirstChar { it.uppercase() }
        }
    }
}

/**
 * Filtros aplicables al catálogo de ejercicios (Stitch 03 y 04).
 */
data class ExerciseFilter(
    val query: String = "",
    val bodyPart: BodyPartCategory? = null,
    val targetMuscle: String? = null,
    val equipment: String? = null,
    val onlyFavorites: Boolean = false
)
