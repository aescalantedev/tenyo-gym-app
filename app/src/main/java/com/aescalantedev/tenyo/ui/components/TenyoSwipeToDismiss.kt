package com.aescalantedev.tenyo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonContainer
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Trash2

/**
 * Envoltorio reutilizable para el gesto Swipe-to-Dismiss de Material 3 adaptado a la estética Tenyo.
 *
 * Soporta:
 * - Deslizamiento de derecha a izquierda (EndToStart) para eliminar (fondo crimson con icono Trash2).
 * - Deslizamiento de izquierda a derecha (StartToEnd) para reemplazar o acción secundaria opcional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenyoSwipeToDismiss(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onSecondaryAction: (() -> Unit)? = null,
    cornerRadius: Dp = 14.dp,
    enableDismissFromStartToEnd: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDismiss()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    if (onSecondaryAction != null) {
                        onSecondaryAction()
                        false
                    } else {
                        false
                    }
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
        enableDismissFromStartToEnd = enableDismissFromStartToEnd && onSecondaryAction != null,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val backgroundColor by animateColorAsState(
                targetValue = when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> TenyoCrimsonContainer
                    SwipeToDismissBoxValue.StartToEnd -> TenyoJadeContainer
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "swipeBackgroundColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Alignment.CenterEnd
                } else {
                    Alignment.CenterStart
                }
            ) {
                if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Lucide.Trash2,
                        contentDescription = "Eliminar",
                        tint = TenyoCrimsonTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (direction == SwipeToDismissBoxValue.StartToEnd && onSecondaryAction != null) {
                    Icon(
                        imageVector = Lucide.RefreshCw,
                        contentDescription = "Reemplazar",
                        tint = TenyoJadePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        content = content
    )
}
