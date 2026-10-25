package com.aescalantedev.tenyo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary

/**
 * Selector segmentado horizontal de pildoras (Segmented Picker / Control de pestanas).
 * Usado para Sexo, Nivel de Actividad, Dias por semana y Tiempo por sesion.
 */
@Composable
fun <T> TenyoSegmentedPicker(
    items: List<T>,
    selectedItem: T,
    onSelect: (T) -> Unit,
    labelMapper: (T) -> String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    label: String? = null,
    trailingLabel: String? = null,
    height: Dp = 48.dp
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null || icon != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TenyoTextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = TenyoTextSecondary
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = TenyoSurfaceContainer,
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier
                    .weight(1f)
                    .height(height)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { item ->
                        val isSelected = item == selectedItem
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) TenyoJadePrimary else Color.Transparent,
                            animationSpec = tween(180),
                            label = "pillBgColor"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) TenyoOnJadePrimary else TenyoTextSecondary,
                            animationSpec = tween(180),
                            label = "pillTextColor"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(height - 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelect(item) },
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = bgColor,
                                modifier = Modifier.matchParentSize()
                            ) {}
                            Text(
                                text = labelMapper(item),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = textColor
                            )
                        }
                    }
                }
            }

            if (trailingLabel != null) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = trailingLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = TenyoTextSecondary
                )
            }
        }
    }
}
