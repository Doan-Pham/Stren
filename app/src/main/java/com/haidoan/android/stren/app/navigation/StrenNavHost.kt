package com.haidoan.android.stren.app.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.haidoan.android.stren.feat.auth.AUTH_GRAH_ROUTE
import com.haidoan.android.stren.feat.auth.authenticationGraph
import com.haidoan.android.stren.feat.auth.navigateToAuthentication
import com.haidoan.android.stren.feat.dashboard.dashboardGraph
import com.haidoan.android.stren.feat.nutrition.nutritionGraph
import com.haidoan.android.stren.feat.onboarding.navigateToOnboarding
import com.haidoan.android.stren.feat.onboarding.onboardingGraph
import com.haidoan.android.stren.feat.settings.settingsGraph
import com.haidoan.android.stren.feat.training.trainingGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StrenNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userId: String,
    shouldShowOnboarding: Boolean,
    isUserSignedIn: Boolean,
    startDestination: String = AUTH_GRAH_ROUTE,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {},
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize(),
//        enterTransition = {
//            slideIntoContainer(
//                AnimatedContentScope.SlideDirection.Left,
//                animationSpec = tween(700)
//            )
//        },
//        popEnterTransition = {
//            slideIntoContainer(
//                AnimatedContentScope.SlideDirection.Left,
//                animationSpec = tween(700)
//            )
//        },
//        popExitTransition = {
//            slideOutOfContainer(
//                AnimatedContentScope.SlideDirection.Left,
//                animationSpec = tween(700)
//            )
//        },
//        exitTransition = {
//            slideOutOfContainer(
//                AnimatedContentScope.SlideDirection.Left,
//                animationSpec = tween(700)
//            )
//        }
    ) {
        authenticationGraph(navController)
        onboardingGraph(onCompleteOnboarding = {
            navController.navigate(TopLevelDestination.DASHBOARD.route) {
                popUpTo(0)
            }
        })
        dashboardGraph(navController, appBarConfigurationChangeHandler)
        trainingGraph(navController, appBarConfigurationChangeHandler)
        nutritionGraph(navController, appBarConfigurationChangeHandler)
        settingsGraph(navController, appBarConfigurationChangeHandler)
    }

    LaunchedEffect(key1 = isUserSignedIn, block = {
        if (!isUserSignedIn) {
            navController.navigateToAuthentication {
                // This removes all screens on back stack
                popUpTo(0)
            }
        } else if (shouldShowOnboarding) {
            navController.navigateToOnboarding(userId) {
                // This removes all screens on back stack
                popUpTo(0)
            }
        }
    })
}