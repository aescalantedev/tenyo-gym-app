package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary

/**
 * Modelo para representar una tarjeta en la fila horizontal de músculos involucrados.
 */
data class MuscleCardItem(
    val name: String,
    val role: String,
    val isPrimary: Boolean,
    val activationPercentage: Int,
    @DrawableRes val imageRes: Int
)

/**
 * Fila horizontal desplazable de tarjetas musculares con imágenes reales de `res/drawable`.
 */
@Composable
fun MuscleInvolvedCardsRow(
    primaryMuscleName: String,
    secondaryMuscles: List<String>,
    modifier: Modifier = Modifier
) {
    val items = mutableListOf<MuscleCardItem>()

    // Músculo Principal
    items.add(
        MuscleCardItem(
            name = primaryMuscleName.replaceFirstChar { it.uppercase() },
            role = "Agonista principal",
            isPrimary = true,
            activationPercentage = 85,
            imageRes = MuscleVisuals.drawableForMuscle(primaryMuscleName)
        )
    )

    // Músculos Secundarios
    secondaryMuscles.forEachIndexed { index, sec ->
        val pct = (55 - (index * 12)).coerceAtLeast(25)
        items.add(
            MuscleCardItem(
                name = sec.replaceFirstChar { it.uppercase() },
                role = "Sinergista",
                isPrimary = false,
                activationPercentage = pct,
                imageRes = MuscleVisuals.drawableForMuscle(sec)
            )
        )
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.name + it.role }) { item ->
            MuscleCard(item = item)
        }
    }
}

@Composable
private fun MuscleCard(
    item: MuscleCardItem,
    modifier: Modifier = Modifier
) {
    val borderColor = if (item.isPrimary) {
        TenyoJadePrimary.copy(alpha = 0.50f)
    } else {
        TenyoOutlineSubtle
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.width(150.dp)
    ) {
        Column {
            // Contenedor de la Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                )

                // Scrim degradado inferior para suavizar con el contenedor
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    TenyoSurfaceContainer
                                )
                            )
                        )
                )

                // Badge en la esquina superior
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    TenyoBadge(
                        text = if (item.isPrimary) "Principal" else "Secundario",
                        tone = if (item.isPrimary) TenyoBadgeTone.JADE else TenyoBadgeTone.VIOLET
                    )
                }
            }

            // Información y activación
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.role,
                    style = MaterialTheme.typography.labelSmall,
                    color = TenyoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { item.activationPercentage / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = if (item.isPrimary) TenyoJadePrimary else TenyoVioletSecondary,
                        trackColor = TenyoSurfaceContainerHigh
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${item.activationPercentage}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (item.isPrimary) TenyoJadePrimary else TenyoTextSecondary
                    )
                }
            }
        }
    }
}
