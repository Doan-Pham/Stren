package com.haidoan.android.stren.feat.trainining

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesRoute
import com.haidoan.android.stren.feat.trainining.exercises.navigation.exerciseGraph
import com.haidoan.android.stren.feat.trainining.exercises.navigation.navigateToExerciseDetail
import com.haidoan.android.stren.feat.trainining.history.TrainingHistoryRoute
import com.haidoan.android.stren.feat.trainining.history.log_workout.navigateToLogWorkoutScreen
import com.haidoan.android.stren.feat.trainining.history.log_workout.workoutGraph
import com.haidoan.android.stren.feat.trainining.routines.RoutinesRoute
import com.haidoan.android.stren.feat.trainining.routines.navigateToRoutineGraph
import com.haidoan.android.stren.feat.trainining.routines.routineGraph
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
            TrainingTabsScreen(
                tabNamesAndScreenComposables = listOf(
                    Pair("Exercises") {
                        ExercisesRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                                Timber.d("App bar configured")
                            },
                            onNavigateToExerciseDetailScreen = {
                                navController.navigateToExerciseDetail(it)
                            })
                    },
                    Pair("History") {
                        TrainingHistoryRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                            },
                            onNavigateToAddWorkoutScreen = { userId, selectedDate ->
                                navController.navigateToLogWorkoutScreen(
                                    userId = userId,
                                    isAddingWorkout = true,
                                    selectedDate = selectedDate
                                )
                            },
                            onNavigateToEditWorkoutScreen = { userId, workoutId ->
                                navController.navigateToLogWorkoutScreen(
                                    userId = userId,
                                    isAddingWorkout = false,
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

        workoutGraph(
            navController = navController,
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler
        )
    }
}