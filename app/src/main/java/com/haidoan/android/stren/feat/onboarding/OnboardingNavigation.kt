package com.haidoan.android.stren.feat.onboarding

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable

private const val ONBOARDING_GRAPH_ROUTE = "onboarding_graph_route"

fun NavController.navigateToOnboarding(
    userId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    this.navigate(
        ONBOARDING_GRAPH_ROUTE +
                "/" + userId,
        navOptions
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.onboardingGraph() {
    composable(
        route = ONBOARDING_GRAPH_ROUTE +
                "/" + "{$USER_ID_ONBOARDING_NAV_ARG}",
        arguments = listOf(
            navArgument(USER_ID_ONBOARDING_NAV_ARG) { type = NavType.StringType })
    ) {
        OnboardingRoute()
    }
}