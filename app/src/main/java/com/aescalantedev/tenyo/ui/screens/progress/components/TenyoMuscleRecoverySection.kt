package com.aescalantedev.tenyo.ui.screens.progress.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.R
import com.aescalantedev.tenyo.core.domain.model.MuscleRecoveryItem
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.ui.screens.welcome.components.TojiMuscleOption
import com.aescalantedev.tenyo.ui.screens.welcome.components.WelcomeTojiMuscleViewer
import com.aescalantedev.tenyo.ui.theme.TenyoAmberWarning
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary

/**
 * Seccion de Recuperacion Muscular con:
 * 1. Modelo interactivo (`WelcomeTojiMuscleViewer`, Toji o Maki según el género) que resalta los musculos trabajados en las sesiones.
 * 2. Tarjetas con las imagenes reales de drawables de cada musculo y su porcentaje real de recuperacion.
 */
@Composable
fun TenyoMuscleRecoverySection(
    muscleRecoveries: List<MuscleRecoveryItem>,
    workedMuscleOptionIds: Set<String>,
    isTojiBackView: Boolean,
    onToggleTojiSide: () -> Unit,
    modifier: Modifier = Modifier,
    gender: UserGender = UserGender.MALE
) {
    val selectedTojiMuscles = remember(workedMuscleOptionIds) {
        workedMuscleOptionIds.mapNotNull { id ->
            TojiMuscleOption.entries.firstOrNull { it.id == id }
        }.toSet()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tarjeta con el Modelo Anatómico (Toji / Maki)
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = TenyoSurfaceContainer,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Mapa Anatómico de Estímulo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
                Text(
                    text = "Músculos entrenados recientemente destacados en verde",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                WelcomeTojiMuscleViewer(
                    selectedMuscles = selectedTojiMuscles,
                    isBackView = isTojiBackView,
                    onToggleMuscle = { /* Informativo */ },
                    onToggleViewSide = onToggleTojiSide,
                    gender = gender,
                    height = 290.dp
                )
            }
        }

        // 2. Encabezado de la lista de recuperacion
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Estado de Recuperación Muscular",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
                Text(
                    text = "Basado en el descanso biológico transcurrido (48 a 72 hrs)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary
                )
            }
        }

        // 3. Tarjetas individuales de cada grupo muscular
        if (muscleRecoveries.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = TenyoSurfaceContainer,
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Todos los grupos musculares están al 100% listos para tu primer entrenamiento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TenyoTextSecondary
                    )
                }
            }
        } else {
            muscleRecoveries.forEach { item ->
                MuscleRecoveryCard(item = item)
            }
        }
    }
}

@Composable
fun MuscleRecoveryCard(
    item: MuscleRecoveryItem,
    modifier: Modifier = Modifier
) {
    val drawableRes = getMuscleDrawableRes(item.drawableAssetName)

    val progressFraction = item.recoveryPercentage / 100f
    val indicatorColor = when {
        item.recoveryPercentage >= 85 -> TenyoJadePrimary
        item.recoveryPercentage >= 50 -> TenyoAmberWarning
        else -> TenyoCrimsonTertiary
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del musculo desde res/drawable
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = item.muscleNameEs,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(TenyoSurfaceContainerHigh)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.muscleNameEs,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = indicatorColor.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, indicatorColor.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = "${item.recoveryPercentage}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = indicatorColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = indicatorColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = indicatorColor,
                    trackColor = TenyoSurfaceContainerHigh
                )

                Spacer(modifier = Modifier.height(6.dp))

                val subLabel = if (item.lastTrainedDateFormatted != null) {
                    "Último estímulo: ${item.lastTrainedDateFormatted} (${item.hoursSinceLastTrained}h)"
                } else {
                    "Sin entrenamientos recientes registrados"
                }

                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TenyoTextTertiary
                )
            }
        }
    }
}

private fun getMuscleDrawableRes(drawableName: String): Int {
    return when (drawableName) {
        "cat_muscle_chest" -> R.drawable.cat_muscle_chest
        "cat_muscle_back" -> R.drawable.cat_muscle_back
        "cat_muscle_shoulders" -> R.drawable.cat_muscle_shoulders
        "cat_muscle_biceps" -> R.drawable.cat_muscle_biceps
        "cat_muscle_triceps" -> R.drawable.cat_muscle_triceps
        "cat_muscle_abs" -> R.drawable.cat_muscle_abs
        "cat_muscle_quads" -> R.drawable.cat_muscle_quads
        "cat_muscle_hamstrings" -> R.drawable.cat_muscle_hamstrings
        "cat_muscle_calves" -> R.drawable.cat_muscle_calves
        "cat_muscle_forearms" -> R.drawable.cat_muscle_forearms
        "cat_muscle_glutes" -> R.drawable.cat_muscle_glutes
        "cat_muscle_neck" -> R.drawable.cat_muscle_neck
        else -> R.drawable.cat_muscle_chest
    }
}
