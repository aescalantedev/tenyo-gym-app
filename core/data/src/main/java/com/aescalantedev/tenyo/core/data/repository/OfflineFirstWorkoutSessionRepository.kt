package com.aescalantedev.tenyo.core.data.repository

import com.aescalantedev.tenyo.core.data.local.dao.ExerciseDao
import com.aescalantedev.tenyo.core.data.local.dao.ExercisePersonalRecordDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutPlanDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutSessionDao
import com.aescalantedev.tenyo.core.data.local.entity.ExercisePersonalRecordEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSessionEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSetLogEntity
import com.aescalantedev.tenyo.core.data.mapper.TenyoEntityMappers.toDomain
import com.aescalantedev.tenyo.core.data.media.MediaUrlResolver
import com.aescalantedev.tenyo.core.domain.model.ExerciseHistoryEntry
import com.aescalantedev.tenyo.core.domain.model.ExerciseHistorySet
import com.aescalantedev.tenyo.core.domain.model.ExerciseRecord
import com.aescalantedev.tenyo.core.domain.model.SessionStatus
import com.aescalantedev.tenyo.core.domain.model.WorkoutSession
import com.aescalantedev.tenyo.core.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implementacion Offline-First de [WorkoutSessionRepository].
 *
 * Gestiona el ciclo de vida completo de una sesion de entrenamiento activa (inicio, registro de
 * series, pausa, reanudacion, agregacion dinamica de ejercicios, swipe-to-dismiss y finalizacion).
 */
