package com.haidoan.android.stren.feat.auth

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.feat.auth.login.LOGIN_SCREEN_ROUTE
import com.haidoan.android.stren.feat.auth.login.LoginScreen
import com.haidoan.android.stren.feat.auth.signup.SIGNUP_SCREEN_ROUTE
import com.haidoan.android.stren.feat.auth.signup.SignupScreen

const val NAV_ROUTE_AUTH = "auth_graph_route"

fun NavController.navigateToAuthentication(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(NAV_ROUTE_AUTH, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.authenticationGraph(
    navController: NavHostController
) {
    navigation(startDestination = LOGIN_SCREEN_ROUTE, route = NAV_ROUTE_AUTH) {
        composable(route = LOGIN_SCREEN_ROUTE) {
            LoginScreen(onSignupClick = {
                navController.navigate(SIGNUP_SCREEN_ROUTE) {
                    popUpTo(
                        LOGIN_SCREEN_ROUTE
                    ) { inclusive = true }
                }
            })


        }
        composable(route = SIGNUP_SCREEN_ROUTE) {
            SignupScreen(onSignInClick = {
                navController.navigate(LOGIN_SCREEN_ROUTE) {
                    popUpTo(
                        SIGNUP_SCREEN_ROUTE
                    ) { inclusive = true }
                }
            }, onCreateAccountSuccess = {
                navController.navigate(LOGIN_SCREEN_ROUTE) {
                    popUpTo(
                        SIGNUP_SCREEN_ROUTE
                    ) { inclusive = true }
                }
            })
        }

    }
}