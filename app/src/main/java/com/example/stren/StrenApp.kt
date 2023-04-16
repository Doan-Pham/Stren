package com.example.stren

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
fun StrenApp(modifier: Modifier, viewModel: StrenAppViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val isUserSignedIn by viewModel.isUserSignedIn

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
                    SplashScreen(onNavigateToNextScreen = {
                        if (!isUserSignedIn) navController.navigate(NAV_ROUTE_AUTH) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        } else navController.navigate("//TODO: Add a destination") {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    })
                }

                authenticationGraph()
            }
        }
    }
}