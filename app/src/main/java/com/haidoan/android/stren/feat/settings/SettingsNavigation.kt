package com.haidoan.android.stren.feat.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.settings.about.ABOUT_SCREEN_ROUTE
import com.haidoan.android.stren.feat.settings.about.AboutRoute
import com.haidoan.android.stren.feat.settings.measurements.MEASUREMENTS_SCREEN_ROUTE
import com.haidoan.android.stren.feat.settings.measurements.MeasurementsRoute
import com.haidoan.android.stren.feat.settings.measurements.USER_ID_MEASUREMENTS_NAV_ARG
import com.haidoan.android.stren.feat.settings.profile.PROFILE_SCREEN_ROUTE
import com.haidoan.android.stren.feat.settings.profile.ProfileRoute
import com.haidoan.android.stren.feat.settings.profile.USER_ID_PROFILE_NAV_ARG


const val SETTINGS_GRAPH_ROUTE = "profile_graph_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {
    navigation(startDestination = SETTINGS_SCREEN_ROUTE, route = SETTINGS_GRAPH_ROUTE) {
        composable(route = SETTINGS_SCREEN_ROUTE) {
            SettingsRoute(
                appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
                onNavigateToEditProfile = { userId ->
                    navController.navigate(
                        "$PROFILE_SCREEN_ROUTE/$userId"
                    )
                },
                onNavigateToMeasurements = { userId ->
                    navController.navigate(
                        "$MEASUREMENTS_SCREEN_ROUTE/$userId"
                    )
                },
                onNavigateToAbout = {
                    navController.navigate(ABOUT_SCREEN_ROUTE)
                })
        }
        composable(
            route = PROFILE_SCREEN_ROUTE +
                    "/" + "{$USER_ID_PROFILE_NAV_ARG}",
            arguments = listOf(
                navArgument(USER_ID_PROFILE_NAV_ARG) { type = NavType.StringType },
            )
        ) {
            ProfileRoute(
                appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
                onBackToPreviousScreen = { navController.popBackStack() })
        }
        composable(
            route = MEASUREMENTS_SCREEN_ROUTE +
                    "/" + "{$USER_ID_MEASUREMENTS_NAV_ARG}",
            arguments = listOf(
                navArgument(USER_ID_MEASUREMENTS_NAV_ARG) { type = NavType.StringType },
            )
        ) {
            MeasurementsRoute(
                appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
                onBackToPreviousScreen = { navController.popBackStack() })
        }
        composable(
            route = ABOUT_SCREEN_ROUTE,
        ) {
            AboutRoute(
                appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
                onBackToPreviousScreen = { navController.popBackStack() })
        }
    }
}