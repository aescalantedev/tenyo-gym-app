package com.aescalantedev.tenyo.ui.screens.welcome.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Alignment as RiveAlignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.ViewModelInstance
import com.aescalantedev.tenyo.R
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.UserGender
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import kotlinx.coroutines.flow.collectLatest

/**
 * Opciones anatomicas disponibles para la seleccion de musculos en Toji (`toji.riv`).
 */
enum class TojiMuscleOption(
    val id: String,
    val labelEs: String,
    val anatomicalZoneEs: String,
    val category: BodyPartCategory,
    val isBackView: Boolean,
    val activeOpacityProperties: List<String>,
    val riveSelectKeys: Set<String>
) {
    CHEST(
        id = "CHEST",
        labelEs = "Pecho",
        anatomicalZoneEs = "Pectoral mayor y menor",
        category = BodyPartCategory.CHEST,
        isBackView = false,
        activeOpacityProperties = listOf("chestOpacity"),
        riveSelectKeys = setOf("CHEST", "PECTORALES")
    ),
    BACK_LATS(
        id = "LATS",
        labelEs = "Espalda",
        anatomicalZoneEs = "Dorsales, trapecio y lumbar",
        category = BodyPartCategory.BACK,
        isBackView = true,
        activeOpacityProperties = listOf("latsOpacity", "trapsBackOpacity", "lowerBackOpacity"),
        riveSelectKeys = setOf(
            "LATS",
            "TRAPS_BACK",
            "LOWER_BACK",
            "DORSALES (LATS)",
            "ESPALDA ALTA / TRAPECIO",
            "ESPALDA BAJA / LUMBAR"
        )
    ),
    SHOULDERS(
        id = "SHOULDERS",
        labelEs = "Hombros",
        anatomicalZoneEs = "Deltoides 3D y trapecio sup.",
        category = BodyPartCategory.SHOULDERS,
        isBackView = false,
        activeOpacityProperties = listOf(
            "shouldersFrontOpacity",
            "trapsFrontOpacity",
            "shouldersBackOpacity"
        ),
        riveSelectKeys = setOf(
            "SHOULDERS_FRONT",
            "TRAPS_FRONT",
            "SHOULDERS_BACK",
            "HOMBROS (FRONTAL)",
            "TRAPECIO",
            "HOMBRO POSTERIOR"
        )
    ),
    BICEPS(
        id = "BICEPS",
        labelEs = "Bíceps",
        anatomicalZoneEs = "Bíceps braquial y braquial",
        category = BodyPartCategory.UPPER_ARMS,
        isBackView = false,
        activeOpacityProperties = listOf("bicepsOpacity"),
        riveSelectKeys = setOf("BICEPS")
    ),
    TRICEPS(
        id = "TRICEPS",
        labelEs = "Tríceps",
        anatomicalZoneEs = "Cabeza larga, lateral y medial",
        category = BodyPartCategory.UPPER_ARMS,
        isBackView = true,
        activeOpacityProperties = listOf("tricepsOpacity"),
        riveSelectKeys = setOf("TRICEPS")
    ),
    ABS_CORE(
        id = "ABS",
        labelEs = "Abdomen",
        anatomicalZoneEs = "Recto abdominal y oblicuos",
        category = BodyPartCategory.WAIST,
        isBackView = false,
        activeOpacityProperties = listOf("absOpacity", "obliquesOpacity"),
        riveSelectKeys = setOf("ABS", "OBLIQUES", "ABDOMINALES", "OBLICUOS")
    ),
    FOREARMS(
        id = "FOREARMS",
        labelEs = "Antebrazos",
        anatomicalZoneEs = "Flexores, extensores y agarre",
        category = BodyPartCategory.LOWER_ARMS,
        isBackView = false,
        activeOpacityProperties = listOf("forearmsFrontOpacity", "forearmsBackOpacity"),
        riveSelectKeys = setOf("FOREARMS_FRONT", "FOREARMS_BACK", "ANTEBRAZOS", "ANTEBRAZOS (POST)")
    ),
    QUADS_LEGS(
        id = "QUADS",
        labelEs = "Piernas (Cuádriceps)",
        anatomicalZoneEs = "Vasto lateral, medial y recto",
        category = BodyPartCategory.UPPER_LEGS,
        isBackView = false,
        activeOpacityProperties = listOf("quadsOpacity"),
        riveSelectKeys = setOf("QUADS", "CUADRICEPS")
    ),
    POSTERIOR_LEGS(
        id = "HAMSTRINGS",
        labelEs = "Isquios y Glúteos",
        anatomicalZoneEs = "Cadena posterior inferior",
        category = BodyPartCategory.UPPER_LEGS,
        isBackView = true,
        activeOpacityProperties = listOf("glutesOpacity", "hamstringsOpacity"),
        riveSelectKeys = setOf("GLUTES", "HAMSTRINGS", "GLUTEOS", "ISQUIOTIBIALES")
    ),
    CALVES(
        id = "CALVES",
        labelEs = "Pantorrillas",
        anatomicalZoneEs = "Gastrocnemio y sóleo",
        category = BodyPartCategory.LOWER_LEGS,
        isBackView = true,
        activeOpacityProperties = listOf("calvesBackOpacity", "calvesFrontOpacity"),
        riveSelectKeys = setOf("CALVES_BACK", "CALVES_FRONT", "PANTORRILLAS", "GEMELOS")
    );

    companion object {
        fun fromRiveMuscleKey(rawKey: String): TojiMuscleOption? {
            val normalized = rawKey.trim().uppercase()
            if (normalized.isEmpty() || normalized == "NONE" || normalized == "INITIAL VALUE") {
                return null
            }
            val cleanKey = normalized.removePrefix("SELECT_").trim()
            return entries.firstOrNull {
                normalized in it.riveSelectKeys || cleanKey in it.riveSelectKeys
            }
        }

        fun optionsForCategory(category: BodyPartCategory): List<TojiMuscleOption> =
            entries.filter { it.category == category }
    }
}

