package com.aescalantedev.tenyo.core.data.repository

import com.aescalantedev.tenyo.core.data.local.dao.ExerciseDao
import com.aescalantedev.tenyo.core.data.local.dao.ExercisePersonalRecordDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutSessionDao
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSessionEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSetLogEntity
import com.aescalantedev.tenyo.core.domain.model.BodyPartCategory
import com.aescalantedev.tenyo.core.domain.model.MuscleDistributionItem
import com.aescalantedev.tenyo.core.domain.model.MuscleRecoveryItem
import com.aescalantedev.tenyo.core.domain.model.PersonalRecord
import com.aescalantedev.tenyo.core.domain.model.PhysicalRank
import com.aescalantedev.tenyo.core.domain.model.ProgressSummary
import com.aescalantedev.tenyo.core.domain.model.WeeklyActivityDay
import com.aescalantedev.tenyo.core.domain.model.WorkoutDayMarker
import com.aescalantedev.tenyo.core.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Implementacion Offline-First de [ProgressRepository].
 *
 * Calcula en tiempo real el 100% de las metricas de evolucion fisica del usuario
 * a partir de las sesiones completadas (`workout_sessions`), series ejecutadas (`workout_set_logs`)
 * y records personales (`exercise_personal_records`) almacenados en Room:
 * - KPIs reales: Sesiones completadas, volumen total acumulado en kg, tiempo total bajo tension.
 * - Rango Fisico Tenyo progresivo real (Grado 4 a Restriccion Celestial).
 * - Actividad semanal real calculada de Lunes a Domingo para la semana actual.
 * - Racha real de semanas consecutivas entrenadas.
 * - Calendario interactivo con mapa de dias reales entrenados.
 * - Recuperacion muscular porcentual (0% - 100%) por grupo muscular segun el tiempo transcurrido.
 * - Musculos estimulados para el mapa anatomico interactivo de Toji.
 * - Records personales (PRs) reales alcanzados.
 */
