package com.example.stren.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.example.stren.feat.auth.LoginScreen
import com.example.stren.feat.auth.SignupScreen
import com.google.accompanist.navigation.animation.composable

const val NAV_ROUTE_AUTH = "auth_graph"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.authenticationGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Login.route, route = NAV_ROUTE_AUTH) {
        composable(route = Screen.Login.route) {
            LoginScreen(onSignupClick = {
                navController.navigate(Screen.Signup.route) {
                    popUpTo(
                        Screen.Login.route
                    ) { inclusive = true }
                }
            })
        }
        composable(route = Screen.Signup.route) {
            SignupScreen(onSignInClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(
                        Screen.Signup.route
                    ) { inclusive = true }
                }
            }, onCreateAccountSuccess = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(
                        Screen.Signup.route
                    ) { inclusive = true }
                }
            })
        }
    }
}