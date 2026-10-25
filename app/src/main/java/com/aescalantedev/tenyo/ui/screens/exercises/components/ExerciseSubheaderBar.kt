package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseSortOrder
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.List
import com.composables.icons.lucide.Lucide

/**
 * Barra de subtítulo para ordenar y cambiar la vista de la lista (Grid 2 columnas vs Lista).
 */
@Composable
fun ExerciseSubheaderBar(
    sortOrder: ExerciseSortOrder,
    isGridView: Boolean,
    onSelectSortOrder: (ExerciseSortOrder) -> Unit,
    onToggleViewMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selector de ordenamiento estilo chip/dropdown
        Box {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showSortMenu = true },
                shape = RoundedCornerShape(10.dp),
                color = TenyoSurfaceContainerLow,
                border = BorderStroke(1.dp, TenyoOutlineSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = sortOrder.labelEs,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                    Icon(
                        imageVector = Lucide.ChevronDown,
                        contentDescription = "Cambiar orden",
                        tint = TenyoTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false },
                containerColor = TenyoSurfaceContainerHigh
            ) {
                ExerciseSortOrder.entries.forEach { order ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = order.labelEs,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (order == sortOrder) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (order == sortOrder) TenyoJadePrimary else TenyoTextPrimary
                            )
                        },
                        onClick = {
                            showSortMenu = false
                            onSelectSortOrder(order)
                        },
                        trailingIcon = if (order == sortOrder) {
                            {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = TenyoJadePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }

        // Alternador de modo cuadrícula o lista
        Surface(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onToggleViewMode),
            shape = CircleShape,
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isGridView) Lucide.List else Lucide.LayoutGrid,
                    contentDescription = if (isGridView) "Ver como lista" else "Ver como cuadrícula",
                    tint = TenyoTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
