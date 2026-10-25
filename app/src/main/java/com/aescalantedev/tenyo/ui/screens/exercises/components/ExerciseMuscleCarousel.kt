package com.aescalantedev.tenyo.ui.screens.exercises.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.R
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoVoidBlack
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide

/**
 * Modelo para las opciones musculares del carrusel con sus respectivos renders 3D de Nano Banana.
 * Cada opción tiene un identificador único [id] y un filtro opcional [targetMuscle] para evitar
 * colisiones entre músculos de la misma región (ej: bíceps vs tríceps, quads vs isquios vs glúteos).
 */
data class CarouselMuscleOption(
    val id: String,
    val labelEs: String,
    val category: BodyPartCategory,
    val targetMuscle: String? = null,
    @DrawableRes val drawableRes: Int
)

/**
 * Lista de opciones musculares mostradas en el carrusel horizontal superior.
 */
val CAROUSEL_MUSCLE_OPTIONS: List<CarouselMuscleOption> = listOf(
    CarouselMuscleOption("chest", "Pecho", BodyPartCategory.CHEST, null, R.drawable.cat_muscle_chest),
    CarouselMuscleOption("biceps", "Bíceps", BodyPartCategory.UPPER_ARMS, "biceps", R.drawable.cat_muscle_biceps),
    CarouselMuscleOption("triceps", "Tríceps", BodyPartCategory.UPPER_ARMS, "triceps", R.drawable.cat_muscle_triceps),
    CarouselMuscleOption("back", "Espalda", BodyPartCategory.BACK, null, R.drawable.cat_muscle_back),
    CarouselMuscleOption("shoulders", "Hombros", BodyPartCategory.SHOULDERS, null, R.drawable.cat_muscle_shoulders),
    CarouselMuscleOption("abs", "Abdomen", BodyPartCategory.WAIST, "abs", R.drawable.cat_muscle_abs),
    CarouselMuscleOption("quads", "Cuádriceps", BodyPartCategory.UPPER_LEGS, "quads", R.drawable.cat_muscle_quads),
    CarouselMuscleOption("hamstrings", "Isquios", BodyPartCategory.UPPER_LEGS, "hamstrings", R.drawable.cat_muscle_hamstrings),
    CarouselMuscleOption("glutes", "Glúteos", BodyPartCategory.UPPER_LEGS, "glutes", R.drawable.cat_muscle_glutes),
    CarouselMuscleOption("calves", "Pantorrillas", BodyPartCategory.LOWER_LEGS, "calves", R.drawable.cat_muscle_calves),
    CarouselMuscleOption("forearms", "Antebrazos", BodyPartCategory.LOWER_ARMS, "forearms", R.drawable.cat_muscle_forearms),
    CarouselMuscleOption("neck", "Cuello", BodyPartCategory.NECK, null, R.drawable.cat_muscle_neck)
)

/**
 * Carrusel horizontal superior de grupos musculares y acceso directo a favoritos.
 * Utiliza los renders anatómicos 3D y resalta la selección con el acento del tema activo [TenyoJadePrimary].
 */
@Composable
fun ExerciseMuscleCarousel(
    selectedOptionId: String?,
    onlyFavorites: Boolean,
    onToggleOnlyFavorites: () -> Unit,
    onSelectMuscleOption: (CarouselMuscleOption?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Botón de Bookmark / Favoritos en el extremo izquierdo
        item {
            val favBorder = if (onlyFavorites) {
                BorderStroke(2.dp, TenyoJadePrimary)
            } else {
                BorderStroke(1.dp, TenyoOutlineSubtle)
            }
            val favBg = if (onlyFavorites) {
                TenyoJadeContainer.copy(alpha = 0.35f)
            } else {
                TenyoSurfaceContainerLow
            }

            Surface(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onToggleOnlyFavorites),
                shape = RoundedCornerShape(14.dp),
                color = favBg,
                border = favBorder
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.Bookmark,
                        contentDescription = "Filtrar por favoritos",
                        tint = if (onlyFavorites) TenyoJadePrimary else TenyoTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 2. Chip "Todos los músculos" (Mancuerna neutral)
        item {
            val isAllSelected = selectedOptionId == null && !onlyFavorites
            val allBorder = if (isAllSelected) {
                BorderStroke(2.dp, TenyoJadePrimary)
            } else {
                BorderStroke(1.dp, TenyoOutlineSubtle)
            }
            val allBg = if (isAllSelected) {
                TenyoJadeContainer.copy(alpha = 0.35f)
            } else {
                TenyoSurfaceContainerLow
            }
            val allIconTint = if (isAllSelected) TenyoJadePrimary else TenyoTextSecondary

            Surface(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectMuscleOption(null) },
                shape = RoundedCornerShape(14.dp),
                color = allBg,
                border = allBorder
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.Dumbbell,
                        contentDescription = "Todos los músculos",
                        tint = allIconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 3. Fichas individuales de músculos con imágenes 3D de Nano Banana
        items(
            items = CAROUSEL_MUSCLE_OPTIONS,
            key = { it.id }
        ) { option ->
            // La selección se basa en el ID único (ej: "biceps" vs "triceps")
            val isSelected = selectedOptionId == option.id
            val border = if (isSelected) {
                BorderStroke(2.dp, TenyoJadePrimary)
            } else {
                BorderStroke(1.dp, TenyoOutlineSubtle)
            }
            val bg = if (isSelected) {
                TenyoJadeContainer.copy(alpha = 0.35f)
            } else {
                TenyoVoidBlack
            }

            Surface(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        onSelectMuscleOption(if (isSelected) null else option)
                    },
                shape = RoundedCornerShape(14.dp),
                color = bg,
                border = border
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = option.drawableRes),
                        contentDescription = option.labelEs,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}
