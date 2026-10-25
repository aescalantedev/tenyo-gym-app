package com.aescalantedev.tenyo.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aescalantedev.tenyo.core.domain.model.ActivityLevel
import com.aescalantedev.tenyo.core.domain.model.AppThemeMode
import com.aescalantedev.tenyo.core.domain.model.ArchetypeProtocol
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.ExperienceLevel
import com.aescalantedev.tenyo.core.domain.model.OnboardingProfileData
import com.aescalantedev.tenyo.core.domain.model.TrainingGoal
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.model.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import java.io.IOException

/**
 * Fuente de datos local basada en `androidx.datastore:datastore-preferences-core`.
 *
 * Almacena el estado de Onboarding, el perfil biometrico del atleta (peso, talla, edad, nivel,
 * dias de entrenamiento por semana y zonas musculares prioritarias), el idioma, el tema visual
 * y el protocolo activo.
 */
class TenyoPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {
    /**
     * Flujo reactivo con las preferencias y el perfil fisico actual del usuario.
     */
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val archetypeName = preferences[Keys.ARCHETYPE_PROTOCOL]
            val archetype = ArchetypeProtocol.entries.firstOrNull { it.name == archetypeName }
                ?: ArchetypeProtocol.TOJI_SORCERER_KILLER

            val weightUnitName = preferences[Keys.WEIGHT_UNIT]
            val weightUnit = WeightUnit.entries.firstOrNull { it.name == weightUnitName }
                ?: WeightUnit.KG

            val experienceName = preferences[Keys.EXPERIENCE_LEVEL]
            val experienceLevel = ExperienceLevel.entries.firstOrNull { it.name == experienceName }
                ?: ExperienceLevel.INTERMEDIATE

            val genderName = preferences[Keys.GENDER]
            val gender = UserGender.entries.firstOrNull { it.name == genderName }
                ?: UserGender.MALE

            val activityName = preferences[Keys.ACTIVITY_LEVEL]
            val activityLevel = ActivityLevel.entries.firstOrNull { it.name == activityName }
                ?: ActivityLevel.MODERATE

            val goalName = preferences[Keys.TRAINING_GOAL]
            val trainingGoal = TrainingGoal.entries.firstOrNull { it.name == goalName }
                ?: TrainingGoal.BUILD_MUSCLE

            val themeModeName = preferences[Keys.THEME_MODE]
            val themeMode = AppThemeMode.fromName(themeModeName)

            val focusRaw = preferences[Keys.FOCUS_BODY_PARTS].orEmpty()
            val focusBodyParts = focusRaw
                .split(",")
                .mapNotNull { token ->
                    BodyPartCategory.entries.firstOrNull { it.name == token.trim() }
                }
                .ifEmpty {
                    listOf(
                        BodyPartCategory.BACK,
                        BodyPartCategory.CHEST,
                        BodyPartCategory.SHOULDERS
                    )
                }

