package com.example.stren

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stren.navigation.NAV_ROUTE_AUTH
import com.example.stren.navigation.authenticationGraph
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

private const val TAG = "StrenApp"
val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StrenApp(modifier: Modifier, viewModel: StrenAppViewModel = hiltViewModel()) {
    val navController = rememberAnimatedNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val isUserSignedIn by rememberSaveable { viewModel.isUserSignedIn }
    Log.d(TAG, "isUserSignedIn: $isUserSignedIn")
    // This allows any screen in the composition to access snackbar
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
            AnimatedNavHost(
                navController = navController,
                startDestination = NAV_ROUTE_AUTH,
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
                            navController.navigate("Test") {
                                launchSingleTop = true
                                popUpTo(NAV_ROUTE_AUTH) { inclusive = true }
                            }
                        }
                    },
                )
                composable(route = "Test") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black)
                    )
                    if (!isUserSignedIn) {
                        navController.navigate(NAV_ROUTE_AUTH) {
                            launchSingleTop = true
                            popUpTo("Test") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
