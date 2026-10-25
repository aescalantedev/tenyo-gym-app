package com.aescalantedev.tenyo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.ArchetypeProtocol
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletContainer
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Zap

/**
 * Tarjetas reutilizables del sistema de diseno Tenyo para encabezados de seccion,
 * chips de metricas biometricas y selectores de protocolo.
 */

/**
 * Tarjeta contenedora de seccion con icono, subtitulo en mayusculas, titulo principal y descripcion.
 */
@Composable
fun TenyoSectionHeaderCard(
    tagText: String,
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accentColor: Color = TenyoJadePrimary,
    extraContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutline),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tagText,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TenyoTextSecondary
            )
            if (extraContent != null) {
                Spacer(modifier = Modifier.height(14.dp))
                extraContent()
            }
        }
    }
}

/**
 * Chip compacto de metrica (Peso, Talla, IMC, Volumen, etc.) reutilizable en Home, Onboarding y Progreso.
 */
@Composable
fun TenyoMetricChipCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TenyoTextPrimary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = TenyoSurfaceContainerHigh,
        border = BorderStroke(1.dp, TenyoOutlineSubtle)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = valueColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TenyoTextTertiary
            )
        }
    }
}

/**
 * Tarjeta seleccionable de Protocolo (Toji vs Maki) reutilizable entre Onboarding y Perfil.
 */
@Composable
fun TenyoArchetypeSelectionCard(
    protocol: ArchetypeProtocol,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToji = protocol == ArchetypeProtocol.TOJI_SORCERER_KILLER
    val accentColor = if (isToji) TenyoJadePrimary else TenyoVioletSecondary
    val borderColor = if (selected) accentColor else TenyoOutlineSubtle
    val containerColor = if (selected) {
        if (isToji) TenyoJadeContainer.copy(alpha = 0.35f) else TenyoVioletContainer.copy(alpha = 0.35f)
    } else {
        TenyoSurfaceContainer
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.16f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isToji) Lucide.Dumbbell else Lucide.Zap,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = protocol.subtitleEs,
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                shape = CircleShape,
                color = if (selected) accentColor else TenyoSurfaceContainerHigh,
                border = BorderStroke(1.dp, if (selected) accentColor else TenyoOutline),
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (selected) {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = null,
                            tint = TenyoOnJadePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