            UserPreferences(
                userName = preferences[Keys.USER_NAME] ?: "Atleta Tenyo",
                gender = gender,
                hasCompletedOnboarding = preferences[Keys.HAS_COMPLETED_ONBOARDING] ?: false,
                ageYears = preferences[Keys.AGE_YEARS] ?: 24,
                weightKg = preferences[Keys.WEIGHT_KG] ?: 75.0,
                heightCm = preferences[Keys.HEIGHT_CM] ?: 176,
                activityLevel = activityLevel,
                trainingGoal = trainingGoal,
                sessionDurationMinutes = preferences[Keys.SESSION_DURATION_MINUTES] ?: 45,
                experienceLevel = experienceLevel,
                trainingDaysPerWeek = preferences[Keys.TRAINING_DAYS_PER_WEEK] ?: 4,
                focusBodyParts = focusBodyParts,
                languageCode = preferences[Keys.LANGUAGE_CODE] ?: "es",
                themeMode = themeMode,
                weightUnit = weightUnit,
                archetypeProtocol = archetype,
                activePlanId = preferences[Keys.ACTIVE_PLAN_ID] ?: archetype.defaultPlanId,
                catalogSeedVersion = preferences[Keys.CATALOG_SEED_VERSION] ?: 0
            )
        }

    /**
     * Registra la finalizacion del Onboarding o la edicion del perfil guardando los datos biometricos,
     * las zonas musculares que el atleta desea priorizar y el protocolo elegido.
     */
    suspend fun completeOnboarding(profile: OnboardingProfileData) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_NAME] = profile.userName
            preferences[Keys.GENDER] = profile.gender.name
            preferences[Keys.AGE_YEARS] = profile.ageYears
            preferences[Keys.WEIGHT_KG] = profile.weightKg
            preferences[Keys.HEIGHT_CM] = profile.heightCm
            preferences[Keys.ACTIVITY_LEVEL] = profile.activityLevel.name
            preferences[Keys.TRAINING_GOAL] = profile.trainingGoal.name
            preferences[Keys.SESSION_DURATION_MINUTES] = profile.sessionDurationMinutes
            preferences[Keys.EXPERIENCE_LEVEL] = profile.experienceLevel.name
            preferences[Keys.TRAINING_DAYS_PER_WEEK] = profile.trainingDaysPerWeek
            preferences[Keys.FOCUS_BODY_PARTS] = profile.focusBodyParts.joinToString(",") { it.name }
            preferences[Keys.ARCHETYPE_PROTOCOL] = profile.archetypeProtocol.name
            preferences[Keys.ACTIVE_PLAN_ID] = profile.archetypeProtocol.defaultPlanId
            preferences[Keys.HAS_COMPLETED_ONBOARDING] = true
        }
    }

    /**
     * Actualiza el codigo ISO de idioma preferido para las instrucciones del dataset (`es`, `en`, etc.).
     */
    suspend fun updateLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[Keys.LANGUAGE_CODE] = languageCode
        }
    }

    /**
     * Actualiza el tema visual activo de la aplicacion ([AppThemeMode]).
     */
    suspend fun updateThemeMode(themeMode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }

    /**
     * Actualiza la unidad de peso preferida (`KG` o `LBS`).
     */
    suspend fun updateWeightUnit(weightUnit: WeightUnit) {
        dataStore.edit { preferences ->
            preferences[Keys.WEIGHT_UNIT] = weightUnit.name
        }
    }

    /**
     * Actualiza el identificador del plan de entrenamiento activo actual.
     */
    suspend fun updateActivePlanId(planId: String) {
        dataStore.edit { preferences ->
            preferences[Keys.ACTIVE_PLAN_ID] = planId
        }
    }

    /**
     * Actualiza la version del catalogo sembrado en Room.
     */
    suspend fun updateCatalogSeedVersion(version: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.CATALOG_SEED_VERSION] = version
        }
    }

    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val GENDER = stringPreferencesKey("gender")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val AGE_YEARS = intPreferencesKey("age_years")
        val WEIGHT_KG = doublePreferencesKey("weight_kg")
        val HEIGHT_CM = intPreferencesKey("height_cm")
        val ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
        val TRAINING_GOAL = stringPreferencesKey("training_goal")
        val SESSION_DURATION_MINUTES = intPreferencesKey("session_duration_minutes")
        val EXPERIENCE_LEVEL = stringPreferencesKey("experience_level")
        val TRAINING_DAYS_PER_WEEK = intPreferencesKey("training_days_per_week")
        val FOCUS_BODY_PARTS = stringPreferencesKey("focus_body_parts")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        val ARCHETYPE_PROTOCOL = stringPreferencesKey("archetype_protocol")
        val ACTIVE_PLAN_ID = stringPreferencesKey("active_plan_id")
        val CATALOG_SEED_VERSION = intPreferencesKey("catalog_seed_version")
    }

    companion object {
        const val DATASTORE_FILE_NAME = "tenyo_user_preferences.preferences_pb"

        fun createDataStore(produceFilePath: () -> String): DataStore<Preferences> =
            PreferenceDataStoreFactory.createWithPath(
                produceFile = { produceFilePath().toPath() }
            )
    }
}
