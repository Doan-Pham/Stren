package com.haidoan.android.stren.feat.nutrition.food

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.nutrition.food.detail.FOOD_DETAIL_SCREEN_ROUTE
import com.haidoan.android.stren.feat.nutrition.food.detail.FOOD_ID_FOOD_DETAIL_NAV_ARG
import com.haidoan.android.stren.feat.nutrition.food.detail.FoodDetailRoute

internal fun NavController.navigateToFoodDetail(
    foodId: String
) {
    this.navigate("$FOOD_DETAIL_SCREEN_ROUTE/$foodId")
}

/**
 *  This graph doesn't use navigation() to create nested graph since its startDestination requires arguments which will be difficult to pass

Reference: https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
 */
@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.foodGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {

    composable(
        route = FOOD_DETAIL_SCREEN_ROUTE +
                "/" + "{$FOOD_ID_FOOD_DETAIL_NAV_ARG}",
        arguments = listOf(
            navArgument(FOOD_ID_FOOD_DETAIL_NAV_ARG) { type = NavType.StringType },
        )

    ) {
        FoodDetailRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() })
    }
}