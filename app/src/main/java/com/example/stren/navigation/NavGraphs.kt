package com.example.stren.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.stren.feat.auth.LoginScreen

const val NAV_ROUTE_AUTH = "auth_graph;"
fun NavGraphBuilder.authenticationGraph() {
    navigation(startDestination = Screen.Login.route, route = NAV_ROUTE_AUTH) {
        composable(route = Screen.Login.route) {
            LoginScreen()
        }
    }
}