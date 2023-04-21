package com.haidoan.android.stren.app.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.haidoan.android.stren.app.navigation.TopLevelDestination


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberStrenAppState(
    navController: NavHostController = rememberAnimatedNavController(),
): StrenAppState {
    return remember(navController) { StrenAppState(navController) }
}

class StrenAppState(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            TopLevelDestination.DASHBOARD.route -> TopLevelDestination.DASHBOARD
            TopLevelDestination.TRAINING.route -> TopLevelDestination.TRAINING
            TopLevelDestination.NUTRITION.route -> TopLevelDestination.NUTRITION
            TopLevelDestination.PROFILE.route -> TopLevelDestination.PROFILE
            else -> null
        }

    val shouldShowBottomBar: Boolean
        @Composable get() = currentTopLevelDestination != null
}