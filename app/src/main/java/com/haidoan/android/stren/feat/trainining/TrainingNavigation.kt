package com.haidoan.android.stren.feat.trainining

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.feat.trainining.exercises.EXERCISES_SCREEN_ROUTE

const val TRAINING_GRAPH_ROUTE = "training_graph_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.trainingGraph() {
    navigation(startDestination = EXERCISES_SCREEN_ROUTE, route = TRAINING_GRAPH_ROUTE) {
        composable(route = EXERCISES_SCREEN_ROUTE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red)
            )
        }
    }
}