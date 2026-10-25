package com.aescalantedev.tenyo.ui.screens.plans.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus

/**
 * Botón de acción flotante (FAB) con diseño squircle idéntico al modelo de referencia.
 *
 * Responsabilidad única:
 * - Botón de acento con icono "+" para disparar la selección de ejercicios o creación de planes.
 */
@Composable
fun PlanFloatingAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = HtmlAppPrimary,
        modifier = modifier
            .size(64.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = HtmlAppPrimary,
                spotColor = HtmlAppPrimary
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Lucide.Plus,
                contentDescription = "Añadir",
                tint = TenyoOnJadePrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
