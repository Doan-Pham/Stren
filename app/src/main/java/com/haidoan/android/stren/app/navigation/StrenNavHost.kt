package com.haidoan.android.stren.app.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.firebase.auth.FirebaseAuth
import com.haidoan.android.stren.feat.auth.NAV_ROUTE_AUTH
import com.haidoan.android.stren.feat.auth.authenticationGraph
import com.haidoan.android.stren.feat.auth.navigateToAuthentication
import com.haidoan.android.stren.feat.training.trainingGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StrenNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isUserSignedIn: Boolean,
    startDestination: String = NAV_ROUTE_AUTH,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}
) {
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
        authenticationGraph(navController)
        composable(route = TopLevelDestination.DASHBOARD.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black)
                    .testTag("Screen-Dashboard")
            ) {
                var isAppBarConfigured by remember { mutableStateOf(false) }
                if (!isAppBarConfigured) {
                    appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                    isAppBarConfigured = true
                }
            }
        }
        trainingGraph(navController, appBarConfigurationChangeHandler)
        composable(route = TopLevelDestination.NUTRITION.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("nutrition")
                var isAppBarConfigured by remember { mutableStateOf(false) }
                if (!isAppBarConfigured) {
                    appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                    isAppBarConfigured = true
                }
            }
        }
        composable(route = TopLevelDestination.PROFILE.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Green)
                    .clickable {
                        FirebaseAuth
                            .getInstance()
                            .signOut()
                    }
            ) {
                var isAppBarConfigured by remember { mutableStateOf(false) }
                if (!isAppBarConfigured) {
                    appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                    isAppBarConfigured = true
                }
            }
        }
    }

    LaunchedEffect(key1 = isUserSignedIn, block = {
        if (isUserSignedIn) {
            navController.navigate(TopLevelDestination.DASHBOARD.route) {
                popUpTo(0)
            }
        } else {
            navController.navigateToAuthentication {
                // This removes all screens on back stack
                popUpTo(0)
            }
        }
    })
}