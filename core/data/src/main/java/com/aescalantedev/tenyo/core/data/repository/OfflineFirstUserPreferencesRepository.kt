package com.aescalantedev.tenyo.core.data.repository

import com.aescalantedev.tenyo.core.data.datastore.TenyoPreferencesDataSource
import com.aescalantedev.tenyo.core.domain.model.AppThemeMode
import com.aescalantedev.tenyo.core.domain.model.OnboardingProfileData
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.model.WeightUnit
import com.aescalantedev.tenyo.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementacion de [UserPreferencesRepository] respaldada por [TenyoPreferencesDataSource]
 * (`androidx.datastore:datastore-preferences-core`).
 */
class OfflineFirstUserPreferencesRepository(
    private val preferencesDataSource: TenyoPreferencesDataSource
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> =
        preferencesDataSource.userPreferences

    override suspend fun completeOnboarding(profile: OnboardingProfileData) {
        preferencesDataSource.completeOnboarding(profile = profile)
    }

    override suspend fun updateLanguage(languageCode: String) {
        preferencesDataSource.updateLanguage(languageCode)
    }

    override suspend fun updateThemeMode(themeMode: AppThemeMode) {
        preferencesDataSource.updateThemeMode(themeMode)
    }

    override suspend fun updateWeightUnit(weightUnit: WeightUnit) {
        preferencesDataSource.updateWeightUnit(weightUnit)
    }

    override suspend fun updateActivePlanId(planId: String) {
        preferencesDataSource.updateActivePlanId(planId)
    }

    override suspend fun updateCatalogSeedVersion(version: Int) {
        preferencesDataSource.updateCatalogSeedVersion(version)
    }
}
