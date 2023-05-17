package com.haidoan.android.stren.feat.nutrition

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText
import com.haidoan.android.stren.core.designsystem.component.TabLayout
import com.haidoan.android.stren.feat.nutrition.food.FoodRoute
import com.haidoan.android.stren.feat.nutrition.food.foodGraph
import com.haidoan.android.stren.feat.nutrition.food.navigateToFoodDetail
import com.haidoan.android.stren.feat.training.history.log_workout.navigateToAddWorkoutWithRoutine
import com.haidoan.android.stren.feat.training.routines.RoutinesRoute
import com.haidoan.android.stren.feat.training.routines.navigateToRoutineGraph
import com.haidoan.android.stren.feat.training.routines.routineGraph


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
                        DummyBoxWithText(text = "Food Diary")
                        var isAppBarConfigured by remember { mutableStateOf(false) }
                        if (!isAppBarConfigured) {
                            appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
                            isAppBarConfigured = true
                        }
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
                    Pair("Routines") {
                        RoutinesRoute(
                            appBarConfigurationChangeHandler = {
                                appBarConfigurationChangeHandler(it)
                            },
                            onNavigateToAddRoutineScreen = { userId ->
                                navController.navigateToRoutineGraph(
                                    userId = userId,
                                    isAddingRoutine = true
                                )
                            },
                            onNavigateToEditRoutineScreen = { userId, routineId ->
                                navController.navigateToRoutineGraph(
                                    isAddingRoutine = false,
                                    userId = userId,
                                    routineId = routineId
                                )
                            },
                            onNavigateToAddWorkoutScreen = { userId, routineId ->
                                navController.navigateToAddWorkoutWithRoutine(
                                    userId = userId,
                                    routineId = routineId
                                )
                            }
                        )
                    },
                )
            )
        }

        foodGraph(
            navController = navController,
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler
        )

        routineGraph(
            navController,
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler
        )
    }
}