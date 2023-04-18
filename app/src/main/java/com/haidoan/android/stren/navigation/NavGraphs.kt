package com.haidoan.android.stren.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.feat.auth.login.LoginScreen
import com.haidoan.android.stren.feat.auth.signup.SignupScreen
import kotlinx.coroutines.delay

const val NAV_ROUTE_AUTH = "auth_graph"
private const val TAG = "authenticationGraph"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.authenticationGraph(
    navController: NavHostController, onUserAlreadySignedIn: () -> Unit
) {
    navigation(startDestination = Screen.Login.route, route = NAV_ROUTE_AUTH) {
        composable(route = Screen.Login.route) {
            LoginScreen(onSignupClick = {
                navController.navigate(Screen.Signup.route) {
                    popUpTo(
                        Screen.Login.route
                    ) { inclusive = true }
                }
            }, onAuthSuccess = onUserAlreadySignedIn)

            LaunchedEffect(key1 = true, block = {
                delay(300)
                onUserAlreadySignedIn()
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
