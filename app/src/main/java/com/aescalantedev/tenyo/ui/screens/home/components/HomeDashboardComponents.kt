package com.aescalantedev.tenyo.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.R
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.CategorySummary
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.UserPreferences
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.ui.components.CategoryVisuals
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.components.TenyoCircularProgressRing
import com.aescalantedev.tenyo.ui.components.TenyoExerciseMedia
import com.aescalantedev.tenyo.ui.components.TenyoMetricChipCard
import com.aescalantedev.tenyo.ui.components.TenyoPrimaryButton
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoVoidBlack
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.User
import java.util.Locale

/**
 * Tarjeta de resumen biometrico y zonas musculares prioritarias del atleta en el Dashboard (Diseno Image 2).
 * Incluye Peso, Estatura, IMC con iconos limpios y badges inferiores de frecuencia semanal y foco muscular.
 */
@Composable
fun HomeActiveProfileCard(
    preferences: UserPreferences,
    onSelectFocusCategory: (BodyPartCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // 3 Columnas principales de biometria con iconos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Peso
                ProfileMetricItem(
                    icon = Lucide.Dumbbell,
                    label = "Peso",
                    value = "${preferences.weightKg.toInt()} kg",
                    modifier = Modifier.weight(1f)
                )

                // Estatura
                ProfileMetricItem(
                    icon = Lucide.User,
                    label = "Estatura",
                    value = "${preferences.heightCm} cm",
                    modifier = Modifier.weight(1f)
                )

                // IMC
                ProfileMetricItem(
                    icon = Lucide.Flame,
                    label = "IMC",
                    value = String.format(Locale.US, "%.1f", preferences.bmi),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pills inferiores: Dias por semana y Enfoque muscular
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pill 1: Dias de entrenamiento
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = TenyoSurfaceContainerHigh,
                    border = BorderStroke(1.dp, TenyoOutlineSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = null,
                            tint = TenyoJadePrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${preferences.trainingDaysPerWeek} días/sem",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = TenyoTextPrimary
                        )
                    }
                }

                // Pill 2: Foco muscular
                val focusLabel = if (preferences.focusBodyParts.isNotEmpty()) {
                    "Foco: " + preferences.focusBodyParts.take(2).joinToString(" · ") { it.nameEs }
                } else {
                    "Foco: Cuerpo completo"
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = TenyoSurfaceContainerHigh,
                    border = BorderStroke(1.dp, TenyoOutlineSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Dumbbell,
                            contentDescription = null,
                            tint = TenyoJadePrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = focusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = TenyoTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = TenyoSurfaceContainerHigh,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TenyoJadePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TenyoTextTertiary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = TenyoTextPrimary
            )
        }
    }
}

/**
 * Tarjeta compacta de categoria muscular para el carrusel horizontal del Dashboard.
 *
 * Utiliza los renders 3D anatómicos de alta fidelidad alojados en res/drawable
 * para una coherencia visual absoluta con la pantalla de Categorías.
 */
@Composable
fun HomeCategoryChipCard(
    summary: CategorySummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawableRes = when (summary.category) {
        BodyPartCategory.CHEST -> R.drawable.cat_muscle_chest
        BodyPartCategory.BACK -> R.drawable.cat_muscle_back
        BodyPartCategory.SHOULDERS -> R.drawable.cat_muscle_shoulders
        BodyPartCategory.UPPER_ARMS -> R.drawable.cat_muscle_biceps
        BodyPartCategory.LOWER_ARMS -> R.drawable.cat_muscle_forearms
        BodyPartCategory.UPPER_LEGS -> R.drawable.cat_muscle_quads
        BodyPartCategory.LOWER_LEGS -> R.drawable.cat_muscle_calves
        BodyPartCategory.WAIST -> R.drawable.cat_muscle_abs
        BodyPartCategory.CARDIO -> R.drawable.cat_muscle_quads
        BodyPartCategory.NECK -> R.drawable.cat_muscle_neck
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TenyoVoidBlack),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = drawableRes),
                    contentDescription = summary.category.nameEs,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = summary.category.nameEs,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${summary.exerciseCount} ejercicios",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextTertiary
                )
            }
        }
    }
}

/**
 * Tarjeta Hero de "Tu entrenamiento de hoy" en el Dashboard principal.
 *
 * Consume [TenyoCircularProgressRing] y [TenyoPrimaryButton].
 */
@Composable
fun TodayWorkoutHeroCard(
    activePlan: WorkoutPlan?,
    onStartWorkout: () -> Unit,
    onViewPlanDetail: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (activePlan == null) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = TenyoSurfaceContainer,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .clickable(onClick = onStartWorkout)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TenyoBadge(
                    text = "TU ENTRENAMIENTO",
                    tone = TenyoBadgeTone.NEUTRAL
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sin rutina activa",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Configura tu plan de entrenamiento semanal o crea una rutina desde cero.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                TenyoPrimaryButton(
                    text = "Configurar Rutina",
                    onClick = onStartWorkout,
                    leadingIcon = Lucide.Plus
                )
            }
        }
        return
    }

    val planTitle = activePlan.title
    val planSubtitle = activePlan.subtitle
    val exercisesCount = activePlan.exercises.size.takeIf { it > 0 } ?: 0
    val durationMinutes = activePlan.estimatedMinutesPerSession
    val estimatedKcal = (durationMinutes * 6.5).toInt().coerceAtLeast(180)
    val progress = activePlan.progressFraction
    val percentageInt = (progress * 100).toInt().coerceIn(0, 100)

    val onAction = onViewPlanDetail ?: onStartWorkout

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onAction)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TenyoBadge(
                        text = "TU ENTRENAMIENTO DE HOY",
                        tone = TenyoBadgeTone.JADE
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = planTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = TenyoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = planSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                TenyoCircularProgressRing(
                    progressFraction = percentageInt / 100f,
                    centerLabel = "$percentageInt%"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Dumbbell,
                        contentDescription = null,
                        tint = TenyoTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$exercisesCount ejercicios",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Clock,
                        contentDescription = null,
                        tint = TenyoTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$durationMinutes min",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Flame,
                        contentDescription = null,
                        tint = TenyoCrimsonTertiary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$estimatedKcal kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TenyoPrimaryButton(
                text = "Ver rutina e iniciar",
                onClick = onAction,
                leadingIcon = Lucide.Play
            )
        }
    }
}

/**
 * Tarjeta de fila de ejercicio destacado en el Dashboard o listado compacto.
 */
@Composable
fun FeaturedExerciseRowCard(
    exercise: Exercise,
    languageCode: String,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TenyoExerciseMedia(
                mediaUrl = exercise.imageUrl,
                contentDescription = exercise.name,
                modifier = Modifier.size(64.dp),
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
                    text = "${exercise.bodyPart.nameEs} · ${exercise.localizedTargetMuscle(languageCode)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TenyoBadge(
                        text = exercise.localizedEquipment(languageCode),
                        tone = TenyoBadgeTone.NEUTRAL
                    )
                    TenyoBadge(
                        text = "${exercise.defaultSets} x ${exercise.defaultReps}",
                        tone = TenyoBadgeTone.JADE
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = Lucide.Heart,
                    contentDescription = "Favorito",
                    tint = if (exercise.isFavorite) TenyoCrimsonTertiary else TenyoTextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
