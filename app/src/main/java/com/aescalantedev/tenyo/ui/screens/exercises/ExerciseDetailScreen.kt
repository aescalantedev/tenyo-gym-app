package com.aescalantedev.tenyo.ui.screens.exercises

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.core.domain.model.ExerciseHistoryEntry
import com.aescalantedev.tenyo.core.domain.model.ExerciseRecord
import com.aescalantedev.tenyo.ui.components.CategoryVisuals
import com.aescalantedev.tenyo.ui.components.TenyoBadge
import com.aescalantedev.tenyo.ui.components.TenyoBadgeTone
import com.aescalantedev.tenyo.ui.components.TenyoExerciseMedia
import com.aescalantedev.tenyo.ui.components.TenyoPrimaryButton
import com.aescalantedev.tenyo.ui.components.TenyoSecondaryButton
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseDetailSkeleton
import com.aescalantedev.tenyo.ui.screens.exercises.components.MuscleInvolvedCardsRow
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseDetailUiState
import com.aescalantedev.tenyo.ui.theme.TenyoAmberWarning
import com.aescalantedev.tenyo.ui.theme.TenyoCrimsonTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainer
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.theme.TenyoVioletSecondary
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Trophy
import com.composables.icons.lucide.Zap
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Ficha Técnica y Detalle del Ejercicio (Stitch 05).
 *
 * Arquitectura estética:
 * - HeroMediaHeader inmersivo a sangre dentro del LazyColumn con GIF y degradado.
 * - TopBar flotante transparente en reposo que al hacer scroll anima su fondo a sólido
 *   y fija suavemente el título del ejercicio arriba entre los botones de volver y favorito.
 * - Pestañas de navegación segmentada: "Técnica y Guía" vs "Historial y PRs".
 * - Fila desplazable de músculos involucrados con imágenes reales de `res/drawable`.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseDetailScreen(
    uiState: ExerciseDetailUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlan: () -> Unit,
    onStartNow: () -> Unit,
    onSelectTab: (Int) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val exercise = uiState.exercise
    val listState = rememberLazyListState()

    // Detectamos si el usuario scrolleó más allá del Hero Media para fijar el título
    val showPinnedTitle by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 220
        }
    }

    val topBarAlpha by animateFloatAsState(
        targetValue = if (showPinnedTitle) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "TopBarAlpha"
    )

    Crossfade(
        targetState = uiState.isLoading || exercise == null,
        animationSpec = tween(durationMillis = 350),
        label = "ExerciseDetailCrossfade"
    ) { loading ->
        if (loading || exercise == null) {
            ExerciseDetailSkeleton(
                contentPadding = contentPadding,
                modifier = modifier
            )
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(TenyoObsidianBackground)
            ) {
                // 1. Contenido principal con Scroll
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = maxOf(
                            85.dp,
                            contentPadding.calculateBottomPadding() + 85.dp
                        )
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Hero Media Inmersivo (GIF con degradado)
                    item {
                        HeroMediaHeader(exercise = exercise)
                    }

                    // Título grande del ejercicio, Badges y Selector de Pestañas
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = exercise.name.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TenyoTextPrimary
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TenyoBadge(
                                    text = exercise.bodyPart.nameEs,
                                    tone = TenyoBadgeTone.JADE
                                )
                                TenyoBadge(
                                    text = exercise.category.replaceFirstChar { it.uppercase() }.ifBlank { "Fuerza" },
                                    tone = TenyoBadgeTone.VIOLET
                                )
                                TenyoBadge(
                                    text = exercise.localizedEquipment(uiState.languageCode),
                                    tone = TenyoBadgeTone.NEUTRAL
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Selector de Pestañas (Técnica vs Historial)
                            DetailSegmentedTabs(
                                selectedIndex = uiState.selectedTabIndex,
                                onSelectTab = onSelectTab,
                                historyCount = uiState.history.size
                            )
                        }
                    }

                    // Contenido según la pestaña activa
                    if (uiState.selectedTabIndex == 0) {
                        // --- TAB 0: TÉCNICA Y GUÍA ---

                        // Bento Grid de Métricas Clave
                        item {
                            DetailBentoMetricsGrid(
                                exercise = exercise,
                                languageCode = uiState.languageCode,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }

                        // Músculos Involucrados con imágenes reales de drawable
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    Text(
                                        text = "Músculos involucrados",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TenyoTextPrimary
                                    )
                                    Text(
                                        text = "Agonista principal y estabilizadores clave",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TenyoTextSecondary
                                    )
                                }

                                MuscleInvolvedCardsRow(
                                    primaryMuscleName = exercise.localizedTargetMuscle(uiState.languageCode),
                                    secondaryMuscles = exercise.secondaryMuscles
                                )
                            }
                        }

                        // Guía Paso a Paso (Timeline / Stepper vertical)
                        item {
                            val steps = exercise.getInstructionSteps(uiState.languageCode)
                            VerticalInstructionTimeline(
                                steps = steps,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }

                        // Nota Técnica · Protocolo Tenyo
                        item {
                            TechnicalProtocolCallout(
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }

                    } else {
                        // --- TAB 1: HISTORIAL Y RÉCORDS (PRs) ---

                        // Tarjeta de Récord Personal (PR Hero Card)
                        item {
                            PersonalRecordHeroCard(
                                record = uiState.personalRecord,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }

                        // Sección de Historial de Entrenamientos
                        item {
                            WorkoutHistorySection(
                                history = uiState.history,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }

                    if (uiState.statusMessage != null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TenyoBadge(
                                    text = uiState.statusMessage,
                                    tone = TenyoBadgeTone.JADE
                                )
                            }
                        }
                    }
                }

                // 2. TopBar Flotante Dinámico (Transparente al inicio, Sólido con Título fijo al scrollear)
                Surface(
                    color = TenyoSurfaceContainerLow.copy(alpha = topBarAlpha),
                    border = BorderStroke(
                        1.dp,
                        if (topBarAlpha > 0.1f) TenyoOutlineSubtle.copy(alpha = topBarAlpha) else Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón de Volver
                        Surface(
                            shape = CircleShape,
                            color = TenyoSurfaceContainer.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, TenyoOutlineSubtle),
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onBack)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.ArrowLeft,
                                    contentDescription = "Volver",
                                    tint = TenyoTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Título Pinned (Fijado) que aparece y se mantiene visible con scroll
                        Text(
                            text = exercise.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TenyoTextPrimary.copy(alpha = topBarAlpha),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 14.dp)
                                .graphicsLayer { alpha = topBarAlpha }
                        )

                        // Botón de Favorito
                        Surface(
                            shape = CircleShape,
                            color = TenyoSurfaceContainer.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, TenyoOutlineSubtle),
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onToggleFavorite)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Heart,
                                    contentDescription = "Favorito",
                                    tint = if (exercise.isFavorite) TenyoCrimsonTertiary else TenyoTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Barra Inferior Fija de Acciones
                Surface(
                    color = TenyoSurfaceContainerLow,
                    border = BorderStroke(1.dp, TenyoOutlineSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TenyoSecondaryButton(
                            text = "Añadir a rutina",
                            onClick = onAddToPlan,
                            leadingIcon = Lucide.Plus,
                            modifier = Modifier.weight(1f)
                        )

                        TenyoPrimaryButton(
                            text = "Iniciar ahora",
                            onClick = onStartNow,
                            leadingIcon = Lucide.Play,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// HERO MEDIA HEADER (A sangre con degradado)
// -------------------------------------------------------------------------

@Composable
private fun HeroMediaHeader(
    exercise: Exercise
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // GIF / Imagen del Ejercicio
        TenyoExerciseMedia(
            mediaUrl = exercise.gifUrl.ifBlank { exercise.imageUrl },
            contentDescription = exercise.name,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(0.dp),
            contentScale = ContentScale.Fit,
            fallbackIcon = CategoryVisuals.iconFor(exercise.bodyPart),
            iconTint = CategoryVisuals.accentColorFor(exercise.bodyPart)
        )

        // Degradado inferior para fundir de forma cinematográfica con el fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.40f),
                            Color.Transparent,
                            TenyoObsidianBackground
                        )
                    )
                )
        )
    }
}

