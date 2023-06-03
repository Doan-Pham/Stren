package com.haidoan.android.stren.feat.profile

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.profile.edit.EDIT_PROFILE_SCREEN_ROUTE
import com.haidoan.android.stren.feat.profile.edit.EditProfileRoute
import com.haidoan.android.stren.feat.profile.edit.USER_ID_EDIT_PROFILE_NAV_ARG


const val PROFILE_GRAPH_ROUTE = "profile_graph_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {
    navigation(startDestination = PROFILE_SCREEN_ROUTE, route = PROFILE_GRAPH_ROUTE) {
        composable(route = PROFILE_SCREEN_ROUTE) {
            ProfileRoute(
                appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
                onNavigateToEditProfile = { userId ->
                    navController.navigate(
                        "$EDIT_PROFILE_SCREEN_ROUTE/$userId"
                    )
                })
        }
        composable(
            route = EDIT_PROFILE_SCREEN_ROUTE +
                    "/" + "{$USER_ID_EDIT_PROFILE_NAV_ARG}",
            arguments = listOf(
                navArgument(USER_ID_EDIT_PROFILE_NAV_ARG) { type = NavType.StringType },
            )
        ) {
            EditProfileRoute(
                appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
                onBackToPreviousScreen = { navController.popBackStack() })
        }
    }
}