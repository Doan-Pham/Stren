package com.haidoan.android.stren.feat.onboarding

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import com.google.accompanist.navigation.animation.composable

private const val ONBOARDING_GRAPH_ROUTE = "onboarding_graph_route"

fun NavController.navigateToOnboarding(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    this.navigate(ONBOARDING_GRAPH_ROUTE, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.onboardingGraph() {
    composable(route = ONBOARDING_GRAPH_ROUTE) {
        OnboardingRoute()
    }
}