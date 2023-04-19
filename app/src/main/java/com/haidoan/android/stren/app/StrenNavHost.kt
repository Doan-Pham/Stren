package com.haidoan.android.stren.app

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.navigation.NAV_ROUTE_AUTH
import com.haidoan.android.stren.navigation.TopLevelDestination
import com.haidoan.android.stren.navigation.authenticationGraph

private const val TAG = "StrenNavHost"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StrenNavHost(
    navController: NavHostController, modifier: Modifier = Modifier, isUserSignedIn: Boolean,
    startDestination: String = NAV_ROUTE_AUTH,
) {
    Log.d(TAG, "isUserSignedIn: $isUserSignedIn")
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize(),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(700)
            )
        }
    ) {
        authenticationGraph(
            navController,
            onUserAlreadySignedIn = {
                if (isUserSignedIn) {
                    navController.navigate(TopLevelDestination.DASHBOARD.route) {
                        launchSingleTop = true
                        popUpTo(NAV_ROUTE_AUTH) { inclusive = true }
                    }
                }
            },
        )
        composable(route = TopLevelDestination.DASHBOARD.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black)
                    .testTag("Screen-Dashboard")
            )
            if (!isUserSignedIn) {
                navController.navigate(NAV_ROUTE_AUTH) {
                    launchSingleTop = true
                    popUpTo("Test") { inclusive = true }
                }
            }
        }
        composable(route = TopLevelDestination.TRAINING.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red)
            )
            if (!isUserSignedIn) {
                navController.navigate(NAV_ROUTE_AUTH) {
                    launchSingleTop = true
                    popUpTo("Test") { inclusive = true }
                }
            }
        }
        composable(route = TopLevelDestination.NUTRITION.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Yellow)
            )
            if (!isUserSignedIn) {
                navController.navigate(NAV_ROUTE_AUTH) {
                    launchSingleTop = true
                    popUpTo("Test") { inclusive = true }
                }
            }
        }
        composable(route = TopLevelDestination.PROFILE.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Green)
            )
            if (!isUserSignedIn) {
                navController.navigate(NAV_ROUTE_AUTH) {
                    launchSingleTop = true
                    popUpTo("Test") { inclusive = true }
                }
            }
        }
    }

    if (isUserSignedIn) {
        navController.navigate(TopLevelDestination.DASHBOARD.route)
    }
}