class OfflineFirstWorkoutSessionRepository(
    private val workoutSessionDao: WorkoutSessionDao,
    private val workoutPlanDao: WorkoutPlanDao,
    private val exerciseDao: ExerciseDao,
    private val exercisePersonalRecordDao: ExercisePersonalRecordDao,
    private val mediaUrlResolver: MediaUrlResolver
) : WorkoutSessionRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeActiveSession(): Flow<WorkoutSession?> {
        return workoutSessionDao.observeActiveSession().flatMapLatest { sessionEntity ->
            if (sessionEntity == null) {
                flowOf(null)
            } else {
                workoutSessionDao.observeSetLogsForSession(sessionEntity.id).mapToDomain(sessionEntity)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCompletedSessions(limit: Int): Flow<List<WorkoutSession>> {
        return workoutSessionDao.observeCompletedSessions().flatMapLatest { entities ->
            val topEntities = entities.take(limit)
            if (topEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                val sessionFlows = topEntities.map { entity ->
                    workoutSessionDao.observeSetLogsForSession(entity.id).map { logs ->
                        entity.toDomain(logs)
                    }
                }
                combine(sessionFlows) { it.toList() }
            }
        }
    }

    override fun observeSessionById(sessionId: String): Flow<WorkoutSession?> {
        return combine(
            workoutSessionDao.observeSessionById(sessionId),
            workoutSessionDao.observeSetLogsForSession(sessionId)
        ) { sessionEntity, setLogs ->
            sessionEntity?.toDomain(setLogs)
        }
    }

    override suspend fun startSession(
        planId: String?,
        singleExerciseId: String?,
        dayOfWeek: Int?
    ): String {
        val existingActive = workoutSessionDao.getActiveSession()
        if (existingActive != null && singleExerciseId == null && (planId == null || existingActive.planId == planId)) {
            return existingActive.id
        }

        // Si ya existia una sesion activa y se inicia una nueva, descartamos la anterior para evitar sesiones zombie
        if (existingActive != null) {
            workoutSessionDao.discardAllActiveSessions()
        }

        val sessionId = "session_${UUID.randomUUID().toString().take(8)}"
        val now = System.currentTimeMillis()

        // Caso 1: Inicio desde un ejercicio individual ("Iniciar ahora")
        if (!singleExerciseId.isNullOrBlank()) {
            val exercise = exerciseDao.getExerciseById(singleExerciseId)
                ?: exerciseDao.getExerciseById("0025")
            val sessionTitle = exercise?.name?.replaceFirstChar { it.uppercase() } ?: "Entrenamiento Libre"

            workoutSessionDao.insertSession(
                WorkoutSessionEntity(
                    id = sessionId,
                    planId = planId,
                    title = sessionTitle,
                    status = SessionStatus.IN_PROGRESS.name,
                    startedAtEpochMillis = now,
                    elapsedSeconds = 0L,
                    currentExerciseIndex = 0
                )
            )

            val setsCount = exercise?.defaultSets ?: 4
            val parsedTargetReps = parseTargetRepsInt(exercise?.defaultReps ?: "10")
            val initialWeight = defaultStartingWeightForEquipment(exercise?.equipment ?: "barbell")
            val restSeconds = exercise?.defaultRestSeconds ?: 90

            val isDumbbell = exercise?.equipment?.contains("dumbbell", ignoreCase = true) == true
            val setLogs = (1..setsCount).map { setNum ->
                val prev = workoutSessionDao.getPreviousCompletedSet(
                    exerciseId = exercise?.id ?: singleExerciseId,
                    setNumber = setNum,
                    currentSessionId = sessionId
                )
                val prevSummary = prev?.let { "${formatWeight(it.weightKg)}kg x ${it.actualReps}" }

                WorkoutSetLogEntity(
                    sessionId = sessionId,
                    exerciseId = exercise?.id ?: singleExerciseId,
                    exerciseName = exercise?.name ?: "Ejercicio",
                    bodyPart = exercise?.bodyPart ?: "chest",
                    setNumber = setNum,
                    targetReps = parsedTargetReps,
                    actualReps = parsedTargetReps,
                    weightKg = initialWeight,
                    isCompleted = false,
                    restSeconds = restSeconds,
                    previousSummary = prevSummary,
                    imageUrl = mediaUrlResolver.resolve(exercise?.imageRelativePath.orEmpty()),
                    multiplier = if (isDumbbell) 2 else 1
                )
            }
            workoutSessionDao.insertSetLogs(setLogs)
            return sessionId
        }

        // Caso 2: Nuevo entrenamiento libre en blanco (sin planId ni ejercicio inicial)
        if (planId.isNullOrBlank()) {
            workoutSessionDao.insertSession(
                WorkoutSessionEntity(
                    id = sessionId,
                    planId = null,
                    title = "Nuevo entrenamiento",
                    status = SessionStatus.IN_PROGRESS.name,
                    startedAtEpochMillis = now,
                    elapsedSeconds = 0L,
                    currentExerciseIndex = 0
                )
            )
            // No se agregan series inicialmente: la pantalla se abre vacia con el boton "Anadir ejercicios"
            return sessionId
        }

        // Caso 3: Inicio desde un Plan estructurado existente
        val planEntity = workoutPlanDao.getPlanById(planId)
        val allPlanRefs = workoutPlanDao.getPlanExerciseRefs(planId)
        val planDays = workoutPlanDao.getPlanDays(planId)

        val targetDayOfWeek = dayOfWeek ?: run {
            val calendarDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
            when (calendarDay) {
                java.util.Calendar.MONDAY -> 1
                java.util.Calendar.TUESDAY -> 2
                java.util.Calendar.WEDNESDAY -> 3
                java.util.Calendar.THURSDAY -> 4
                java.util.Calendar.FRIDAY -> 5
                java.util.Calendar.SATURDAY -> 6
                java.util.Calendar.SUNDAY -> 7
                else -> 1
            }
        }

        val targetDay = planDays.firstOrNull { it.dayOfWeek == targetDayOfWeek }
        val dayRefs = allPlanRefs.filter { it.dayOfWeek == targetDayOfWeek }
        val planRefs = if (dayRefs.isNotEmpty()) dayRefs else allPlanRefs

        val daySubtitle = targetDay?.name?.let { " · $it" }.orEmpty()
        val sessionTitle = (planEntity?.title ?: "Protocolo Tenyo") + daySubtitle
        workoutSessionDao.insertSession(
            WorkoutSessionEntity(
                id = sessionId,
                planId = planId,
                dayOfWeek = targetDayOfWeek,
                title = sessionTitle,
                status = SessionStatus.IN_PROGRESS.name,
                startedAtEpochMillis = now,
                elapsedSeconds = 0L,
                currentExerciseIndex = 0
            )
        )

        val exerciseIds = planRefs.map { it.exerciseId }
        val exercisesById = exerciseDao.getExercisesByIds(exerciseIds).associateBy { it.id }

        val generatedLogs = mutableListOf<WorkoutSetLogEntity>()
        planRefs.sortedBy { it.sortOrder }.forEachIndexed { exIdx, ref ->
            val ex = exercisesById[ref.exerciseId]
            val weightsList = ref.setWeights?.split(",")?.mapNotNull { it.trim().toDoubleOrNull() }.orEmpty()
            val repsList = ref.setReps?.split(",")?.map { it.trim() }.orEmpty()

            for (setNum in 1..ref.targetSets) {
                val setWeight = weightsList.getOrNull(setNum - 1) ?: ref.suggestedWeightKg
                val setRepsStr = repsList.getOrNull(setNum - 1)?.ifBlank { ref.targetReps } ?: ref.targetReps
                val repsInt = parseTargetRepsInt(setRepsStr)

                val prev = workoutSessionDao.getPreviousCompletedSet(
                    exerciseId = ref.exerciseId,
                    setNumber = setNum,
                    currentSessionId = sessionId
                )
                val prevSummary = prev?.let { "${formatWeight(it.weightKg)}kg x ${it.actualReps}" }

                val isDumbbell = ex?.equipment?.contains("dumbbell", ignoreCase = true) == true
                generatedLogs.add(
                    WorkoutSetLogEntity(
                        sessionId = sessionId,
                        exerciseId = ref.exerciseId,
                        exerciseName = ex?.name ?: "Ejercicio ${exIdx + 1}",
                        bodyPart = ex?.bodyPart ?: "chest",
                        setNumber = setNum,
                        targetReps = repsInt,
                        actualReps = repsInt,
                        weightKg = setWeight,
                        isCompleted = false,
                        restSeconds = ref.restSeconds,
                        previousSummary = prevSummary,
                        imageUrl = mediaUrlResolver.resolve(ex?.imageRelativePath.orEmpty()),
                        multiplier = if (isDumbbell) 2 else 1,
                        note = ref.note
                    )
                )
            }
        }

        workoutSessionDao.insertSetLogs(generatedLogs)
        return sessionId
    }

    override suspend fun updateSetLog(
        setLogId: Long,
        actualReps: Int,
        weightKg: Double,
        isCompleted: Boolean
    ) {
        val completedTimestamp = if (isCompleted) System.currentTimeMillis() else null
        var isPr = false

        if (isCompleted && weightKg > 0.0 && actualReps > 0) {
            val currentLog = workoutSessionDao.getSetLogById(setLogId)
            if (currentLog != null) {
                val exId = currentLog.exerciseId
                val existingRecord = exercisePersonalRecordDao.getByExerciseId(exId)
                val current1Rm = weightKg * (1.0 + actualReps / 30.0)
                val currentSetVolume = weightKg * actualReps * currentLog.multiplier

                val isNewMaxWeight = existingRecord == null || weightKg > existingRecord.maxWeightKg
                val isNewMaxRepsAtWeight = existingRecord != null && weightKg == existingRecord.maxWeightKg && actualReps > existingRecord.repsAtMaxWeight
                val isNew1Rm = existingRecord == null || current1Rm > existingRecord.estimated1RmKg

                if (isNewMaxWeight || isNewMaxRepsAtWeight || isNew1Rm) {
                    isPr = true
                    val updatedMaxWeight = maxOf(existingRecord?.maxWeightKg ?: 0.0, weightKg)
                    val updatedRepsAtMax = if (weightKg >= (existingRecord?.maxWeightKg ?: 0.0)) actualReps else (existingRecord?.repsAtMaxWeight ?: actualReps)
                    val updated1Rm = maxOf(existingRecord?.estimated1RmKg ?: 0.0, current1Rm)
                    val updatedVolume = maxOf(existingRecord?.bestSetVolumeKg ?: 0.0, currentSetVolume)

                    exercisePersonalRecordDao.upsert(
                        ExercisePersonalRecordEntity(
                            exerciseId = exId,
                            maxWeightKg = updatedMaxWeight,
                            repsAtMaxWeight = updatedRepsAtMax,
                            estimated1RmKg = updated1Rm,
                            bestSetVolumeKg = updatedVolume,
                            achievedAtEpochMillis = completedTimestamp ?: System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        workoutSessionDao.updateSetLog(
            setLogId = setLogId,
            actualReps = actualReps,
            weightKg = weightKg,
            isCompleted = isCompleted,
            completedAtEpochMillis = completedTimestamp,
            isPr = isPr
        )
    }

    override suspend fun deleteSet(setLogId: Long) {
        workoutSessionDao.deleteSetLog(setLogId)
    }

    override suspend fun deleteExercise(sessionId: String, exerciseId: String) {
        workoutSessionDao.deleteExerciseFromSession(sessionId, exerciseId)
    }

    override suspend fun addExerciseToSession(sessionId: String, exerciseId: String, setsCount: Int) {
        val exercise = exerciseDao.getExerciseById(exerciseId) ?: return
        val targetSets = setsCount.coerceIn(1, 10)
        val repsInt = parseTargetRepsInt(exercise.defaultReps)
        val initialWeight = defaultStartingWeightForEquipment(exercise.equipment)
        val restSecs = exercise.defaultRestSeconds

        val isDumbbell = exercise.equipment.contains("dumbbell", ignoreCase = true)
        val newLogs = (1..targetSets).map { setNum ->
            val prev = workoutSessionDao.getPreviousCompletedSet(
                exerciseId = exerciseId,
                setNumber = setNum,
                currentSessionId = sessionId
            )
            val prevSummary = prev?.let { "${formatWeight(it.weightKg)}kg x ${it.actualReps}" }

            WorkoutSetLogEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                exerciseName = exercise.name,
                bodyPart = exercise.bodyPart,
                setNumber = setNum,
                targetReps = repsInt,
                actualReps = repsInt,
                weightKg = initialWeight,
                isCompleted = false,
                restSeconds = restSecs,
                previousSummary = prevSummary,
                imageUrl = mediaUrlResolver.resolve(exercise.imageRelativePath),
                multiplier = if (isDumbbell) 2 else 1
            )
        }
        workoutSessionDao.insertSetLogs(newLogs)
    }

    override suspend fun replaceExerciseInSession(
        sessionId: String,
        oldExerciseId: String,
        newExerciseId: String
    ) {
        val newExercise = exerciseDao.getExerciseById(newExerciseId) ?: return
        workoutSessionDao.replaceExerciseInSession(
            sessionId = sessionId,
            oldExerciseId = oldExerciseId,
            newExerciseId = newExerciseId,
            newExerciseName = newExercise.name,
            newBodyPart = newExercise.bodyPart
        )
    }

    override suspend fun updateExerciseRestSeconds(
        sessionId: String,
        exerciseId: String,
        restSeconds: Int
    ) {
        workoutSessionDao.updateExerciseRestSeconds(
            sessionId = sessionId,
            exerciseId = exerciseId,
            restSeconds = restSeconds.coerceIn(10, 600)
        )
    }

    override suspend fun updateExerciseNote(
        sessionId: String,
        exerciseId: String,
        note: String?
    ) {
        workoutSessionDao.updateExerciseNote(
            sessionId = sessionId,
            exerciseId = exerciseId,
            note = note?.trim()
        )
    }

    override suspend fun addExtraSet(sessionId: String, exerciseId: String) {
        val currentLogs = workoutSessionDao.getSetLogsForSession(sessionId)
            .filter { it.exerciseId == exerciseId }
        val lastSet = currentLogs.maxByOrNull { it.setNumber }
        val nextSetNumber = (lastSet?.setNumber ?: 0) + 1
        val exercise = exerciseDao.getExerciseById(exerciseId)

        val prev = workoutSessionDao.getPreviousCompletedSet(
            exerciseId = exerciseId,
            setNumber = nextSetNumber,
            currentSessionId = sessionId
        )
        val prevSummary = prev?.let { "${formatWeight(it.weightKg)}kg x ${it.actualReps}" }

        val isDumbbell = lastSet?.multiplier == 2 || exercise?.equipment?.contains("dumbbell", ignoreCase = true) == true
        workoutSessionDao.insertSetLog(
            WorkoutSetLogEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                exerciseName = lastSet?.exerciseName ?: exercise?.name ?: "Ejercicio",
                bodyPart = lastSet?.bodyPart ?: exercise?.bodyPart ?: "chest",
                setNumber = nextSetNumber,
                targetReps = lastSet?.targetReps ?: 10,
                actualReps = lastSet?.actualReps ?: 10,
                weightKg = lastSet?.weightKg ?: 40.0,
                isCompleted = false,
                restSeconds = lastSet?.restSeconds ?: 90,
                note = lastSet?.note,
                previousSummary = prevSummary,
                imageUrl = lastSet?.imageUrl ?: mediaUrlResolver.resolve(exercise?.imageRelativePath.orEmpty()),
                multiplier = if (isDumbbell) 2 else 1
            )
        )
    }

    override suspend fun updateCurrentExerciseIndex(sessionId: String, exerciseIndex: Int) {
        workoutSessionDao.updateCurrentExerciseIndex(sessionId, exerciseIndex)
    }

    override suspend fun togglePauseSession(sessionId: String, elapsedSeconds: Long) {
        val session = workoutSessionDao.getSessionById(sessionId) ?: return
        val nextStatus = if (session.status == SessionStatus.PAUSED.name) {
            SessionStatus.IN_PROGRESS.name
        } else {
            SessionStatus.PAUSED.name
        }
        workoutSessionDao.updateSessionStatusAndElapsed(
            sessionId = sessionId,
            status = nextStatus,
            elapsedSeconds = elapsedSeconds
        )
    }

    override suspend fun discardSession(sessionId: String) {
        workoutSessionDao.discardSessionAndSetLogs(sessionId)
        // Por seguridad, aseguramos que ninguna otra sesion activa en progreso quede huerfana
        workoutSessionDao.discardAllActiveSessions()
    }

    override suspend fun discardActiveSession() {
        workoutSessionDao.discardAllActiveSessions()
    }

    override suspend fun completeSession(
        sessionId: String,
        elapsedSeconds: Long,
        updatePlanValues: Boolean
    ) {
        val session = workoutSessionDao.getSessionById(sessionId) ?: return
        val logs = workoutSessionDao.getSetLogsForSession(sessionId)
        val completedLogs = logs.filter { it.isCompleted }

        val totalVolume = completedLogs.sumOf { it.actualReps * it.weightKg * it.multiplier }
        val durationMinutes = (elapsedSeconds / 60L).coerceAtLeast(1L)
        val estimatedCalories = ((durationMinutes * 7.5) + (completedLogs.size * 12)).toInt()

        workoutSessionDao.markSessionCompleted(
            sessionId = sessionId,
            endedAtEpochMillis = System.currentTimeMillis(),
            elapsedSeconds = elapsedSeconds.coerceAtLeast(1L),
            totalVolumeKg = totalVolume,
            estimatedCaloriesKcal = estimatedCalories
        )

        session.planId?.let { associatedPlanId ->
            workoutPlanDao.incrementCompletedSessions(associatedPlanId)

            // Sobrecarga progresiva: sincronizar las cargas y repeticiones superadas al plan
            if (updatePlanValues && session.dayOfWeek != null && completedLogs.isNotEmpty()) {
                val dayOfWeek = session.dayOfWeek
                val logsByExercise = completedLogs.groupBy { it.exerciseId }
                logsByExercise.forEach { (exerciseId, exerciseSets) ->
                    val sortedSets = exerciseSets.sortedBy { it.setNumber }
                    val weightsStr = sortedSets.joinToString(",") { formatWeight(it.weightKg) }
                    val repsStr = sortedSets.joinToString(",") { it.actualReps.toString() }
                    val setsCount = sortedSets.size
                    val firstWeight = sortedSets.firstOrNull()?.weightKg ?: 0.0
                    val firstReps = sortedSets.firstOrNull()?.actualReps?.toString() ?: "10"

                    workoutPlanDao.updatePlanExerciseSetConfigs(
                        planId = associatedPlanId,
                        dayOfWeek = dayOfWeek,
                        exerciseId = exerciseId,
                        setWeights = weightsStr,
                        setReps = repsStr,
                        suggestedWeightKg = firstWeight,
                        targetReps = firstReps
                    )
                    workoutPlanDao.updateSetsAndSerializedConfigs(
                        planId = associatedPlanId,
                        dayOfWeek = dayOfWeek,
                        exerciseId = exerciseId,
                        targetSets = setsCount,
                        setWeights = weightsStr,
                        setReps = repsStr
                    )
                }
            }
        }
    }

    private fun Flow<List<WorkoutSetLogEntity>>.mapToDomain(
        sessionEntity: WorkoutSessionEntity
    ): Flow<WorkoutSession> = combine(flowOf(sessionEntity), this) { entity, logs ->
        entity.toDomain(logs)
    }

    private fun parseTargetRepsInt(repsString: String): Int {
        val numbers = Regex("\\d+").findAll(repsString).mapNotNull { it.value.toIntOrNull() }.toList()
        return numbers.lastOrNull() ?: 10
    }

    private fun defaultStartingWeightForEquipment(equipment: String): Double =
        when (equipment.lowercase()) {
            "body weight" -> 0.0
            "dumbbell" -> 20.0
            "barbell" -> 60.0
            else -> 30.0
        }

    override fun observeExerciseRecord(exerciseId: String): Flow<ExerciseRecord?> {
        return exercisePersonalRecordDao.observeByExerciseId(exerciseId).map { entity ->
            entity?.let {
                ExerciseRecord(
                    exerciseId = it.exerciseId,
                    maxWeightKg = it.maxWeightKg,
                    repsAtMaxWeight = it.repsAtMaxWeight,
                    estimated1RmKg = it.estimated1RmKg,
                    bestSetVolumeKg = it.bestSetVolumeKg,
                    achievedAtEpochMillis = it.achievedAtEpochMillis
                )
            }
        }
    }

    override fun observeExerciseHistory(exerciseId: String): Flow<List<ExerciseHistoryEntry>> {
        return combine(
            workoutSessionDao.observeCompletedSetLogsForExercise(exerciseId),
            workoutSessionDao.observeCompletedSessions()
        ) { setLogs, completedSessions ->
            if (setLogs.isEmpty()) {
                emptyList()
            } else {
                val sessionsMap = completedSessions.associateBy { it.id }
                setLogs.groupBy { it.sessionId }.mapNotNull { (sessionId, sets) ->
                    val session = sessionsMap[sessionId]
                    val date = session?.startedAtEpochMillis
                        ?: sets.mapNotNull { it.completedAtEpochMillis }.minOrNull()
                        ?: System.currentTimeMillis()
                    val title = session?.title?.ifBlank { "Entrenamiento" } ?: "Entrenamiento"
                    val historySets = sets.sortedBy { it.setNumber }.map { log ->
                        ExerciseHistorySet(
                            setNumber = log.setNumber,
                            weightKg = log.weightKg,
                            reps = log.actualReps,
                            isPr = log.isPr
                        )
                    }
                    val totalVolume = sets.sumOf { it.weightKg * it.actualReps }
                    val maxWeight = sets.maxOfOrNull { it.weightKg } ?: 0.0
                    ExerciseHistoryEntry(
                        sessionId = sessionId,
                        sessionTitle = title,
                        dateEpochMillis = date,
                        sets = historySets,
                        totalVolumeKg = totalVolume,
                        maxWeightKg = maxWeight
                    )
                }.sortedByDescending { it.dateEpochMillis }
            }
        }
    }

    private fun formatWeight(weightKg: Double): String {
        return if (weightKg % 1.0 == 0.0) {
            weightKg.toInt().toString()
        } else {
            "%.1f".format(weightKg)
        }
    }
}
