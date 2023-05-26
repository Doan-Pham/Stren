package com.haidoan.android.stren.feat.dashboard

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText

const val DASHBOARD_GRAPH_ROUTE = "dashboard_graph_route"
const val DASHBOARD_GRAPH_STARTING_ROUTE = "dashboard_graph_starting_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.dashboardGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {
    navigation(startDestination = DASHBOARD_GRAPH_STARTING_ROUTE, route = DASHBOARD_GRAPH_ROUTE) {
        composable(route = DASHBOARD_GRAPH_STARTING_ROUTE) {
            DummyBoxWithText(text = "Dashboard")
            var isAppBarConfigured by remember { mutableStateOf(false) }
            if (!isAppBarConfigured) {
                appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                isAppBarConfigured = true
            }
        }
    }
}