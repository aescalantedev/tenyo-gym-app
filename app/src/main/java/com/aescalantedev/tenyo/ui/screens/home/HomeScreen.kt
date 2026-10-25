package com.aescalantedev.tenyo.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.PhysicalRank
import com.aescalantedev.tenyo.navigation.ProvideFab
import com.aescalantedev.tenyo.ui.screens.home.components.FeaturedExerciseRowCard
import com.aescalantedev.tenyo.ui.screens.home.components.HomeActiveProfileCard
import com.aescalantedev.tenyo.ui.screens.home.components.HomeActiveSessionMiniBar
import com.aescalantedev.tenyo.ui.screens.home.components.HomeCategoryChipCard
import com.aescalantedev.tenyo.ui.screens.home.components.HomeDashboardSkeleton
import com.aescalantedev.tenyo.ui.screens.home.components.HomeRecentWorkoutsCard
import com.aescalantedev.tenyo.ui.screens.home.components.HomeWeeklySummaryCard
import com.aescalantedev.tenyo.ui.screens.home.components.TodayWorkoutHeroCard
import com.aescalantedev.tenyo.ui.screens.home.viewmodel.HomeUiState
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play

/**
 * Pantalla Principal de Inicio (Home Dashboard).
 *
 * Se renderiza como hijo directo del Scaffold raiz de la app, evitando Scaffolds anidados
 * para permitir que el FloatingActionButton y BottomNavigationBar funcionen de manera nativa
 * sin solapamientos.
 */
@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToCategories: () -> Unit,
    onNavigateToCategoryExercises: (BodyPartCategory) -> Unit,
    onSearchExercises: (String) -> Unit,
    onNavigateToExerciseDetail: (String) -> Unit,
    onStartWorkoutSession: (planId: String?) -> Unit,
    onNavigateToPlanDetail: (String) -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onNavigateToProgress: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    sharedTransitionScope: androidx.compose.animation.SharedTransitionScope? = null,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val currentRank = uiState.progressSummary?.currentRank ?: PhysicalRank.GRADE_2
    val activeSession = uiState.activeSession

    // Solicitud de permisos en tiempo de ejecución para notificaciones en Android 13+ (Tiramisu / SDK 33+)
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { /* Permiso otorgado o denegado */ }
        )

        LaunchedEffect(Unit) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Registro dinamico del FAB en el Scaffold raiz solo cuando haya terminado de cargar
    if (!uiState.isLoading) {
        ProvideFab(
            position = if (activeSession != null) FabPosition.Center else FabPosition.End
        ) {
            if (activeSession != null) {
                HomeActiveSessionMiniBar(
                    session = activeSession,
                    onResume = {
                        onStartWorkoutSession(activeSession.planId)
                    },
                    onViewPlanDetail = { planId ->
                        onNavigateToPlanDetail(planId)
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            } else {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToPlans,
                    icon = {
                        Icon(
                            imageVector = Lucide.Play,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Nuevo entrenamiento",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    shape = CircleShape,
                    containerColor = TenyoJadePrimary,
                    contentColor = TenyoOnJadePrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                )
            }
        }
    }

    Crossfade(
        targetState = uiState.isLoading,
        animationSpec = tween(durationMillis = 350),
        label = "HomeDashboardCrossfade"
    ) { loading ->
        if (loading) {
            HomeDashboardSkeleton(
                contentPadding = contentPadding,
                modifier = modifier
            )
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(TenyoObsidianBackground)
            ) {
                // Cabecera superior minimalista y fluida
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TENYO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.5.sp
                            ),
                            color = TenyoJadePrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hola, ${uiState.preferences.userName.ifBlank { "Atleta" }}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TenyoTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                }

                // Contenido scrolleable
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 16.dp,
                        bottom = contentPadding.calculateBottomPadding() + if (activeSession != null) 72.dp else 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // 1. Tarjeta de Perfil Fisico Activo (Peso, Talla, IMC, Foco)
                    item {
                        HomeActiveProfileCard(
                            preferences = uiState.preferences,
                            onSelectFocusCategory = onNavigateToCategoryExercises
                        )
                    }

                    // 2. Resumen Semanal (Estilo Lyfta: Entrenamientos, Duracion, Volumen)
                    item {
                        HomeWeeklySummaryCard(
                            progressSummary = uiState.progressSummary,
                            onViewMore = onNavigateToProgress
                        )
                    }

                    // 3. Historial de Entrenamientos Recientes (Feed tipo Lyfta con ejercicios completados)
                    if (uiState.completedSessions.isNotEmpty()) {
                        val hasCompletedToday = uiState.completedSessions.any { session ->
                            session.endedAtEpochMillis?.let { endedAt ->
                                val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = endedAt }
                                val cal2 = java.util.Calendar.getInstance()
                                cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                                        cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
                            } ?: false
                        }

                        item {
                            Text(
                                text = if (hasCompletedToday) "Entrenamiento completado hoy" else "Último entrenamiento completado",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )
                        }

                        items(
                            items = uiState.completedSessions.take(2),
                            key = { it.id }
                        ) { session ->
                            HomeRecentWorkoutsCard(
                                session = session,
                                userName = uiState.preferences.userName
                            )
                        }
                    } else {
                        // Solo si el usuario es nuevo y no ha completado ningun entrenamiento, mostramos la tarjeta de bienvenida a su plan
                        item {
                            TodayWorkoutHeroCard(
                                activePlan = uiState.activePlan,
                                onStartWorkout = {
                                    if (uiState.activePlan != null) {
                                        onNavigateToPlanDetail(uiState.activePlan.id)
                                    } else {
                                        onNavigateToPlans()
                                    }
                                },
                                onViewPlanDetail = {
                                    if (uiState.activePlan != null) {
                                        onNavigateToPlanDetail(uiState.activePlan.id)
                                    } else {
                                        onNavigateToPlans()
                                    }
                                }
                            )
                        }
                    }

                    // 5. Carrusel de Categorias Musculares
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Categorías musculares",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TenyoTextPrimary
                                )
                                Text(
                                    text = "Ver todas",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TenyoJadePrimary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = onNavigateToCategories)
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = uiState.categories,
                                    key = { it.category.name }
                                ) { summary ->
                                    HomeCategoryChipCard(
                                        summary = summary,
                                        onClick = { onNavigateToCategoryExercises(summary.category) }
                                    )
                                }
                            }
                        }
                    }

                    // 6. Encabezado de Ejercicios Clave
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ejercicios clave del protocolo",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary
                            )
                            Text(
                                text = "Explorar",
                                style = MaterialTheme.typography.labelLarge,
                                color = TenyoJadePrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSearchExercises("") }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 7. Lista de Ejercicios Clave
                    items(
                        items = uiState.featuredExercises,
                        key = { it.id }
                    ) { exercise ->
                        FeaturedExerciseRowCard(
                            exercise = exercise,
                            languageCode = uiState.preferences.languageCode,
                            onClick = { onNavigateToExerciseDetail(exercise.id) },
                            onToggleFavorite = { onToggleFavorite(exercise.id) }
                        )
                    }
                }
            }
        }
    }
}
