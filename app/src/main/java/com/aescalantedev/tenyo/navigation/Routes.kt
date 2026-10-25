package com.aescalantedev.tenyo.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Definicion tipada de todas las rutas de navegacion de Tenyo utilizando Jetpack Navigation 3 ([NavKey]).
 *
 * Cubre los flujos de la aplicacion:
 * 1. [SplashRoute]: Pantalla de inicio y verificacion de estado de sesion / catalogo.
 * 2. [WelcomeRoute]: Onboarding de 3 pasos y perfilado inicial.
 * 3. [HomeRoute]: Dashboard principal de entrenamiento diario.
 * 4. [CategoriesRoute]: Explorador de categorias musculares.
 * 5. [WorkoutPlansRoute]: Gestor de planes de entrenamiento ("Mis planes" y catalogo).
 * 6. [ProgressRoute]: Panel de evolucion fisica, rango Tenyo y records personales (PRs).
 * 7. [ProfileRoute]: Ajustes de perfil, idioma y temas visuales.
 * 8. [ExerciseListRoute]: Listado de ejercicios por categoria o busqueda libre.
 * 9. [ExerciseDetailRoute]: Ficha tecnica del ejercicio con animacion demostrativa.
 * 10. [ActiveWorkoutRoute]: Sesion de entrenamiento en vivo con cronometro y registro de series.
 */
sealed interface TenyoDestination : NavKey

/**
 * Interfaz marcadora para los destinos principales de la barra de navegacion inferior (`BottomBar`).
 */
sealed interface TopLevelDestination : TenyoDestination {
    val labelEs: String
}

@Serializable
data object SplashRoute : TenyoDestination

@Serializable
data object WelcomeRoute : TenyoDestination

@Serializable
data object HomeRoute : TopLevelDestination {
    override val labelEs: String = "Inicio"
}

@Serializable
data object CategoriesRoute : TopLevelDestination {
    override val labelEs: String = "Ejercicios"
}

@Serializable
data object WorkoutPlansRoute : TopLevelDestination {
    override val labelEs: String = "Planes"
}

@Serializable
data object ProgressRoute : TopLevelDestination {
    override val labelEs: String = "Progreso"
}

@Serializable
data object ProfileRoute : TopLevelDestination {
    override val labelEs: String = "Perfil"
}

@Serializable
data class ExerciseListRoute(
    val bodyPartRawKey: String? = null,
    val initialSearchQuery: String = "",
    val equipment: String? = null,
    val isSelectionMode: Boolean = false,
    val targetPlanId: String? = null,
    val targetDayOfWeek: Int? = null
) : TenyoDestination

@Serializable
data class ExerciseDetailRoute(
    val exerciseId: String
) : TenyoDestination

@Serializable
data class PlanDetailRoute(
    val planId: String
) : TenyoDestination

@Serializable
data class ActiveWorkoutRoute(
    val planId: String? = null,
    val singleExerciseId: String? = null,
    val dayOfWeek: Int? = null
) : TenyoDestination

/**
 * Lista ordenada de las 5 pestanas principales de la barra inferior.
 */
val TOP_LEVEL_DESTINATIONS: List<TopLevelDestination> = listOf(
    HomeRoute,
    CategoriesRoute,
    WorkoutPlansRoute,
    ProgressRoute,
    ProfileRoute
)
