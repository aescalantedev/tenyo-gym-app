package com.aescalantedev.tenyo.core.data.seed

import android.content.Context
import com.aescalantedev.tenyo.core.data.datastore.TenyoPreferencesDataSource
import com.aescalantedev.tenyo.core.data.local.dao.ExerciseDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutPlanDao
import com.aescalantedev.tenyo.core.data.local.dao.WorkoutSessionDao
import com.aescalantedev.tenyo.core.data.local.entity.PlanExerciseCrossRefEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanDayEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutPlanEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSessionEntity
import com.aescalantedev.tenyo.core.data.local.entity.WorkoutSetLogEntity
import com.aescalantedev.tenyo.core.data.local.model.ExerciseSeedDto
import com.aescalantedev.tenyo.core.data.mapper.TenyoEntityMappers.toEntity
import com.aescalantedev.tenyo.core.domain.model.PlanDifficulty
import com.aescalantedev.tenyo.core.domain.model.PlanGoal
import com.aescalantedev.tenyo.core.domain.model.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/**
 * Gestor responsable del sembrado inicial (Seeding) de la base de datos local SQLite (Room).
 *
 * En la primera ejecucion de la aplicacion:
 * 1. Lee el archivo empaquetado `assets/exercises.json` (1,324 ejercicios con 10 idiomas).
 * 2. Deserializa y transforma cada registro en [com.aescalantedev.tenyo.core.data.local.entity.ExerciseEntity].
 * 3. Inserta los ejercicios por lotes de 300 elementos para optimizar el uso de memoria y velocidad de I/O.
 * 4. Crea los planes de entrenamiento iniciales inspirados en la disciplina fisica de Tenyo (Toji y Maki).
 * 5. Registra un historial base inicial para poblar las graficas de progreso, distribucion muscular y records personales.
 */
