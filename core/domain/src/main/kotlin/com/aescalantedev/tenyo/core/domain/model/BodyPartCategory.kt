package com.aescalantedev.tenyo.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Macro-región anatómica utilizada en el filtro de la pantalla de Categorías (Stitch 03).
 */
@Serializable
enum class CategoryRegion(val labelEs: String, val labelEn: String) {
    ALL("Todos", "All"),
    UPPER_BODY("Tren superior", "Upper Body"),
    LOWER_BODY("Tren inferior", "Lower Body"),
    CORE_AND_CARDIO("Core y Cardio", "Core & Cardio")
}

/**
 * Categorías oficiales del dataset `exercises-dataset` (campo `body_part`),
 * enriquecidas con nombres localizados y región anatómica para la UI de Tenyo.
 */
@Serializable
enum class BodyPartCategory(
    val rawKey: String,
    val nameEs: String,
    val nameEn: String,
    val subtitleEs: String,
    val region: CategoryRegion
) {
    CHEST(
        rawKey = "chest",
        nameEs = "Pecho",
        nameEn = "Chest",
        subtitleEs = "Pectoral mayor, menor y empuje horizontal",
        region = CategoryRegion.UPPER_BODY
    ),
    BACK(
        rawKey = "back",
        nameEs = "Espalda",
        nameEn = "Back",
        subtitleEs = "Dorsales, trapecio, romboides y tracción",
        region = CategoryRegion.UPPER_BODY
    ),
    SHOULDERS(
        rawKey = "shoulders",
        nameEs = "Hombros",
        nameEn = "Shoulders",
        subtitleEs = "Deltoides anterior, lateral y posterior",
        region = CategoryRegion.UPPER_BODY
    ),
    UPPER_ARMS(
        rawKey = "upper arms",
        nameEs = "Brazos",
        nameEn = "Upper Arms",
        subtitleEs = "Bíceps, tríceps y braquial",
        region = CategoryRegion.UPPER_BODY
    ),
    LOWER_ARMS(
        rawKey = "lower arms",
        nameEs = "Antebrazos",
        nameEn = "Lower Arms",
        subtitleEs = "Agarre de acero, flexores y extensores",
        region = CategoryRegion.UPPER_BODY
    ),
    UPPER_LEGS(
        rawKey = "upper legs",
        nameEs = "Piernas",
        nameEn = "Upper Legs",
        subtitleEs = "Cuádriceps, isquiotibiales y glúteos",
        region = CategoryRegion.LOWER_BODY
    ),
    LOWER_LEGS(
        rawKey = "lower legs",
        nameEs = "Pantorrillas",
        nameEn = "Lower Legs",
        subtitleEs = "Gastrocnemio, sóleo y potencia reactiva",
        region = CategoryRegion.LOWER_BODY
    ),
    WAIST(
        rawKey = "waist",
        nameEs = "Abdomen y Core",
        nameEn = "Waist & Core",
        subtitleEs = "Recto abdominal, oblicuos y estabilidad",
        region = CategoryRegion.CORE_AND_CARDIO
    ),
    CARDIO(
        rawKey = "cardio",
        nameEs = "Cardio y Explosividad",
        nameEn = "Cardio & Conditioning",
        subtitleEs = "Resistencia metabólica y velocidad de combate",
        region = CategoryRegion.CORE_AND_CARDIO
    ),
    NECK(
        rawKey = "neck",
        nameEs = "Cuello",
        nameEn = "Neck",
        subtitleEs = "Estabilidad cervical y trapecio superior",
        region = CategoryRegion.UPPER_BODY
    );

    fun displayName(languageCode: String = "es"): String =
        if (languageCode.lowercase().startsWith("en")) nameEn else nameEs

    companion object {
        fun fromRawKey(raw: String): BodyPartCategory =
            entries.firstOrNull { it.rawKey.equals(raw.trim(), ignoreCase = true) } ?: CHEST
    }
}

/**
 * Resumen de una categoría muscular junto con el conteo real de ejercicios en Room.
 */
data class CategorySummary(
    val category: BodyPartCategory,
    val exerciseCount: Int
)
