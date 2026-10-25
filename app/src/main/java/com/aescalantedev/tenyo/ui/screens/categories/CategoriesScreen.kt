package com.aescalantedev.tenyo.ui.screens.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.CategoryRegion
import com.aescalantedev.tenyo.ui.components.TenyoSearchBar
import com.aescalantedev.tenyo.ui.screens.categories.components.AnatomicalMuscleCard
import com.aescalantedev.tenyo.ui.screens.categories.components.CategoriesSkeleton
import com.aescalantedev.tenyo.ui.screens.categories.components.EquipmentGridCard
import com.aescalantedev.tenyo.ui.screens.categories.components.PRIMARY_ANATOMICAL_MUSCLES
import com.aescalantedev.tenyo.ui.screens.categories.components.PRIMARY_EQUIPMENT_ITEMS
import com.aescalantedev.tenyo.ui.screens.categories.viewmodel.CategoriesUiState
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoOnJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoOutlineSubtle
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.SlidersHorizontal
import kotlinx.coroutines.launch

/**
 * Pantalla 03: Explorador de Ejercicios y Categorías (Rediseñada con estética Lyfta / Tenyo HUD).
 *
 * Características del Sistema de Diseño Tenyo:
 * 1. 100% reactiva al tema activo (Tenyo Dark, Restricción Esmeralda, Monocromo).
 * 2. Barra de búsqueda superior estilizada y directa.
 * 3. Selector de secciones con indicadores activos sincronizados con [TenyoJadePrimary].
 * 4. Rejilla anatómica de 3 columnas de los 12 grupos musculares con renders 3D.
 * 5. Rejilla de equipamiento con ilustraciones vectoriales reactivas al tema.
 * 6. Filtrado interactivo en tiempo real al escribir en la barra de búsqueda.
 */
