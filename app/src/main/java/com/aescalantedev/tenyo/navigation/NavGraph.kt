package com.aescalantedev.tenyo.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.aescalantedev.tenyo.ui.components.TenyoBottomNavigationBar
import com.aescalantedev.tenyo.ui.screens.categories.CategoriesScreen
import com.aescalantedev.tenyo.ui.screens.categories.viewmodel.CategoriesViewModel
import com.aescalantedev.tenyo.ui.screens.exercises.ExerciseDetailScreen
import com.aescalantedev.tenyo.ui.screens.exercises.ExerciseListScreen
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseDetailViewModel
import com.aescalantedev.tenyo.ui.screens.exercises.viewmodel.ExerciseListViewModel
import com.aescalantedev.tenyo.ui.screens.home.HomeScreen
import com.aescalantedev.tenyo.ui.screens.home.viewmodel.HomeViewModel
import com.aescalantedev.tenyo.ui.screens.plans.PlanDetailScreen
import com.aescalantedev.tenyo.ui.screens.plans.WorkoutPlansScreen
import com.aescalantedev.tenyo.ui.screens.plans.viewmodel.PlanDetailViewModel
import com.aescalantedev.tenyo.ui.screens.plans.viewmodel.WorkoutPlansViewModel
import com.aescalantedev.tenyo.ui.screens.profile.ProfileScreen
import com.aescalantedev.tenyo.ui.screens.profile.viewmodel.ProfileViewModel
import com.aescalantedev.tenyo.ui.screens.progress.ProgressScreen
import com.aescalantedev.tenyo.ui.screens.progress.viewmodel.ProgressViewModel
import com.aescalantedev.tenyo.ui.screens.splash.SplashScreen
import com.aescalantedev.tenyo.ui.screens.splash.viewmodel.AppBootstrapViewModel
import com.aescalantedev.tenyo.ui.screens.welcome.WelcomeScreen
import com.aescalantedev.tenyo.ui.screens.welcome.viewmodel.WelcomeViewModel
import com.aescalantedev.tenyo.ui.screens.workout.ActiveWorkoutScreen
import com.aescalantedev.tenyo.ui.screens.workout.viewmodel.ActiveWorkoutViewModel
import com.aescalantedev.tenyo.ui.theme.TenyoObsidianBackground
import com.aescalantedev.tenyo.ui.theme.TenyoTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Grafo Principal de Navegacion de Tenyo (Jetpack Navigation 3).
 *
 * Responsabilidades:
 * - Aloja el [Scaffold] global que coordina el [TenyoBottomNavigationBar] persistente
 *   y el [LocalFabState] dinamico ([ProvideFab]).
 * - Provee el [entryProvider] con las 10 rutas tipadas del catalogo.
 * - Conecta las transiciones [TenyoTransitions].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TenyoNavGraph(
    navViewModel: NavViewModel = koinViewModel(),
    bootstrapViewModel: AppBootstrapViewModel = koinViewModel(),
    activeWorkoutViewModel: ActiveWorkoutViewModel = koinViewModel(),
    planDetailViewModel: PlanDetailViewModel = koinViewModel()
) {
    val bootstrapState by bootstrapViewModel.uiState.collectAsStateWithLifecycle()
    val fabState = remember { FabState() }

    val currentDestination = navViewModel.currentDestination
    val showBottomBar = navViewModel.showBottomBar

    // Redireccion automatica desde Splash segun estado de Onboarding
    LaunchedEffect(bootstrapState.isSplashCompleted) {
        if (bootstrapState.isSplashCompleted && currentDestination == SplashRoute) {
            if (bootstrapState.preferences.hasCompletedOnboarding) {
                navViewModel.resetRoot(HomeRoute)
            } else {
                navViewModel.resetRoot(WelcomeRoute)
            }
        }
    }

    TenyoTheme(themeMode = bootstrapState.preferences.themeMode) {
        CompositionLocalProvider(LocalFabState provides fabState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = TenyoObsidianBackground,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (showBottomBar) {
                        TenyoBottomNavigationBar(
                            currentTopLevel = navViewModel.activeTopLevel,
                            onSelectTopLevel = { selectedTab ->
                                navViewModel.switchTopLevel(selectedTab)
                            }
                        )
                    }
                },
                floatingActionButtonPosition = fabState.position,
                floatingActionButton = {
                    fabState.content?.invoke()
                }
            ) { innerPadding ->
                SharedTransitionLayout {
                    NavDisplay(
                        modifier = Modifier.fillMaxSize(),
                        backStack = navViewModel.backStackManager.backStack,
                        onBack = {
                            navViewModel.popBack()
                        },
                        transitionSpec = TenyoTransitions.forwardTransition,
                        popTransitionSpec = TenyoTransitions.popTransition,
                        predictivePopTransitionSpec = TenyoTransitions.predictivePopTransition,
                        entryProvider = entryProvider {
                            // 0. SplashScreen
                            entry<SplashRoute> {
                                SplashScreen(
                                    isSeedingCatalog = bootstrapState.isSeedingCatalog
                                )
                            }

                            // 1. Onboarding y Perfil Inicial
                            entry<WelcomeRoute> {
                                val welcomeViewModel: WelcomeViewModel = koinViewModel()
                                val welcomeState by welcomeViewModel.uiState.collectAsStateWithLifecycle()

                                WelcomeScreen(
                                    uiState = welcomeState,
                                    isSeedingCatalog = bootstrapState.isSeedingCatalog,
                                    onNameChange = welcomeViewModel::updateAthleteName,
                                    onGenderChange = welcomeViewModel::selectGender,
                                    onAgeChange = welcomeViewModel::updateAge,
                                    onHeightChange = welcomeViewModel::updateHeight,
                                    onWeightChange = welcomeViewModel::updateWeight,
                                    onActivityLevelChange = welcomeViewModel::selectActivityLevel,
                                    onSelectGoal = welcomeViewModel::selectTrainingGoal,
                                    onSelectDaysPerWeek = welcomeViewModel::selectTrainingDaysPerWeek,
                                    onSelectDurationMinutes = welcomeViewModel::selectSessionDuration,
                                    onToggleMuscle = welcomeViewModel::toggleMuscle,
                                    onToggleViewSide = welcomeViewModel::toggleViewSide,
                                    onToggleBodyPartCategory = welcomeViewModel::toggleBodyPartCategory,
                                    onPreviousStep = welcomeViewModel::previousStep,
                                    onNextStep = welcomeViewModel::nextStep,
                                    onCompleteOnboarding = { openCategoriesFirst ->
                                        welcomeViewModel.completeOnboarding(
                                            openCategoriesFirst = openCategoriesFirst,
                                            onCompleted = { openCategories ->
                                                navViewModel.resetRoot(
                                                    if (openCategories) CategoriesRoute else HomeRoute
                                                )
                                            }
                                        )
                                    }
                                )
                            }

                            // 2. Pantalla de Inicio / Dashboard
                            entry<HomeRoute> {
                                val homeViewModel: HomeViewModel = koinViewModel()
                                val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()

                                HomeScreen(
                                    uiState = homeState,
                                    contentPadding = innerPadding,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                    onNavigateToCategories = {
                                        navViewModel.switchTopLevel(CategoriesRoute)
                                    },
                                    onNavigateToCategoryExercises = { category ->
                                        navViewModel.navigate(
                                            ExerciseListRoute(bodyPartRawKey = category.rawKey)
                                        )
                                    },
                                    onSearchExercises = { query ->
                                        navViewModel.navigate(
                                            ExerciseListRoute(
                                                bodyPartRawKey = null,
                                                initialSearchQuery = query
                                            )
                                        )
                                    },
                                    onNavigateToExerciseDetail = { exerciseId ->
                                        navViewModel.navigate(ExerciseDetailRoute(exerciseId))
                                    },
                                    onStartWorkoutSession = { planId ->
                                        if (planId != null) {
                                            navViewModel.navigate(ActiveWorkoutRoute(planId = planId))
                                        } else {
                                            navViewModel.switchTopLevel(WorkoutPlansRoute)
                                        }
                                    },
                                    onNavigateToPlanDetail = { planId ->
                                        navViewModel.navigate(PlanDetailRoute(planId))
                                    },
                                    onNavigateToPlans = {
                                        navViewModel.switchTopLevel(WorkoutPlansRoute)
                                    },
                                    onNavigateToProgress = {
                                        navViewModel.switchTopLevel(ProgressRoute)
                                    },
                                    onToggleFavorite = homeViewModel::toggleFavorite
                                )
                            }

                        // 3. Explorar Categorias Musculares y Equipamiento
                        entry<CategoriesRoute> {
                            val categoriesViewModel: CategoriesViewModel = koinViewModel()
                            val categoriesState by categoriesViewModel.uiState.collectAsStateWithLifecycle()

                            CategoriesScreen(
                                uiState = categoriesState,
                                contentPadding = innerPadding,
                                onSelectRegion = categoriesViewModel::selectRegion,
                                onSearchQueryChange = categoriesViewModel::updateSearchQuery,
                                onSelectCategory = { category ->
                                    navViewModel.navigate(
                                        ExerciseListRoute(bodyPartRawKey = category.rawKey)
                                    )
                                },
                                onSelectCategoryWithFilter = { category, query ->
                                    navViewModel.navigate(
                                        ExerciseListRoute(
                                            bodyPartRawKey = category.rawKey,
                                            initialSearchQuery = query
                                        )
                                    )
                                },
                                onSelectEquipment = { equipKey ->
                                    navViewModel.navigate(
                                        ExerciseListRoute(
                                            bodyPartRawKey = null,
                                            initialSearchQuery = "",
                                            equipment = equipKey
                                        )
                                    )
                                },
                                onOpenAllExercisesSearch = { query ->
                                    navViewModel.navigate(
                                        ExerciseListRoute(
                                            bodyPartRawKey = null,
                                            initialSearchQuery = query
                                        )
                                    )
                                },
                                onNavigateToPrograms = {
                                    navViewModel.switchTopLevel(WorkoutPlansRoute)
                                }
                            )
                        }

                        // 4. Listado de Ejercicios
                        entry<ExerciseListRoute> { route ->
                            val listViewModel: ExerciseListViewModel = koinViewModel()
                            val listState by listViewModel.uiState.collectAsStateWithLifecycle()

                            LaunchedEffect(route.bodyPartRawKey, route.initialSearchQuery, route.equipment) {
                                listViewModel.applyInitialArguments(
                                    bodyPartRawKey = route.bodyPartRawKey,
                                    initialSearchQuery = route.initialSearchQuery,
                                    equipment = route.equipment
                                )
                            }

                            ExerciseListScreen(
                                uiState = listState,
                                contentPadding = innerPadding,
                                onBack = { navViewModel.popBack() },
                                isSelectionMode = route.isSelectionMode,
                                onSelectMuscleOption = listViewModel::selectMuscleOption,
                                onSelectBodyPart = listViewModel::selectBodyPart,
                                onSelectEquipment = listViewModel::selectEquipment,
                                onSearchQueryChange = listViewModel::updateSearchQuery,
                                onToggleOnlyFavorites = listViewModel::toggleOnlyFavorites,
                                onToggleExerciseFavorite = listViewModel::toggleFavorite,
                                onSelectSortOrder = listViewModel::setSortOrder,
                                onToggleViewMode = listViewModel::toggleViewMode,
                                onClearFilters = listViewModel::clearFilters,
                                onUpdateScrollPosition = listViewModel::updateScrollPosition,
                                onOpenExerciseDetail = { exerciseId ->
                                    navViewModel.navigate(ExerciseDetailRoute(exerciseId))
                                },
                                onConfirmSelection = { exerciseIds ->
                                    if (route.targetPlanId != null) {
                                        planDetailViewModel.addExercises(
                                            planId = route.targetPlanId,
                                            dayOfWeek = route.targetDayOfWeek ?: 1,
                                            exerciseIds = exerciseIds
                                        )
                                        navViewModel.popBack()
                                    } else {
                                        activeWorkoutViewModel.addExercisesToSession(exerciseIds)
                                        navViewModel.popBack()
                                    }
                                },
                                onSelectExercise = { exerciseId ->
                                    if (route.isSelectionMode) {
                                        if (route.targetPlanId != null) {
                                            planDetailViewModel.addSingleExercise(
                                                planId = route.targetPlanId,
                                                dayOfWeek = route.targetDayOfWeek ?: 1,
                                                exerciseId = exerciseId
                                            )
                                            navViewModel.popBack()
                                        } else {
                                            activeWorkoutViewModel.addExerciseToSession(exerciseId)
                                            navViewModel.popBack()
                                        }
                                    } else {
                                        navViewModel.navigate(ExerciseDetailRoute(exerciseId))
                                    }
                                },
                                onCreateCustomExercise = { name, bodyPart, equipment, target, instructions ->
                                    listViewModel.createCustomExercise(
                                        name = name,
                                        bodyPart = bodyPart,
                                        equipment = equipment,
                                        targetMuscle = target,
                                        instructions = instructions,
                                        onCreated = { newId ->
                                            if (route.isSelectionMode) {
                                                if (route.targetPlanId != null) {
                                                    planDetailViewModel.addSingleExercise(
                                                        planId = route.targetPlanId,
                                                        dayOfWeek = route.targetDayOfWeek ?: 1,
                                                        exerciseId = newId
                                                    )
                                                    navViewModel.popBack()
                                                } else {
                                                    activeWorkoutViewModel.addExerciseToSession(newId)
                                                    navViewModel.popBack()
                                                }
                                            } else {
                                                navViewModel.navigate(ExerciseDetailRoute(newId))
                                            }
                                        }
                                    )
                                }
                            )
                        }

                        // 5. Detalle de Ejercicio
                        entry<ExerciseDetailRoute> { route ->
                            val detailViewModel: ExerciseDetailViewModel = koinViewModel()
                            val detailState by detailViewModel.uiState.collectAsStateWithLifecycle()

                            LaunchedEffect(route.exerciseId) {
                                detailViewModel.loadExercise(route.exerciseId)
                            }

                            ExerciseDetailScreen(
                                uiState = detailState,
                                contentPadding = innerPadding,
                                onBack = { navViewModel.popBack() },
                                onToggleFavorite = detailViewModel::toggleFavorite,
                                onAddToPlan = detailViewModel::addToActivePlan,
                                onSelectTab = detailViewModel::selectTab,
                                onStartNow = {
                                    detailViewModel.startSingleExerciseWorkout {
                                        navViewModel.navigate(
                                            ActiveWorkoutRoute(singleExerciseId = route.exerciseId)
                                        )
                                    }
                                }
                            )
                        }

                        // 6. Entrenamiento Activo en Vivo
                        entry<ActiveWorkoutRoute> { route ->
                            val activeState by activeWorkoutViewModel.uiState.collectAsStateWithLifecycle()

                            LaunchedEffect(route.planId, route.singleExerciseId, route.dayOfWeek) {
                                activeWorkoutViewModel.startOrAttachSession(
                                    planId = route.planId,
                                    singleExerciseId = route.singleExerciseId,
                                    dayOfWeek = route.dayOfWeek
                                )
                            }

                            ActiveWorkoutScreen(
                                uiState = activeState,
                                contentPadding = innerPadding,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                onBack = { navViewModel.popBack() },
                                onNavigateToExerciseList = {
                                    activeWorkoutViewModel.clearReplaceTarget()
                                    navViewModel.navigate(ExerciseListRoute(isSelectionMode = true))
                                },
                                onNavigateToExerciseDetail = { exerciseId ->
                                    navViewModel.navigate(ExerciseDetailRoute(exerciseId))
                                },
                                onToggleSetCompleted = activeWorkoutViewModel::toggleSetCompleted,
                                onAdjustSetParameters = activeWorkoutViewModel::adjustSetParameters,
                                onDeleteSet = activeWorkoutViewModel::deleteSet,
                                onAddExtraSet = activeWorkoutViewModel::addExtraSet,
                                onDeleteExercise = activeWorkoutViewModel::deleteExercise,
                                onOpenReplaceExercise = activeWorkoutViewModel::prepareReplaceExercise,
                                onOpenAddExerciseSheet = activeWorkoutViewModel::openAddExerciseSheet,
                                onCloseAddExerciseSheet = activeWorkoutViewModel::closeAddExerciseSheet,
                                onUpdatePickerQuery = activeWorkoutViewModel::updateExercisePickerQuery,
                                onSelectPickerCategory = activeWorkoutViewModel::selectExercisePickerCategory,
                                onSelectExerciseToAdd = activeWorkoutViewModel::onSelectExerciseFromPicker,
                                onOpenRestTimerPicker = activeWorkoutViewModel::openRestTimerPicker,
                                onCloseRestTimerPicker = activeWorkoutViewModel::closeRestTimerPicker,
                                onConfirmRestSeconds = activeWorkoutViewModel::updateExerciseRestSeconds,
                                onSaveExerciseNote = activeWorkoutViewModel::updateExerciseNote,
                                onCancelRestTimer = activeWorkoutViewModel::cancelRestTimer,
                                onTogglePause = activeWorkoutViewModel::togglePause,
                                onReorderExercises = activeWorkoutViewModel::reorderExercises,
                                onDiscardWorkout = {
                                    activeWorkoutViewModel.discardWorkout {
                                        navViewModel.popBack()
                                    }
                                },
                                onFinishWorkout = { updatePlan ->
                                    activeWorkoutViewModel.finishWorkout(updatePlan) {
                                        navViewModel.popBack()
                                        navViewModel.switchTopLevel(ProgressRoute)
                                    }
                                }
                            )
                        }

                        // 7. Planes de Entrenamiento
                        entry<WorkoutPlansRoute> {
                            val plansViewModel: WorkoutPlansViewModel = koinViewModel()
                            val plansState by plansViewModel.uiState.collectAsStateWithLifecycle()

                            WorkoutPlansScreen(
                                uiState = plansState,
                                contentPadding = innerPadding,
                                onSelectTab = plansViewModel::selectTab,
                                onSelectGoal = plansViewModel::selectGoal,
                                onActivatePlan = plansViewModel::activatePlan,
                                onStartPlanSession = { planId ->
                                    navViewModel.navigate(ActiveWorkoutRoute(planId = planId))
                                },
                                onNavigateToPlanDetail = { planId ->
                                    navViewModel.navigate(PlanDetailRoute(planId))
                                },
                                onCreateNewPlan = {
                                    plansViewModel.createNewEmptyPlan { createdPlanId ->
                                        navViewModel.navigate(PlanDetailRoute(createdPlanId))
                                    }
                                },
                                onDeletePlan = plansViewModel::deletePlan
                            )
                        }

                        // 7.1 Detalle y Editor del Plan de Entrenamiento
                        entry<PlanDetailRoute> { route ->
                            val planDetailViewModel: PlanDetailViewModel = koinViewModel()
                            val detailState by planDetailViewModel.uiState.collectAsStateWithLifecycle()

                            LaunchedEffect(route.planId) {
                                planDetailViewModel.init(route.planId)
                            }

                            PlanDetailScreen(
                                uiState = detailState,
                                onBack = { navViewModel.popBack() },
                                onSelectDay = planDetailViewModel::selectDay,
                                onToggleReminder = planDetailViewModel::toggleReminders,
                                onUpdateReminderTime = planDetailViewModel::updateReminderTime,
                                onActivatePlan = planDetailViewModel::activatePlan,
                                onToggleRestDay = planDetailViewModel::toggleRestDayForCurrentDay,
                                onOpenEditExerciseConfig = planDetailViewModel::openEditExerciseConfig,
                                onCloseEditExerciseConfig = planDetailViewModel::closeEditExerciseConfig,
                                onUpdateExerciseConfig = planDetailViewModel::updateExerciseConfig,
                                onRemoveExerciseFromCurrentDay = planDetailViewModel::removeExerciseFromCurrentDay,
                                onStartWorkoutForDay = { planId, dayOfWeek ->
                                    navViewModel.navigate(ActiveWorkoutRoute(planId = planId, dayOfWeek = dayOfWeek))
                                },
                                onOpenExerciseSelectionList = {
                                    navViewModel.navigate(
                                        ExerciseListRoute(
                                            isSelectionMode = true,
                                            targetPlanId = route.planId,
                                            targetDayOfWeek = detailState.selectedDayOfWeek
                                        )
                                    )
                                },
                                onOpenTimePicker = planDetailViewModel::openTimePicker,
                                onCloseTimePicker = planDetailViewModel::closeTimePicker,
                                onOpenRenameDayDialog = planDetailViewModel::openRenameDayDialog,
                                onCloseRenameDayDialog = planDetailViewModel::closeRenameDayDialog,
                                onUpdateDayDetails = planDetailViewModel::updateDayDetails,
                                onOpenRenameTitleDialog = planDetailViewModel::openRenameTitleDialog,
                                onCloseRenameTitleDialog = planDetailViewModel::closeRenameTitleDialog,
                                onUpdatePlanTitle = planDetailViewModel::updatePlanTitle,
                                onSaveExerciseNote = planDetailViewModel::saveExerciseNote,
                                onAddExtraSet = planDetailViewModel::addExtraSet,
                                onDeleteExerciseSet = planDetailViewModel::deleteExerciseSet,
                                onUpdateExerciseRestSeconds = planDetailViewModel::updateExerciseRestSeconds,
                                onAdjustExerciseSetParameters = planDetailViewModel::updateExerciseSetParameters,
                                onNavigateToExerciseDetail = { exId ->
                                    navViewModel.navigate(ExerciseDetailRoute(exId))
                                },
                                onReorderExercises = planDetailViewModel::reorderExercises,
                                contentPadding = innerPadding
                            )
                        }

                        // 8. Progreso y Rango Tenyo
                        entry<ProgressRoute> {
                            val progressViewModel: ProgressViewModel = koinViewModel()
                            val progressState by progressViewModel.uiState.collectAsStateWithLifecycle()

                            ProgressScreen(
                                uiState = progressState,
                                contentPadding = innerPadding,
                                onSelectSubTab = progressViewModel::selectSubTab,
                                onPreviousMonth = progressViewModel::previousMonth,
                                onNextMonth = progressViewModel::nextMonth,
                                onSelectCalendarDay = progressViewModel::selectCalendarDay,
                                onToggleTojiSide = progressViewModel::toggleTojiViewSide,
                                onSelectExerciseRecord = { exerciseId ->
                                    navViewModel.navigate(ExerciseDetailRoute(exerciseId))
                                }
                            )
                        }

                        // 9. Perfil de Usuario y Ajustes
                        entry<ProfileRoute> {
                            val profileViewModel: ProfileViewModel = koinViewModel()
                            val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

                            ProfileScreen(
                                uiState = profileState,
                                contentPadding = innerPadding,
                                onSelectTab = profileViewModel::selectTab,
                                onUpdateAthleteName = profileViewModel::updateAthleteName,
                                onUpdateAgeText = profileViewModel::updateAgeText,
                                onUpdateWeightKg = profileViewModel::updateWeightKg,
                                onUpdateHeightCm = profileViewModel::updateHeightCm,
                                onToggleMuscle = profileViewModel::toggleMuscle,
                                onToggleViewSide = profileViewModel::toggleViewSide,
                                onToggleBodyPartCategory = profileViewModel::toggleBodyPartCategory,
                                onSelectArchetype = profileViewModel::selectArchetype,
                                onSelectExperience = profileViewModel::selectExperience,
                                onSelectTrainingDays = profileViewModel::selectTrainingDays,
                                onSaveProfile = profileViewModel::saveProfile,
                                onSelectLanguage = profileViewModel::selectLanguage,
                                onSelectThemeMode = profileViewModel::selectThemeMode,
                                onSelectWeightUnit = profileViewModel::selectWeightUnit,
                                onReopenOnboardingWizard = {
                                    navViewModel.navigate(WelcomeRoute)
                                }
                            )
                        }
                    }
                )
                }
            }
        }
    }
}
