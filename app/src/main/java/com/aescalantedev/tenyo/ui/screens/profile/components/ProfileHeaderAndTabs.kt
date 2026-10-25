package com.aescalantedev.tenyo.ui.screens.profile.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.PhysicalRank
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileTab
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileUiState
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Zap
import java.util.Locale

/**
 * Encabezado minimalista del Atleta: Identidad, Rango kanji, tira métrica y selector de pestañas segmentado.
 */
@Composable
fun ProfileHeaderAndTabs(
    uiState: ProfileUiState,
    onSelectTab: (ProfileTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRank = uiState.progressSummary?.currentRank ?: PhysicalRank.GRADE_2

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tarjeta de Identidad Superior
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ATLETA TENYO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoJadePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.athleteName.ifBlank { uiState.preferences.userName },
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TenyoTextPrimary
                        )
                    }

                    TenyoBadge(
                        text = currentRank.titleEs,
                        tone = TenyoBadgeTone.JADE
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tira métrica minimalista: Peso · Estatura · IMC
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Peso
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PESO",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoTextTertiary
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f kg", uiState.weightKg),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = TenyoJadePrimary
                            )
                        }
                    }

                    // Talla
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ESTATURA",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoTextTertiary
                            )
                            Text(
                                text = "${uiState.heightCm.toInt()} cm",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = TenyoVioletSecondary
                            )
                        }
                    }

                    // IMC
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "IMC",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoTextTertiary
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f", uiState.liveBmi),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Pestañas segmentadas minimalistas
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProfileTab.entries.forEach { tab ->
                    val isSelected = uiState.selectedTab == tab
                    val tabBg by animateColorAsState(
                        targetValue = if (isSelected) TenyoJadePrimary.copy(alpha = 0.18f) else Color.Transparent,
                        animationSpec = tween(150),
                        label = "tabBg"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectTab(tab) },
                        shape = RoundedCornerShape(12.dp),
                        color = tabBg,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) TenyoJadePrimary else Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (tab) {
                                ProfileTab.BIOMETRICS_AND_MUSCLES -> Lucide.User
                                ProfileTab.PROTOCOL -> Lucide.Zap
                                ProfileTab.SETTINGS_AND_THEME -> Lucide.Palette
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) TenyoJadePrimary else TenyoTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.titleEs,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) TenyoJadePrimary else TenyoTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
