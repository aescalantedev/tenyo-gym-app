package com.aescalantedev.tenyo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.Activity
import com.composables.icons.lucide.Award
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Shield
import com.composables.icons.lucide.Swords
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Zap

/**
 * Asigna un icono vectorial de Lucide y un color de acento a cada categoria muscular ([BodyPartCategory]).
 */
object CategoryVisuals {
    fun iconFor(category: BodyPartCategory): ImageVector = when (category) {
        BodyPartCategory.CHEST -> Lucide.Shield
        BodyPartCategory.BACK -> Lucide.Swords
        BodyPartCategory.SHOULDERS -> Lucide.Target
        BodyPartCategory.UPPER_ARMS -> Lucide.Dumbbell
        BodyPartCategory.LOWER_ARMS -> Lucide.Zap
        BodyPartCategory.UPPER_LEGS -> Lucide.Flame
        BodyPartCategory.LOWER_LEGS -> Lucide.Activity
        BodyPartCategory.WAIST -> Lucide.Award
        BodyPartCategory.CARDIO -> Lucide.Flame
        BodyPartCategory.NECK -> Lucide.Shield
    }

    @Composable
    fun accentColorFor(category: BodyPartCategory): Color = when (category) {
        BodyPartCategory.CHEST,
        BodyPartCategory.UPPER_ARMS,
        BodyPartCategory.WAIST -> TenyoJadePrimary

        BodyPartCategory.BACK,
        BodyPartCategory.SHOULDERS,
        BodyPartCategory.LOWER_ARMS -> TenyoVioletSecondary

        BodyPartCategory.UPPER_LEGS,
        BodyPartCategory.LOWER_LEGS,
        BodyPartCategory.CARDIO,
        BodyPartCategory.NECK -> TenyoCrimsonTertiary
    }
}
