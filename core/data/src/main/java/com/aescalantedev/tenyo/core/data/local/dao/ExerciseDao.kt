package com.aescalantedev.tenyo.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aescalantedev.tenyo.core.data.local.entity.BodyPartCountProjection
import com.aescalantedev.tenyo.core.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Objeto de acceso a datos (DAO) para la tabla `exercises`.
 * Provee consultas reactivas mediante [Flow] para busqueda, filtrado por categoria,
 * equipamiento, ejercicios destacados y favoritos.
 */
@Dao
interface ExerciseDao {

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun countExercises(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Query(
        """
        SELECT body_part, COUNT(*) AS exercise_count
        FROM exercises
        GROUP BY body_part
        """
    )
    fun observeCountsByBodyPart(): Flow<List<BodyPartCountProjection>>

    @Query(
        """
        SELECT * FROM exercises
        WHERE (:bodyPart IS NULL OR LOWER(body_part) = LOWER(:bodyPart))
          AND (:targetMuscle IS NULL OR LOWER(target_muscle) LIKE '%' || LOWER(:targetMuscle) || '%')
          AND (:equipment IS NULL OR LOWER(equipment) = LOWER(:equipment))
          AND (:onlyFavorites = 0 OR is_favorite = 1)
          AND (
              :query = ''
              OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
              OR LOWER(target_muscle) LIKE '%' || LOWER(:query) || '%'
              OR LOWER(muscle_group) LIKE '%' || LOWER(:query) || '%'
              OR LOWER(equipment) LIKE '%' || LOWER(:query) || '%'
          )
        ORDER BY is_favorite DESC, is_custom DESC, id ASC
        LIMIT :limit
        """
    )
    fun observeFilteredExercises(
        query: String,
        bodyPart: String?,
        targetMuscle: String?,
        equipment: String?,
        onlyFavorites: Boolean,
        limit: Int
    ): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT * FROM exercises
        WHERE id IN ('0025', '0032', '0043', '0027', '0091', '0031', '0334', '0472')
        ORDER BY is_favorite DESC, id ASC
        LIMIT :limit
        """
    )
    fun observeFeaturedExercises(limit: Int): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :exerciseId LIMIT 1")
    fun observeExerciseById(exerciseId: String): Flow<ExerciseEntity?>

    @Query("SELECT * FROM exercises WHERE id = :exerciseId LIMIT 1")
    suspend fun getExerciseById(exerciseId: String): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE id IN (:exerciseIds)")
    suspend fun getExercisesByIds(exerciseIds: List<String>): List<ExerciseEntity>

    @Query(
        """
        SELECT DISTINCT equipment FROM exercises
        WHERE (:bodyPart IS NULL OR LOWER(body_part) = LOWER(:bodyPart))
        ORDER BY equipment ASC
        """
    )
    fun observeDistinctEquipment(bodyPart: String?): Flow<List<String>>

    @Query("UPDATE exercises SET is_favorite = NOT is_favorite WHERE id = :exerciseId")
    suspend fun toggleFavorite(exerciseId: String)
}
