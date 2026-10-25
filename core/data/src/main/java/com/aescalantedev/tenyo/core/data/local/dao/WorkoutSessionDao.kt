package com.aescalantedev.tenyo.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSessionEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSetLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Objeto de acceso a datos (DAO) para las tablas `workout_sessions` y `workout_set_logs`.
 * Soporta tanto la ejecucion en vivo de entrenamientos como el calculo de metricas de progreso.
 */
@Dao
interface WorkoutSessionDao {

    @Query("SELECT COUNT(*) FROM workout_sessions")
    suspend fun countSessions(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<WorkoutSessionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetLogs(setLogs: List<WorkoutSetLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetLog(setLog: WorkoutSetLogEntity): Long

    @Query(
        """
        SELECT * FROM workout_sessions
        WHERE status IN ('IN_PROGRESS', 'PAUSED')
        ORDER BY started_at_epoch_millis DESC
        LIMIT 1
        """
    )
    fun observeActiveSession(): Flow<WorkoutSessionEntity?>

    @Query(
        """
        SELECT * FROM workout_sessions
        WHERE status IN ('IN_PROGRESS', 'PAUSED')
        ORDER BY started_at_epoch_millis DESC
        LIMIT 1
        """
    )
    suspend fun getActiveSession(): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    fun observeSessionById(sessionId: String): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): WorkoutSessionEntity?

    @Query(
        """
        SELECT * FROM workout_set_logs
        WHERE session_id = :sessionId
        ORDER BY id ASC, set_number ASC
        """
    )
    fun observeSetLogsForSession(sessionId: String): Flow<List<WorkoutSetLogEntity>>

    @Query(
        """
        SELECT * FROM workout_set_logs
        WHERE session_id = :sessionId
        ORDER BY id ASC, set_number ASC
        """
    )
    suspend fun getSetLogsForSession(sessionId: String): List<WorkoutSetLogEntity>

    @Query("SELECT * FROM workout_set_logs WHERE id = :setLogId LIMIT 1")
    suspend fun getSetLogById(setLogId: Long): WorkoutSetLogEntity?

    @Query(
        """
        UPDATE workout_set_logs
        SET actual_reps = :actualReps,
            weight_kg = :weightKg,
            is_completed = :isCompleted,
            completed_at_epoch_millis = :completedAtEpochMillis,
            is_pr = :isPr
        WHERE id = :setLogId
        """
    )
    suspend fun updateSetLog(
        setLogId: Long,
        actualReps: Int,
        weightKg: Double,
        isCompleted: Boolean,
        completedAtEpochMillis: Long?,
        isPr: Boolean = false
    )

    @Query("DELETE FROM workout_set_logs WHERE id = :setLogId")
    suspend fun deleteSetLog(setLogId: Long)

    @Query("DELETE FROM workout_set_logs WHERE session_id = :sessionId AND exercise_id = :exerciseId")
    suspend fun deleteExerciseFromSession(sessionId: String, exerciseId: String)

    @Query(
        """
        UPDATE workout_set_logs
        SET exercise_id = :newExerciseId,
            exercise_name = :newExerciseName,
            body_part = :newBodyPart
        WHERE session_id = :sessionId AND exercise_id = :oldExerciseId
        """
    )
    suspend fun replaceExerciseInSession(
        sessionId: String,
        oldExerciseId: String,
        newExerciseId: String,
        newExerciseName: String,
        newBodyPart: String
    )

    @Query(
        """
        UPDATE workout_set_logs
        SET rest_seconds = :restSeconds
        WHERE session_id = :sessionId AND exercise_id = :exerciseId
        """
    )
    suspend fun updateExerciseRestSeconds(sessionId: String, exerciseId: String, restSeconds: Int)

    @Query(
        """
        UPDATE workout_set_logs
        SET note = :note
        WHERE session_id = :sessionId AND exercise_id = :exerciseId
        """
    )
    suspend fun updateExerciseNote(sessionId: String, exerciseId: String, note: String?)

    @Query(
        """
        SELECT COALESCE(MAX(set_number), 0) FROM workout_set_logs
        WHERE session_id = :sessionId AND exercise_id = :exerciseId
        """
    )
    suspend fun getMaxSetNumber(sessionId: String, exerciseId: String): Int

    @Query(
        """
        SELECT * FROM workout_set_logs
        WHERE exercise_id = :exerciseId
          AND set_number = :setNumber
          AND is_completed = 1
          AND session_id != :currentSessionId
        ORDER BY completed_at_epoch_millis DESC
        LIMIT 1
        """
    )
    suspend fun getPreviousCompletedSet(
        exerciseId: String,
        setNumber: Int,
        currentSessionId: String
    ): WorkoutSetLogEntity?

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM workout_set_logs WHERE session_id = :sessionId")
    suspend fun deleteSetLogsForSession(sessionId: String)

    @Query(
        """
        DELETE FROM workout_set_logs
        WHERE session_id IN (
            SELECT id FROM workout_sessions WHERE status IN ('IN_PROGRESS', 'PAUSED')
        )
        """
    )
    suspend fun deleteActiveSessionsSetLogs()

    @Query("DELETE FROM workout_sessions WHERE status IN ('IN_PROGRESS', 'PAUSED')")
    suspend fun deleteAllActiveSessions()

    @Transaction
    suspend fun discardSessionAndSetLogs(sessionId: String) {
        deleteSetLogsForSession(sessionId)
        deleteSession(sessionId)
    }

    @Transaction
    suspend fun discardAllActiveSessions() {
        deleteActiveSessionsSetLogs()
        deleteAllActiveSessions()
    }

    @Query(
        """
        UPDATE workout_sessions
        SET current_exercise_index = :exerciseIndex
        WHERE id = :sessionId
        """
    )
    suspend fun updateCurrentExerciseIndex(sessionId: String, exerciseIndex: Int)

    @Query(
        """
        UPDATE workout_sessions
        SET status = :status,
            elapsed_seconds = :elapsedSeconds
        WHERE id = :sessionId
        """
    )
    suspend fun updateSessionStatusAndElapsed(
        sessionId: String,
        status: String,
        elapsedSeconds: Long
    )

    @Query(
        """
        UPDATE workout_sessions
        SET status = 'COMPLETED',
            ended_at_epoch_millis = :endedAtEpochMillis,
            elapsed_seconds = :elapsedSeconds,
            total_volume_kg = :totalVolumeKg,
            estimated_calories_kcal = :estimatedCaloriesKcal
        WHERE id = :sessionId
        """
    )
    suspend fun markSessionCompleted(
        sessionId: String,
        endedAtEpochMillis: Long,
        elapsedSeconds: Long,
        totalVolumeKg: Double,
        estimatedCaloriesKcal: Int
    )

    @Query(
        """
        SELECT * FROM workout_sessions
        WHERE status = 'COMPLETED'
        ORDER BY started_at_epoch_millis DESC
        """
    )
    fun observeCompletedSessions(): Flow<List<WorkoutSessionEntity>>

    @Query(
        """
        SELECT * FROM workout_set_logs
        WHERE is_completed = 1
        ORDER BY completed_at_epoch_millis DESC, id DESC
        """
    )
    fun observeAllCompletedSetLogs(): Flow<List<WorkoutSetLogEntity>>

    @Query(
        """
        SELECT * FROM workout_set_logs
        WHERE exercise_id = :exerciseId AND is_completed = 1
        ORDER BY completed_at_epoch_millis DESC, id DESC
        """
    )
    fun observeCompletedSetLogsForExercise(exerciseId: String): Flow<List<WorkoutSetLogEntity>>
}
