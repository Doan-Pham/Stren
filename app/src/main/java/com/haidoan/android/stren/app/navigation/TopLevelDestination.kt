package com.haidoan.android.stren.app.navigation

import com.haidoan.android.stren.R
import com.haidoan.android.stren.feat.dashboard.DASHBOARD_GRAPH_ROUTE
import com.haidoan.android.stren.feat.dashboard.DASHBOARD_GRAPH_STARTING_ROUTE
import com.haidoan.android.stren.feat.nutrition.NUTRITION_GRAPH_ROUTE
import com.haidoan.android.stren.feat.nutrition.NUTRITION_GRAPH_STARTING_ROUTE
import com.haidoan.android.stren.feat.settings.SETTINGS_GRAPH_ROUTE
import com.haidoan.android.stren.feat.settings.SETTINGS_SCREEN_ROUTE
import com.haidoan.android.stren.feat.training.TRAINING_GRAPH_ROUTE
import com.haidoan.android.stren.feat.training.TRAINING_GRAPH_STARTING_ROUTE
import com.haidoan.android.stren.feat.training.exercises.view_exercises.EXERCISES_SCREEN_ROUTE

/**
 * @param startingChildDestinationRoute is for the case where the top-level destination is
 * a nested graph and need the graph's start destination for some purpose
 */
enum class TopLevelDestination(
    val route: String,
    val iconDrawableId: Int,
    val titleTextId: Int,
    val descriptionTextId: Int,
    val immediateChildDestinationRoutes: List<String> = listOf(),
    val startingChildDestinationRoute: String = ""
) {
    DASHBOARD(
        route = DASHBOARD_GRAPH_ROUTE,
        iconDrawableId = R.drawable.ic_dashboard,
        titleTextId = R.string.bottom_nav_title_dashboard,
        descriptionTextId = R.string.bottom_nav_title_dashboard,
        immediateChildDestinationRoutes = listOf(DASHBOARD_GRAPH_STARTING_ROUTE),
        startingChildDestinationRoute = DASHBOARD_GRAPH_STARTING_ROUTE
    ),
    TRAINING(
        route = TRAINING_GRAPH_ROUTE,
        iconDrawableId = R.drawable.ic_training,
        titleTextId = R.string.bottom_nav_title_training,
        descriptionTextId = R.string.bottom_nav_title_training,
        immediateChildDestinationRoutes = listOf(
            EXERCISES_SCREEN_ROUTE,
            TRAINING_GRAPH_STARTING_ROUTE
        )
    ),

    NUTRITION(
        route = NUTRITION_GRAPH_ROUTE,
        iconDrawableId = R.drawable.ic_nutrition,
        titleTextId = R.string.bottom_nav_title_nutrition,
        descriptionTextId = R.string.bottom_nav_title_nutrition,
        immediateChildDestinationRoutes = listOf(
            NUTRITION_GRAPH_STARTING_ROUTE
        )
    ),

    SETTINGS(
        route = SETTINGS_GRAPH_ROUTE,
        iconDrawableId = R.drawable.ic_settings,
        titleTextId = R.string.bottom_nav_title_settings,
        descriptionTextId = R.string.bottom_nav_title_settings,
        immediateChildDestinationRoutes = listOf(
            SETTINGS_SCREEN_ROUTE
        )
    )
}
