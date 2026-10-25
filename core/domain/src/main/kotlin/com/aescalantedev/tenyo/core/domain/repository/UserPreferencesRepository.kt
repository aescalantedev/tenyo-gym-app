package com.aescalantedev.tenyo.core.domain.repository

import com.aescalantedev.tenyo.core.domain.model.AppThemeMode
import com.aescalantedev.tenyo.core.domain.model.OnboardingProfileData
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.model.WeightUnit
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para las preferencias, idioma, tema visual y perfil fisico del usuario
 * respaldado por DataStore Preferences Core.
 */
interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    suspend fun completeOnboarding(profile: OnboardingProfileData)

    suspend fun updateLanguage(languageCode: String)

    suspend fun updateThemeMode(themeMode: AppThemeMode)

    suspend fun updateWeightUnit(weightUnit: WeightUnit)

    suspend fun updateActivePlanId(planId: String)

    suspend fun updateCatalogSeedVersion(version: Int)
}
