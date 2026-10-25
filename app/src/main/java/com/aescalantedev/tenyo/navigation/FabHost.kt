package com.aescalantedev.tenyo.navigation

import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue

/**
 * State holder para el FloatingActionButton dinámico del Scaffold raíz.
 *
 * Mantiene la propiedad (ownership) del FAB actual para evitar condiciones de carrera
 * durante transiciones entre pantallas (ej: Home -> Planes -> Detalle).
 */
class FabState {

    private var currentOwner: Any? = null

    var content by mutableStateOf<(@Composable () -> Unit)?>(null)
        private set

    var position by mutableStateOf(FabPosition.End)
        private set

    fun setFab(
        owner: Any,
        position: FabPosition = FabPosition.End,
        content: (@Composable () -> Unit)?
    ) {
        this.currentOwner = owner
        this.position = position
        this.content = content
    }

    fun clear(owner: Any? = null) {
        // Solo limpia si la pantalla que solicita la limpieza es la dueña actual del FAB.
        // Esto evita que el onDispose de una pantalla anterior borre el FAB de la nueva pantalla.
        if (owner == null || this.currentOwner === owner) {
            this.content = null
            this.currentOwner = null
            this.position = FabPosition.End
        }
    }
}

/**
 * CompositionLocal que expone el [FabState] global en el árbol de vistas de Compose.
 */
val LocalFabState = compositionLocalOf { FabState() }

/**
 * Composable helper para registrar un FAB en el Scaffold raíz de forma segura.
 *
 * - Utiliza [rememberUpdatedState] para que los cambios de estado dentro del contenido no
 *   disparen recomposiciones del efecto ni causen parpadeos.
 * - Registra un token único de propietario para que las transiciones de salida (onDispose)
 *   no destruyan el FAB recién montado por la pantalla entrante.
 */
@Composable
fun ProvideFab(
    position: FabPosition = FabPosition.End,
    content: @Composable () -> Unit
) {
    val fabState = LocalFabState.current
    val currentContent by rememberUpdatedState(content)
    val owner = remember { Any() }

    DisposableEffect(owner, position) {
        fabState.setFab(
            owner = owner,
            position = position,
            content = { currentContent() }
        )
        onDispose {
            fabState.clear(owner)
        }
    }
}
