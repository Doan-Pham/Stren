package com.haidoan.android.stren.app.navigation

import com.haidoan.android.stren.R

enum class TopLevelDestination(
    val route: String,
    val iconDrawableId: Int,
    val titleTextId: Int,
    val descriptionTextId: Int
) {
    DASHBOARD(
        route = "dashboard_screen",
        iconDrawableId = R.drawable.ic_dashboard,
        titleTextId = R.string.bottom_nav_title_dashboard,
        descriptionTextId = R.string.bottom_nav_title_dashboard
    ),
    TRAINING(
        route = "training_screen",
        iconDrawableId = R.drawable.ic_training,
        titleTextId = R.string.bottom_nav_title_training,
        descriptionTextId = R.string.bottom_nav_title_training
    ),

    NUTRITION(
        route = "nutrition_screen",
        iconDrawableId = R.drawable.ic_nutrition,
        titleTextId = R.string.bottom_nav_title_nutrition,
        descriptionTextId = R.string.bottom_nav_title_nutrition
    ),

    PROFILE(
        route = "profile_screen",
        iconDrawableId = R.drawable.ic_user,
        titleTextId = R.string.bottom_nav_title_profile,
        descriptionTextId = R.string.bottom_nav_title_profile
    )
}
