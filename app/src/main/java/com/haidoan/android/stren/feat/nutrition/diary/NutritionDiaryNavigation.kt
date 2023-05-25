package com.haidoan.android.stren.feat.nutrition.diary


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.training.history.log_workout.*
import com.haidoan.android.stren.feat.training.routines.add_edit.*
import timber.log.Timber

internal fun NavController.navigateToAddFoodToMeal(
    userId: String,
    eatingDayId: String,
    mealId: String,
) {
    Timber.d(
        ADD_FOOD_TO_MEAL_SCREEN_ROUTE +
                "/" + userId +
                "/" + eatingDayId +
                "/" + mealId

    )
    this.navigate(
        ADD_FOOD_TO_MEAL_SCREEN_ROUTE +
                "/" + userId +
                "/" + eatingDayId +
                "/" + mealId
    )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class AddFoodToMealArgs(
    val userId: String,
    val eatingDayId: String,
    val mealId: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[USER_ID_ADD_FOOD_TO_MEAL_NAV_ARG]),
                checkNotNull(savedStateHandle[EATING_DAY_ID_ADD_FOOD_TO_MEAL_NAV_ARG]),
                checkNotNull(savedStateHandle[MEAL_ID_ADD_FOOD_TO_MEAL_NAV_ARG])
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
                "/" + "{$MEAL_ID_ADD_FOOD_TO_MEAL_NAV_ARG}",

        arguments = listOf(
            navArgument(USER_ID_ADD_FOOD_TO_MEAL_NAV_ARG) { type = NavType.StringType },
            navArgument(EATING_DAY_ID_ADD_FOOD_TO_MEAL_NAV_ARG) { type = NavType.StringType },
            navArgument(MEAL_ID_ADD_FOOD_TO_MEAL_NAV_ARG) { type = NavType.StringType }
        )

    ) {
        AddFoodToMealRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onNavigateToFoodDetailScreen = { },
            onBackToPreviousScreen = { navController.popBackStack() }
        )
//        AddFoodToMealRoute(
//            exercisesIdsToAdd = list,
//            onAddExercisesCompleted = {
//            },
//            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
//            onBackToPreviousScreen = { navController.popBackStack() },
//            onNavigateToAddExercise = {
//                navController.navigate(ADD_EXERCISE_TO_WORKOUT_SCREEN_ROUTE)
//            }
//        )
    }
//    composable(
//        route = ADD_EXERCISE_TO_WORKOUT_SCREEN_ROUTE
//    ) {
////        AddExerciseToRoutineRoute(appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
////            onBackToPreviousScreen = { navController.popBackStack() },
////            onAddExercisesToRoutine = { selectedExercisesIds ->
////                // For some reason, app crashes if "selectedExercisesIds" is not wrapped
////                // in listOf() call
////                navController.previousBackStackEntry?.savedStateHandle?.set(
////                    SELECTED_EXERCISES_IDS_SAVED_STATE_KEY, listOf(selectedExercisesIds).flatten()
////                )
////                navController.popBackStack()
////            })
//    }
}