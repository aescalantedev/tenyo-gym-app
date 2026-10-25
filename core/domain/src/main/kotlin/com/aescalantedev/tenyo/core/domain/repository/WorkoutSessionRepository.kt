package com.aescalantedev.tenyo.core.domain.repository

import com.aescalantedev.tenyo.core.domain.model.ExerciseHistoryEntry
import com.aescalantedev.tenyo.core.domain.model.ExerciseRecord
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para gestionar Sesiones de Entrenamiento en vivo y su historial.
 */
interface WorkoutSessionRepository {
    /**
     * Observa la sesión de entrenamiento activa en curso, si existe.
     */
    fun observeActiveSession(): Flow<WorkoutSession?>

    /**
     * Observa el historial de sesiones de entrenamiento completadas recientemente.
     */
    fun observeCompletedSessions(limit: Int = 5): Flow<List<WorkoutSession>>

    /**
     * Observa una sesión de entrenamiento específica por su `sessionId`.
     */
    fun observeSessionById(sessionId: String): Flow<WorkoutSession?>

    /**
     * Inicia una nueva sesión de entrenamiento a partir de un Plan o de un Ejercicio individual,
     * o inicia un entrenamiento libre en blanco si ambos son nulos.
     * Devuelve el `sessionId` generado.
     */
    suspend fun startSession(
        planId: String? = null,
        singleExerciseId: String? = null,
        dayOfWeek: Int? = null
    ): String

    /**
     * Actualiza los valores de repeticiones, peso (`kg`) y estado completado de una serie específica.
     */
    suspend fun updateSetLog(
        setLogId: Long,
        actualReps: Int,
        weightKg: Double,
        isCompleted: Boolean
    )

    /**
     * Elimina una serie individual por su identificador (usado por Swipe-to-Dismiss).
     */
    suspend fun deleteSet(setLogId: Long)

    /**
     * Elimina un ejercicio completo y todas sus series de la sesión activa.
     */
    suspend fun deleteExercise(sessionId: String, exerciseId: String)

    /**
     * Añade un nuevo ejercicio a la sesión activa con una cantidad predeterminada de series.
     */
    suspend fun addExerciseToSession(sessionId: String, exerciseId: String, setsCount: Int = 3)

    /**
     * Reemplaza un ejercicio de la sesión activa por otro preservando la sesión.
     */
    suspend fun replaceExerciseInSession(sessionId: String, oldExerciseId: String, newExerciseId: String)

    /**
     * Actualiza el tiempo de descanso configurado para un ejercicio en la sesión activa.
     */
    suspend fun updateExerciseRestSeconds(sessionId: String, exerciseId: String, restSeconds: Int)

    /**
     * Actualiza la nota asociada a un ejercicio dentro de la sesión activa.
     */
    suspend fun updateExerciseNote(sessionId: String, exerciseId: String, note: String?)

    /**
     * Añade una serie adicional para un ejercicio dentro de la sesión activa.
     */
    suspend fun addExtraSet(sessionId: String, exerciseId: String)

    /**
     * Cambia el índice del ejercicio enfocado actualmente en la sesión activa.
     */
    suspend fun updateCurrentExerciseIndex(sessionId: String, exerciseIndex: Int)

    /**
     * Alterna entre estado `IN_PROGRESS` y `PAUSED`, guardando el tiempo transcurrido.
     */
    suspend fun togglePauseSession(sessionId: String, elapsedSeconds: Long)

    /**
     * Descarta la sesión activa eliminando su registro temporal.
     */
    suspend fun discardSession(sessionId: String)

    /**
     * Descarta cualquier sesión activa o en pausa residual existente.
     */
    suspend fun discardActiveSession()

    /**
     * Finaliza la sesión activa, calcula el volumen total (`kg`) y las calorías estimadas,
     * e incrementa el progreso del plan asociado. Si [updatePlanValues] es verdadero,
     * actualiza los pesos y repeticiones del plan semanal con las nuevas marcas alcanzadas.
     */
    suspend fun completeSession(
        sessionId: String,
        elapsedSeconds: Long,
        updatePlanValues: Boolean = false
    )

    /**
     * Observa el record personal histórico registrado para un ejercicio.
     */
    fun observeExerciseRecord(exerciseId: String): Flow<ExerciseRecord?>

    /**
     * Observa el historial de sesiones completadas en las que se realizó este ejercicio.
     */
    fun observeExerciseHistory(exerciseId: String): Flow<List<ExerciseHistoryEntry>>
}
