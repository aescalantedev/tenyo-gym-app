package com.aescalantedev.tenyo.ui.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Plus

/**
 * Control stepper reutilizable de diseno minimalista para seleccion numerica (Edad, Estatura, Peso).
 * Incluye cabecera con icono + etiqueta, y un contenedor con botones [-] y [+] a los extremos.
 */
@Composable
fun TenyoStepperInput(
    icon: ImageVector,
    label: String,
    value: Int,
    unit: String,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
    step: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TenyoTextTertiary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = TenyoTextSecondary
            )
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TenyoSurfaceContainer,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Boton de decremento [-]
                val canDecrement = value > range.first
                Surface(
                    shape = CircleShape,
                    color = if (canDecrement) TenyoSurfaceContainerHigh else TenyoSurfaceContainer,
                    border = BorderStroke(
                        1.dp,
                        if (canDecrement) TenyoOutline else TenyoOutlineSubtle.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable(enabled = canDecrement) {
                            onValueChange((value - step).coerceIn(range))
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.Minus,
                            contentDescription = "Disminuir",
                            tint = if (canDecrement) TenyoTextPrimary else TenyoTextTertiary.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Valor central formateado
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$value",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = TenyoTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = TenyoTextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Boton de incremento [+]
                val canIncrement = value < range.last
                Surface(
                    shape = CircleShape,
                    color = if (canIncrement) TenyoSurfaceContainerHigh else TenyoSurfaceContainer,
                    border = BorderStroke(
                        1.dp,
                        if (canIncrement) TenyoOutline else TenyoOutlineSubtle.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable(enabled = canIncrement) {
                            onValueChange((value + step).coerceIn(range))
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = "Incrementar",
                            tint = if (canIncrement) TenyoTextPrimary else TenyoTextTertiary.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
