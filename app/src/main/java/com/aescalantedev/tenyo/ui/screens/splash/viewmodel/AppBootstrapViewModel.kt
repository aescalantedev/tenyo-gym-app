package com.aescalantedev.tenyo.ui.screens.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescalantedev.tenyo.core.domain.model.OnboardingProfileData
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.usecase.EnsureCatalogSeededUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ManageUserPreferencesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado global de inicializacion de la aplicacion para el SplashScreen y Onboarding.
 *
 * @property isSplashCompleted Indica si ya concluyo la verificacion inicial de DataStore, el sembrado
 * basico y la animacion del SplashScreen.
 * @property isSeedingCatalog Indica si la base de datos Room sigue procesando el archivo JSON.
 * @property isActivatingProtocol Indica si se esta guardando el perfil del Onboarding antes de ir al Home.
 * @property preferences Preferencias y datos biometricos actuales del atleta.
 */
data class AppBootstrapUiState(
    val isSplashCompleted: Boolean = false,
    val isSeedingCatalog: Boolean = true,
    val isActivatingProtocol: Boolean = false,
    val preferences: UserPreferences = UserPreferences()
)

/**
 * ViewModel responsable del arranque de Tenyo en el SplashScreen:
 * - Ejecuta en paralelo la verificacion del estado de Onboarding en DataStore y el sembrado de los
 *   1,324 ejercicios en Room.
 * - Cuando `isSplashCompleted` pasa a `true`, el SplashScreen decide de forma determinista si enviar
 *   al usuario nuevo a `WelcomeRoute` (Onboarding) o directamente a `HomeRoute` sin pantallas fantasma.
 */
class AppBootstrapViewModel(
    private val ensureCatalogSeededUseCase: EnsureCatalogSeededUseCase,
    private val manageUserPreferencesUseCase: ManageUserPreferencesUseCase
) : ViewModel() {

    private val isSeeding = MutableStateFlow(true)
    private val isSplashMinTimeDone = MutableStateFlow(false)
    private val isActivatingProtocol = MutableStateFlow(false)

    val uiState: StateFlow<AppBootstrapUiState> = combine(
        isSeeding,
        isSplashMinTimeDone,
        isActivatingProtocol,
        manageUserPreferencesUseCase.preferences
    ) { seeding, minTimeDone, activating, prefs ->
        AppBootstrapUiState(
            isSplashCompleted = minTimeDone && !seeding,
            isSeedingCatalog = seeding,
            isActivatingProtocol = activating,
            preferences = prefs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppBootstrapUiState()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                ensureCatalogSeededUseCase()
            }
            isSeeding.value = false
        }
        viewModelScope.launch {
            delay(SPLASH_MIN_DURATION_MILLIS)
            isSplashMinTimeDone.value = true
        }
    }

    fun completeOnboarding(
        profile: OnboardingProfileData,
        onCompleted: () -> Unit
    ) {
        if (isActivatingProtocol.value) return
        viewModelScope.launch {
            isActivatingProtocol.value = true
            manageUserPreferencesUseCase.completeOnboarding(profile = profile)
            delay(PROTOCOL_ACTIVATION_DELAY_MILLIS)
            isActivatingProtocol.value = false
            onCompleted()
        }
    }

    companion object {
        private const val SPLASH_MIN_DURATION_MILLIS = 2_900L
        private const val PROTOCOL_ACTIVATION_DELAY_MILLIS = 500L
    }
}
