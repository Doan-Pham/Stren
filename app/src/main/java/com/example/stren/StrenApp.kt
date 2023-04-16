package com.example.stren

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stren.feat.auth.SplashScreen
import com.example.stren.navigation.NAV_ROUTE_AUTH
import com.example.stren.navigation.Screen
import com.example.stren.navigation.authenticationGraph

val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
fun StrenApp(modifier: Modifier) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }


    // This allows any screen in the composition to access snackbar
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = modifier
            ) {
                composable(route = Screen.Splash.route) {
                    SplashScreen(onNavigateToNextScreen = { navController.navigate(NAV_ROUTE_AUTH) })
                }

                authenticationGraph()
            }
        }
    }
}