package com.aescalantedev.tenyo.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.ArchetypeProtocol
import com.aescalantedev.tenyo.core.domain.model.ExperienceLevel
import com.aescalantedev.tenyo.ui.components.TenyoPrimaryButton
import com.aescalantedev.tenyo.ui.components.TenyoSectionHeaderCard
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileUiState
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletContainer
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.Activity
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Zap

/**
 * Pestaña 2: Protocolo Maestro, Nivel de Experiencia y Frecuencia Semanal.
 */
@Composable
fun ProfileProtocolSection(
    uiState: ProfileUiState,
    onSelectArchetype: (ArchetypeProtocol) -> Unit,
    onSelectExperience: (ExperienceLevel) -> Unit,
    onSelectTrainingDays: (Int) -> Unit,
    onSaveProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TenyoSectionHeaderCard(
            tagText = "PROTOCOLO MAESTRO DE ENTRENAMIENTO",
            title = "Arquetipo y Filosofía",
            description = "Elige tu senda de entrenamiento entre la fuerza bruta de Toji o la agilidad de Maki.",
            icon = Lucide.Zap,
            accentColor = TenyoVioletSecondary
        ) {
            ArchetypeProtocol.entries.forEach { protocol ->
                val isSelected = uiState.selectedArchetype == protocol
                val accent = if (protocol == ArchetypeProtocol.TOJI_SORCERER_KILLER) {
                    TenyoJadePrimary
                } else {
                    TenyoVioletSecondary
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectArchetype(protocol) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) accent.copy(alpha = 0.14f) else TenyoSurfaceContainerHigh,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) accent else TenyoOutlineSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = accent.copy(alpha = 0.18f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (protocol == ArchetypeProtocol.TOJI_SORCERER_KILLER) {
                                        Lucide.Dumbbell
                                    } else {
                                        Lucide.Zap
                                    },
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = protocol.titleEs,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = protocol.subtitleEs,
                                style = MaterialTheme.typography.bodySmall,
                                color = TenyoTextSecondary
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Experiencia y Días
        TenyoSectionHeaderCard(
            tagText = "CADENCIA Y EXPERIENCIA",
            title = "Nivel y Frecuencia Semanal",
            description = "Establece los días de entrenamiento semanal y tu rango de veteranía.",
            icon = Lucide.Activity,
            accentColor = TenyoJadePrimary
        ) {
            Text(
                text = "NIVEL DE EXPERIENCIA",
                style = MaterialTheme.typography.labelSmall,
                color = TenyoTextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExperienceLevel.entries.forEach { level ->
                    val selected = uiState.selectedExperience == level
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectExperience(level) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) TenyoJadeContainer.copy(alpha = 0.45f) else TenyoSurfaceContainerHigh,
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) TenyoJadePrimary else TenyoOutlineSubtle
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level.labelEs.substringBefore(" ·"),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (selected) TenyoJadePrimary else TenyoTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "DÍAS DE ENTRENAMIENTO POR SEMANA",
                style = MaterialTheme.typography.labelSmall,
                color = TenyoTextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(3, 4, 5, 6).forEach { days ->
                    val selected = uiState.trainingDays == days
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectTrainingDays(days) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) TenyoVioletContainer.copy(alpha = 0.45f) else TenyoSurfaceContainerHigh,
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) TenyoVioletSecondary else TenyoOutlineSubtle
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$days d/sem",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (selected) TenyoVioletSecondary else TenyoTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TenyoPrimaryButton(
                text = if (uiState.isSaving) "Guardando..." else "Guardar protocolo y cadencia",
                enabled = !uiState.isSaving,
                onClick = onSaveProfile
            )
        }
    }
}
