package com.aescalantedev.tenyo.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.AppThemeMode
import com.aescalantedev.tenyo.core.domain.model.ArchetypeProtocol
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.ExperienceLevel
import com.aescalantedev.tenyo.core.domain.model.WeightUnit
import com.aescalantedev.tenyo.ui.screens.profile.components.ProfileBodyAndMusclesSection
import com.aescalantedev.tenyo.ui.screens.profile.components.ProfileHeaderAndTabs
import com.aescalantedev.tenyo.ui.screens.profile.components.ProfileProtocolSection
import com.aescalantedev.tenyo.ui.screens.profile.components.ProfileSettingsAndThemeSection
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileTab
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileUiState
import com.aescalantedev.tenyo.ui.screens.welcome.components.TojiMuscleOption
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide

/**
 * Pantalla 09: Perfil del Atleta y Configuración Global de Tenyo.
 *
 * Diseño Minimalista Modular organizado en 3 pestañas:
 * 1. [ProfileTab.BIOMETRICS_AND_MUSCLES]: Biometría (peso, talla, IMC) y Visor Interactivo Rive de Toji con 8 grupos musculares.
 * 2. [ProfileTab.PROTOCOL]: Protocolo Maestro (Toji vs Maki), Nivel de Experiencia y Frecuencia Semanal.
 * 3. [ProfileTab.SETTINGS_AND_THEME]: Paleta de Colores (Temas), Idioma oficial (6 idiomas), Unidades de peso y Onboarding.
 */
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSelectTab: (ProfileTab) -> Unit,
    onUpdateAthleteName: (String) -> Unit,
    onUpdateAgeText: (String) -> Unit,
    onUpdateWeightKg: (Float) -> Unit,
    onUpdateHeightCm: (Float) -> Unit,
    onToggleMuscle: (TojiMuscleOption) -> Unit,
    onToggleViewSide: () -> Unit,
    onToggleBodyPartCategory: (BodyPartCategory) -> Unit,
    onSelectArchetype: (ArchetypeProtocol) -> Unit,
    onSelectExperience: (ExperienceLevel) -> Unit,
    onSelectTrainingDays: (Int) -> Unit,
    onSaveProfile: () -> Unit,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onSelectWeightUnit: (WeightUnit) -> Unit,
    onReopenOnboardingWizard: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TenyoObsidianBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .consumeWindowInsets(scaffoldPadding),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 16.dp,
                bottom = maxOf(
                    scaffoldPadding.calculateBottomPadding(),
                    contentPadding.calculateBottomPadding()
                ) + 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de estado (toast integrado)
            uiState.statusMessage?.let { message ->
                item(key = "status_banner") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = TenyoJadeContainer.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, TenyoJadePrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = TenyoJadePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TenyoTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Encabezado minimalista: Identidad, resumen biométrico y selector de pestañas
            item(key = "header_and_tabs") {
                ProfileHeaderAndTabs(
                    uiState = uiState,
                    onSelectTab = onSelectTab
                )
            }

            // Contenido dinámico según la pestaña activa
            when (uiState.selectedTab) {
                ProfileTab.BIOMETRICS_AND_MUSCLES -> {
                    item(key = "tab_biometrics_and_muscles") {
                        ProfileBodyAndMusclesSection(
                            uiState = uiState,
                            onUpdateAthleteName = onUpdateAthleteName,
                            onUpdateAgeText = onUpdateAgeText,
                            onUpdateWeightKg = onUpdateWeightKg,
                            onUpdateHeightCm = onUpdateHeightCm,
                            onToggleMuscle = onToggleMuscle,
                            onToggleViewSide = onToggleViewSide,
                            onToggleBodyPartCategory = onToggleBodyPartCategory,
                            onSaveProfile = onSaveProfile
                        )
                    }
                }

                ProfileTab.PROTOCOL -> {
                    item(key = "tab_protocol") {
                        ProfileProtocolSection(
                            uiState = uiState,
                            onSelectArchetype = onSelectArchetype,
                            onSelectExperience = onSelectExperience,
                            onSelectTrainingDays = onSelectTrainingDays,
                            onSaveProfile = onSaveProfile
                        )
                    }
                }

                ProfileTab.SETTINGS_AND_THEME -> {
                    item(key = "tab_settings_and_theme") {
                        ProfileSettingsAndThemeSection(
                            currentThemeMode = uiState.preferences.themeMode,
                            currentLanguageCode = uiState.preferences.languageCode,
                            currentWeightUnit = uiState.preferences.weightUnit,
                            onSelectThemeMode = onSelectThemeMode,
                            onSelectLanguage = onSelectLanguage,
                            onSelectWeightUnit = onSelectWeightUnit,
                            onReopenOnboardingWizard = onReopenOnboardingWizard
                        )
                    }
                }
            }
        }
    }
}
