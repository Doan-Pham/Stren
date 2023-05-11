package com.haidoan.android.stren.feat.trainining.routines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.trainining.routines.add_edit.ADD_EXERCISE_TO_ROUTINE_SCREEN_ROUTE
import com.haidoan.android.stren.feat.trainining.routines.add_edit.AddExerciseToRoutineRoute

internal fun NavController.navigateToRoutineGraph(
    isAddingRoutine: Boolean,
    routineId: String = "UNDEFINED_ROUTINE_ID"
) {
    this.navigate("$ADD_EDIT_ROUTINE_SCREEN_ROUTE/$routineId/$isAddingRoutine")
}

// This encapsulate the SavedStateHandle access to allow AddEditRoutineViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class AddEditRoutineArgs(val routineId: String, val isAddingRoutine: Boolean) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[ROUTINE_ID_NAV_ARG]),
                checkNotNull(savedStateHandle[IS_ADDING_ROUTINE_NAV_ARG])
            )
}


/**
 *  routineGraph doesn't use navigation() to create nested graph since its startDestination(AddEditRoute) requires arguments which will be difficult to pass

Reference: https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
 */
@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.routineGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {

    composable(
        route = "$ADD_EDIT_ROUTINE_SCREEN_ROUTE/{$ROUTINE_ID_NAV_ARG}/{$IS_ADDING_ROUTINE_NAV_ARG}",
        arguments = listOf(
            navArgument(ROUTINE_ID_NAV_ARG) { type = NavType.StringType },
            navArgument(IS_ADDING_ROUTINE_NAV_ARG) { type = NavType.BoolType }
        )
    ) {
        AddEditRoutineRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() },
            onNavigateToAddExercise = {
                navController.navigate(ADD_EXERCISE_TO_ROUTINE_SCREEN_ROUTE)
            }
        )
    }
    composable(
        route = ADD_EXERCISE_TO_ROUTINE_SCREEN_ROUTE
    ) {
        AddExerciseToRoutineRoute(appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() })
    }
}