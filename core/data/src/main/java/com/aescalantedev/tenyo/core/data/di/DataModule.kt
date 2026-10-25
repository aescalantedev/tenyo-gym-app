package com.aescalantedev.tenyo.core.data.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.aescalantedev.tenyo.core.data.datastore.TenyoPreferencesDataSource
import com.aescalantedev.tenyo.core.data.local.database.TenyoDatabase
import com.aescalantedev.tenyo.core.data.media.MediaUrlResolver
import com.aescalantedev.tenyo.core.data.repository.OfflineFirstExerciseRepository
import com.aescalantedev.tenyo.core.data.repository.OfflineFirstProgressRepository
import com.aescalantedev.tenyo.core.data.repository.OfflineFirstUserPreferencesRepository
import com.aescalantedev.tenyo.core.data.repository.OfflineFirstWorkoutPlanRepository
import com.aescalantedev.tenyo.core.data.repository.OfflineFirstWorkoutSessionRepository
import com.aescalantedev.tenyo.core.data.seed.CatalogSeedManager
import com.aescalantedev.tenyo.core.domain.repository.ExerciseRepository
import com.aescalantedev.tenyo.core.domain.repository.ProgressRepository
import com.aescalantedev.tenyo.core.domain.repository.UserPreferencesRepository
import com.aescalantedev.tenyo.core.domain.repository.WorkoutPlanRepository
import com.aescalantedev.tenyo.core.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Modulo Koin de la capa de Datos (`:core:data`).
 *
 * Configura:
 * - [MediaUrlResolver]: Resolutor de rutas relativas para miniaturas y GIFs (listo para S3/Supabase).
 * - [TenyoPreferencesDataSource]: DataStore Preferences Core inicializado mediante ruta de archivo (`okio.Path`).
 * - [TenyoDatabase]: Base de datos Room 2.8.5 utilizando [BundledSQLiteDriver] para compatibilidad KMP.
 * - [CatalogSeedManager]: Sembrado automatico de los 1,324 ejercicios del dataset y planes iniciales.
 * - Implementaciones Offline-First de todos los repositorios de dominio.
 */
val dataModule = module {

    single { MediaUrlResolver() }

    single {
        val context = androidContext()
        TenyoPreferencesDataSource.createDataStore(
            produceFilePath = {
                context.filesDir.resolve(TenyoPreferencesDataSource.DATASTORE_FILE_NAME).absolutePath
            }
        )
    }

    singleOf(::TenyoPreferencesDataSource)

    single {
        val context = androidContext()
        val dbFile = context.getDatabasePath(TenyoDatabase.DATABASE_NAME)
        Room.databaseBuilder<TenyoDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<TenyoDatabase>().exerciseDao() }
    single { get<TenyoDatabase>().workoutPlanDao() }
    single { get<TenyoDatabase>().workoutSessionDao() }
    single { get<TenyoDatabase>().exercisePersonalRecordDao() }

    single {
        CatalogSeedManager(
            context = androidContext(),
            exerciseDao = get(),
            workoutPlanDao = get(),
            workoutSessionDao = get(),
            preferencesDataSource = get()
        )
    }

    singleOf(::OfflineFirstExerciseRepository) { bind<ExerciseRepository>() }
    singleOf(::OfflineFirstWorkoutPlanRepository) { bind<WorkoutPlanRepository>() }
    singleOf(::OfflineFirstWorkoutSessionRepository) { bind<WorkoutSessionRepository>() }
    singleOf(::OfflineFirstProgressRepository) { bind<ProgressRepository>() }
    singleOf(::OfflineFirstUserPreferencesRepository) { bind<UserPreferencesRepository>() }
}
