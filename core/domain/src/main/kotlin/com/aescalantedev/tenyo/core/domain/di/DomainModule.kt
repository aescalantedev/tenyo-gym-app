package com.aescalantedev.tenyo.core.domain.di

import com.aescalantedev.tenyo.core.domain.usecase.AddExerciseToPlanUseCase
import com.aescalantedev.tenyo.core.domain.usecase.CreateCustomExerciseUseCase
import com.aescalantedev.tenyo.core.domain.usecase.CreateCustomWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.domain.usecase.EnsureCatalogSeededUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ManageActiveSessionUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ManagePlanRoutinesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ManageUserPreferencesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveActiveWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveCategorySummariesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExerciseDetailUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExercisePerformanceUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveExercisesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveFeaturedExercisesUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObservePlanByIdUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveProgressSummaryUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveWorkoutPlansUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ObserveWorkoutSessionUseCase
import com.aescalantedev.tenyo.core.domain.usecase.SelectActiveWorkoutPlanUseCase
import com.aescalantedev.tenyo.core.domain.usecase.StartWorkoutSessionUseCase
import com.aescalantedev.tenyo.core.domain.usecase.ToggleFavoriteExerciseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Módulo Koin de la capa de Dominio (`:core:domain`).
 * 100% Kotlin puro y portable directamente a Kotlin Multiplatform (KMP).
 */
val domainModule = module {
    factoryOf(::EnsureCatalogSeededUseCase)
    factoryOf(::ObserveCategorySummariesUseCase)
    factoryOf(::ObserveExercisesUseCase)
    factoryOf(::ObserveFeaturedExercisesUseCase)
    factoryOf(::ObserveExerciseDetailUseCase)
    factoryOf(::ObserveExercisePerformanceUseCase)
    factoryOf(::ToggleFavoriteExerciseUseCase)
    factoryOf(::CreateCustomExerciseUseCase)

    factoryOf(::ObserveWorkoutPlansUseCase)
    factoryOf(::ObservePlanByIdUseCase)
    factoryOf(::ManagePlanRoutinesUseCase)
    factoryOf(::ObserveActiveWorkoutPlanUseCase)
    factoryOf(::SelectActiveWorkoutPlanUseCase)
    factoryOf(::AddExerciseToPlanUseCase)
    factoryOf(::CreateCustomWorkoutPlanUseCase)

    factoryOf(::StartWorkoutSessionUseCase)
    factoryOf(::ObserveWorkoutSessionUseCase)
    factoryOf(::ManageActiveSessionUseCase)

    factoryOf(::ObserveProgressSummaryUseCase)
    factoryOf(::ManageUserPreferencesUseCase)
}
