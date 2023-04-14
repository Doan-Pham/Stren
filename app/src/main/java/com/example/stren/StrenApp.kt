package com.example.stren

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stren.feat.auth.SplashScreen
import com.example.stren.navigation.NAV_ROUTE_AUTH
import com.example.stren.navigation.Screen
import com.example.stren.navigation.authenticationGraph

@Composable
fun StrenApp(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(onNavigateToNextScreen = { navController.navigate(NAV_ROUTE_AUTH)})
        }

        authenticationGraph()
    }
}