package com.haidoan.android.stren.feat.dashboard

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.training.history.navigateToAddWorkoutWithRoutine

const val DASHBOARD_GRAPH_ROUTE = "dashboard_graph_route"
const val DASHBOARD_GRAPH_STARTING_ROUTE = "dashboard_graph_starting_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.dashboardGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    navigation(startDestination = DASHBOARD_GRAPH_STARTING_ROUTE, route = DASHBOARD_GRAPH_ROUTE) {
        composable(route = DASHBOARD_GRAPH_STARTING_ROUTE) {
            DashboardRoute(appBarConfigurationChangeHandler = {
                appBarConfigurationChangeHandler(it)
            },
                onNavigateToLogWorkoutScreen = {
                    navController.navigateToAddWorkoutWithRoutine(userId = "UNDEFINED USER_ID", routineId = it)
                })
        }
    }
}