class CatalogSeedManager(
    private val context: Context,
    private val exerciseDao: ExerciseDao,
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val preferencesDataSource: TenyoPreferencesDataSource
) {
    private val mutex = Mutex()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Verifica si el catalogo y los planes iniciales ya estan sembrados en Room;
     * de lo contrario, ejecuta el proceso de sembrado en el hilo de entrada/salida ([Dispatchers.IO]).
     */
    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        mutex.withLock {
            val currentExercisesCount = exerciseDao.countExercises()
            if (currentExercisesCount == 0) {
                seedExercisesFromAssets()
            }

            // Los planes empiezan vacíos para que el usuario construya su propia rutina desde cero
            // val currentPlansCount = workoutPlanDao.countPlans()
            // if (currentPlansCount == 0) { seedDefaultWorkoutPlans() }

            preferencesDataSource.updateCatalogSeedVersion(CURRENT_SEED_VERSION)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun seedExercisesFromAssets() = withContext(Dispatchers.IO) {
        val seedDtos = context.assets.open(EXERCISES_ASSET_FILE_NAME).use { inputStream ->
            json.decodeFromStream<List<ExerciseSeedDto>>(inputStream)
        }
        val entities = seedDtos.map { it.toEntity() }

        entities.chunked(BATCH_INSERT_SIZE).forEach { chunk ->
            exerciseDao.insertAll(chunk)
        }
    }

    private suspend fun seedDefaultWorkoutPlans() {
        val defaultPlans = listOf(
            WorkoutPlanEntity(
                id = "plan_toji_heavenly_restriction",
                title = "Protocolo Toji · Restricción Celestial",
                subtitle = "Espalda en V, hombros tridimensionales y fuerza bruta",
                description = "Entrenamiento estructurado de 5 días enfocado en tracción pesada, press compuesto y potencia explosiva para construir el físico dominante de Toji Fushiguro.",
                goal = PlanGoal.HEAVENLY_RESTRICTION.name,
                difficulty = PlanDifficulty.HEAVENLY_RESTRICTION.name,
                daysPerWeek = 5,
                durationWeeks = 8,
                estimatedMinutesPerSession = 55,
                isUserPlan = true,
                isActivePlan = true,
                completedSessionsCount = 0,
                reminderHour = 18,
                reminderMinute = 0,
                reminderEnabled = false
            ),
            WorkoutPlanEntity(
                id = "plan_maki_cursed_mastery",
                title = "Protocolo Maki · Dominio Marcial",
                subtitle = "Agilidad reactiva, tren inferior potente y core de acero",
                description = "Rutina diseñada para maximizar la fuerza relativa, estabilidad central, velocidad de desplazamiento y resistencia muscular sin pausa.",
                goal = PlanGoal.HEAVENLY_RESTRICTION.name,
                difficulty = PlanDifficulty.ADVANCED.name,
                daysPerWeek = 4,
                durationWeeks = 8,
                estimatedMinutesPerSession = 50,
                isUserPlan = true,
                isActivePlan = false,
                completedSessionsCount = 0,
                reminderHour = 18,
                reminderMinute = 0,
                reminderEnabled = false
            ),
            WorkoutPlanEntity(
                id = "plan_ppl_hypertrophy",
                title = "Push / Pull / Legs · Hipertrofia",
                subtitle = "6 días/sem · Nivel Intermedio-Avanzado",
                description = "División clásica de empuje, tracción y pierna optimizada para volumen muscular e hipertrofia muscular progresiva.",
                goal = PlanGoal.HYPERTROPHY.name,
                difficulty = PlanDifficulty.INTERMEDIATE.name,
                daysPerWeek = 6,
                durationWeeks = 10,
                estimatedMinutesPerSession = 60,
                isUserPlan = false,
                isActivePlan = false,
                completedSessionsCount = 0,
                reminderHour = 18,
                reminderMinute = 0,
                reminderEnabled = false
            ),
            WorkoutPlanEntity(
                id = "plan_upper_lower_strength",
                title = "Torso / Pierna · Fuerza Máxima",
                subtitle = "4 días/sem · Progresión de cargas pesadas",
                description = "Programa estructurado alrededor de levantamientos multiarticulares básicos con enfoque en sobrecarga progresiva semanal.",
                goal = PlanGoal.STRENGTH.name,
                difficulty = PlanDifficulty.INTERMEDIATE.name,
                daysPerWeek = 4,
                durationWeeks = 8,
                estimatedMinutesPerSession = 50,
                isUserPlan = false,
                isActivePlan = false,
                completedSessionsCount = 0,
                reminderHour = 18,
                reminderMinute = 0,
                reminderEnabled = false
            ),
            WorkoutPlanEntity(
                id = "plan_zero_cursed_energy_conditioning",
                title = "Cero Energía Maldita · Acondicionamiento",
                subtitle = "3 días/sem · Definición y capacidad atlética",
                description = "Circuitos metabólicos combinando calistenia avanzada, abdomen y trabajo de alta intensidad para reducir grasa conservando masa magra.",
                goal = PlanGoal.CONDITIONING.name,
                difficulty = PlanDifficulty.BEGINNER.name,
                daysPerWeek = 3,
                durationWeeks = 6,
                estimatedMinutesPerSession = 40,
                isUserPlan = false,
                isActivePlan = false,
                completedSessionsCount = 0,
                reminderHour = 18,
                reminderMinute = 0,
                reminderEnabled = false
            )
        )

        workoutPlanDao.insertPlans(defaultPlans)

        val planDays = listOf(
            // Protocolo Toji (5 días activos, 2 descanso)
            WorkoutPlanDayEntity("toji_day_1", "plan_toji_heavenly_restriction", 1, "Empuje y Pecho Potente", false),
            WorkoutPlanDayEntity("toji_day_2", "plan_toji_heavenly_restriction", 2, "Tracción y Espalda en V", false),
            WorkoutPlanDayEntity("toji_day_3", "plan_toji_heavenly_restriction", 3, "Piernas Explosivas", false),
            WorkoutPlanDayEntity("toji_day_4", "plan_toji_heavenly_restriction", 4, "Descanso Activo / Movilidad", true),
            WorkoutPlanDayEntity("toji_day_5", "plan_toji_heavenly_restriction", 5, "Hombros 3D y Brazos", false),
            WorkoutPlanDayEntity("toji_day_6", "plan_toji_heavenly_restriction", 6, "Fuerza y Acondicionamiento", false),
            WorkoutPlanDayEntity("toji_day_7", "plan_toji_heavenly_restriction", 7, "Recuperación Total", true),

            // Protocolo Maki (4 días activos, 3 descanso)
            WorkoutPlanDayEntity("maki_day_1", "plan_maki_cursed_mastery", 1, "Potencia Tren Inferior", false),
            WorkoutPlanDayEntity("maki_day_2", "plan_maki_cursed_mastery", 2, "Tracción y Estabilidad", false),
            WorkoutPlanDayEntity("maki_day_3", "plan_maki_cursed_mastery", 3, "Descanso y Movilidad", true),
            WorkoutPlanDayEntity("maki_day_4", "plan_maki_cursed_mastery", 4, "Agilidad y Core de Acero", false),
            WorkoutPlanDayEntity("maki_day_5", "plan_maki_cursed_mastery", 5, "Acondicionamiento Cero Energía", false),
            WorkoutPlanDayEntity("maki_day_6", "plan_maki_cursed_mastery", 6, "Descanso", true),
            WorkoutPlanDayEntity("maki_day_7", "plan_maki_cursed_mastery", 7, "Descanso", true),

            // Push / Pull / Legs (6 días activos, 1 descanso)
            WorkoutPlanDayEntity("ppl_day_1", "plan_ppl_hypertrophy", 1, "Push 1 · Pecho y Tríceps", false),
            WorkoutPlanDayEntity("ppl_day_2", "plan_ppl_hypertrophy", 2, "Pull 1 · Espalda y Bíceps", false),
            WorkoutPlanDayEntity("ppl_day_3", "plan_ppl_hypertrophy", 3, "Legs 1 · Cuádriceps y Gemelos", false),
            WorkoutPlanDayEntity("ppl_day_4", "plan_ppl_hypertrophy", 4, "Push 2 · Pectoral y Hombro", false),
            WorkoutPlanDayEntity("ppl_day_5", "plan_ppl_hypertrophy", 5, "Pull 2 · Dorsal y Brazos", false),
            WorkoutPlanDayEntity("ppl_day_6", "plan_ppl_hypertrophy", 6, "Legs 2 · Isquios y Glúteos", false),
            WorkoutPlanDayEntity("ppl_day_7", "plan_ppl_hypertrophy", 7, "Descanso Total", true),

            // Torso / Pierna (4 días activos, 3 descanso)
            WorkoutPlanDayEntity("ul_day_1", "plan_upper_lower_strength", 1, "Torso Fuerza Pesada", false),
            WorkoutPlanDayEntity("ul_day_2", "plan_upper_lower_strength", 2, "Pierna Fuerza Pesada", false),
            WorkoutPlanDayEntity("ul_day_3", "plan_upper_lower_strength", 3, "Descanso", true),
            WorkoutPlanDayEntity("ul_day_4", "plan_upper_lower_strength", 4, "Torso Hipertrofia", false),
            WorkoutPlanDayEntity("ul_day_5", "plan_upper_lower_strength", 5, "Pierna Hipertrofia", false),
            WorkoutPlanDayEntity("ul_day_6", "plan_upper_lower_strength", 6, "Descanso", true),
            WorkoutPlanDayEntity("ul_day_7", "plan_upper_lower_strength", 7, "Descanso", true),

            // Cero Energía (3 días activos, 4 descanso)
            WorkoutPlanDayEntity("ce_day_1", "plan_zero_cursed_energy_conditioning", 1, "Circuito Metabólico Alpha", false),
            WorkoutPlanDayEntity("ce_day_2", "plan_zero_cursed_energy_conditioning", 2, "Descanso", true),
            WorkoutPlanDayEntity("ce_day_3", "plan_zero_cursed_energy_conditioning", 3, "Calistenia y Core", false),
            WorkoutPlanDayEntity("ce_day_4", "plan_zero_cursed_energy_conditioning", 4, "Descanso", true),
            WorkoutPlanDayEntity("ce_day_5", "plan_zero_cursed_energy_conditioning", 5, "Circuito Metabólico Beta", false),
            WorkoutPlanDayEntity("ce_day_6", "plan_zero_cursed_energy_conditioning", 6, "Descanso", true),
            WorkoutPlanDayEntity("ce_day_7", "plan_zero_cursed_energy_conditioning", 7, "Descanso", true)
        )

        workoutPlanDao.insertPlanDays(planDays)

        val crossRefs = listOf(
            // Protocolo Toji
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0025", 1, 4, "8-10", 90, 80.0, 1),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0027", 2, 4, "8-10", 90, 75.0, 1),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0091", 1, 4, "8-10", 90, 55.0, 2),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0032", 2, 3, "5-6", 120, 120.0, 2),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0043", 1, 4, "8-10", 90, 100.0, 3),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0085", 2, 4, "10-12", 75, 40.0, 3),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0334", 1, 4, "12-15", 60, 16.0, 5),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0031", 2, 3, "10-12", 60, 40.0, 5),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "1160", 1, 4, "20", 45, 0.0, 6),
            PlanExerciseCrossRefEntity("plan_toji_heavenly_restriction", "0472", 2, 4, "15", 45, 0.0, 6),

            // Protocolo Maki
            PlanExerciseCrossRefEntity("plan_maki_cursed_mastery", "0043", 1, 4, "8-10", 90, 95.0, 1),
            PlanExerciseCrossRefEntity("plan_maki_cursed_mastery", "0085", 2, 4, "8-10", 90, 85.0, 1),
            PlanExerciseCrossRefEntity("plan_maki_cursed_mastery", "0293", 1, 4, "10-12", 75, 30.0, 2),
            PlanExerciseCrossRefEntity("plan_maki_cursed_mastery", "0091", 2, 4, "8-10", 90, 50.0, 2),
            PlanExerciseCrossRefEntity("plan_maki_cursed_mastery", "0472", 1, 4, "15", 60, 0.0, 4),
            PlanExerciseCrossRefEntity("plan_maki_cursed_mastery", "1160", 2, 3, "15", 60, 0.0, 4),
            PlanExerciseCrossRefEntity("plan_maki_cursed_mastery", "0025", 1, 4, "10", 60, 60.0, 5),

            // Push / Pull / Legs
            PlanExerciseCrossRefEntity("plan_ppl_hypertrophy", "0025", 1, 4, "8-12", 90, 75.0, 1),
            PlanExerciseCrossRefEntity("plan_ppl_hypertrophy", "0091", 1, 4, "8-12", 90, 50.0, 2),
            PlanExerciseCrossRefEntity("plan_ppl_hypertrophy", "0043", 1, 4, "8-10", 90, 90.0, 3),
            PlanExerciseCrossRefEntity("plan_ppl_hypertrophy", "0334", 1, 4, "12-15", 60, 14.0, 4),
            PlanExerciseCrossRefEntity("plan_ppl_hypertrophy", "0241", 1, 4, "12-15", 60, 32.0, 5),
            PlanExerciseCrossRefEntity("plan_ppl_hypertrophy", "0085", 1, 4, "10-12", 75, 50.0, 6),

            // Torso / Pierna
            PlanExerciseCrossRefEntity("plan_upper_lower_strength", "0025", 1, 5, "5", 120, 85.0, 1),
            PlanExerciseCrossRefEntity("plan_upper_lower_strength", "0091", 2, 4, "6-8", 90, 70.0, 1),
            PlanExerciseCrossRefEntity("plan_upper_lower_strength", "0043", 1, 5, "5", 120, 110.0, 2),
            PlanExerciseCrossRefEntity("plan_upper_lower_strength", "0032", 2, 3, "5", 120, 130.0, 2),
            PlanExerciseCrossRefEntity("plan_upper_lower_strength", "0027", 1, 4, "8-10", 90, 70.0, 4),
            PlanExerciseCrossRefEntity("plan_upper_lower_strength", "0085", 1, 4, "10-12", 75, 45.0, 5),

            // Cero Energía Maldita Conditioning
            PlanExerciseCrossRefEntity("plan_zero_cursed_energy_conditioning", "1160", 1, 4, "20", 45, 0.0, 1),
            PlanExerciseCrossRefEntity("plan_zero_cursed_energy_conditioning", "0472", 1, 4, "15", 45, 0.0, 3),
            PlanExerciseCrossRefEntity("plan_zero_cursed_energy_conditioning", "0293", 1, 3, "12", 60, 24.0, 5)
        )

        workoutPlanDao.insertPlanExerciseCrossRefs(crossRefs)
    }

    private suspend fun seedInitialProgressHistory() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        val historicalSessions = listOf(
            WorkoutSessionEntity(
                id = "seed_session_1",
                planId = "plan_toji_heavenly_restriction",
                title = "Protocolo Toji · Dia 1 (Empuje y Espalda)",
                status = SessionStatus.COMPLETED.name,
                startedAtEpochMillis = now - (5 * oneDayMillis),
                endedAtEpochMillis = now - (5 * oneDayMillis) + (52 * 60 * 1000L),
                elapsedSeconds = 52 * 60L,
                totalVolumeKg = 6840.0,
                estimatedCaloriesKcal = 440,
                currentExerciseIndex = 0
            ),
            WorkoutSessionEntity(
                id = "seed_session_2",
                planId = "plan_maki_cursed_mastery",
                title = "Protocolo Maki · Tren Inferior y Core",
                status = SessionStatus.COMPLETED.name,
                startedAtEpochMillis = now - (4 * oneDayMillis),
                endedAtEpochMillis = now - (4 * oneDayMillis) + (48 * 60 * 1000L),
                elapsedSeconds = 48 * 60L,
                totalVolumeKg = 7420.0,
                estimatedCaloriesKcal = 475,
                currentExerciseIndex = 0
            ),
            WorkoutSessionEntity(
                id = "seed_session_3",
                planId = "plan_toji_heavenly_restriction",
                title = "Protocolo Toji · Traccion Pesada",
                status = SessionStatus.COMPLETED.name,
                startedAtEpochMillis = now - (2 * oneDayMillis),
                endedAtEpochMillis = now - (2 * oneDayMillis) + (60 * 60 * 1000L),
                elapsedSeconds = 60 * 60L,
                totalVolumeKg = 8150.0,
                estimatedCaloriesKcal = 520,
                currentExerciseIndex = 0
            ),
            WorkoutSessionEntity(
                id = "seed_session_4",
                planId = "plan_toji_heavenly_restriction",
                title = "Protocolo Toji · Hombros y Brazos",
                status = SessionStatus.COMPLETED.name,
                startedAtEpochMillis = now - oneDayMillis,
                endedAtEpochMillis = now - oneDayMillis + (45 * 60 * 1000L),
                elapsedSeconds = 45 * 60L,
                totalVolumeKg = 5400.0,
                estimatedCaloriesKcal = 390,
                currentExerciseIndex = 0
            )
        )

        workoutSessionDao.insertSessions(historicalSessions)

        val historicalSetLogs = listOf(
            // Press de banca (0025) - Evolucion hasta 95 kg
            WorkoutSetLogEntity(0, "seed_session_1", "0025", "barbell bench press", "chest", 1, 8, 8, 85.0, true, now - (5 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_1", "0025", "barbell bench press", "chest", 2, 8, 8, 90.0, true, now - (5 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_1", "0025", "barbell bench press", "chest", 3, 6, 6, 95.0, true, now - (5 * oneDayMillis)),

            // Sentadilla profunda (0043) - Evolucion hasta 125 kg
            WorkoutSetLogEntity(0, "seed_session_2", "0043", "barbell full squat", "upper legs", 1, 8, 8, 110.0, true, now - (4 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_2", "0043", "barbell full squat", "upper legs", 2, 6, 6, 120.0, true, now - (4 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_2", "0043", "barbell full squat", "upper legs", 3, 5, 5, 125.0, true, now - (4 * oneDayMillis)),

            // Peso muerto con barra (0032) - Evolucion hasta 145 kg
            WorkoutSetLogEntity(0, "seed_session_3", "0032", "barbell deadlift", "upper legs", 1, 5, 5, 130.0, true, now - (2 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_3", "0032", "barbell deadlift", "upper legs", 2, 5, 5, 140.0, true, now - (2 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_3", "0032", "barbell deadlift", "upper legs", 3, 5, 5, 145.0, true, now - (2 * oneDayMillis)),

            // Remo con barra (0027) - Evolucion hasta 85 kg
            WorkoutSetLogEntity(0, "seed_session_3", "0027", "barbell bent over row", "back", 1, 8, 8, 75.0, true, now - (2 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_3", "0027", "barbell bent over row", "back", 2, 8, 8, 80.0, true, now - (2 * oneDayMillis)),
            WorkoutSetLogEntity(0, "seed_session_3", "0027", "barbell bent over row", "back", 3, 8, 8, 85.0, true, now - (2 * oneDayMillis)),

            // Press militar (0091) - Evolucion hasta 60 kg
            WorkoutSetLogEntity(0, "seed_session_4", "0091", "barbell seated overhead press", "shoulders", 1, 8, 8, 52.5, true, now - oneDayMillis),
            WorkoutSetLogEntity(0, "seed_session_4", "0091", "barbell seated overhead press", "shoulders", 2, 8, 8, 57.5, true, now - oneDayMillis),
            WorkoutSetLogEntity(0, "seed_session_4", "0091", "barbell seated overhead press", "shoulders", 3, 6, 6, 60.0, true, now - oneDayMillis),

            // Elevaciones de piernas colgado (0472) - Abdomen
            WorkoutSetLogEntity(0, "seed_session_4", "0472", "hanging leg raise", "waist", 1, 15, 15, 0.0, true, now - oneDayMillis),
            WorkoutSetLogEntity(0, "seed_session_4", "0472", "hanging leg raise", "waist", 2, 15, 15, 0.0, true, now - oneDayMillis)
        )

        workoutSessionDao.insertSetLogs(historicalSetLogs)
    }

    companion object {
        private const val EXERCISES_ASSET_FILE_NAME = "exercises.json"
        private const val BATCH_INSERT_SIZE = 300
        private const val CURRENT_SEED_VERSION = 1
    }
}
