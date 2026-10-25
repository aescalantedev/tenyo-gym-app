package com.aescalantedev.tenyo.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aescalantedev.tenyo.navigation.CategoriesRoute
import com.aescalantedev.tenyo.navigation.HomeRoute
import com.aescalantedev.tenyo.navigation.ProfileRoute
import com.aescalantedev.tenyo.navigation.ProgressRoute
import com.aescalantedev.tenyo.navigation.TOP_LEVEL_DESTINATIONS
import com.aescalantedev.tenyo.navigation.TopLevelDestination
import com.aescalantedev.tenyo.navigation.WorkoutPlansRoute
import com.aescalantedev.tenyo.ui.theme.TenyoJadeContainer
import com.aescalantedev.tenyo.ui.theme.TenyoJadePrimary
import com.aescalantedev.tenyo.ui.theme.TenyoSurfaceContainerLow
import com.aescalantedev.tenyo.ui.theme.TenyoTextTertiary
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User

/**
 * Barra de navegacion inferior para cambiar entre las 5 secciones principales de Tenyo:
 * Inicio, Ejercicios (Categorias), Planes, Progreso y Perfil.
 */
@Composable
fun TenyoBottomNavigationBar(
    currentTopLevel: TopLevelDestination,
    onSelectTopLevel: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = TenyoSurfaceContainerLow,
        tonalElevation = 0.dp
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { destination ->
            val selected = destination == currentTopLevel
            val icon = when (destination) {
                HomeRoute -> Lucide.House
                CategoriesRoute -> Lucide.Dumbbell
                WorkoutPlansRoute -> Lucide.Calendar
                ProgressRoute -> Lucide.ChartBar
                ProfileRoute -> Lucide.User
            }
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTopLevel(destination) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = destination.labelEs,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = destination.labelEs,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TenyoJadePrimary,
                    selectedTextColor = TenyoJadePrimary,
                    indicatorColor = TenyoJadeContainer.copy(alpha = 0.65f),
                    unselectedIconColor = TenyoTextTertiary,
                    unselectedTextColor = TenyoTextTertiary
                )
            )
        }
    }
}
