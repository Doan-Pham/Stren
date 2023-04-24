package com.haidoan.android.stren.feat.trainining

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText
import com.haidoan.android.stren.feat.trainining.exercises.EXERCISES_SCREEN_ROUTE
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesRoute

const val TRAINING_GRAPH_ROUTE = "training_graph_route"
const val TRAINING_GRAPH_STARTING_ROUTE = "training_graph_starting_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.trainingGraph() {
    navigation(startDestination = TRAINING_GRAPH_STARTING_ROUTE, route = TRAINING_GRAPH_ROUTE) {
        composable(route = TRAINING_GRAPH_STARTING_ROUTE) {
            //val tabs = listOf("History", "Routines", "Exercises")

            TrainingTabsScreen(
                tabNamesAndScreenComposables = listOf(
                    Pair("Exercises") { ExercisesRoute() },
                    Pair("History") { DummyBoxWithText(text = "History") },
                    Pair("Routines") { DummyBoxWithText(text = "Routines") },
                )
            )
        }
        composable(route = EXERCISES_SCREEN_ROUTE) {
            ExercisesRoute()
        }
    }
}