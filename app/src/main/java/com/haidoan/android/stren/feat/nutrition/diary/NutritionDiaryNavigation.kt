package com.haidoan.android.stren.feat.nutrition.diary


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.nutrition.diary.add_food.*
import com.haidoan.android.stren.feat.training.history.log_workout.*
import com.haidoan.android.stren.feat.training.routines.add_edit.*

internal fun NavController.navigateToAddFoodToMeal(
    userId: String,
    eatingDayId: String,
    mealId: String,
    mealName: String,
) {
    this.navigate(
        ADD_FOOD_TO_MEAL_SCREEN_ROUTE +
                "/" + userId +
                "/" + eatingDayId +
                "/" + mealId +
                "/" + mealName
    )
}

internal fun NavController.navigateToEditFoodEntry(
    userId: String,
    eatingDayId: String,
    mealId: String,
    mealName: String,
    foodId: String,
) {
    this.navigate(
        EDIT_FOOD_ENTRY_SCREEN_ROUTE +
                "/" + userId +
                "/" + eatingDayId +
                "/" + mealId +
                "/" + mealName +
                "/" + foodId
    )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class AddFoodToMealArgs(
    val userId: String,
    val eatingDayId: String,
    val mealId: String,
    val mealName: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[USER_ID_ADD_FOOD_TO_MEAL_NAV_ARG]),
                checkNotNull(savedStateHandle[EATING_DAY_ID_ADD_FOOD_TO_MEAL_NAV_ARG]),
                checkNotNull(savedStateHandle[MEAL_ID_ADD_FOOD_TO_MEAL_NAV_ARG]),
                checkNotNull(savedStateHandle[MEAL_NAME_ADD_FOOD_TO_MEAL_NAV_ARG])
            )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class EditFoodEntryArgs(
    val userId: String,
    val eatingDayId: String,
    val mealId: String,
    val mealName: String,
    val foodId: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[USER_ID_EDIT_FOOD_ENTRY_NAV_ARG]),
                checkNotNull(savedStateHandle[EATING_DAY_ID_EDIT_FOOD_ENTRY_NAV_ARG]),
                checkNotNull(savedStateHandle[MEAL_ID_EDIT_FOOD_ENTRY_NAV_ARG]),
                checkNotNull(savedStateHandle[MEAL_NAME_EDIT_FOOD_ENTRY_NAV_ARG]),
                checkNotNull(savedStateHandle[FOOD_ID_EDIT_FOOD_ENTRY_NAV_ARG])
            )
}

/**
 *  The below navGraph doesn't use navigation() to create nested graph since its startDestination requires arguments which will be difficult to pass

Reference: https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
 */
@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.nutritionDiaryGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {
    composable(
        route = ADD_FOOD_TO_MEAL_SCREEN_ROUTE +
                "/" + "{$USER_ID_ADD_FOOD_TO_MEAL_NAV_ARG}" +
                "/" + "{$EATING_DAY_ID_ADD_FOOD_TO_MEAL_NAV_ARG}" +
                "/" + "{$MEAL_ID_ADD_FOOD_TO_MEAL_NAV_ARG}" +
                "/" + "{$MEAL_NAME_ADD_FOOD_TO_MEAL_NAV_ARG}",

        arguments = listOf(
            navArgument(USER_ID_ADD_FOOD_TO_MEAL_NAV_ARG) { type = NavType.StringType },
            navArgument(EATING_DAY_ID_ADD_FOOD_TO_MEAL_NAV_ARG) { type = NavType.StringType },
            navArgument(MEAL_ID_ADD_FOOD_TO_MEAL_NAV_ARG) { type = NavType.StringType },
            navArgument(MEAL_NAME_ADD_FOOD_TO_MEAL_NAV_ARG) { type = NavType.StringType }
        )

    ) {
        AddFoodToMealRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onNavigateToEditFoodEntry = { userId, eatingDayId, mealId, mealName, foodId ->
                navController.navigateToEditFoodEntry(
                    userId,
                    eatingDayId,
                    mealId,
                    mealName,
                    foodId
                )
            },
            onBackToPreviousScreen = { navController.popBackStack() }
        )
    }
    composable(
        route = EDIT_FOOD_ENTRY_SCREEN_ROUTE +
                "/" + "{$USER_ID_EDIT_FOOD_ENTRY_NAV_ARG}" +
                "/" + "{$EATING_DAY_ID_EDIT_FOOD_ENTRY_NAV_ARG}" +
                "/" + "{$MEAL_ID_EDIT_FOOD_ENTRY_NAV_ARG}" +
                "/" + "{$MEAL_NAME_EDIT_FOOD_ENTRY_NAV_ARG}" +
                "/" + "{$FOOD_ID_EDIT_FOOD_ENTRY_NAV_ARG}",

        arguments = listOf(
            navArgument(USER_ID_EDIT_FOOD_ENTRY_NAV_ARG) { type = NavType.StringType },
            navArgument(EATING_DAY_ID_EDIT_FOOD_ENTRY_NAV_ARG) { type = NavType.StringType },
            navArgument(MEAL_ID_EDIT_FOOD_ENTRY_NAV_ARG) { type = NavType.StringType },
            navArgument(MEAL_NAME_EDIT_FOOD_ENTRY_NAV_ARG) { type = NavType.StringType },
            navArgument(FOOD_ID_EDIT_FOOD_ENTRY_NAV_ARG) { type = NavType.StringType }
        )) {
        EditFoodEntryRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() })
    }
}