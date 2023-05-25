package com.haidoan.android.stren.feat.nutrition

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.TabLayout
import com.haidoan.android.stren.feat.nutrition.diary.NutritionDiaryRoute
import com.haidoan.android.stren.feat.nutrition.diary.navigateToAddFoodToMeal
import com.haidoan.android.stren.feat.nutrition.diary.nutritionDiaryGraph
import com.haidoan.android.stren.feat.nutrition.food.FoodRoute
import com.haidoan.android.stren.feat.nutrition.food.foodGraph
import com.haidoan.android.stren.feat.nutrition.food.navigateToFoodDetail


const val NUTRITION_GRAPH_ROUTE = "nutrition_graph_route"
const val NUTRITION_GRAPH_STARTING_ROUTE = "nutrition_graph_starting_route"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.nutritionGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}
) {
    navigation(startDestination = NUTRITION_GRAPH_STARTING_ROUTE, route = NUTRITION_GRAPH_ROUTE) {
        composable(route = NUTRITION_GRAPH_STARTING_ROUTE) {
            TabLayout(
                tabNamesAndScreenComposables = listOf(
                    Pair("Diary") {
                        NutritionDiaryRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                            },
                            onNavigateToAddWorkoutScreen = { _, _ -> },
                            onNavigateToAddFoodToMeal = { userId, eatingDayId, mealId ->
                                navController.navigateToAddFoodToMeal(userId, eatingDayId, mealId)
                            }
                        )
                    },
                    Pair("Food") {
                        FoodRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                            },
                            onNavigateToFoodDetailScreen = {
                                navController.navigateToFoodDetail(it)
                            })
                    },
                )
            )
        }

        foodGraph(
            navController = navController,
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler
        )

        nutritionDiaryGraph(
            navController = navController,
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler
        )
    }
}