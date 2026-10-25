package com.aescalantedev.tenyo.ui.screens.welcome.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.ActivityLevel
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.ui.components.TenyoSegmentedPicker
import com.aescalantedev.tenyo.ui.components.TenyoStepperInput
import com.aescalantedev.tenyo.ui.components.TenyoTextInput
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Ruler
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Zap

/**
 * Paso 1 del Onboarding: Perfil Inicial del Atleta (Diseno premium-minimal).
 * Incluye Nombre de Usuario, Sexo biologico, Steppers de Edad, Estatura y Peso, y Nivel de Actividad.
 */
@Composable
fun WelcomeStep1Biometrics(
    athleteName: String,
    onNameChange: (String) -> Unit,
    gender: UserGender,
    onGenderChange: (UserGender) -> Unit,
    ageYears: Int,
    onAgeChange: (Int) -> Unit,
    heightCm: Int,
    onHeightChange: (Int) -> Unit,
    weightKg: Int,
    onWeightChange: (Int) -> Unit,
    activityLevel: ActivityLevel,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Titulo con acento en purpura: "Empecemos por ti."
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            val annotatedTitle = buildAnnotatedString {
                append("Empecemos por ")
                withStyle(
                    style = SpanStyle(
                        color = TenyoJadePrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("ti.")
                }
            }

            Text(
                text = annotatedTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Datos básicos para adaptar tu entrenamiento.",
                style = MaterialTheme.typography.bodyMedium,
                color = TenyoTextSecondary
            )
        }

        // Nombre de usuario / alias
        TenyoTextInput(
            value = athleteName,
            onValueChange = onNameChange,
            label = "Nombre o alias",
            placeholder = "Ej. Toji / Alex",
            icon = Lucide.User
        )

        // Sexo biologico
        TenyoSegmentedPicker(
            label = "Sexo biológico",
            icon = Lucide.User,
            items = listOf(UserGender.MALE, UserGender.FEMALE),
            selectedItem = gender,
            onSelect = onGenderChange,
            labelMapper = { it.labelEs }
        )

        // Control Stepper de Edad
        TenyoStepperInput(
            icon = Lucide.Calendar,
            label = "Edad",
            value = ageYears,
            unit = "años",
            range = 14..90,
            onValueChange = onAgeChange
        )

        // Control Stepper de Estatura
        TenyoStepperInput(
            icon = Lucide.Ruler,
            label = "Estatura",
            value = heightCm,
            unit = "cm",
            range = 130..220,
            onValueChange = onHeightChange
        )

        // Control Stepper de Peso actual
        TenyoStepperInput(
            icon = Lucide.Dumbbell,
            label = "Peso actual",
            value = weightKg,
            unit = "kg",
            range = 40..150,
            onValueChange = onWeightChange
        )

        // Selector de Nivel de actividad cotidiana
        TenyoSegmentedPicker(
            label = "Nivel de actividad",
            icon = Lucide.Zap,
            items = ActivityLevel.entries,
            selectedItem = activityLevel,
            onSelect = onActivityLevelChange,
            labelMapper = { it.labelEs }
        )

        // Nota al pie
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp, start = 2.dp)
        ) {
            Icon(
                imageVector = Lucide.Info,
                contentDescription = null,
                tint = TenyoTextTertiary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Puedes editar estos datos después.",
                style = MaterialTheme.typography.bodySmall,
                color = TenyoTextTertiary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