private val ALL_RIVE_MUSCLE_OPACITY_PROPERTIES = listOf(
    "chestOpacity",
    "absOpacity",
    "obliquesOpacity",
    "shouldersFrontOpacity",
    "trapsFrontOpacity",
    "bicepsOpacity",
    "forearmsFrontOpacity",
    "quadsOpacity",
    "calvesFrontOpacity",
    "trapsBackOpacity",
    "shouldersBackOpacity",
    "latsOpacity",
    "lowerBackOpacity",
    "tricepsOpacity",
    "forearmsBackOpacity",
    "glutesOpacity",
    "hamstringsOpacity",
    "calvesBackOpacity"
)

/**
 * Sincroniza dinamicamente el ViewModel `BodyMapVM` de Rive en escala 0f..1f (normalizada).
 */
private fun syncRiveDynamicSelection(
    vmi: ViewModelInstance,
    selectedMuscles: Set<TojiMuscleOption>,
    isBackView: Boolean
) {
    runCatching {
        // Escala normalizada 0f..1f para no quemar las texturas
        vmi.getNumberProperty("frontOpacity").value = if (isBackView) 0f else 1f
        vmi.getNumberProperty("backOpacity").value = if (isBackView) 1f else 0f

        val activeProps = selectedMuscles.flatMap { it.activeOpacityProperties }.toSet()
        ALL_RIVE_MUSCLE_OPACITY_PROPERTIES.forEach { propName ->
            val targetOpacity = if (propName in activeProps) 0.85f else 0f
            vmi.getNumberProperty(propName).value = targetOpacity
        }
    }
}

/**
 * Visor interactivo centralizado del personaje anatómico (Toji Fushiguro / Maki Zenin) según el género.
 * Incluye el toggle flotante superior [ Frente | Espalda ] y boton de rotacion [🔄].
 */
