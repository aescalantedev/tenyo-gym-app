package com.aescalantedev.tenyo.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.AppLanguage
import com.aescalantedev.tenyo.core.domain.model.AppThemeMode
import com.aescalantedev.tenyo.core.domain.model.WeightUnit
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.components.TenyoSectionHeaderCard
import com.aescalantedev.tenyo.ui.theme.TenyoAmberWarning
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.paletteForThemeMode
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Coffee
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.SlidersHorizontal

/**
 * Pestaña 3: Ajustes Visuales (Tema), Idioma, Unidades de Carga y Asistente.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSettingsAndThemeSection(
    currentThemeMode: AppThemeMode,
    currentLanguageCode: String,
    currentWeightUnit: WeightUnit,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onSelectWeightUnit: (WeightUnit) -> Unit,
    onReopenOnboardingWizard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selector de Tema Visual Minimalista
        TenyoSectionHeaderCard(
            tagText = "APARIENCIA",
            title = "Tema Visual",
            description = "Elige la estética y los colores de acento de la aplicación.",
            icon = Lucide.Palette,
            accentColor = TenyoJadePrimary
        ) {
            AppThemeMode.entries.forEach { mode ->
                val isSelected = currentThemeMode == mode
                val modePalette = paletteForThemeMode(mode)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectThemeMode(mode) },
                    shape = RoundedCornerShape(16.dp),
                    color = modePalette.surfaceContainerLow,
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) modePalette.jadePrimary else modePalette.outlineSubtle
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mode.titleEs,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = modePalette.textPrimary
                            )
                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = modePalette.jadePrimary,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Lucide.Check,
                                            contentDescription = "Activo",
                                            tint = modePalette.onJadePrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = mode.subtitleEs,
                            style = MaterialTheme.typography.bodySmall,
                            color = modePalette.textSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Muestras de color compactas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            modePalette.swatchPreview.forEach { swatchColor ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(20.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    color = swatchColor,
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
                                ) {}
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Selector de Idioma
        TenyoSectionHeaderCard(
            tagText = "IDIOMA",
            title = "Idioma de la Aplicación",
            description = "Español es el idioma oficial predeterminado. Próximamente se integrarán más opciones.",
            icon = Lucide.Globe,
            accentColor = TenyoAmberWarning
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLanguage.entries.forEach { lang ->
                    val isOfficialEs = lang == AppLanguage.ES

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectLanguage(lang.code) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isOfficialEs) {
                            TenyoJadeContainer.copy(alpha = 0.45f)
                        } else {
                            TenyoSurfaceContainerHigh.copy(alpha = 0.4f)
                        },
                        border = BorderStroke(
                            width = if (isOfficialEs) 1.5.dp else 1.dp,
                            color = if (isOfficialEs) TenyoJadePrimary else TenyoOutlineSubtle
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.code.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (isOfficialEs) TenyoJadePrimary else TenyoTextTertiary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOfficialEs) "Español (Actual)" else "${lang.nativeName} (Próximamente)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isOfficialEs) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isOfficialEs) TenyoTextPrimary else TenyoTextTertiary
                            )
                        }
                    }
                }
            }
        }

        // Unidades de Carga y Asistente
        TenyoSectionHeaderCard(
            tagText = "UNIDADES Y ASISTENTE",
            title = "Sistema de Pesas",
            description = "Selecciona la unidad de peso para tus registros y calibración.",
            icon = Lucide.SlidersHorizontal,
            accentColor = TenyoJadePrimary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WeightUnit.entries.forEach { unit ->
                    val selected = currentWeightUnit == unit
                    val isLbs = unit == WeightUnit.LBS
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectWeightUnit(unit) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) TenyoJadeContainer.copy(alpha = 0.45f) else TenyoSurfaceContainerHigh,
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) TenyoJadePrimary else TenyoOutlineSubtle
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isLbs) "Libras (Próximamente)" else unit.labelEs,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (selected) TenyoJadePrimary else if (isLbs) TenyoTextTertiary else TenyoTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onReopenOnboardingWizard),
                shape = RoundedCornerShape(14.dp),
                color = TenyoSurfaceContainerHigh,
                border = BorderStroke(1.dp, TenyoOutline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.RefreshCw,
                        contentDescription = null,
                        tint = TenyoJadePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reiniciar asistente de bienvenida",
                        style = MaterialTheme.typography.labelLarge,
                        color = TenyoTextPrimary
                    )
                }
            }
        }

        // Sección de Apoyo al Desarrollador (Buy Me a Coffee)
        val uriHandler = LocalUriHandler.current
        val buyMeACoffeeUrl = "https://buymeacoffee.com/aescalante.dev"

        TenyoSectionHeaderCard(
            tagText = "COMUNIDAD & APOYO",
            title = "Apoyar al Desarrollador",
            description = "Tenyo es un proyecto independiente y libre de publicidad. Si la app te ayuda en tus entrenamientos diarios, puedes invitarle un café al desarrollador para apoyar las futuras mejoras.",
            icon = Lucide.Coffee,
            accentColor = Color(0xFFFFDD00)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { uriHandler.openUri(buyMeACoffeeUrl) },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1B1A14),
                border = BorderStroke(1.dp, Color(0xFFFFDD00).copy(alpha = 0.40f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFDD00),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Coffee,
                                    contentDescription = "Buy Me a Coffee",
                                    tint = Color(0xFF121212),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Invítame un café",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFDD00)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "buymeacoffee.com/aescalante.dev",
                                style = MaterialTheme.typography.bodySmall,
                                color = TenyoTextTertiary
                            )
                        }
                    }
                    Icon(
                        imageVector = Lucide.ExternalLink,
                        contentDescription = "Abrir enlace",
                        tint = Color(0xFFFFDD00),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Tarjeta de Versión y Estado del Sistema
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TENYO PROTOCOL",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TenyoBadge(
                        text = "v1.0.0",
                        tone = TenyoBadgeTone.JADE
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Entrenamiento de fuerza y disciplina personal",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextTertiary
                )
            }
        }
    }
}
