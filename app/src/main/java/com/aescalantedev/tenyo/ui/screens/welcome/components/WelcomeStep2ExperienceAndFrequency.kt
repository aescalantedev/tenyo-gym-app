package com.aescalantedev.tenyo.ui.screens.welcome.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.TrainingGoal
import com.aescalantedev.tenyo.ui.components.TenyoSegmentedPicker
import com.aescalantedev.tenyo.ui.components.TenyoSelectionCard
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.composables.icons.lucide.Activity
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide

/**
 * Paso 2 del Onboarding: Objetivo Principal y Disponibilidad de Tiempo (Diseno premium-minimal).
 * Incluye cards de objetivo (Ganar musculo, Perder grasa, Mejorar fuerza), selector de dias y tiempo por sesion.
 */
@Composable
fun WelcomeStep2ExperienceAndFrequency(
    trainingGoal: TrainingGoal,
    onSelectGoal: (TrainingGoal) -> Unit,
    trainingDaysPerWeek: Int,
    onSelectDaysPerWeek: (Int) -> Unit,
    sessionDurationMinutes: Int,
    onSelectDurationMinutes: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Titulo con acento en purpura: "¿Qué quieres conseguir?"
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            val annotatedTitle = buildAnnotatedString {
                append("¿Qué quieres ")
                withStyle(
                    style = SpanStyle(
                        color = TenyoJadePrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("conseguir?")
                }
            }

            Text(
                text = annotatedTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TenyoTextPrimary
            )
        }

        // Cards de Objetivo Principal
        TrainingGoal.entries.forEach { goal ->
            val icon = when (goal) {
                TrainingGoal.BUILD_MUSCLE -> Lucide.Activity
                TrainingGoal.LOSE_FAT -> Lucide.Flame
                TrainingGoal.BUILD_STRENGTH -> Lucide.Dumbbell
            }

            TenyoSelectionCard(
                icon = icon,
                title = goal.titleEs,
                description = goal.descriptionEs,
                isSelected = trainingGoal == goal,
                onClick = { onSelectGoal(goal) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Selector de Disponibilidad semanal (dias)
        TenyoSegmentedPicker(
            icon = Lucide.Calendar,
            label = "Disponibilidad semanal",
            trailingLabel = "días",
            items = listOf(2, 3, 4, 5, 6),
            selectedItem = trainingDaysPerWeek,
            onSelect = onSelectDaysPerWeek,
            labelMapper = { "$it" }
        )

        // Selector de Tiempo por sesion (minutos)
        TenyoSegmentedPicker(
            icon = Lucide.Clock,
            label = "Tiempo por sesión",
            items = listOf(30, 45, 60),
            selectedItem = sessionDurationMinutes,
            onSelect = onSelectDurationMinutes,
            labelMapper = { "$it min" }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
