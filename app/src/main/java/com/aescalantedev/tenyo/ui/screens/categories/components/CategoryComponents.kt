package com.aescalantedev.tenyo.ui.screens.categories.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.R
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHighest
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoVoidBlack

/**
 * Modelo de datos para un elemento de la rejilla anatómica de 12 músculos.
 */
data class MuscleGridItem(
    val id: String,
    val labelEs: String,
    val category: BodyPartCategory,
    val searchFilter: String = "",
    @DrawableRes val drawableRes: Int
)

/**
 * Catálogo de los 12 grupos musculares con renders anatómicos 3D dedicados.
 */
val PRIMARY_ANATOMICAL_MUSCLES: List<MuscleGridItem> = listOf(
    MuscleGridItem("chest", "Pecho", BodyPartCategory.CHEST, "", R.drawable.cat_muscle_chest),
    MuscleGridItem("biceps", "Bíceps", BodyPartCategory.UPPER_ARMS, "biceps", R.drawable.cat_muscle_biceps),
    MuscleGridItem("triceps", "Tríceps", BodyPartCategory.UPPER_ARMS, "triceps", R.drawable.cat_muscle_triceps),
    MuscleGridItem("back", "Espalda", BodyPartCategory.BACK, "", R.drawable.cat_muscle_back),
    MuscleGridItem("shoulders", "Hombros", BodyPartCategory.SHOULDERS, "", R.drawable.cat_muscle_shoulders),
    MuscleGridItem("abs", "Abdominales", BodyPartCategory.WAIST, "", R.drawable.cat_muscle_abs),
    MuscleGridItem("quads", "Cuádriceps femoral", BodyPartCategory.UPPER_LEGS, "quads", R.drawable.cat_muscle_quads),
    MuscleGridItem("hamstrings", "Isquiotibiales", BodyPartCategory.UPPER_LEGS, "hamstrings", R.drawable.cat_muscle_hamstrings),
    MuscleGridItem("glutes", "Caderas", BodyPartCategory.UPPER_LEGS, "glutes", R.drawable.cat_muscle_glutes),
    MuscleGridItem("calves", "Pantorrillas", BodyPartCategory.LOWER_LEGS, "", R.drawable.cat_muscle_calves),
    MuscleGridItem("forearms", "Antebrazos", BodyPartCategory.LOWER_ARMS, "", R.drawable.cat_muscle_forearms),
    MuscleGridItem("neck", "Cuello", BodyPartCategory.NECK, "", R.drawable.cat_muscle_neck)
)

/**
 * Tipo de representación gráfica para la rejilla de equipamiento.
 */
enum class EquipmentGraphicType {
    BARBELL,
    DUMBBELL,
    BODY_WEIGHT,
    KETTLEBELL,
    EZ_BARBELL,
    HEX_BAR,
    CABLE,
    LEVERAGE_MACHINE,
    SLED_MACHINE,
    SMITH_MACHINE,
    POWER_SLED,
    WEIGHTED
}

/**
 * Modelo de datos para un elemento de la rejilla de 12 tipos de equipamiento.
 */
data class EquipmentGridItem(
    val id: String,
    val labelEs: String,
    val equipmentKey: String,
    val graphicType: EquipmentGraphicType,
    @DrawableRes val drawableRes: Int? = null
)

/**
 * Catálogo de los 12 tipos de equipamiento ("Por equipo").
 *
 * Los tipos de equipamiento primarios cuentan con renders 3D dedicados fotorrealistas con
 * estética oscura y detalles en verde esmeralda, complementados por gráficos vectoriales reactivos.
 */
