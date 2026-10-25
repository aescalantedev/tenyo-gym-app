package com.aescalantedev.tenyo.di

import com.aescalantedev.tenyo.core.notification.WorkoutNotificationManager
import com.aescalantedev.tenyo.core.notification.WorkoutPlanAlarmScheduler
import com.aescalantedev.tenyo.core.service.WorkoutTimerCoordinator
import com.aescalantedev.tenyo.navigation.NavViewModel
import com.aescalantedev.tenyo.ui.screens.categories.viewmodel.CategoriesViewModel
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseDetailViewModel
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseListViewModel
import com.aescalantedev.tenyo.ui.screens.home.viewmodel.HomeViewModel
import com.aescalantedev.tenyo.ui.screens.plans.viewmodel.PlanDetailViewModel
import com.aescalantedev.tenyo.ui.screens.plans.viewmodel.WorkoutPlansViewModel
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileViewModel
import com.aescalantedev.tenyo.ui.screens.progress.viewmodel.ProgressViewModel
import com.aescalantedev.tenyo.ui.screens.splash.viewmodel.AppBootstrapViewModel
import com.aescalantedev.tenyo.ui.screens.welcome.viewmodel.WelcomeViewModel
import com.aescalantedev.tenyo.ui.screens.workout.viewmodel.ActiveWorkoutViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Modulo Koin de la capa de Presentacion (`:app`).
 * Registra todos los ViewModels que alimentan las pantallas de Tenyo.
 */
val appModule = module {
    single { WorkoutNotificationManager(androidContext()) }
    single { WorkoutTimerCoordinator(androidContext(), get()) }
    single { WorkoutPlanAlarmScheduler(androidContext()) }

    viewModelOf(::NavViewModel)
    viewModelOf(::AppBootstrapViewModel)
    viewModelOf(::WelcomeViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CategoriesViewModel)
    viewModelOf(::ExerciseListViewModel)
    viewModelOf(::ExerciseDetailViewModel)
    viewModelOf(::ActiveWorkoutViewModel)
    viewModelOf(::WorkoutPlansViewModel)
    viewModelOf(::PlanDetailViewModel)
    viewModelOf(::ProgressViewModel)
    viewModelOf(::ProfileViewModel)
}
