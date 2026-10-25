package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.ui.components.TenyoFilterChip
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RotateCcw

/**
 * Modal Bottom Sheet para filtrar ejercicios por equipamiento, grupo muscular y favoritos.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExerciseFilterBottomSheet(
    availableEquipment: List<String>,
    selectedEquipment: String?,
    selectedBodyPart: BodyPartCategory?,
    onlyFavorites: Boolean,
    onSelectEquipment: (String?) -> Unit,
    onSelectBodyPart: (BodyPartCategory?) -> Unit,
    onToggleOnlyFavorites: () -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TenyoSurfaceContainerLow,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Cabecera del BottomSheet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros de Ejercicios",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TenyoTextPrimary
                )

                TextButton(onClick = onClearFilters) {
                    Icon(
                        imageVector = Lucide.RotateCcw,
                        contentDescription = null,
                        tint = TenyoJadePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Restablecer",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoJadePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sección 1: Equipamiento
            Text(
                text = "Por equipamiento",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TenyoFilterChip(
                    label = "Todo el equipo",
                    selected = selectedEquipment == null,
                    onClick = { onSelectEquipment(null) }
                )
                availableEquipment.take(12).forEach { equip ->
                    val isSelected = selectedEquipment.equals(equip, ignoreCase = true)
                    TenyoFilterChip(
                        label = equip.replaceFirstChar { it.uppercase() },
                        selected = isSelected,
                        onClick = {
                            onSelectEquipment(if (isSelected) null else equip)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sección 2: Grupo Muscular
            Text(
                text = "Por grupo muscular",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TenyoFilterChip(
                    label = "Todos los músculos",
                    selected = selectedBodyPart == null,
                    onClick = { onSelectBodyPart(null) }
                )
                BodyPartCategory.entries.forEach { category ->
                    val isSelected = selectedBodyPart == category
                    TenyoFilterChip(
                        label = category.nameEs,
                        selected = isSelected,
                        onClick = {
                            onSelectBodyPart(if (isSelected) null else category)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sección 3: Guardados en Favoritos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleOnlyFavorites)
                    .background(TenyoSurfaceContainer)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Bookmark,
                        contentDescription = null,
                        tint = if (onlyFavorites) TenyoJadePrimary else TenyoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Ver únicamente mis favoritos",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = TenyoTextPrimary
                    )
                }

                if (onlyFavorites) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = null,
                        tint = TenyoJadePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de aplicación
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Aplicar filtros",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
