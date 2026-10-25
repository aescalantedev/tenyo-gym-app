package com.aescalantedev.tenyo.ui.screens.plans

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aescalantedev.tenyo.core.domain.model.PlanGoal
import com.aescalantedev.tenyo.core.domain.model.WorkoutPlan
import com.aescalantedev.tenyo.navigation.ProvideFab
import com.aescalantedev.tenyo.ui.components.TenyoSwipeToDismiss
import com.aescalantedev.tenyo.ui.theme.TenyoError
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerHigh
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextPrimary
import com.aescalantedev.tenyo.ui.theme.TenyoTextSecondary
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.aescalantedev.tenyo.ui.screens.plans.components.ActivePlanHeroCard
import com.aescalantedev.tenyo.ui.screens.plans.components.HtmlAppBg
import com.aescalantedev.tenyo.ui.screens.plans.components.HtmlAppPrimary
import com.aescalantedev.tenyo.ui.screens.plans.components.HtmlBorderSubtle
import com.aescalantedev.tenyo.ui.screens.plans.components.PlanUnderlineTabs
import com.aescalantedev.tenyo.ui.screens.plans.components.WorkoutPlanCard
import com.aescalantedev.tenyo.ui.screens.plans.components.WorkoutPlansSkeleton
import com.aescalantedev.tenyo.ui.screens.plans.viewmodel.WorkoutPlansUiState
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.X

/**
 * Pantalla de Planes de Entrenamiento (Stitch 07 - Diseño Minimalista).
 *
 * Características:
 * - Cabecera condensada "Planes" con botón de búsqueda circular.
 * - Pestañas con línea indicadora lila (#9B83F5) ("Mis rutinas" / "Explorar catálogo").
 * - Hero Card compacto para el plan activo con botón de Play directo.
 * - Chips de objetivo de entrenamiento con bordes elegantes.
 * - Listado de tarjetas de rutina compactas con recuadro "N DÍAS" y menú de 3 puntos.
 * - Botón Flotante (FAB) "+ Nueva Rutina" integrado con [ProvideFab].
 */