// -------------------------------------------------------------------------
// SELECTOR DE PESTAÑAS (SEGMENTED TABS)
// -------------------------------------------------------------------------

@Composable
private fun DetailSegmentedTabs(
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    historyCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = TenyoSurfaceContainerLow,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TabButton(
                title = "Técnica y Guía",
                icon = Lucide.BookOpen,
                isSelected = selectedIndex == 0,
                onClick = { onSelectTab(0) },
                modifier = Modifier.weight(1f)
            )

            TabButton(
                title = "Historial y PRs",
                icon = Lucide.History,
                badgeCount = if (historyCount > 0) historyCount else null,
                isSelected = selectedIndex == 1,
                onClick = { onSelectTab(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) TenyoSurfaceContainerHigh else Color.Transparent,
        label = "TabBgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) TenyoJadePrimary else TenyoTextSecondary,
        label = "TabTextColor"
    )
    val borderColor = if (isSelected) TenyoJadePrimary.copy(alpha = 0.35f) else Color.Transparent

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )

            if (badgeCount != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = CircleShape,
                    color = TenyoJadeContainer,
                    modifier = Modifier.size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$badgeCount",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TenyoJadePrimary
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// BENTO GRID DE MÉTRICAS (TAB 0)
// -------------------------------------------------------------------------

@Composable
private fun DetailBentoMetricsGrid(
    exercise: Exercise,
    languageCode: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BentoMetricCard(
                icon = Lucide.Dumbbell,
                label = "Equipo",
                value = exercise.localizedEquipment(languageCode),
                modifier = Modifier.weight(1f)
            )
            BentoMetricCard(
                icon = Lucide.Target,
                label = "Prescripción",
                value = "${exercise.defaultSets} × ${exercise.defaultReps}",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BentoMetricCard(
                icon = Lucide.Clock,
                label = "Descanso sugerido",
                value = "${exercise.defaultRestSeconds} seg",
                modifier = Modifier.weight(1f)
            )
            BentoMetricCard(
                icon = Lucide.Zap,
                label = "Dificultad",
                value = if (exercise.defaultSets >= 4) "Intermedio" else "Básico",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BentoMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = TenyoSurfaceContainerHigh,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TenyoJadePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TenyoTextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// STEPPER / TIMELINE VERTICAL DE EJECUCIÓN (TAB 0)
// -------------------------------------------------------------------------

@Composable
private fun VerticalInstructionTimeline(
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ejecución técnica paso a paso",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )

            steps.forEachIndexed { index, stepText ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TenyoJadeContainer,
                        border = BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TenyoJadePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TenyoTextSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// CALLOUT DE PROTOCOLO TÉCNICO (TAB 0)
// -------------------------------------------------------------------------

@Composable
private fun TechnicalProtocolCallout(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = TenyoAmberWarning.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, TenyoAmberWarning.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Lucide.Info,
                contentDescription = null,
                tint = TenyoAmberWarning,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Nota Técnica · Protocolo Tenyo",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TenyoAmberWarning
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prioriza la fase excéntrica controlada (2 a 3 segundos) y evita el impulso inercial. La verdadera fuerza sobrehumana proviene del control total del rango articular.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// TAB 1: HERO DE RÉCORD PERSONAL (PR)
// -------------------------------------------------------------------------

@Composable
private fun PersonalRecordHeroCard(
    record: ExerciseRecord?,
    modifier: Modifier = Modifier
) {
    if (record == null) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = TenyoSurfaceContainer,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = TenyoSurfaceContainerHigh,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Lucide.Trophy,
                            contentDescription = null,
                            tint = TenyoTextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = "Sin récords registrados aún",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TenyoTextPrimary
                )
                Text(
                    text = "Completa tu primer entrenamiento con este ejercicio para establecer tu récord personal de referencia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TenyoTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TenyoSurfaceContainer,
            border = BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.45f)),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TenyoJadeContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Trophy,
                                    contentDescription = null,
                                    tint = TenyoJadePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "RÉCORD PERSONAL ACTUAL",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = TenyoJadePrimary
                        )
                    }

                    Text(
                        text = formatEpochDate(record.achievedAtEpochMillis),
                        style = MaterialTheme.typography.labelSmall,
                        color = TenyoTextSecondary
                    )
                }

                // Estadísticas Clave del Récord
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Carga Máxima",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary
                        )
                        Text(
                            text = "${formatWeight(record.maxWeightKg)} kg",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TenyoTextPrimary
                        )
                        Text(
                            text = "${record.repsAtMaxWeight} repeticiones",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoJadePrimary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "1RM Estimado",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary
                        )
                        Text(
                            text = "${"%.1f".format(record.estimated1RmKg)} kg",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TenyoTextPrimary
                        )
                        Text(
                            text = "Fórmula Epley",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextTertiary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mejor Serie",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary
                        )
                        Text(
                            text = "${record.bestSetVolumeKg.toInt()} kg",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TenyoTextPrimary
                        )
                        Text(
                            text = "Volumen serie",
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextTertiary
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// TAB 1: LISTA HISTÓRICA DE ENTRENAMIENTOS
// -------------------------------------------------------------------------

@Composable
private fun WorkoutHistorySection(
    history: List<ExerciseHistoryEntry>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial de entrenamientos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TenyoTextPrimary
            )

            if (history.isNotEmpty()) {
                Text(
                    text = "${history.size} sesiones",
                    style = MaterialTheme.typography.labelMedium,
                    color = TenyoTextSecondary
                )
            }
        }

        if (history.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = TenyoSurfaceContainer,
                border = BorderStroke(1.dp, TenyoOutlineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TenyoSurfaceContainerHigh,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.History,
                                contentDescription = null,
                                tint = TenyoTextTertiary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Text(
                        text = "Sin sesiones finalizadas",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                    Text(
                        text = "Cada serie completada en una sesión de entrenamiento se guardará automáticamente en este historial.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                history.forEach { entry ->
                    HistorySessionCard(entry = entry)
                }
            }
        }
    }
}

@Composable
private fun HistorySessionCard(
    entry: ExerciseHistoryEntry
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = TenyoSurfaceContainer,
        border = BorderStroke(1.dp, TenyoOutlineSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = entry.sessionTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = null,
                            tint = TenyoTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = formatEpochDate(entry.dateEpochMillis),
                            style = MaterialTheme.typography.labelSmall,
                            color = TenyoTextSecondary
                        )
                    }
                }

                TenyoBadge(
                    text = "${entry.totalVolumeKg.toInt()} kg total",
                    tone = TenyoBadgeTone.JADE
                )
            }

            // Desglose de series realizadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                entry.sets.forEach { setItem ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (setItem.isPr) TenyoJadeContainer else TenyoSurfaceContainerHigh,
                        border = BorderStroke(
                            1.dp,
                            if (setItem.isPr) TenyoJadePrimary.copy(alpha = 0.5f) else TenyoOutlineSubtle
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "S${setItem.setNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (setItem.isPr) TenyoJadePrimary else TenyoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${formatWeight(setItem.weightKg)}k",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary
                            )
                            Text(
                                text = "×${setItem.reps}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// UTILIDADES DE FORMATO
// -------------------------------------------------------------------------

private fun formatWeight(weightKg: Double): String {
    return if (weightKg % 1.0 == 0.0) {
        weightKg.toInt().toString()
    } else {
        "%.1f".format(weightKg)
    }
}

private fun formatEpochDate(epochMillis: Long): String {
    return try {
        val instant = Instant.ofEpochMilli(epochMillis)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("es"))
        localDate.format(formatter)
    } catch (_: Exception) {
        "Fecha registrada"
    }
}
