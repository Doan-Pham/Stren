package com.haidoan.android.stren.feat.training

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.TabLayout
import com.haidoan.android.stren.feat.training.exercises.exerciseGraph
import com.haidoan.android.stren.feat.training.exercises.navigateToCreateExercise
import com.haidoan.android.stren.feat.training.exercises.navigateToExerciseDetail
import com.haidoan.android.stren.feat.training.exercises.view_exercises.ExercisesRoute
import com.haidoan.android.stren.feat.training.history.*
import com.haidoan.android.stren.feat.training.history.log_workout.*
import com.haidoan.android.stren.feat.training.routines.RoutinesRoute
import com.haidoan.android.stren.feat.training.routines.navigateToRoutineGraph
import com.haidoan.android.stren.feat.training.routines.routineGraph
import timber.log.Timber

internal const val TRAINING_GRAPH_ROUTE = "training_graph_route"
const val TRAINING_GRAPH_STARTING_ROUTE = "training_graph_starting_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.trainingGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}
) {
    navigation(startDestination = TRAINING_GRAPH_STARTING_ROUTE, route = TRAINING_GRAPH_ROUTE) {
        composable(route = TRAINING_GRAPH_STARTING_ROUTE) {
            TabLayout(
                tabNamesAndScreenComposables = listOf(
                    Pair("Exercises") {
                        ExercisesRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                                Timber.d("App bar configured")
                            },
                            onNavigateToExerciseDetailScreen = {
                                navController.navigateToExerciseDetail(it)
                            },
                            onNavigateToCreateExerciseScreen = {
                                navController.navigateToCreateExercise(it)
                            })

                    },
                    Pair("History") {
                        TrainingHistoryRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                            },
                            onNavigateToAddWorkoutScreen = { userId, selectedDate ->
                                navController.navigateToAddWorkoutScreen(
                                    userId = userId,
                                    selectedDate = selectedDate
                                )
                            },
                            onNavigateToStartWorkoutScreen = { userId, selectedDate ->
                                navController.navigateToStartWorkoutScreen(
                                    userId = userId,
                                    selectedDate = selectedDate
                                )
                            },
                            onNavigateToEditWorkoutScreen = { userId, workoutId ->
                                navController.navigateToEditWorkoutScreen(
                                    userId = userId,
                                    workoutId = workoutId
                                )
                            }
                        )
                    },
                    Pair("Routines") {
                        RoutinesRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                            },
                            onNavigateToAddRoutineScreen = { userId ->
                                navController.navigateToRoutineGraph(
                                    userId = userId,
                                    isAddingRoutine = true
                                )
                            },
                            onNavigateToEditRoutineScreen = { userId, routineId ->
                                navController.navigateToRoutineGraph(
                                    isAddingRoutine = false,
                                    userId = userId,
                                    routineId = routineId
                                )
                            },
                            onNavigateToAddWorkoutScreen = { userId, routineId ->
                                navController.navigateToAddWorkoutWithRoutine(
                                    userId = userId,
                                    routineId = routineId
                                )
                            }
                        )
                    },
                )
            )
        }
        exerciseGraph(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() })

        routineGraph(
            navController = navController,
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler
        )

        trainingHistoryGraph(
            navController = navController,
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler
        )
    }
}