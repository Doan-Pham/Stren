package com.haidoan.android.stren.app.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.TopLevelDestination


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
        navController.navigate(topLevelDestination.route) {
            popUpTo(startingTopLevelDestination.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

    }

    val shouldShowBottomBar: Boolean
        @Composable get() = currentDestination.isTopLevelOrTopLevelImmediateChild()

    val shouldShowTopBar: Boolean
        @Composable get() = currentTopLevelDestination != null


    var currentAppBarConfiguration by mutableStateOf<AppBarConfiguration>(AppBarConfiguration.NavigationAppBar())

    var previousAppBarConfiguration: AppBarConfiguration = currentAppBarConfiguration
}