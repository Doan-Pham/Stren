package com.haidoan.android.stren.feat.trainining

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesRoute
import com.haidoan.android.stren.feat.trainining.exercises.navigation.exerciseGraph
import com.haidoan.android.stren.feat.trainining.exercises.navigation.navigateToExerciseDetail
import com.haidoan.android.stren.feat.trainining.history.TrainingHistoryRoute
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
                        )
                    },
                    Pair("Routines") {
                        DummyBoxWithText(text = "Routines")
                        appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                    },
                )
            )
        }
        exerciseGraph(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() })
    }
}