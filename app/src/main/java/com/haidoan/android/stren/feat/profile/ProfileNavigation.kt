package com.haidoan.android.stren.feat.profile

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration


const val PROFILE_GRAPH_ROUTE = "profile_graph_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {
    navigation(startDestination = PROFILE_SCREEN_ROUTE, route = PROFILE_GRAPH_ROUTE) {
        composable(route = PROFILE_SCREEN_ROUTE) {
            ProfileRoute(appBarConfigurationChangeHandler = appBarConfigurationChangeHandler)
        }
    }
}