@Composable
fun WorkoutPlansScreen(
    uiState: WorkoutPlansUiState,
    onSelectTab: (Int) -> Unit,
    onSelectGoal: (PlanGoal) -> Unit,
    onActivatePlan: (String) -> Unit,
    onStartPlanSession: (String) -> Unit,
    onNavigateToPlanDetail: (String) -> Unit,
    onCreateNewPlan: () -> Unit,
    onDeletePlan: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var planToDelete by remember { mutableStateOf<WorkoutPlan?>(null) }

    if (uiState.isLoading) {
        WorkoutPlansSkeleton(
            contentPadding = contentPadding,
            modifier = modifier
        )
        return
    }

    // Registro del FAB dinámico en el Scaffold raíz
    ProvideFab(position = FabPosition.End) {
        ExtendedFloatingActionButton(
            onClick = onCreateNewPlan,
            icon = {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            text = {
                Text(
                    text = "Nueva Rutina",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = HtmlAppPrimary,
            contentColor = HtmlAppBg,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        )
    }

    val rawPlans = if (uiState.selectedTabIndex == 0) {
        uiState.myPlans
    } else {
        uiState.catalogPlans
    }

    val displayedPlans = remember(rawPlans, searchQuery) {
        if (searchQuery.isBlank()) rawPlans
        else rawPlans.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.subtitle.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TenyoObsidianBackground)
    ) {
        // 1. Cabecera superior minimalista: "Planes" + Buscar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Planes",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                ),
                color = Color.White
            )

            Surface(
                shape = CircleShape,
                color = TenyoSurfaceContainerLow,
                border = BorderStroke(1.dp, HtmlBorderSubtle),
                modifier = Modifier.size(42.dp)
            ) {
                IconButton(onClick = {
                    isSearchVisible = !isSearchVisible
                    if (!isSearchVisible) searchQuery = ""
                }) {
                    Icon(
                        imageVector = if (isSearchVisible) Lucide.X else Lucide.Search,
                        contentDescription = "Buscar",
                        tint = TenyoTextPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        // Campo de búsqueda expandible
        AnimatedVisibility(
            visible = isSearchVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar rutina o protocolo...", color = TenyoTextTertiary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TenyoTextPrimary,
                    unfocusedTextColor = TenyoTextPrimary,
                    focusedContainerColor = TenyoSurfaceContainerHigh,
                    unfocusedContainerColor = TenyoSurfaceContainerHigh,
                    focusedBorderColor = HtmlAppPrimary,
                    unfocusedBorderColor = HtmlBorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        // 2. Selector de pestañas con línea indicadora ("Mis rutinas" / "Explorar catálogo")
        PlanUnderlineTabs(
            selectedTabIndex = uiState.selectedTabIndex,
            onSelectTab = onSelectTab,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        // 3. Contenido con scroll
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = contentPadding.calculateBottomPadding() + 84.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Card del Plan Activo (compacto)
            uiState.activePlan?.let { activePlan ->
                item {
                    ActivePlanHeroCard(
                        plan = activePlan,
                        onNavigateToDetail = { onNavigateToPlanDetail(activePlan.id) },
                        onStartSession = { onStartPlanSession(activePlan.id) }
                    )
                }
            }

            // Filtros horizontales por objetivo
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    val filterGoals = listOf(
                        PlanGoal.ALL to "Todos",
                        PlanGoal.STRENGTH to "Fuerza",
                        PlanGoal.HYPERTROPHY to "Hipertrofia",
                        PlanGoal.CONDITIONING to "Resistencia",
                        PlanGoal.HEAVENLY_RESTRICTION to "Restricción Celestial"
                    )

                    items(
                        items = filterGoals,
                        key = { it.first.name }
                    ) { (goal, label) ->
                        val isSelected = uiState.selectedGoal == goal
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = TenyoSurfaceContainerLow,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) HtmlAppPrimary else HtmlBorderSubtle
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectGoal(goal) }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                color = if (isSelected) HtmlAppPrimary else TenyoTextSecondary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }

            // Lista de planes compactos o estado vacío
            if (displayedPlans.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = TenyoSurfaceContainerLow,
                        border = BorderStroke(1.dp, HtmlBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Lucide.Dumbbell,
                                contentDescription = null,
                                tint = HtmlAppPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (uiState.selectedTabIndex == 0) {
                                    "Aún no tienes rutinas personalizadas"
                                } else {
                                    "No hay planes disponibles"
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = TenyoTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.selectedTabIndex == 0) {
                                    "Toca '+ Nueva Rutina' para crear tu semana desde cero."
                                } else {
                                    "Prueba a seleccionar otro objetivo de entrenamiento."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TenyoTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(
                    items = displayedPlans,
                    key = { it.id }
                ) { plan ->
                    TenyoSwipeToDismiss(
                        onDismiss = {
                            planToDelete = plan
                        },
                        cornerRadius = 18.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkoutPlanCard(
                            plan = plan,
                            onNavigateToDetail = { onNavigateToPlanDetail(plan.id) },
                            onActivatePlan = { onActivatePlan(plan.id) },
                            onStartSession = { onStartPlanSession(plan.id) },
                            onDeletePlan = { planToDelete = plan }
                        )
                    }
                }
            }
        }

        // Diálogo de confirmación para eliminar la rutina
        if (planToDelete != null) {
            val target = planToDelete!!
            AlertDialog(
                onDismissRequest = { planToDelete = null },
                containerColor = TenyoSurfaceContainerLow,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "Eliminar rutina",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TenyoTextPrimary
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar '${target.title}'? Se borrarán todos los días y ejercicios configurados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TenyoTextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val id = target.id
                            planToDelete = null
                            onDeletePlan(id)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TenyoError,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Eliminar", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { planToDelete = null }) {
                        Text("Cancelar", color = TenyoTextSecondary)
                    }
                }
            )
        }
    }
}