@Composable
fun WelcomeTojiMuscleViewer(
    selectedMuscles: Set<TojiMuscleOption>,
    isBackView: Boolean,
    onToggleMuscle: (TojiMuscleOption) -> Unit,
    onToggleViewSide: () -> Unit,
    modifier: Modifier = Modifier,
    gender: UserGender = UserGender.MALE,
    height: Dp? = null
) {
    var viewModelInstance by remember(gender) { mutableStateOf<ViewModelInstance?>(null) }

    LaunchedEffect(selectedMuscles, isBackView, viewModelInstance, gender) {
        viewModelInstance?.let { vmi ->
            syncRiveDynamicSelection(
                vmi = vmi,
                selectedMuscles = selectedMuscles,
                isBackView = isBackView
            )
        }
    }

    LaunchedEffect(viewModelInstance, gender) {
        val vmi = viewModelInstance ?: return@LaunchedEffect
        runCatching {
            vmi.getStringProperty("selectedMuscle").valueFlow.collectLatest { muscleKey ->
                val tappedOption = TojiMuscleOption.fromRiveMuscleKey(muscleKey)
                if (tappedOption != null) {
                    runCatching { vmi.getStringProperty("selectedMuscle").value = "NONE" }
                    onToggleMuscle(tappedOption)
                }
            }
        }
    }

    val boxModifier = if (height != null) {
        modifier
            .fillMaxWidth()
            .height(height)
    } else {
        modifier.fillMaxWidth()
    }

    Box(
        modifier = boxModifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TenyoJadePrimary.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Personaje en Rive (Toji para Hombre, Maki para Mujer)
        TojiRiveCharacterCanvas(
            gender = gender,
            onViewModelReady = { vmi ->
                viewModelInstance = vmi
                syncRiveDynamicSelection(
                    vmi = vmi,
                    selectedMuscles = selectedMuscles,
                    isBackView = isBackView
                )
            },
            modifier = Modifier.fillMaxSize()
        )

        // Control flotante superior derecho: [ Frente | Espalda ] + [ 🔄 ]
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pildora Frente | Espalda
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = TenyoSurfaceContainer.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier.height(34.dp)
            ) {
                Row(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Frente
                    val frontBg by animateColorAsState(
                        targetValue = if (!isBackView) TenyoJadePrimary.copy(alpha = 0.25f) else Color.Transparent,
                        animationSpec = tween(150),
                        label = "frontBg"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(frontBg)
                            .clickable { if (isBackView) onToggleViewSide() }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Frente",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (!isBackView) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (!isBackView) TenyoTextPrimary else TenyoTextSecondary
                        )
                    }

                    // Espalda
                    val backBg by animateColorAsState(
                        targetValue = if (isBackView) TenyoJadePrimary.copy(alpha = 0.25f) else Color.Transparent,
                        animationSpec = tween(150),
                        label = "backBg"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(backBg)
                            .clickable { if (!isBackView) onToggleViewSide() }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Espalda",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isBackView) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isBackView) TenyoTextPrimary else TenyoTextSecondary
                        )
                    }
                }
            }

            // Boton circular de giro rapido
            Surface(
                shape = CircleShape,
                color = TenyoSurfaceContainerHigh.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onToggleViewSide)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Lucide.RefreshCw,
                        contentDescription = "Girar vista",
                        tint = TenyoTextPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/**
 * Vista nativa de Rive ([RiveAnimationView]) encapsulada para Jetpack Compose.
 * Carga dinámicamente `R.raw.toji` (`UserGender.MALE`) o `R.raw.maki` (`UserGender.FEMALE`).
 */
@Composable
fun TojiRiveCharacterCanvas(
    onViewModelReady: (ViewModelInstance) -> Unit,
    modifier: Modifier = Modifier,
    gender: UserGender = UserGender.MALE
) {
    val rawResId = if (gender == UserGender.FEMALE) R.raw.maki else R.raw.toji
    var riveViewRef by remember(rawResId) { mutableStateOf<RiveAnimationView?>(null) }

    key(rawResId) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                RiveAnimationView(context).apply {
                    riveViewRef = this
                    setRiveResource(
                        resId = rawResId,
                        artboardName = "iPhone 16 Pro Max - 1",
                        stateMachineName = "State Machine 1",
                        alignment = RiveAlignment.CENTER,
                        fit = Fit.CONTAIN,
                        autoBind = true
                    )
                    val resolvedVmi = controller.stateMachines.firstOrNull()?.viewModelInstance
                        ?: controller.activeArtboard?.viewModelInstance
                        ?: controller.file?.getViewModelByName("BodyMapVM")?.createDefaultInstance()?.also { vmi ->
                            controller.stateMachines.firstOrNull()?.viewModelInstance = vmi
                            controller.activeArtboard?.viewModelInstance = vmi
                        }

                    if (resolvedVmi != null) {
                        onViewModelReady(resolvedVmi)
                    }
                }
            }
        )
    }

    DisposableEffect(rawResId) {
        onDispose {
            riveViewRef = null
        }
    }
}