val PRIMARY_EQUIPMENT_ITEMS: List<EquipmentGridItem> = listOf(
    EquipmentGridItem("barbell", "Barra", "barbell", EquipmentGraphicType.BARBELL, R.drawable.cat_equip_barbell),
    EquipmentGridItem("dumbbell", "Mancuerna", "dumbbell", EquipmentGraphicType.DUMBBELL, R.drawable.cat_equip_dumbbell),
    EquipmentGridItem("bodyweight", "Peso corporal", "body weight", EquipmentGraphicType.BODY_WEIGHT, R.drawable.cat_equip_bodyweight),
    EquipmentGridItem("kettlebell", "Kettlebell", "kettlebell", EquipmentGraphicType.KETTLEBELL, R.drawable.cat_equip_kettlebell),
    EquipmentGridItem("ezbar", "Barra EZ", "ez barbell", EquipmentGraphicType.EZ_BARBELL, R.drawable.cat_equip_ezbar),
    EquipmentGridItem("hexbar", "Barra hexagonal", "trap bar", EquipmentGraphicType.HEX_BAR, R.drawable.cat_equip_barbell),
    EquipmentGridItem("cable", "Cable", "cable", EquipmentGraphicType.CABLE, R.drawable.cat_equip_cable),
    EquipmentGridItem("leverage", "Máquina de palanca", "leverage machine", EquipmentGraphicType.LEVERAGE_MACHINE),
    EquipmentGridItem("sledmachine", "Máquina de trineo", "sled machine", EquipmentGraphicType.SLED_MACHINE),
    EquipmentGridItem("smith", "Máquina Smith", "smith machine", EquipmentGraphicType.SMITH_MACHINE),
    EquipmentGridItem("powersled", "Trineo de potencia", "power sled", EquipmentGraphicType.POWER_SLED),
    EquipmentGridItem("weighted", "Con peso", "weighted", EquipmentGraphicType.WEIGHTED, R.drawable.cat_equip_dumbbell)
)

// =============================================================================
// COMPONENTES DE CELDA DE LA REJILLA
// =============================================================================

/**
 * Celda individual de grupo muscular con render 3D anatómico y zona activa destacada.
 */
