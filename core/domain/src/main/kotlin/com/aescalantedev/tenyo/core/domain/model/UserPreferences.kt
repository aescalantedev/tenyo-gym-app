package com.aescalantedev.tenyo.core.domain.model

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/**
 * Arquetipo de entrenamiento elegido por el usuario durante el Onboarding o en su Perfil.
 */
@Serializable
enum class ArchetypeProtocol(
    val titleEs: String,
    val subtitleEs: String,
    val characterInspiration: String,
    val defaultPlanId: String
) {
    TOJI_SORCERER_KILLER(
        titleEs = "Protocolo Toji · Fuerza y Densidad",
        subtitleEs = "Hipertrofia miofibrilar, espalda en V, hombros 3D y potencia explosiva con cargas pesadas.",
        characterInspiration = "Toji Fushiguro",
        defaultPlanId = "plan_toji_heavenly_restriction"
    ),
    MAKI_DEMON_TWIN(
        titleEs = "Protocolo Maki · Agilidad y Dominio",
        subtitleEs = "Fuerza relativa, core de acero, tren inferior reactivo y resistencia de combate sin pausa.",
        characterInspiration = "Maki Zenin",
        defaultPlanId = "plan_maki_cursed_mastery"
    )
}

/**
 * Nivel de experiencia fisica inicial del usuario en el Onboarding y Perfil.
 */
@Serializable
enum class ExperienceLevel(
    val labelEs: String,
    val descriptionEs: String
) {
    BEGINNER(
        labelEs = "Iniciado · Grado 4",
        descriptionEs = "Menos de 6 meses entrenando o retomando la disciplina."
    ),
    INTERMEDIATE(
        labelEs = "Intermedio · Grado 2",
        descriptionEs = "De 6 meses a 2 anos dominando ejercicios compuestos."
    ),
    ADVANCED(
        labelEs = "Avanzado · Grado 1",
        descriptionEs = "Mas de 2 anos entrenando con sobrecarga progresiva estricta."
    )
}

/**
 * Sexo biológico del atleta para cálculo metabólico y de composición física.
 */
@Serializable
enum class UserGender(val labelEs: String) {
    MALE("Hombre"),
    FEMALE("Mujer")
}

/**
 * Nivel de actividad física cotidiana.
 */
@Serializable
enum class ActivityLevel(val labelEs: String) {
    LOW("Bajo"),
    MODERATE("Moderado"),
    HIGH("Alto")
}

/**
 * Objetivo principal de entrenamiento seleccionado durante el Onboarding.
 */
@Serializable
enum class TrainingGoal(
    val titleEs: String,
    val descriptionEs: String
) {
    BUILD_MUSCLE(
        titleEs = "Ganar músculo",
        descriptionEs = "Aumenta tu masa muscular y define tu físico."
    ),
    LOSE_FAT(
        titleEs = "Perder grasa",
        descriptionEs = "Reduce tu porcentaje de grasa corporal y mejora tu composición."
    ),
    BUILD_STRENGTH(
        titleEs = "Mejorar fuerza",
        descriptionEs = "Incrementa tu rendimiento y capacidad física."
    )
}

/**
 * Paletas de colores oficiales de Tenyo.
 * Se simplifica a 3 temas esenciales:
 * 1. TENYO_DARK: Tema principal minimalista con acento púrpura celestial (#9B83F5).
 * 2. EMERALD_RESTRICTION: Tema oscuro con acento esmeralda (#00C896).
 * 3. MONOCHROME: Tema monocromo en negro (#0A0A0A) y blanco (#FFFFFF).
 */
