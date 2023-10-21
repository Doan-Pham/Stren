package com.haidoan.android.stren.feat.training.routines

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.training.exercises.navigateToCreateExercise
import com.haidoan.android.stren.feat.training.routines.add_edit.*
import timber.log.Timber

internal enum class NavigationPurpose {
    ADD_ROUTINE, EDIT_ROUTINE
}

internal fun NavController.navigateToRoutineGraph(
    navigationPurpose: NavigationPurpose,
    userId: String,
    routineId: String = "UNDEFINED_ROUTINE_ID",
) {
    this.navigate("$ADD_EDIT_ROUTINE_SCREEN_ROUTE/$userId/$routineId/$navigationPurpose")
}


// This encapsulate the SavedStateHandle access to allow AddEditRoutineViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class AddEditRoutineArgs(
    val userId: String,
    val routineId: String,
    val navigationPurpose: NavigationPurpose,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[USER_ID_ROUTINE_NAV_ARG]),
                checkNotNull(savedStateHandle[ROUTINE_ID_NAV_ARG]),
                checkNotNull(savedStateHandle[NAVIGATION_PURPOSE_NAV_ARG]),
            )
}


/**
 *  routineGraph doesn't use navigation() to create nested graph since its startDestination(AddEditRoute) requires arguments which will be difficult to pass

Reference: https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
 */
@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.routineGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {

    composable(
        route = "$ADD_EDIT_ROUTINE_SCREEN_ROUTE/{$USER_ID_ROUTINE_NAV_ARG}/{$ROUTINE_ID_NAV_ARG}/{$NAVIGATION_PURPOSE_NAV_ARG}",
        arguments = listOf(
            navArgument(USER_ID_ROUTINE_NAV_ARG) { type = NavType.StringType },
            navArgument(ROUTINE_ID_NAV_ARG) { type = NavType.StringType },
            navArgument(NAVIGATION_PURPOSE_NAV_ARG) {
                type = NavType.EnumType(NavigationPurpose::class.java)
            },
        )

    ) { navBackStackEntry ->
        val exercisesIdsToAdd: List<String> by navBackStackEntry
            .savedStateHandle
            .getStateFlow(SELECTED_EXERCISES_IDS_SAVED_STATE_KEY, listOf<String>())
            .collectAsStateWithLifecycle()
        Timber.d("exercisesIdsToAdd: $exercisesIdsToAdd")

        AddEditRoutineRoute(
            exercisesIdsToAdd = exercisesIdsToAdd,
            onAddExercisesCompleted = {
                navBackStackEntry.savedStateHandle[SELECTED_EXERCISES_IDS_SAVED_STATE_KEY] =
                    listOf<String>()
            },
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
            onBackToPreviousScreen = { navController.popBackStack() },
            onAddExercisesToRoutine = { selectedExercisesIds ->
                // For some reason, app crashes if "selectedExercisesIds" is not wrapped
                // in listOf() call
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    SELECTED_EXERCISES_IDS_SAVED_STATE_KEY, listOf(selectedExercisesIds).flatten()
                )
                navController.popBackStack()
            },
            onNavigateToCreateExerciseScreen = {
                navController.navigateToCreateExercise(it)
            })
    }
}