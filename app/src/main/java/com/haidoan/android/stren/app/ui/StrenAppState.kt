package com.haidoan.android.stren.app.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.TopLevelDestination
import com.haidoan.android.stren.feat.auth.NAV_ROUTE_AUTH


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberStrenAppState(
    navController: NavHostController = rememberAnimatedNavController(),
): StrenAppState {
    return remember(navController) { StrenAppState(navController) }
}

class StrenAppState(val navController: NavHostController) {

    val topLevelDestinations = TopLevelDestination.values().asList()

    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = topLevelDestinations.firstOrNull { it.route == currentDestination?.route || it.route == currentDestination?.parent?.route }

    private fun NavDestination?.isTopLevelOrTopLevelImmediateChild() =
        topLevelDestinations.map { it.route }.contains(this?.route) ||
                topLevelDestinations.flatMap { it.immediateChildDestinationRoutes }
                    .contains(this?.route)


    private val startingTopLevelDestination = TopLevelDestination.DASHBOARD

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        /**
         * If the starting top-level destination doesn't have any children, proceed as usual.

        However, if the top-level destination has immediate children, this means the destination
        is a nested nav graph, and since any navigation to nested graph is actually navigation to
        the graph's start destination route, need to change "startingRoute" val to this nav graph's start destination.

         */
        val startingRoute =
            if (startingTopLevelDestination.startingChildDestinationRoute.isEmpty()) {
                startingTopLevelDestination.route
            } else {
                startingTopLevelDestination.startingChildDestinationRoute
            }
        navController.navigate(topLevelDestination.route) {
            popUpTo(startingRoute) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

    }

    val shouldShowBottomBar: Boolean
        @Composable get() = currentDestination.isTopLevelOrTopLevelImmediateChild()

    val shouldShowTopBar: Boolean
        @Composable get() = currentDestination?.parent?.route != NAV_ROUTE_AUTH


    var currentAppBarConfiguration by mutableStateOf<AppBarConfiguration>(AppBarConfiguration.NavigationAppBar())

    var previousAppBarConfiguration: AppBarConfiguration = currentAppBarConfiguration
}