@Composable
fun AnatomicalMuscleCard(
    item: MuscleGridItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = TenyoSurfaceContainerLow,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Render 3D anatómico sobre fondo void black profundo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TenyoVoidBlack),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.drawableRes),
                    contentDescription = item.labelEs,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Nombre de la categoría muscular
            Text(
                text = item.labelEs,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TenyoTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Celda individual de equipamiento con ilustración gráfica vectorial del accesorio o máquina.
 */
@Composable
fun EquipmentGridCard(
    item: EquipmentGridItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = TenyoSurfaceContainerLow,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lienzo del equipamiento: Render 3D fotorrealista o Fallback Canvas reactivo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TenyoVoidBlack),
                contentAlignment = Alignment.Center
            ) {
                if (item.drawableRes != null) {
                    Image(
                        painter = painterResource(id = item.drawableRes),
                        contentDescription = item.labelEs,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                } else {
                    EquipmentGraphic(
                        graphicType = item.graphicType,
                        modifier = Modifier.size(width = 80.dp, height = 90.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Nombre del equipamiento
            Text(
                text = item.labelEs,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TenyoTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// =============================================================================
// RENDERIZADO VECTORIAL DE EQUIPAMIENTO (CANVAS) REACTIVO AL TEMA
// =============================================================================

@Composable
fun EquipmentGraphic(
    graphicType: EquipmentGraphicType,
    modifier: Modifier = Modifier
) {
    val equipSteel = TenyoTextSecondary
    val equipDarkMetal = TenyoSurfaceContainerHighest
    val equipSilver = TenyoTextPrimary
    val highlightAccent = TenyoJadePrimary

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        when (graphicType) {
            EquipmentGraphicType.BARBELL -> {
                // Barra recta horizontal con discos en los extremos
                drawLine(
                    color = equipSilver,
                    start = Offset(w * 0.1f, cy),
                    end = Offset(w * 0.9f, cy),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
                // Discos lado izquierdo
                drawRoundRect(
                    color = equipDarkMetal,
                    topLeft = Offset(w * 0.20f, cy - 18f),
                    size = Size(6f, 36f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = equipSteel,
                    topLeft = Offset(w * 0.26f, cy - 22f),
                    size = Size(8f, 44f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                // Discos lado derecho
                drawRoundRect(
                    color = equipSteel,
                    topLeft = Offset(w * 0.66f, cy - 22f),
                    size = Size(8f, 44f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = equipDarkMetal,
                    topLeft = Offset(w * 0.74f, cy - 18f),
                    size = Size(6f, 36f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }

            EquipmentGraphicType.DUMBBELL -> {
                // Mancuerna isométrica
                drawLine(
                    color = equipSilver,
                    start = Offset(cx - 20f, cy + 12f),
                    end = Offset(cx + 20f, cy - 12f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                // Cabezas hexagonales
                drawRoundRect(
                    color = equipDarkMetal,
                    topLeft = Offset(cx - 30f, cy + 4f),
                    size = Size(16f, 24f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawRoundRect(
                    color = equipDarkMetal,
                    topLeft = Offset(cx + 14f, cy - 26f),
                    size = Size(16f, 24f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }

            EquipmentGraphicType.BODY_WEIGHT -> {
                // Atleta en postura de flexión (pushup)
                // Cabeza
                drawCircle(color = equipSilver, radius = 6f, center = Offset(cx + 24f, cy - 8f))
                // Cuerpo/tronco
                drawLine(
                    color = equipSilver,
                    start = Offset(cx + 18f, cy - 4f),
                    end = Offset(cx - 24f, cy + 12f),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
                // Brazo al suelo
                drawLine(
                    color = equipSteel,
                    start = Offset(cx + 16f, cy - 2f),
                    end = Offset(cx + 16f, cy + 14f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }

            EquipmentGraphicType.KETTLEBELL -> {
                // Pesa rusa (Kettlebell)
                // Asa superior
                drawArc(
                    color = equipSilver,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(cx - 16f, cy - 24f),
                    size = Size(32f, 28f),
                    style = Stroke(width = 4f)
                )
                // Cuerpo esférico
                drawCircle(
                    color = equipDarkMetal,
                    radius = 20f,
                    center = Offset(cx, cy + 8f)
                )
                // Anillo de color/acento con el color del tema activo
                drawCircle(
                    color = highlightAccent,
                    radius = 18f,
                    center = Offset(cx, cy + 8f),
                    style = Stroke(width = 2.5f)
                )
            }

            EquipmentGraphicType.EZ_BARBELL -> {
                // Barra Z ondulada con discos
                val ezPath = Path().apply {
                    moveTo(w * 0.14f, cy)
                    lineTo(w * 0.32f, cy)
                    lineTo(w * 0.42f, cy - 6f)
                    lineTo(w * 0.50f, cy + 6f)
                    lineTo(w * 0.58f, cy - 6f)
                    lineTo(w * 0.68f, cy)
                    lineTo(w * 0.86f, cy)
                }
                drawPath(ezPath, equipSilver, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                drawRoundRect(
                    color = equipSteel,
                    topLeft = Offset(w * 0.22f, cy - 16f),
                    size = Size(7f, 32f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = equipSteel,
                    topLeft = Offset(w * 0.71f, cy - 16f),
                    size = Size(7f, 32f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }

            EquipmentGraphicType.HEX_BAR -> {
                // Barra hexagonal (Trap bar)
                val hexPath = Path().apply {
                    moveTo(cx - 18f, cy)
                    lineTo(cx - 10f, cy - 16f)
                    lineTo(cx + 10f, cy - 16f)
                    lineTo(cx + 18f, cy)
                    lineTo(cx + 10f, cy + 16f)
                    lineTo(cx - 10f, cy + 16f)
                    close()
                }
                drawPath(hexPath, equipSteel, style = Stroke(width = 3.5f))
                drawLine(
                    color = equipSilver,
                    start = Offset(w * 0.12f, cy),
                    end = Offset(cx - 18f, cy),
                    strokeWidth = 3.5f
                )
                drawLine(
                    color = equipSilver,
                    start = Offset(cx + 18f, cy),
                    end = Offset(w * 0.88f, cy),
                    strokeWidth = 3.5f
                )
            }

            EquipmentGraphicType.CABLE -> {
                // Polea / Torre de cable
                // Estructura vertical
                drawLine(
                    color = equipDarkMetal,
                    start = Offset(cx - 14f, cy - 24f),
                    end = Offset(cx - 14f, cy + 24f),
                    strokeWidth = 4f
                )
                // Polea alta y cable con agarradera
                drawCircle(color = equipSilver, radius = 5f, center = Offset(cx - 14f, cy - 20f))
                drawLine(
                    color = equipSteel,
                    start = Offset(cx - 14f, cy - 20f),
                    end = Offset(cx + 16f, cy),
                    strokeWidth = 2f
                )
                drawCircle(color = highlightAccent, radius = 4f, center = Offset(cx + 16f, cy))
            }

            EquipmentGraphicType.LEVERAGE_MACHINE -> {
                // Máquina de palanca (Leverage press)
                // Marco y asiento
                drawLine(
                    color = equipDarkMetal,
                    start = Offset(cx - 16f, cy + 22f),
                    end = Offset(cx - 16f, cy - 18f),
                    strokeWidth = 4f
                )
                drawLine(
                    color = equipDarkMetal,
                    start = Offset(cx - 24f, cy + 22f),
                    end = Offset(cx + 16f, cy + 22f),
                    strokeWidth = 4f
                )
                // Asiento y respaldo
                drawRoundRect(
                    color = equipSteel,
                    topLeft = Offset(cx - 14f, cy - 8f),
                    size = Size(6f, 20f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                // Brazo de palanca móvil
                drawLine(
                    color = equipSilver,
                    start = Offset(cx - 14f, cy - 18f),
                    end = Offset(cx + 14f, cy - 2f),
                    strokeWidth = 3f
                )
                drawCircle(color = highlightAccent, radius = 5f, center = Offset(cx + 14f, cy - 2f))
            }

            EquipmentGraphicType.SLED_MACHINE -> {
                // Prensa inclinada / trineo a 45 grados
                drawLine(
                    color = equipDarkMetal,
                    start = Offset(cx - 24f, cy + 20f),
                    end = Offset(cx + 20f, cy - 18f),
                    strokeWidth = 4f
                )
                // Carro deslizable
                drawRoundRect(
                    color = equipSilver,
                    topLeft = Offset(cx - 6f, cy - 4f),
                    size = Size(16f, 12f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                // Plataforma
                drawLine(
                    color = equipSteel,
                    start = Offset(cx + 14f, cy - 24f),
                    end = Offset(cx + 26f, cy - 10f),
                    strokeWidth = 4f
                )
            }

            EquipmentGraphicType.SMITH_MACHINE -> {
                // Máquina Smith (Guías verticales con barra)
                drawLine(
                    color = equipDarkMetal,
                    start = Offset(cx - 20f, cy - 24f),
                    end = Offset(cx - 20f, cy + 24f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = equipDarkMetal,
                    start = Offset(cx + 20f, cy - 24f),
                    end = Offset(cx + 20f, cy + 24f),
                    strokeWidth = 3f
                )
                // Barra montada en guías
                drawLine(
                    color = equipSilver,
                    start = Offset(cx - 26f, cy - 2f),
                    end = Offset(cx + 26f, cy - 2f),
                    strokeWidth = 4f
                )
                drawCircle(color = equipSteel, radius = 4f, center = Offset(cx - 20f, cy - 2f))
                drawCircle(color = equipSteel, radius = 4f, center = Offset(cx + 20f, cy - 2f))
            }

            EquipmentGraphicType.POWER_SLED -> {
                // Trineo de potencia / Prowler
                // Base de trineo
                drawLine(
                    color = equipDarkMetal,
                    start = Offset(cx - 22f, cy + 18f),
                    end = Offset(cx + 22f, cy + 18f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                // Postes verticales de empuje
                drawLine(
                    color = equipSilver,
                    start = Offset(cx - 14f, cy + 18f),
                    end = Offset(cx - 14f, cy - 16f),
                    strokeWidth = 3.5f
                )
                drawLine(
                    color = equipSilver,
                    start = Offset(cx + 14f, cy + 18f),
                    end = Offset(cx + 14f, cy - 16f),
                    strokeWidth = 3.5f
                )
                // Pin central con discos
                drawLine(
                    color = equipSteel,
                    start = Offset(cx, cy + 18f),
                    end = Offset(cx, cy + 4f),
                    strokeWidth = 5f
                )
            }

            EquipmentGraphicType.WEIGHTED -> {
                // Pila de discos olímpicos (Bumper plates)
                drawRoundRect(
                    color = equipDarkMetal,
                    topLeft = Offset(cx - 22f, cy + 6f),
                    size = Size(44f, 10f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = equipSteel,
                    topLeft = Offset(cx - 18f, cy - 4f),
                    size = Size(36f, 10f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = equipSilver,
                    topLeft = Offset(cx - 14f, cy - 14f),
                    size = Size(28f, 10f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
        }
    }
}
