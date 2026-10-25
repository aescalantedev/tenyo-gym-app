package com.aescalantedev.tenyo.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Gestor de pilas de navegacion multiples (Multiple Back Stacks) para Jetpack Navigation 3.
 *
 * Basado en el patron oficial `TopLevelBackStack` de Android Architecture para Navigation 3:
 * - Mantiene una pila independiente por cada pestana de primer nivel ([TopLevelDestination]).
 * - Conserva el historial interno de cada pestana cuando el usuario cambia entre secciones.
 * - Expone una lista reactiva aplanada ([backStack]) consumida directamente por `NavDisplay`.
 */
class TopLevelBackStack<T : Any>(startKey: T) {

    private val defaultKey: T = startKey

    private var topLevelStacks: LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set

    val backStack: SnapshotStateList<T> = mutableStateListOf(startKey)

    val currentDestination: T
        get() = backStack.lastOrNull() ?: topLevelKey

    private fun updateBackStack() {
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }
    }

    /**
     * Cambia a una ruta de nivel superior (pestana del BottomBar) preservando su historial.
     * Si el usuario pulsa nuevamente la pestana activa estando en una subpantalla, vuelve a la raiz de esa pestana.
     */
    fun switchTopLevel(key: T) {
        val currentStack = topLevelStacks[key]
        if (currentStack == null) {
            topLevelStacks[key] = mutableStateListOf(key)
        } else {
            if (topLevelKey == key && currentStack.size > 1) {
                currentStack.clear()
                currentStack.add(key)
            }
            topLevelStacks.remove(key)?.let { existing ->
                topLevelStacks[key] = existing
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    /**
     * Reemplaza toda la pila de navegacion con un nuevo destino raiz (por ejemplo, al completar el Onboarding).
     */
    fun resetRoot(newRootKey: T) {
        topLevelStacks.clear()
        topLevelStacks[newRootKey] = mutableStateListOf(newRootKey)
        topLevelKey = newRootKey
        updateBackStack()
    }

    /**
     * Apila un destino secundario dentro de la pila de la pestana activa actual.
     */
    fun navigate(key: T) {
        val activeStack = topLevelStacks[topLevelKey]
        if (activeStack != null) {
            if (activeStack.lastOrNull() != key) {
                activeStack.add(key)
            }
        } else {
            topLevelStacks[topLevelKey] = mutableStateListOf(key)
        }
        updateBackStack()
    }

    /**
     * Desapila el ultimo destino visitado. Si era la raiz de una pestana secundaria, regresa a la pestana previa.
     */
    fun popBack(): Boolean {
        val currentStack = topLevelStacks[topLevelKey] ?: return false
        if (currentStack.size <= 1 && topLevelStacks.size <= 1) {
            return false
        }
        val removedKey = currentStack.removeLastOrNull()
        if (currentStack.isEmpty() || removedKey == topLevelKey) {
            topLevelStacks.remove(topLevelKey)
            if (topLevelStacks.isEmpty()) {
                topLevelStacks[defaultKey] = mutableStateListOf(defaultKey)
            }
            topLevelKey = topLevelStacks.keys.last()
        }
        updateBackStack()
        return true
    }
}