@Serializable
enum class AppThemeMode(
    val codeNumber: String,
    val titleEs: String,
    val subtitleEs: String,
    val tagsEs: String
) {
    TENYO_DARK(
        codeNumber = "01",
        titleEs = "Tenyo Dark",
        subtitleEs = "Fondo oscuro con acento violeta.",
        tagsEs = "Minimalista / Violeta"
    ),
    EMERALD_RESTRICTION(
        codeNumber = "02",
        titleEs = "Restricción Esmeralda",
        subtitleEs = "Fondo oscuro con acento verde esmeralda.",
        tagsEs = "Deportivo / Esmeralda"
    ),
    MONOCHROME(
        codeNumber = "03",
        titleEs = "Monocromo",
        subtitleEs = "Escala de grises con acento blanco puro.",
        tagsEs = "Neutro / Blanco"
    );

    companion object {
        fun fromName(name: String?): AppThemeMode {
            if (name.isNullOrBlank()) return TENYO_DARK
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: when (name.uppercase()) {
                    "PALETA_PRINCIPAL", "TOJI_VIBES", "DARK_URBAN" -> TENYO_DARK
                    "NATURAL_MAKI", "CREPUSCULO" -> EMERALD_RESTRICTION
                    "MONOCROMA" -> MONOCHROME
                    else -> TENYO_DARK
                }
        }
    }
}

/**
 * Idiomas soportados en la aplicacion y en las traducciones del dataset de 1,324 ejercicios.
 */
@Serializable
enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val subtitleEs: String
) {
    ES(code = "es", nativeName = "Espanol", subtitleEs = "Interfaz e instrucciones en espanol"),
    EN(code = "en", nativeName = "English", subtitleEs = "Instrucciones y musculos en ingles"),
    PT(code = "pt", nativeName = "Portugues", subtitleEs = "Instrucciones traducidas al portugues"),
    FR(code = "fr", nativeName = "Francais", subtitleEs = "Instrucciones traducidas al frances"),
    DE(code = "de", nativeName = "Deutsch", subtitleEs = "Instrucciones traducidas al aleman"),
    IT(code = "it", nativeName = "Italiano", subtitleEs = "Instrucciones traducidas al italiano");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code.equals(code.trim(), ignoreCase = true) } ?: ES
    }
}

@Serializable
enum class WeightUnit(val symbol: String, val labelEs: String) {
    KG("kg", "Kilogramos (kg)"),
    LBS("lb", "Libras (lb)")
}

/**
 * Datos capturados durante el flujo de Onboarding inicial (WelcomeScreen) o editados en Perfil.
 */
data class OnboardingProfileData(
    val userName: String,
    val gender: UserGender = UserGender.MALE,
    val ageYears: Int,
    val weightKg: Double,
    val heightCm: Int,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val trainingGoal: TrainingGoal = TrainingGoal.BUILD_MUSCLE,
    val experienceLevel: ExperienceLevel,
    val trainingDaysPerWeek: Int,
    val sessionDurationMinutes: Int = 45,
    val focusBodyParts: List<BodyPartCategory>,
    val archetypeProtocol: ArchetypeProtocol
)

/**
 * Preferencias, idioma, tema visual y perfil biometrico del atleta persistidos en DataStore Preferences Core (KMP-ready).
 */
data class UserPreferences(
    val userName: String = "Atleta Tenyo",
    val gender: UserGender = UserGender.MALE,
    val hasCompletedOnboarding: Boolean = false,
    val ageYears: Int = 24,
    val weightKg: Double = 75.0,
    val heightCm: Int = 176,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val trainingGoal: TrainingGoal = TrainingGoal.BUILD_MUSCLE,
    val sessionDurationMinutes: Int = 45,
    val experienceLevel: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val trainingDaysPerWeek: Int = 4,
    val focusBodyParts: List<BodyPartCategory> = listOf(
        BodyPartCategory.BACK,
        BodyPartCategory.CHEST,
        BodyPartCategory.SHOULDERS
    ),
    val languageCode: String = "es",
    val themeMode: AppThemeMode = AppThemeMode.TENYO_DARK,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val archetypeProtocol: ArchetypeProtocol = ArchetypeProtocol.TOJI_SORCERER_KILLER,
    val activePlanId: String = ArchetypeProtocol.TOJI_SORCERER_KILLER.defaultPlanId,
    val catalogSeedVersion: Int = 0
) {
    /**
     * Indice de Masa Corporal (IMC) calculado a partir del peso en kg y la estatura en cm.
     */
    val bmi: Double
        get() {
            val heightMeters = (heightCm.coerceAtLeast(100)) / 100.0
            val rawBmi = weightKg / (heightMeters * heightMeters)
            return (rawBmi * 10.0).roundToInt() / 10.0
        }
}