@Composable
fun CategoriesScreen(
    uiState: CategoriesUiState,
    onSelectRegion: (CategoryRegion) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (BodyPartCategory) -> Unit,
    onSelectCategoryWithFilter: (BodyPartCategory, String) -> Unit = { cat, _ -> onSelectCategory(cat) },
    onSelectEquipment: (String) -> Unit = {},
    onOpenAllExercisesSearch: (String) -> Unit,
    onNavigateToPrograms: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var activeTabIndex by rememberSaveable { mutableIntStateOf(0) } // 0: Músculos, 1: Por equipo

    // Filtro interactivo de grupos musculares y equipamiento según searchQuery
    val filteredMuscles = remember(uiState.searchQuery) {
        if (uiState.searchQuery.isBlank()) PRIMARY_ANATOMICAL_MUSCLES
        else PRIMARY_ANATOMICAL_MUSCLES.filter {
            it.labelEs.contains(uiState.searchQuery, ignoreCase = true) ||
            it.searchFilter.contains(uiState.searchQuery, ignoreCase = true) ||
            it.id.contains(uiState.searchQuery, ignoreCase = true)
        }
    }

    val filteredEquipment = remember(uiState.searchQuery) {
        if (uiState.searchQuery.isBlank()) PRIMARY_EQUIPMENT_ITEMS
        else PRIMARY_EQUIPMENT_ITEMS.filter {
            it.labelEs.contains(uiState.searchQuery, ignoreCase = true) ||
            it.equipmentKey.contains(uiState.searchQuery, ignoreCase = true) ||
            it.id.contains(uiState.searchQuery, ignoreCase = true)
        }
    }

    Crossfade(
        targetState = uiState.isLoading,
        animationSpec = tween(durationMillis = 350),
        label = "CategoriesScreenCrossfade"
    ) { loading ->
        if (loading) {
            CategoriesSkeleton(
                contentPadding = contentPadding,
                modifier = modifier
            )
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(TenyoObsidianBackground)
            ) {
        // =====================================================================
        // Cabecera Superior Fija con colores del Theme
        // =====================================================================
        Surface(
            color = TenyoObsidianBackground,
            border = BorderStroke(1.dp, TenyoOutlineSubtle.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Barra de Búsqueda Directa Estilo Tenyo
                TenyoSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholder = "Buscar ejercicios o músculos..."
                )

                // Acceso rápido para buscar el texto en el catálogo completo de 1,324 ejercicios
                AnimatedVisibility(
                    visible = uiState.searchQuery.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        onClick = { onOpenAllExercisesSearch(uiState.searchQuery) },
                        shape = RoundedCornerShape(10.dp),
                        color = TenyoSurfaceContainerLow,
                        border = BorderStroke(1.dp, TenyoJadePrimary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Buscar \"${uiState.searchQuery}\" en los 1,320+ ejercicios",
                                style = MaterialTheme.typography.bodySmall,
                                color = TenyoTextPrimary
                            )
                            Icon(
                                imageVector = Lucide.ArrowRight,
                                contentDescription = null,
                                tint = TenyoJadePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pestañas Superiores: Músculos | Por equipo (La sección de Programas tiene su propia pestaña dedicada en la barra inferior)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pestaña 1: Músculos (Activa con indicador reactivo al tema)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                activeTabIndex = 0
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                            .padding(vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.Dumbbell,
                                contentDescription = "Músculos",
                                tint = if (activeTabIndex == 0) TenyoJadePrimary else TenyoTextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Músculos",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (activeTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (activeTabIndex == 0) TenyoTextPrimary else TenyoTextTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        // Indicador inferior reactivo a TenyoJadePrimary
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(2.5.dp)
                                .background(
                                    if (activeTabIndex == 0) TenyoJadePrimary else Color.Transparent,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }

                    // Pestaña 2: Por equipo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                activeTabIndex = 1
                                coroutineScope.launch {
                                    val muscleRows = (filteredMuscles.size + 2) / 3
                                    listState.animateScrollToItem((muscleRows + 1).coerceAtLeast(0))
                                }
                            }
                            .padding(vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.SlidersHorizontal,
                                contentDescription = "Por equipo",
                                tint = if (activeTabIndex == 1) TenyoJadePrimary else TenyoTextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Por equipo",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (activeTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (activeTabIndex == 1) TenyoTextPrimary else TenyoTextTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(2.5.dp)
                                .background(
                                    if (activeTabIndex == 1) TenyoJadePrimary else Color.Transparent,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }

        // =====================================================================
        // Contenido Scrolleable con Tematización Completa
        // =====================================================================
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // -----------------------------------------------------------------
            // Cabecera de Sección "Grupos musculares"
            // -----------------------------------------------------------------
            if (filteredMuscles.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grupos musculares",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TenyoTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = TenyoSurfaceContainerHigh
                        ) {
                            Text(
                                text = "${filteredMuscles.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoJadePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Rejilla de Músculos (elementos en filas de a 3)
                items(items = filteredMuscles.chunked(3)) { rowMuscles ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowMuscles.forEach { muscle ->
                            AnatomicalMuscleCard(
                                item = muscle,
                                onClick = {
                                    onSelectCategoryWithFilter(muscle.category, muscle.searchFilter)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        val remainder = 3 - rowMuscles.count()
                        if (remainder > 0) {
                            repeat(remainder) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // Cabecera de Sección "Por equipo"
            // -----------------------------------------------------------------
            if (filteredEquipment.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 20.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Por equipamiento",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TenyoTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = TenyoSurfaceContainerHigh
                        ) {
                            Text(
                                text = "${filteredEquipment.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TenyoJadePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Rejilla de Equipamiento (elementos en filas de a 3)
                items(items = filteredEquipment.chunked(3)) { rowEquipment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowEquipment.forEach { equip ->
                            EquipmentGridCard(
                                item = equip,
                                onClick = {
                                    onSelectEquipment(equip.equipmentKey)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        val remainder = 3 - rowEquipment.count()
                        if (remainder > 0) {
                            repeat(remainder) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // Estado vacío cuando no hay coincidencias de búsqueda
            // -----------------------------------------------------------------
            if (filteredMuscles.isEmpty() && filteredEquipment.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = TenyoSurfaceContainerLow,
                        border = BorderStroke(1.dp, TenyoOutlineSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Lucide.Search,
                                contentDescription = null,
                                tint = TenyoTextTertiary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Sin categorías para \"${uiState.searchQuery}\"",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "¿Deseas buscar este término en los ejercicios individuales?",
                                style = MaterialTheme.typography.bodySmall,
                                color = TenyoTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onOpenAllExercisesSearch(uiState.searchQuery) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TenyoJadePrimary,
                                    contentColor = TenyoOnJadePrimary
                                )
                            ) {
                                Text(
                                    text = "Buscar en todo el catálogo",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    }
}
}
