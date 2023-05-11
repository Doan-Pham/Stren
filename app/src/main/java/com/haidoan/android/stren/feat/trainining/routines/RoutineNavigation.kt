package com.haidoan.android.stren.feat.trainining.routines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration

internal fun NavController.navigateToRoutineGraph(
    isAddingRoutine: Boolean,
    routineId: String = "UNDEFINED_ROUTINE_ID"
) {
    this.navigate("$ADD_EDIT_ROUTINE_SCREEN_ROUTE/$routineId/$isAddingRoutine")
}


/**
 *  routineGraph doesn't use navigation() to create nested graph since its startDestination(AddEditRoute) requires arguments which will be difficult to pass

Reference: https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
 */
@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.routineGraph(
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit
) {

    composable(
        route = "$ADD_EDIT_ROUTINE_SCREEN_ROUTE/{$ROUTINE_ID_ARG}/{$IS_ADDING_ARG}",
        arguments = listOf(
            navArgument(ROUTINE_ID_ARG) { type = NavType.StringType },
            navArgument(IS_ADDING_ARG) { type = NavType.BoolType }
        )
    ) {
        AddEditRoutineRoute(appBarConfigurationChangeHandler = appBarConfigurationChangeHandler)
    }
}