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
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.ui.components.TenyoMuscleSelectChip
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Target

private data class MuscleGridCategory(
    val category: BodyPartCategory,
    val labelEs: String
)

private val PRIMARY_MUSCLE_GRID_CATEGORIES = listOf(
    MuscleGridCategory(BodyPartCategory.CHEST, "Pecho"),
    MuscleGridCategory(BodyPartCategory.BACK, "Espalda"),
    MuscleGridCategory(BodyPartCategory.SHOULDERS, "Hombros"),
    MuscleGridCategory(BodyPartCategory.UPPER_ARMS, "Brazos"),
    MuscleGridCategory(BodyPartCategory.LOWER_ARMS, "Antebrazos"),
    MuscleGridCategory(BodyPartCategory.WAIST, "Abdomen"),
    MuscleGridCategory(BodyPartCategory.UPPER_LEGS, "Piernas"),
    MuscleGridCategory(BodyPartCategory.LOWER_LEGS, "Pantorrillas")
)

/**
 * Paso 3 del Onboarding: Enfoque Muscular Interactivo.
 * Estructura agrupada en 2 secciones:
 * - Sección Superior (80% / flexible weight): Título y visor central de Toji Fushiguro expandido.
 * - Sección Inferior (20% / controles): Rejilla de selección de músculos e indicador de prioridad.
 */
@Composable
fun WelcomeStep3MuscleFocusAndArchetype(
    selectedMuscles: Set<TojiMuscleOption>,
    isBackView: Boolean,
    selectedBodyParts: List<BodyPartCategory>,
    onToggleMuscle: (TojiMuscleOption) -> Unit,
    onToggleViewSide: () -> Unit,
    onToggleBodyPartCategory: (BodyPartCategory) -> Unit,
    modifier: Modifier = Modifier,
    gender: UserGender = UserGender.MALE
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // ==========================================
        // SECCIÓN SUPERIOR: ~80% ESPACIO FLEXIBLE
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título y subtítulo
            Column(modifier = Modifier.padding(bottom = 2.dp)) {
                val annotatedTitle = buildAnnotatedString {
                    append("Elige tus ")
                    withStyle(
                        style = SpanStyle(
                            color = TenyoJadePrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("prioridades.")
                    }
                }

                Text(
                    text = annotatedTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TenyoTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Toca los músculos que quieres entrenar más.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TenyoTextSecondary
                )
            }

            // Personaje interactivo (Toji / Maki según género) ocupando todo el alto disponible
            WelcomeTojiMuscleViewer(
                selectedMuscles = selectedMuscles,
                isBackView = isBackView,
                onToggleMuscle = onToggleMuscle,
                onToggleViewSide = onToggleViewSide,
                gender = gender,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // SECCIÓN INFERIOR: ~20% CONTROLES Y CHIPS
        // ==========================================
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rejilla de 2 columnas con los 6 grupos musculares principales
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PRIMARY_MUSCLE_GRID_CATEGORIES.chunked(2).forEach { rowPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowPair.forEach { item ->
                            val isSelected = selectedBodyParts.contains(item.category)
                            TenyoMuscleSelectChip(
                                label = item.labelEs,
                                isSelected = isSelected,
                                onClick = { onToggleBodyPartCategory(item.category) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowPair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Indicador inferior dinámico de prioridad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp, start = 2.dp)
            ) {
                Icon(
                    imageVector = Lucide.Target,
                    contentDescription = null,
                    tint = TenyoJadePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                val priorityLabel = when {
                    selectedMuscles.isEmpty() -> "Selecciona al menos una zona"
                    selectedMuscles.size == 1 -> "Prioridad: ${selectedMuscles.first().labelEs.lowercase()}"
                    else -> {
                        val names = selectedMuscles.take(3).joinToString(" y ") { it.labelEs.lowercase() }
                        "Prioridad: $names"
                    }
                }

                Text(
                    text = priorityLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = TenyoTextSecondary
                )
            }
        }
    }
}
