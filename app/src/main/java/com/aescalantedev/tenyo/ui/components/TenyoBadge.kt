package com.aescalantedev.tenyo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoAmberWarning
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonContainer
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutline
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletContainer
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary

/**
 * Variante cromatica para las insignias [TenyoBadge].
 */
enum class TenyoBadgeTone {
    JADE,
    VIOLET,
    CRIMSON,
    AMBER,
    NEUTRAL
}

/**
 * Insignia compacta para mostrar etiquetas de grupo muscular, rango fisico o nivel de exigencia.
 */
@Composable
fun TenyoBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: TenyoBadgeTone = TenyoBadgeTone.JADE
) {
    val (bgColor, textColor, borderColor) = when (tone) {
        TenyoBadgeTone.JADE -> Triple(
            TenyoJadeContainer.copy(alpha = 0.7f),
            TenyoJadePrimary,
            TenyoJadePrimary.copy(alpha = 0.35f)
        )
        TenyoBadgeTone.VIOLET -> Triple(
            TenyoVioletContainer.copy(alpha = 0.7f),
            TenyoVioletSecondary,
            TenyoVioletSecondary.copy(alpha = 0.35f)
        )
        TenyoBadgeTone.CRIMSON -> Triple(
            TenyoCrimsonContainer.copy(alpha = 0.7f),
            TenyoCrimsonTertiary,
            TenyoCrimsonTertiary.copy(alpha = 0.35f)
        )
        TenyoBadgeTone.AMBER -> Triple(
            TenyoAmberWarning.copy(alpha = 0.16f),
            TenyoAmberWarning,
            TenyoAmberWarning.copy(alpha = 0.35f)
        )
        TenyoBadgeTone.NEUTRAL -> Triple(
            TenyoSurfaceContainerHigh,
            TenyoTextSecondary,
            TenyoOutline
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
