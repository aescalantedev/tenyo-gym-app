package com.aescalantedev.tenyo.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aescalantedev.tenyo.core.data.local.entity.ExercisePersonalRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para consultar y actualizar los records personales (PRs) de cada ejercicio.
 */
@Dao
interface ExercisePersonalRecordDao {

    @Query("SELECT * FROM exercise_personal_records WHERE exercise_id = :exerciseId LIMIT 1")
    suspend fun getByExerciseId(exerciseId: String): ExercisePersonalRecordEntity?

    @Query("SELECT * FROM exercise_personal_records WHERE exercise_id = :exerciseId LIMIT 1")
    fun observeByExerciseId(exerciseId: String): Flow<ExercisePersonalRecordEntity?>

    @Query("SELECT * FROM exercise_personal_records")
    fun observeAll(): Flow<List<ExercisePersonalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: ExercisePersonalRecordEntity)

    @Query("DELETE FROM exercise_personal_records WHERE exercise_id = :exerciseId")
    suspend fun deleteByExerciseId(exerciseId: String)
}
