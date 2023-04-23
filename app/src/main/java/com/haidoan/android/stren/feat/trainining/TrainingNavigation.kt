package com.haidoan.android.stren.feat.trainining

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.feat.trainining.exercises.EXERCISES_SCREEN_ROUTE

const val TRAINING_GRAPH_ROUTE = "training_graph_route"
const val TRAINING_GRAPH_STARTING_ROUTE = "training_graph_starting_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.trainingGraph() {
    navigation(startDestination = TRAINING_GRAPH_STARTING_ROUTE, route = TRAINING_GRAPH_ROUTE) {
        composable(route = TRAINING_GRAPH_STARTING_ROUTE) {
            var tabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("History", "Routines", "Exercises")

            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = tabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title) },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index }
                        )
                    }
                }
                when (tabIndex) {
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(CenterHorizontally)
                            .background(color = Color.White)
                    ) {
                        Text(text = tabs[0])
                    }

                    1 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(CenterHorizontally)
                            .background(color = Color.White)
                    ) {
                        Text(text = tabs[1])
                    }
                    2 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(CenterHorizontally)
                            .background(color = Color.White)
                    ) {
                        Text(text = tabs[2])
                    }
                }
            }
        }

        composable(route = EXERCISES_SCREEN_ROUTE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red)
            )
        }
    }
}