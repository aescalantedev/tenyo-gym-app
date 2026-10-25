package com.aescalantedev.tenyo.navigation

import androidx.lifecycle.ViewModel

/**
 * ViewModel encargado del estado global de navegacion de Tenyo.
 *
 * Mantiene:
 * - La pila de navegacion multiple de Navigation 3 ([TopLevelBackStack]).
 * - El destino actual y el control de visibilidad del BottomBar.
 */
class NavViewModel : ViewModel() {

    val backStackManager: TopLevelBackStack<TenyoDestination> = TopLevelBackStack(SplashRoute)

    val currentDestination: TenyoDestination
        get() = backStackManager.currentDestination

    val activeTopLevel: TopLevelDestination
        get() = (backStackManager.topLevelKey as? TopLevelDestination) ?: HomeRoute

    val showBottomBar: Boolean
        get() = currentDestination is TopLevelDestination

    fun switchTopLevel(tab: TopLevelDestination) {
        backStackManager.switchTopLevel(tab)
    }

    fun navigate(destination: TenyoDestination) {
        backStackManager.navigate(destination)
    }

    fun popBack(): Boolean {
        return backStackManager.popBack()
    }

    fun resetRoot(newRoot: TenyoDestination) {
        backStackManager.resetRoot(newRoot)
    }

    fun navigateToActiveWorkout() {
        if (currentDestination !is ActiveWorkoutRoute) {
            navigate(ActiveWorkoutRoute())
        }
    }
}
