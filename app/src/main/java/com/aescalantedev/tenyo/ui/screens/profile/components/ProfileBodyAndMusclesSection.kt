package com.aescalantedev.tenyo.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.ui.components.TenyoMuscleSelectChip
import com.aescalantedev.tenyo.ui.components.TenyoPrimaryButton
import com.aescalantedev.tenyo.ui.components.TenyoSectionHeaderCard
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileUiState
import com.aescalantedev.tenyo.ui.screens.welcome.components.TojiMuscleOption
import com.aescalantedev.tenyo.ui.screens.welcome.components.WelcomeTojiMuscleViewer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.Activity
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Target
import java.util.Locale

private data class MuscleSelectorItem(
    val category: BodyPartCategory,
    val labelEs: String
)

private val ALL_PROFILE_MUSCLE_CATEGORIES = listOf(
    MuscleSelectorItem(BodyPartCategory.CHEST, "Pecho"),
    MuscleSelectorItem(BodyPartCategory.BACK, "Espalda"),
    MuscleSelectorItem(BodyPartCategory.SHOULDERS, "Hombros"),
    MuscleSelectorItem(BodyPartCategory.UPPER_ARMS, "Brazos"),
    MuscleSelectorItem(BodyPartCategory.LOWER_ARMS, "Antebrazos"),
    MuscleSelectorItem(BodyPartCategory.WAIST, "Abdomen"),
    MuscleSelectorItem(BodyPartCategory.UPPER_LEGS, "Piernas"),
    MuscleSelectorItem(BodyPartCategory.LOWER_LEGS, "Pantorrillas")
)

/**
 * Pestaña 1: Físico, Biometría y Visor Anatómico Interactivo de Toji (Rive).
 */
@Composable
fun ProfileBodyAndMusclesSection(
    uiState: ProfileUiState,
    onUpdateAthleteName: (String) -> Unit,
    onUpdateAgeText: (String) -> Unit,
    onUpdateWeightKg: (Float) -> Unit,
    onUpdateHeightCm: (Float) -> Unit,
    onToggleMuscle: (TojiMuscleOption) -> Unit,
    onToggleViewSide: () -> Unit,
    onToggleBodyPartCategory: (BodyPartCategory) -> Unit,
    onSaveProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta de Datos Biométricos
        TenyoSectionHeaderCard(
            tagText = "BIOMETRÍA Y ESTRUCTURA",
            title = "Datos Físicos del Atleta",
            description = "Ajusta tu nombre, edad, peso y estatura para el cálculo metabólico.",
            icon = Lucide.Activity,
            accentColor = TenyoJadePrimary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(0.68f)) {
                    Text(
                        text = "NOMBRE O ALIAS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextTertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.athleteName,
                        onValueChange = onUpdateAthleteName,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TenyoSurfaceContainerHigh,
                            unfocusedContainerColor = TenyoSurfaceContainerHigh,
                            focusedBorderColor = TenyoJadePrimary,
                            unfocusedBorderColor = TenyoOutlineSubtle,
                            focusedTextColor = TenyoTextPrimary,
                            unfocusedTextColor = TenyoTextPrimary
                        )
                    )
                }

                Column(modifier = Modifier.weight(0.32f)) {
                    Text(
                        text = "EDAD",
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextTertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.ageText,
                        onValueChange = onUpdateAgeText,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TenyoSurfaceContainerHigh,
                            unfocusedContainerColor = TenyoSurfaceContainerHigh,
                            focusedBorderColor = TenyoJadePrimary,
                            unfocusedBorderColor = TenyoOutlineSubtle,
                            focusedTextColor = TenyoTextPrimary,
                            unfocusedTextColor = TenyoTextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Peso Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PESO CORPORAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = TenyoTextTertiary
                )
                Text(
                    text = String.format(Locale.US, "%.1f kg", uiState.weightKg),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = TenyoJadePrimary
                )
            }
            Slider(
                value = uiState.weightKg,
                onValueChange = onUpdateWeightKg,
                valueRange = 40f..140f,
                colors = SliderDefaults.colors(
                    thumbColor = TenyoJadePrimary,
                    activeTrackColor = TenyoJadePrimary,
                    inactiveTrackColor = TenyoSurfaceContainerHigh
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Estatura Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TALLA / ESTATURA",
                    style = MaterialTheme.typography.labelSmall,
                    color = TenyoTextTertiary
                )
                Text(
                    text = "${uiState.heightCm.toInt()} cm",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = TenyoVioletSecondary
                )
            }
            Slider(
                value = uiState.heightCm,
                onValueChange = onUpdateHeightCm,
                valueRange = 140f..210f,
                colors = SliderDefaults.colors(
                    thumbColor = TenyoVioletSecondary,
                    activeTrackColor = TenyoVioletSecondary,
                    inactiveTrackColor = TenyoSurfaceContainerHigh
                )
            )
        }

        // Visor Anatómico Rive (Toji / Maki según género)
        val characterName = if (uiState.preferences.gender == UserGender.FEMALE) "Maki" else "Toji"
        TenyoSectionHeaderCard(
            tagText = "ENFOQUE MUSCULAR INTERACTIVO",
            title = "Zonas de Prioridad Muscular",
            description = "Toca a $characterName o los selectores para definir tus grupos musculares focales.",
            icon = Lucide.Target,
            accentColor = TenyoJadePrimary
        ) {
            // Visor Rive interactivo
            WelcomeTojiMuscleViewer(
                selectedMuscles = uiState.selectedMuscles,
                isBackView = uiState.isBackView,
                onToggleMuscle = onToggleMuscle,
                onToggleViewSide = onToggleViewSide,
                gender = uiState.preferences.gender,
                height = 360.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Rejilla de 8 categorías musculares (incluyendo Pantorrillas y Antebrazos)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ALL_PROFILE_MUSCLE_CATEGORIES.chunked(2).forEach { rowPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowPair.forEach { item ->
                            val isSelected = uiState.selectedBodyParts.contains(item.category)
                            TenyoMuscleSelectChip(
                                label = item.labelEs,
                                isSelected = isSelected,
                                onClick = { onToggleBodyPartCategory(item.category) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TenyoPrimaryButton(
                text = if (uiState.isSaving) "Guardando..." else "Guardar cambios físicos",
                enabled = !uiState.isSaving,
                onClick = onSaveProfile
            )
        }
    }
}
