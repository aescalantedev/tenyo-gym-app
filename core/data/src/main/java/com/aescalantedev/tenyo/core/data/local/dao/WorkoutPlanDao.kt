package com.aescalantedev.tenyo.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aescalantedev.tenyo.core.data.local.entity.PlanExerciseCrossRefEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanDayEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

/**
 * Objeto de acceso a datos (DAO) para las tablas `workout_plans`, `workout_plan_days`
 * y `workout_plan_exercises`.
 * Gestiona la persistencia de planes, distribución semanal y ejercicios asignados.
 */
@Dao
interface WorkoutPlanDao {

    @Query("SELECT COUNT(*) FROM workout_plans")
    suspend fun countPlans(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<WorkoutPlanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanDays(days: List<WorkoutPlanDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanDay(day: WorkoutPlanDayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExerciseCrossRefs(crossRefs: List<PlanExerciseCrossRefEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExerciseCrossRef(crossRef: PlanExerciseCrossRefEntity)

    @Query("SELECT * FROM workout_plans ORDER BY is_active_plan DESC, is_user_plan DESC, id ASC")
    fun observeAllPlans(): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT * FROM workout_plans WHERE is_active_plan = 1 LIMIT 1")
    fun observeActivePlan(): Flow<WorkoutPlanEntity?>

    @Query("SELECT * FROM workout_plans WHERE id = :planId LIMIT 1")
    fun observePlanById(planId: String): Flow<WorkoutPlanEntity?>

    @Query("SELECT * FROM workout_plans WHERE id = :planId LIMIT 1")
    suspend fun getPlanById(planId: String): WorkoutPlanEntity?

    @Query("SELECT * FROM workout_plans ORDER BY is_user_plan DESC, id ASC")
    suspend fun getAllPlans(): List<WorkoutPlanEntity>

    @Query("SELECT * FROM workout_plan_days WHERE plan_id = :planId ORDER BY day_of_week ASC")
    fun observePlanDays(planId: String): Flow<List<WorkoutPlanDayEntity>>

    @Query("SELECT * FROM workout_plan_days WHERE plan_id = :planId ORDER BY day_of_week ASC")
    suspend fun getPlanDays(planId: String): List<WorkoutPlanDayEntity>

    @Query("SELECT * FROM workout_plan_days ORDER BY plan_id ASC, day_of_week ASC")
    fun observeAllPlanDays(): Flow<List<WorkoutPlanDayEntity>>

    @Query("SELECT * FROM workout_plan_exercises WHERE plan_id = :planId ORDER BY day_of_week ASC, sort_order ASC")
    fun observePlanExerciseRefs(planId: String): Flow<List<PlanExerciseCrossRefEntity>>

    @Query("SELECT * FROM workout_plan_exercises WHERE plan_id = :planId ORDER BY day_of_week ASC, sort_order ASC")
    suspend fun getPlanExerciseRefs(planId: String): List<PlanExerciseCrossRefEntity>

    @Query("SELECT * FROM workout_plan_exercises ORDER BY plan_id ASC, day_of_week ASC, sort_order ASC")
    fun observeAllPlanExerciseRefs(): Flow<List<PlanExerciseCrossRefEntity>>

    @Query("UPDATE workout_plans SET is_active_plan = 0")
    suspend fun clearAllActivePlanFlags()

    @Query("UPDATE workout_plans SET is_active_plan = 1, is_user_plan = 1 WHERE id = :planId")
    suspend fun markPlanAsActive(planId: String)

    @Transaction
    suspend fun setActivePlanTransaction(planId: String) {
        clearAllActivePlanFlags()
        markPlanAsActive(planId)
    }

    @Query(
        """
        UPDATE workout_plans
        SET completed_sessions_count = completed_sessions_count + 1
        WHERE id = :planId
        """
    )
    suspend fun incrementCompletedSessions(planId: String)

    @Query(
        """
        UPDATE workout_plans
        SET reminder_hour = :hour, reminder_minute = :minute, reminder_enabled = :enabled
        WHERE id = :planId
        """
    )
    suspend fun updatePlanReminders(planId: String, hour: Int, minute: Int, enabled: Boolean)

    @Query(
        """
        UPDATE workout_plan_days
        SET name = :name, is_rest_day = :isRestDay
        WHERE id = :dayId
        """
    )
    suspend fun updatePlanDay(dayId: String, name: String, isRestDay: Boolean)

    @Query(
        """
        UPDATE workout_plan_exercises
        SET target_sets = :targetSets, target_reps = :targetReps, rest_seconds = :restSeconds, suggested_weight_kg = :suggestedWeightKg
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updatePlanExerciseConfig(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int,
        targetReps: String,
        restSeconds: Int,
        suggestedWeightKg: Double
    )

    @Query(
        """
        UPDATE workout_plan_exercises
        SET note = :note
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updatePlanExerciseNote(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        note: String?
    )

    @Query(
        """
        SELECT * FROM workout_plan_exercises
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun getPlanExerciseRef(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String
    ): PlanExerciseCrossRefEntity?

    @Query(
        """
        UPDATE workout_plan_exercises
        SET set_weights = :setWeights, set_reps = :setReps, suggested_weight_kg = :suggestedWeightKg, target_reps = :targetReps
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updatePlanExerciseSetConfigs(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setWeights: String?,
        setReps: String?,
        suggestedWeightKg: Double,
        targetReps: String
    )

    @Query(
        """
        UPDATE workout_plan_exercises
        SET target_sets = :targetSets, set_weights = :setWeights, set_reps = :setReps
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updateSetsAndSerializedConfigs(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int,
        setWeights: String?,
        setReps: String?
    )

    @androidx.room.Transaction
    suspend fun updateIndividualSetParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int,
        newReps: String,
        newWeightKg: Double
    ) {
        val ref = getPlanExerciseRef(planId, dayOfWeek, exerciseId) ?: return
        val currentWeights = ref.setWeights?.split(",")?.map { it.trim() }?.toMutableList()
            ?: MutableList(ref.targetSets) { ref.suggestedWeightKg.toString() }
        while (currentWeights.size < ref.targetSets) {
            currentWeights.add(ref.suggestedWeightKg.toString())
        }

        val currentReps = ref.setReps?.split(",")?.map { it.trim() }?.toMutableList()
            ?: MutableList(ref.targetSets) { ref.targetReps }
        while (currentReps.size < ref.targetSets) {
            currentReps.add(ref.targetReps)
        }

        val index = (setNumber - 1).coerceIn(0, currentWeights.lastIndex)
        currentWeights[index] = newWeightKg.toString()
        currentReps[index] = newReps.trim()

        val newWeightsStr = currentWeights.joinToString(",")
        val newRepsStr = currentReps.joinToString(",")
        val firstWeight = currentWeights.firstOrNull()?.toDoubleOrNull() ?: newWeightKg
        val firstReps = currentReps.firstOrNull() ?: newReps

        updatePlanExerciseSetConfigs(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            setWeights = newWeightsStr,
            setReps = newRepsStr,
            suggestedWeightKg = firstWeight,
            targetReps = firstReps
        )
    }

    @androidx.room.Transaction
    suspend fun deleteIndividualPlanSet(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        setNumber: Int
    ) {
        val ref = getPlanExerciseRef(planId, dayOfWeek, exerciseId) ?: return
        if (ref.targetSets <= 1) return
        val newSets = ref.targetSets - 1
        val currentWeights = ref.setWeights?.split(",")?.map { it.trim() }?.toMutableList()
            ?: MutableList(ref.targetSets) { ref.suggestedWeightKg.toString() }
        val currentReps = ref.setReps?.split(",")?.map { it.trim() }?.toMutableList()
            ?: MutableList(ref.targetSets) { ref.targetReps }

        val index = (setNumber - 1).coerceIn(0, currentWeights.lastIndex)
        if (index in currentWeights.indices) {
            currentWeights.removeAt(index)
        }
        if (index in currentReps.indices) {
            currentReps.removeAt(index)
        }

        updateSetsAndSerializedConfigs(
            planId = planId,
            dayOfWeek = dayOfWeek,
            exerciseId = exerciseId,
            targetSets = newSets,
            setWeights = currentWeights.take(newSets).joinToString(","),
            setReps = currentReps.take(newSets).joinToString(",")
        )
    }

    @Query(
        """
        UPDATE workout_plan_exercises
        SET target_sets = :targetSets
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updatePlanExerciseSets(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetSets: Int
    )

    @Query(
        """
        UPDATE workout_plan_exercises
        SET rest_seconds = :restSeconds
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updatePlanExerciseRestSeconds(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        restSeconds: Int
    )

    @Query(
        """
        UPDATE workout_plan_exercises
        SET target_reps = :targetReps, suggested_weight_kg = :suggestedWeightKg
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updatePlanExerciseParameters(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        targetReps: String,
        suggestedWeightKg: Double
    )

    @Query(
        """
        UPDATE workout_plans
        SET title = :title
        WHERE id = :planId
        """
    )
    suspend fun updatePlanTitle(planId: String, title: String)

    @Query(
        """
        UPDATE workout_plan_exercises
        SET sort_order = :sortOrder
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun updatePlanExerciseSortOrder(
        planId: String,
        dayOfWeek: Int,
        exerciseId: String,
        sortOrder: Int
    )

    @androidx.room.Transaction
    suspend fun reorderPlanExercises(
        planId: String,
        dayOfWeek: Int,
        orderedExerciseIds: List<String>
    ) {
        orderedExerciseIds.forEachIndexed { index, exerciseId ->
            updatePlanExerciseSortOrder(
                planId = planId,
                dayOfWeek = dayOfWeek,
                exerciseId = exerciseId,
                sortOrder = index + 1
            )
        }
    }

    @Query(
        """
        DELETE FROM workout_plan_exercises
        WHERE plan_id = :planId AND day_of_week = :dayOfWeek AND exercise_id = :exerciseId
        """
    )
    suspend fun deletePlanExerciseCrossRef(planId: String, dayOfWeek: Int, exerciseId: String)

    @Query("DELETE FROM workout_plans WHERE id = :planId")
    suspend fun deletePlanById(planId: String)

    @Query("DELETE FROM workout_plan_days WHERE plan_id = :planId")
    suspend fun deletePlanDays(planId: String)

    @Query("DELETE FROM workout_plan_exercises WHERE plan_id = :planId")
    suspend fun deletePlanExercises(planId: String)

    @androidx.room.Transaction
    suspend fun deleteCompletePlan(planId: String) {
        deletePlanExercises(planId)
        deletePlanDays(planId)
        deletePlanById(planId)
    }
}
