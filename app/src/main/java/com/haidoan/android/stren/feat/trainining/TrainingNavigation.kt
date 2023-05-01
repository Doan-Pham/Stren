package com.haidoan.android.stren.feat.trainining

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText
import com.haidoan.android.stren.feat.trainining.exercises.EXERCISES_SCREEN_ROUTE
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesRoute

const val TRAINING_GRAPH_ROUTE = "training_graph_route"
const val TRAINING_GRAPH_STARTING_ROUTE = "training_graph_starting_route"
private const val TAG = "trainingGraph"

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
fun NavGraphBuilder.trainingGraph(appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}) {
    navigation(startDestination = TRAINING_GRAPH_STARTING_ROUTE, route = TRAINING_GRAPH_ROUTE) {
        composable(route = TRAINING_GRAPH_STARTING_ROUTE) {
            TrainingTabsScreen(
                tabNamesAndScreenComposables = listOf(
                    Pair("Exercises") {
                        ExercisesRoute(appBarConfigurationChangeHandler = {
                            appBarConfigurationChangeHandler(it)
                            Log.d("ExercisesScreen", "App bar configured")
                        })
                    },
                    Pair("History") {
                        DummyBoxWithText(text = "History")
                        appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                    },
                    Pair("Routines") {
                        DummyBoxWithText(text = "Routines")
                        appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                    },
                )
            )
        }
        composable(route = EXERCISES_SCREEN_ROUTE) {

        }
    }
}