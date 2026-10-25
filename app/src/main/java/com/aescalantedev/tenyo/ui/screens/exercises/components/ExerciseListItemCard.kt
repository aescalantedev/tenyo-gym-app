package com.aescalantedev.tenyo.ui.screens.exercises.components

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.ui.components.CategoryVisuals
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.components.TenyoExerciseMedia
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleHelp
import com.composables.icons.lucide.Lucide

/**
 * Tarjeta de ejercicio dentro del catálogo para la vista en lista vertical (1 columna).
 * Soporta modo selección múltiple y acceso a ficha técnica con el icono '?'.
 */
@Composable
fun ExerciseListItemCard(
    exercise: Exercise,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onQuickInfoClick: () -> Unit = onClick,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) TenyoSurfaceContainerHigh else TenyoSurfaceContainerLow,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) TenyoJadePrimary else TenyoOutlineSubtle
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TenyoExerciseMedia(
                mediaUrl = exercise.imageUrl,
                contentDescription = exercise.name,
                modifier = Modifier.size(68.dp),
                shape = RoundedCornerShape(12.dp),
                fallbackIcon = CategoryVisuals.iconFor(exercise.bodyPart),
                iconTint = CategoryVisuals.accentColorFor(exercise.bodyPart)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${exercise.localizedTargetMuscle()} · ${exercise.localizedEquipment()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TenyoBadge(
                        text = "${exercise.defaultSets} series × ${exercise.defaultReps}",
                        tone = TenyoBadgeTone.JADE
                    )
                    if (exercise.isCustom) {
                        TenyoBadge(
                            text = "Personalizado",
                            tone = TenyoBadgeTone.VIOLET
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Lucide.Bookmark,
                        contentDescription = "Marcar favorito",
                        tint = if (exercise.isFavorite) TenyoJadePrimary else TenyoTextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isSelectionMode && isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = TenyoJadePrimary,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClick)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Seleccionado",
                                tint = TenyoOnJadePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    IconButton(onClick = onQuickInfoClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Lucide.CircleHelp,
                            contentDescription = "Ficha del ejercicio",
                            tint = TenyoTextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
