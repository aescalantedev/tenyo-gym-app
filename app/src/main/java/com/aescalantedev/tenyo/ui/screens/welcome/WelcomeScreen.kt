package com.aescalantedev.tenyo.ui.screens.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.ActivityLevel
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.TrainingGoal
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.ui.components.TenyoPrimaryButton
import com.aescalantedev.tenyo.ui.components.TenyoStepProgressBar
import com.aescalantedev.tenyo.ui.screens.welcome.components.TojiMuscleOption
import com.aescalantedev.tenyo.ui.screens.welcome.components.WelcomeStep1Biometrics
import com.aescalantedev.tenyo.ui.screens.welcome.components.WelcomeStep2ExperienceAndFrequency
import com.aescalantedev.tenyo.ui.screens.welcome.components.WelcomeStep3MuscleFocusAndArchetype
import com.aescalantedev.tenyo.ui.screens.welcome.viewmodel.WelcomeUiState
import com.aescalantedev.tenyo.ui.theme.TenyoAccentGold
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Swords

/**
 * Pantalla 01: Onboarding Inicial de Tenyo (Diseno premium-minimal).
 *
 * Flujo de 3 pasos:
 * 01 / 03 · TU PERFIL (Nombre, Sexo, Edad, Estatura, Peso, Nivel de actividad)
 * 02 / 03 · TU OBJETIVO (Ganar musculo, Perder grasa, Fuerza, Dias, Tiempo)
 * 03 / 03 · ENFOQUE MUSCULAR (Toji interactivo a color con musculos en purpura)
 */
@Composable
fun WelcomeScreen(
    uiState: WelcomeUiState,
    isSeedingCatalog: Boolean,
    onNameChange: (String) -> Unit,
    onGenderChange: (UserGender) -> Unit,
    onAgeChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    onWeightChange: (Int) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onSelectGoal: (TrainingGoal) -> Unit,
    onSelectDaysPerWeek: (Int) -> Unit,
    onSelectDurationMinutes: (Int) -> Unit,
    onToggleMuscle: (TojiMuscleOption) -> Unit,
    onToggleViewSide: () -> Unit,
    onToggleBodyPartCategory: (BodyPartCategory) -> Unit,
    onPreviousStep: () -> Unit,
    onNextStep: () -> Unit,
    onCompleteOnboarding: (openCategoriesFirst: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStep = uiState.currentStep
    val totalSteps = uiState.totalSteps
    val isActivatingProtocol = uiState.isActivatingProtocol

    val stepSubtitle = when (currentStep) {
        0 -> "TU PERFIL"
        1 -> "TU OBJETIVO"
        else -> "ENFOQUE MUSCULAR"
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(TenyoObsidianBackground),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Cabecera superior: Logo TENYO a la izquierda, paso a la derecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Lucide.Swords,
                            contentDescription = "Tenyo Logo",
                            tint = TenyoTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TENYO",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            ),
                            color = TenyoTextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "0${currentStep + 1} / 0$totalSteps  ·  ",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = TenyoTextTertiary
                        )
                        Text(
                            text = stepSubtitle,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TenyoAccentGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barra de progreso segmentada minimalista (3 lineas delgadas)
                TenyoStepProgressBar(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    height = 3.dp
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                TenyoPrimaryButton(
                    text = when {
                        isActivatingProtocol -> "Creando tu plan..."
                        currentStep < totalSteps - 1 -> "Continuar"
                        else -> "Crear mi plan"
                    },
                    trailingIcon = Lucide.ArrowRight,
                    enabled = !isActivatingProtocol,
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            onNextStep()
                        } else {
                            onCompleteOnboarding(false)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            AnimatedContent(
                targetState = currentStep,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    if (targetState > initialState) {
                        (fadeIn(tween(280, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(tween(280, easing = FastOutSlowInEasing)) { it / 5 }) togetherWith
                            (fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 5 })
                    } else {
                        (fadeIn(tween(280, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(tween(280, easing = FastOutSlowInEasing)) { -it / 5 }) togetherWith
                            (fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 5 })
                    }
                },
                label = "OnboardingStepTransition"
            ) { step ->
                when (step) {
                    0 -> WelcomeStep1Biometrics(
                        athleteName = uiState.athleteName,
                        onNameChange = onNameChange,
                        gender = uiState.gender,
                        onGenderChange = onGenderChange,
                        ageYears = uiState.ageYears,
                        onAgeChange = onAgeChange,
                        heightCm = uiState.heightCm,
                        onHeightChange = onHeightChange,
                        weightKg = uiState.weightKg,
                        onWeightChange = onWeightChange,
                        activityLevel = uiState.activityLevel,
                        onActivityLevelChange = onActivityLevelChange
                    )

                    1 -> WelcomeStep2ExperienceAndFrequency(
                        trainingGoal = uiState.trainingGoal,
                        onSelectGoal = onSelectGoal,
                        trainingDaysPerWeek = uiState.trainingDaysPerWeek,
                        onSelectDaysPerWeek = onSelectDaysPerWeek,
                        sessionDurationMinutes = uiState.sessionDurationMinutes,
                        onSelectDurationMinutes = onSelectDurationMinutes
                    )

                    else -> WelcomeStep3MuscleFocusAndArchetype(
                        selectedMuscles = uiState.selectedMuscles,
                        isBackView = uiState.isBackView,
                        selectedBodyParts = uiState.selectedBodyParts,
                        onToggleMuscle = onToggleMuscle,
                        onToggleViewSide = onToggleViewSide,
                        onToggleBodyPartCategory = onToggleBodyPartCategory,
                        gender = uiState.gender
                    )
                }
            }
        }
    }
}
