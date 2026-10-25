package com.aescalantedev.tenyo.ui.screens.exercises

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.Exercise
import com.aescalantedev.tenyo.ui.components.TenyoSearchBar
import com.aescalantedev.tenyo.ui.screens.exercises.components.CarouselMuscleOption
import com.aescalantedev.tenyo.ui.screens.exercises.components.CreateCustomExerciseDialog
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseFilterBottomSheet
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseGridCard
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseListItemCard
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseListSkeleton
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseMuscleCarousel
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseQuickInfoDialog
import com.aescalantedev.tenyo.ui.screens.exercises.components.ExerciseSubheaderBar
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseListUiState
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseSortOrder
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.RotateCcw
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.SlidersHorizontal
import com.composables.icons.lucide.X

/**
 * Pantalla de Catálogo de Ejercicios con diseño minimalista premium.
 *
 * Incluye:
 * 1. Barra superior con botón cerrar (X), título y acciones (Búsqueda, Filtros con badge, Crear "+").
 * 2. Carrusel horizontal superior de grupos musculares con botón de bookmark/favoritos a la izquierda.
 * 3. Barra de subtítulo para ordenar ("Recién Realizado", "A-Z", etc.) y alternar vista cuadrícula/lista.
 * 4. Cuadrícula de 2 columnas de tarjetas de ejercicios (o lista) con visor multimedia e indicadores de favoritos y guía rápida.
 * 5. Filtro desplegable en ModalBottomSheet y diálogo de creación de ejercicios personalizados.
 */