class OfflineFirstProgressRepository(
    private val workoutSessionDao: WorkoutSessionDao,
    private val personalRecordDao: ExercisePersonalRecordDao,
    private val exerciseDao: ExerciseDao
) : ProgressRepository {

    override fun observeProgressSummary(): Flow<ProgressSummary> {
        return combine(
            workoutSessionDao.observeCompletedSessions(),
            workoutSessionDao.observeAllCompletedSetLogs(),
            personalRecordDao.observeAll()
        ) { completedSessions, completedLogs, prEntities ->
            val workoutsCount = completedSessions.size
            val totalDurationMinutes = completedSessions.sumOf { (it.elapsedSeconds / 60L).toInt() }
            val totalCalories = completedSessions.sumOf { it.estimatedCaloriesKcal }
            val totalVolumeKg = completedSessions.sumOf { it.totalVolumeKg }

            // 1. Rango fisico Tenyo real basado estrictamente en entrenamientos completados
            val currentRank = PhysicalRank.fromWorkoutsCount(workoutsCount)
            val nextRank = PhysicalRank.nextRank(currentRank)
            val workoutsToNext = if (nextRank != null) {
                (nextRank.minWorkoutsRequired - workoutsCount).coerceAtLeast(0)
            } else {
                0
            }
            val rankProgress = if (nextRank != null) {
                val span = (nextRank.minWorkoutsRequired - currentRank.minWorkoutsRequired).coerceAtLeast(1)
                val earnedInTier = (workoutsCount - currentRank.minWorkoutsRequired).coerceAtLeast(0)
                (earnedInTier.toFloat() / span.toFloat()).coerceIn(0f, 1f)
            } else {
                1f
            }

            // 2. Actividad semanal real de Lunes a Domingo (Semana en curso)
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now(zoneId)
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            val mondayStartMillis = monday.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val sundayEndMillis = sunday.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1L

            val thisWeekSessions = completedSessions.filter { session ->
                val timestamp = session.getEffectiveTimestamp()
                timestamp in mondayStartMillis..sundayEndMillis
            }

            val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")
            val weeklyActivity = (0..6).map { dayOffset ->
                val targetDate = monday.plusDays(dayOffset.toLong())
                val targetEpochDay = targetDate.toEpochDay()

                val sessionsOnDay = thisWeekSessions.filter { session ->
                    val sessionDate = Instant.ofEpochMilli(session.getEffectiveTimestamp())
                        .atZone(zoneId)
                        .toLocalDate()
                    sessionDate.toEpochDay() == targetEpochDay
                }

                val minutes = sessionsOnDay.sumOf { (it.elapsedSeconds / 60L).toInt() }
                val volume = sessionsOnDay.sumOf { it.totalVolumeKg }

                WeeklyActivityDay(
                    dayLabel = dayLabels[dayOffset],
                    durationMinutes = minutes,
                    volumeKg = volume,
                    isToday = targetDate == today
                )
            }

            // 3. Racha semanal real (semanas consecutivas hacia atras con al menos 1 sesion completada)
            val activeStreakWeeks = calculateRealWeeklyStreak(completedSessions, monday, zoneId)

            // 4. Mapa de dias entrenados para el Calendario interactivo
            val workoutDaysMap = buildWorkoutDaysMap(completedSessions, zoneId)

            // 5. Distribucion porcentual real por grupo muscular
            val muscleDistribution = buildMuscleDistribution(completedLogs)

            // 6. Recuperacion muscular real (0% .. 100%) por grupo muscular segun horas transcurridas
            val nowMillis = System.currentTimeMillis()
            val muscleRecoveries = buildMuscleRecoveries(completedLogs, nowMillis, zoneId)

            // 7. Musculos trabajados recientemente para encender en el modelo Toji (ultimas 72 horas)
            val workedMuscleOptionIds = muscleRecoveries
                .filter { item ->
                    val hours = item.hoursSinceLastTrained
                    hours != null && hours < 72L
                }
                .map { it.tojiMuscleOptionId }
                .toSet()
                .ifEmpty {
                    // Si no entreno en las ultimas 72h, iluminar los musculos de la ultima sesion completada
                    completedLogs.take(12).mapNotNull { mapBodyPartToTojiId(it.bodyPart) }.toSet()
                }

            // 8. Records Personales (PRs) reales cruzados de la tabla exercise_personal_records
            val personalRecords = buildRealPersonalRecords(prEntities, completedLogs)

            ProgressSummary(
                completedWorkoutsCount = workoutsCount,
                totalDurationMinutes = totalDurationMinutes,
                totalCaloriesBurned = totalCalories,
                totalVolumeKg = totalVolumeKg,
                activeStreakWeeks = activeStreakWeeks,
                currentRank = currentRank,
                nextRank = nextRank,
                workoutsToNextRank = workoutsToNext,
                rankProgressFraction = rankProgress,
                weeklyActivity = weeklyActivity,
                muscleDistribution = muscleDistribution,
                muscleRecoveries = muscleRecoveries,
                workoutDaysMap = workoutDaysMap,
                personalRecords = personalRecords,
                workedMuscleOptionIds = workedMuscleOptionIds
            )
        }
    }

    private fun calculateRealWeeklyStreak(
        completedSessions: List<WorkoutSessionEntity>,
        currentMonday: LocalDate,
        zoneId: ZoneId
    ): Int {
        if (completedSessions.isEmpty()) return 0

        var streak = 0
        var checkMonday = currentMonday

        while (true) {
            val weekStart = checkMonday.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val weekEnd = checkMonday.plusDays(7).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1L

            val hasWorkoutInWeek = completedSessions.any { session ->
                session.getEffectiveTimestamp() in weekStart..weekEnd
            }

            if (hasWorkoutInWeek) {
                streak++
                checkMonday = checkMonday.minusWeeks(1)
            } else {
                // Si en la semana actual aun no entrena, pero entreno la semana pasada, preservamos la racha previa
                if (checkMonday == currentMonday) {
                    checkMonday = checkMonday.minusWeeks(1)
                    val prevWeekStart = checkMonday.atStartOfDay(zoneId).toInstant().toEpochMilli()
                    val prevWeekEnd = checkMonday.plusDays(7).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1L
                    val hasPrev = completedSessions.any { it.getEffectiveTimestamp() in prevWeekStart..prevWeekEnd }
                    if (hasPrev) {
                        streak++
                        checkMonday = checkMonday.minusWeeks(1)
                        continue
                    }
                }
                break
            }
        }
        return streak
    }

    private fun buildWorkoutDaysMap(
        completedSessions: List<WorkoutSessionEntity>,
        zoneId: ZoneId
    ): Map<Long, WorkoutDayMarker> {
        return completedSessions
            .groupBy { session ->
                val date = Instant.ofEpochMilli(session.getEffectiveTimestamp())
                    .atZone(zoneId)
                    .toLocalDate()
                date.toEpochDay()
            }
            .mapValues { (epochDay, sessionsOnDay) ->
                WorkoutDayMarker(
                    epochDay = epochDay,
                    workoutsCount = sessionsOnDay.size,
                    totalVolumeKg = sessionsOnDay.sumOf { it.totalVolumeKg },
                    durationMinutes = sessionsOnDay.sumOf { (it.elapsedSeconds / 60L).toInt() },
                    sessionTitles = sessionsOnDay.map { it.title.ifBlank { "Entrenamiento" } }
                )
            }
    }

    private fun buildMuscleDistribution(completedLogs: List<WorkoutSetLogEntity>): List<MuscleDistributionItem> {
        val totalSets = completedLogs.size.coerceAtLeast(1)
        return completedLogs
            .groupBy { BodyPartCategory.fromRawKey(it.bodyPart) }
            .map { (category, logs) ->
                val pct = ((logs.size.toDouble() / totalSets.toDouble()) * 100.0).roundToInt()
                MuscleDistributionItem(
                    category = category,
                    completedSets = logs.size,
                    percentage = pct.coerceIn(1, 100)
                )
            }
            .sortedByDescending { it.completedSets }
            .take(6)
    }

    private fun buildMuscleRecoveries(
        completedLogs: List<WorkoutSetLogEntity>,
        nowMillis: Long,
        zoneId: ZoneId
    ): List<MuscleRecoveryItem> {
        val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMM, HH:mm", Locale.forLanguageTag("es-ES"))

        val muscleDefinitions = listOf(
            MuscleDef(BodyPartCategory.CHEST, "Pecho", "CHEST", "cat_muscle_chest"),
            MuscleDef(BodyPartCategory.BACK, "Espalda", "LATS", "cat_muscle_back"),
            MuscleDef(BodyPartCategory.SHOULDERS, "Hombros", "SHOULDERS", "cat_muscle_shoulders"),
            MuscleDef(BodyPartCategory.UPPER_ARMS, "Bíceps", "BICEPS", "cat_muscle_biceps"),
            MuscleDef(BodyPartCategory.UPPER_ARMS, "Tríceps", "TRICEPS", "cat_muscle_triceps"),
            MuscleDef(BodyPartCategory.WAIST, "Abdomen", "ABS", "cat_muscle_abs"),
            MuscleDef(BodyPartCategory.UPPER_LEGS, "Cuádriceps", "QUADS", "cat_muscle_quads"),
            MuscleDef(BodyPartCategory.UPPER_LEGS, "Isquios y Glúteos", "HAMSTRINGS", "cat_muscle_hamstrings"),
            MuscleDef(BodyPartCategory.LOWER_LEGS, "Pantorrillas", "CALVES", "cat_muscle_calves"),
            MuscleDef(BodyPartCategory.LOWER_ARMS, "Antebrazos", "FOREARMS", "cat_muscle_forearms")
        )

        return muscleDefinitions.map { def ->
            val logsForMuscle = completedLogs.filter { log ->
                matchesMuscle(log, def)
            }

            val latestLog = logsForMuscle.maxByOrNull { it.completedAtEpochMillis ?: 0L }
            val lastTrained = latestLog?.completedAtEpochMillis

            val fortyEightHoursAgo = nowMillis - (48L * 3600_000L)
            val setsLast48h = logsForMuscle.count { (it.completedAtEpochMillis ?: 0L) >= fortyEightHoursAgo }

            if (lastTrained == null || lastTrained == 0L) {
                MuscleRecoveryItem(
                    category = def.category,
                    muscleNameEs = def.nameEs,
                    recoveryPercentage = 100,
                    statusText = "100% Recuperado",
                    hoursSinceLastTrained = null,
                    lastTrainedDateFormatted = null,
                    setsTrainedLast48h = 0,
                    tojiMuscleOptionId = def.tojiOptionId,
                    drawableAssetName = def.drawableName
                )
            } else {
                val hoursElapsed = ((nowMillis - lastTrained) / 3600_000L).coerceAtLeast(0L)
                val recoveryPct = if (hoursElapsed >= 72L) {
                    100
                } else {
                    ((hoursElapsed.toDouble() / 72.0) * 100.0).roundToInt().coerceIn(10, 99)
                }

                val statusText = when {
                    recoveryPct >= 95 -> "100% Recuperado"
                    recoveryPct >= 75 -> "$recoveryPct% Listo para entrenar"
                    recoveryPct >= 45 -> "$recoveryPct% En recuperación"
                    else -> "$recoveryPct% Fatiga alta"
                }

                val formattedDate = Instant.ofEpochMilli(lastTrained)
                    .atZone(zoneId)
                    .format(dateFormatter)

                MuscleRecoveryItem(
                    category = def.category,
                    muscleNameEs = def.nameEs,
                    recoveryPercentage = recoveryPct,
                    statusText = statusText,
                    hoursSinceLastTrained = hoursElapsed,
                    lastTrainedDateFormatted = formattedDate,
                    setsTrainedLast48h = setsLast48h,
                    tojiMuscleOptionId = def.tojiOptionId,
                    drawableAssetName = def.drawableName
                )
            }
        }.sortedBy { it.recoveryPercentage } // Mostrar primero los que estan mas fatigados / en recuperacion
    }

    private fun matchesMuscle(log: WorkoutSetLogEntity, def: MuscleDef): Boolean {
        val part = log.bodyPart.lowercase()
        val name = log.exerciseName.lowercase()
        return when (def.tojiOptionId) {
            "CHEST" -> part.contains("chest") || name.contains("pecho") || name.contains("press banca")
            "LATS" -> part.contains("back") || name.contains("espalda") || name.contains("remo") || name.contains("jalón") || name.contains("dominada")
            "SHOULDERS" -> part.contains("shoulder") || name.contains("hombro") || name.contains("press militar") || name.contains("lateral")
            "BICEPS" -> (part.contains("arms") || part.contains("biceps")) && (name.contains("curl") || name.contains("biceps") || name.contains("bíceps"))
            "TRICEPS" -> (part.contains("arms") || part.contains("triceps")) && (name.contains("tríceps") || name.contains("triceps") || name.contains("fondos") || name.contains("copa") || name.contains("press francés"))
            "ABS" -> part.contains("waist") || part.contains("abs") || part.contains("core") || name.contains("crunch") || name.contains("plancha") || name.contains("abdomen")
            "QUADS" -> (part.contains("upper legs") || part.contains("quads")) && (name.contains("sentadilla") || name.contains("prensa") || name.contains("extensión") || name.contains("cuádriceps"))
            "HAMSTRINGS" -> (part.contains("upper legs") || part.contains("glutes") || part.contains("hamstrings")) && (name.contains("peso muerto") || name.contains("femoral") || name.contains("hip thrust") || name.contains("glúteo"))
            "CALVES" -> part.contains("lower legs") || part.contains("calves") || name.contains("gemelos") || name.contains("pantorrilla")
            "FOREARMS" -> part.contains("lower arms") || part.contains("forearms") || name.contains("antebrazo")
            else -> part.equals(def.category.rawKey, ignoreCase = true)
        }
    }

    private fun mapBodyPartToTojiId(bodyPartRaw: String): String? {
        val part = bodyPartRaw.lowercase()
        return when {
            part.contains("chest") -> "CHEST"
            part.contains("back") -> "LATS"
            part.contains("shoulder") -> "SHOULDERS"
            part.contains("upper arms") -> "BICEPS"
            part.contains("lower arms") -> "FOREARMS"
            part.contains("waist") -> "ABS"
            part.contains("upper legs") -> "QUADS"
            part.contains("lower legs") -> "CALVES"
            else -> null
        }
    }

    private suspend fun buildRealPersonalRecords(
        prEntities: List<com.aescalantedev.tenyo.core.data.local.entity.ExercisePersonalRecordEntity>,
        completedLogs: List<WorkoutSetLogEntity>
    ): List<PersonalRecord> {
        if (prEntities.isNotEmpty()) {
            val exerciseIds = prEntities.map { it.exerciseId }
            val exerciseMap = exerciseDao.getExercisesByIds(exerciseIds).associateBy { it.id }

            return prEntities.mapNotNull { pr ->
                val ex = exerciseMap[pr.exerciseId]
                val logsForEx = completedLogs.filter { it.exerciseId == pr.exerciseId && it.weightKg > 0.0 }
                val prevWeight = logsForEx.filter { it.weightKg < pr.maxWeightKg }.maxOfOrNull { it.weightKg }
                    ?: pr.maxWeightKg

                PersonalRecord(
                    exerciseId = pr.exerciseId,
                    exerciseName = ex?.name?.replaceFirstChar { it.uppercase() } ?: "Ejercicio",
                    bodyPart = ex?.bodyPart?.let { BodyPartCategory.fromRawKey(it) } ?: BodyPartCategory.CHEST,
                    maxWeightKg = pr.maxWeightKg,
                    repsAtMaxWeight = pr.repsAtMaxWeight,
                    previousWeightKg = prevWeight,
                    achievedAtEpochMillis = pr.achievedAtEpochMillis
                )
            }.sortedByDescending { it.maxWeightKg }
        }

        // Si la tabla de PRs aún no tiene filas, calcular dinámicamente con los logs reales completados en Room
        return completedLogs
            .filter { it.weightKg > 0.0 }
            .groupBy { it.exerciseId }
            .mapNotNull { (exerciseId, logs) ->
                val maxLog = logs.maxByOrNull { it.weightKg } ?: return@mapNotNull null
                val minWeight = logs.filter { it.weightKg < maxLog.weightKg }.maxOfOrNull { it.weightKg } ?: maxLog.weightKg

                PersonalRecord(
                    exerciseId = exerciseId,
                    exerciseName = maxLog.exerciseName.replaceFirstChar { it.uppercase() },
                    bodyPart = BodyPartCategory.fromRawKey(maxLog.bodyPart),
                    maxWeightKg = maxLog.weightKg,
                    repsAtMaxWeight = maxLog.actualReps,
                    previousWeightKg = minWeight,
                    achievedAtEpochMillis = maxLog.completedAtEpochMillis ?: System.currentTimeMillis()
                )
            }
            .sortedByDescending { it.maxWeightKg }
            .take(10)
    }

    private fun WorkoutSessionEntity.getEffectiveTimestamp(): Long {
        return if (endedAtEpochMillis != null && endedAtEpochMillis > 0L) {
            endedAtEpochMillis
        } else {
            startedAtEpochMillis
        }
    }

    private data class MuscleDef(
        val category: BodyPartCategory,
        val nameEs: String,
        val tojiOptionId: String,
        val drawableName: String
    )
}
