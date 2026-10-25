package com.aescalantedev.tenyo.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene

/**
 * Transiciones de animacion premium para el contenedor [NavDisplay] de Navigation 3.
 */
object TenyoTransitions {

    val forwardTransition: AnimatedContentTransitionScope<Scene<TenyoDestination>>.() -> ContentTransform = {
        val isToOrFromWorkout = targetState.key is ActiveWorkoutRoute || initialState.key is ActiveWorkoutRoute
        if (isToOrFromWorkout) {
            fadeIn(
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) togetherWith fadeOut(
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        } else {
            (fadeIn(
                animationSpec = tween(durationMillis = 440, easing = FastOutSlowInEasing)
            ) + slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 6 },
                animationSpec = tween(durationMillis = 440, easing = FastOutSlowInEasing)
            ) + scaleIn(
                initialScale = 0.96f,
                animationSpec = tween(durationMillis = 440, easing = FastOutSlowInEasing)
            )) togetherWith (fadeOut(
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            ) + slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 8 },
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            ) + scaleOut(
                targetScale = 0.98f,
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            ))
        }
    }

    val popTransition: AnimatedContentTransitionScope<Scene<TenyoDestination>>.() -> ContentTransform = {
        val isToOrFromWorkout = targetState.key is ActiveWorkoutRoute || initialState.key is ActiveWorkoutRoute
        if (isToOrFromWorkout) {
            fadeIn(
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) togetherWith fadeOut(
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        } else {
            (fadeIn(
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            ) + slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 6 },
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )) togetherWith (fadeOut(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 6 },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ))
        }
    }

    val predictivePopTransition: AnimatedContentTransitionScope<Scene<TenyoDestination>>.(Int) -> ContentTransform = {
        val isToOrFromWorkout = targetState.key is ActiveWorkoutRoute || initialState.key is ActiveWorkoutRoute
        if (isToOrFromWorkout) {
            fadeIn(
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) togetherWith fadeOut(
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        } else {
            (fadeIn(
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            ) + slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 6 },
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )) togetherWith (fadeOut(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 6 },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ))
        }
    }
}