@Composable
fun ExerciseListScreen(
    uiState: ExerciseListUiState,
    onBack: () -> Unit,
    onSelectMuscleOption: (CarouselMuscleOption?) -> Unit = {},
    onSelectBodyPart: (BodyPartCategory?) -> Unit,
    onSelectEquipment: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleOnlyFavorites: () -> Unit,
    onToggleExerciseFavorite: (String) -> Unit,
    onSelectExercise: (String) -> Unit,
    onCreateCustomExercise: (
        name: String,
        bodyPart: BodyPartCategory,
        equipment: String,
        targetMuscle: String,
        instructions: String
    ) -> Unit,
    onSelectSortOrder: (ExerciseSortOrder) -> Unit = {},
    onToggleViewMode: () -> Unit = {},
    onClearFilters: () -> Unit = {},
    isSelectionMode: Boolean = false,
    onConfirmSelection: (List<String>) -> Unit = {},
    onOpenExerciseDetail: (String) -> Unit = {},
    onUpdateScrollPosition: (Int, Int) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    var selectedExerciseIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    var isSearchExpanded by rememberSaveable { mutableStateOf(false) }
    var quickInfoExercise by remember { mutableStateOf<Exercise?>(null) }

    // Estados de scroll para preservar la posición exacta de navegación
    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = uiState.scrollPositionIndex,
        initialFirstVisibleItemScrollOffset = uiState.scrollPositionOffset
    )
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.scrollPositionIndex,
        initialFirstVisibleItemScrollOffset = uiState.scrollPositionOffset
    )

    LaunchedEffect(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset) {
        if (uiState.isGridView && (gridState.firstVisibleItemIndex != 0 || gridState.firstVisibleItemScrollOffset != 0)) {
            onUpdateScrollPosition(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset)
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        if (!uiState.isGridView && (listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0)) {
            onUpdateScrollPosition(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
    }

    // Diálogo para crear ejercicio personalizado
    if (showCreateDialog) {
        CreateCustomExerciseDialog(
            initialBodyPart = uiState.selectedBodyPart ?: BodyPartCategory.CHEST,
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, bodyPart, equipment, target, instructions ->
                showCreateDialog = false
                onCreateCustomExercise(name, bodyPart, equipment, target, instructions)
            }
        )
    }

    // ModalBottomSheet para filtros avanzados (equipamiento, grupo muscular, favoritos)
    if (showFilterSheet) {
        ExerciseFilterBottomSheet(
            availableEquipment = uiState.availableEquipment,
            selectedEquipment = uiState.selectedEquipment,
            selectedBodyPart = uiState.selectedBodyPart,
            onlyFavorites = uiState.onlyFavorites,
            onSelectEquipment = onSelectEquipment,
            onSelectBodyPart = onSelectBodyPart,
            onToggleOnlyFavorites = onToggleOnlyFavorites,
            onClearFilters = onClearFilters,
            onDismiss = { showFilterSheet = false }
        )
    }

    // Diálogo de información rápida (?)
    quickInfoExercise?.let { exercise ->
        ExerciseQuickInfoDialog(
            exercise = exercise,
            onDismiss = { quickInfoExercise = null },
            onOpenFullDetail = {
                quickInfoExercise = null
                onSelectExercise(exercise.id)
            }
        )
    }

    Crossfade(
        targetState = uiState.isLoading,
        animationSpec = tween(durationMillis = 350),
        label = "ExerciseListCrossfade"
    ) { loading ->
        if (loading) {
            ExerciseListSkeleton(
                contentPadding = contentPadding,
                modifier = modifier
            )
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(TenyoObsidianBackground)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
        // =========================================================================
        // 1. Barra Superior Minimalista (X, Título, Búsqueda, Filtro, Plus)
        // =========================================================================
        Surface(
            color = TenyoSurfaceContainerLow,
            border = BorderStroke(1.dp, TenyoOutlineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de cerrar / volver (X)
                    Surface(
                        shape = CircleShape,
                        color = TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onBack)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Cerrar",
                                tint = TenyoTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Título central sobrio
                    Text(
                        text = if (isSelectionMode) {
                            uiState.selectedBodyPart?.nameEs ?: "Seleccionar ejercicio"
                        } else {
                            uiState.selectedBodyPart?.nameEs ?: "Ejercicios"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Acciones derechas: Buscar, Filtrar (+ badge), Añadir (+)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Toggle de Búsqueda
                        IconButton(
                            onClick = { isSearchExpanded = !isSearchExpanded },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Search,
                                contentDescription = "Buscar ejercicios",
                                tint = if (isSearchExpanded || uiState.searchQuery.isNotBlank()) {
                                    TenyoJadePrimary
                                } else {
                                    TenyoTextPrimary
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Botón de Filtros con insignia de conteo activo
                        IconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (uiState.activeFilterCount > 0) {
                                        Badge(
                                            containerColor = TenyoJadePrimary,
                                            contentColor = TenyoOnJadePrimary
                                        ) {
                                            Text("${uiState.activeFilterCount}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Lucide.SlidersHorizontal,
                                    contentDescription = "Filtros",
                                    tint = if (uiState.activeFilterCount > 0) {
                                        TenyoJadePrimary
                                    } else {
                                        TenyoTextPrimary
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Botón de Añadir ejercicio personalizado (+)
                        IconButton(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Plus,
                                contentDescription = "Crear ejercicio",
                                tint = TenyoTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Barra de búsqueda expandible
                AnimatedVisibility(
                    visible = isSearchExpanded || uiState.searchQuery.isNotBlank(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 10.dp)) {
                        TenyoSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            placeholder = "Buscar ejercicio, músculo o equipo..."
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // =========================================================================
        // 2. Carrusel Horizontal de Músculos & Acceso Directo a Favoritos
        // =========================================================================
        ExerciseMuscleCarousel(
            selectedOptionId = uiState.selectedMuscleOptionId,
            onlyFavorites = uiState.onlyFavorites,
            onToggleOnlyFavorites = onToggleOnlyFavorites,
            onSelectMuscleOption = onSelectMuscleOption
        )

        // =========================================================================
        // 3. Barra de Subtítulo: Ordenamiento ("Recién Realizado") y Alternador Grid/Lista
        // =========================================================================
        ExerciseSubheaderBar(
            sortOrder = uiState.sortOrder,
            isGridView = uiState.isGridView,
            onSelectSortOrder = onSelectSortOrder,
            onToggleViewMode = onToggleViewMode
        )

        // =========================================================================
        // 4. Contenido Principal: Cuadrícula (2 columnas) o Lista Vertical
        // =========================================================================
        val listBottomPadding = contentPadding.calculateBottomPadding() + 80.dp

        if (uiState.exercises.isEmpty()) {
            // Estado vacío limpio y minimalista
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TenyoSurfaceContainerLow,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.Dumbbell,
                                contentDescription = null,
                                tint = TenyoTextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Text(
                        text = "No se encontraron ejercicios",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Prueba seleccionando otro grupo muscular o restableciendo los filtros de búsqueda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TenyoTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TenyoSurfaceContainerHigh,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onClearFilters)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.RotateCcw,
                                contentDescription = null,
                                tint = TenyoJadePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Restablecer filtros",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TenyoJadePrimary
                            )
                        }
                    }
                }
            }
        } else if (uiState.isGridView) {
            // Vista de cuadrícula (2 columnas) fiel a la propuesta visual
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 6.dp,
                    bottom = if (isSelectionMode && selectedExerciseIds.isNotEmpty()) listBottomPadding + 70.dp else listBottomPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.exercises,
                    key = { it.id }
                ) { exercise ->
                    val isSelected = exercise.id in selectedExerciseIds
                    ExerciseGridCard(
                        exercise = exercise,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                selectedExerciseIds = if (isSelected) {
                                    selectedExerciseIds - exercise.id
                                } else {
                                    selectedExerciseIds + exercise.id
                                }
                            } else {
                                onSelectExercise(exercise.id)
                            }
                        },
                        onToggleFavorite = { onToggleExerciseFavorite(exercise.id) },
                        onQuickInfoClick = { onOpenExerciseDetail(exercise.id) }
                    )
                }
            }
        } else {
            // Vista de lista alternativa (1 columna)
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 6.dp,
                    bottom = if (isSelectionMode && selectedExerciseIds.isNotEmpty()) listBottomPadding + 70.dp else listBottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.exercises,
                    key = { it.id }
                ) { exercise ->
                    val isSelected = exercise.id in selectedExerciseIds
                    ExerciseListItemCard(
                        exercise = exercise,
                        isSelected = isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                selectedExerciseIds = if (isSelected) {
                                    selectedExerciseIds - exercise.id
                                } else {
                                    selectedExerciseIds + exercise.id
                                }
                            } else {
                                onSelectExercise(exercise.id)
                            }
                        },
                        onToggleFavorite = { onToggleExerciseFavorite(exercise.id) },
                        onQuickInfoClick = { onOpenExerciseDetail(exercise.id) }
                    )
                }
            }
        }
    }

    // Boton flotante inferior prominente para agregar multiples ejercicios seleccionados de golpe
    AnimatedVisibility(
        visible = isSelectionMode && selectedExerciseIds.isNotEmpty(),
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        Button(
            onClick = { onConfirmSelection(selectedExerciseIds.toList()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = TenyoJadePrimary,
                contentColor = TenyoOnJadePrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            val count = selectedExerciseIds.size
            val buttonText = if (count == 1) "Añadir 1 ejercicio" else "Añadir $count ejercicios"
            Icon(
                imageVector = Lucide.Check,
                contentDescription = null,
                tint = TenyoOnJadePrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buttonText,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
    }
    }
}
}
