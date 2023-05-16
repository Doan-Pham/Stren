package com.haidoan.android.stren.feat.trainining.history.log_workout


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.trainining.routines.add_edit.*
import timber.log.Timber
import java.time.LocalDate

private const val UNDEFINED_WORKOUT_ID_NAV_ARG = "UNDEFINED_WORKOUT_ID_NAV_ARG"
private val UNDEFINED_SELECTED_DATE_NAV_ARG = LocalDate.of(1900, 12, 12).toEpochDay()

internal fun NavController.navigateToAddWorkoutScreen(
    userId: String,
    selectedDate: LocalDate
) {
    Timber.d(
        "$LOG_WORKOUT_SCREEN_ROUTE/$userId/true" +
                "?" + "$SELECTED_DATE_WORKOUT_NAV_ARG=${selectedDate.toEpochDay()}"
    )
    this.navigate(
        "$LOG_WORKOUT_SCREEN_ROUTE/$userId/true" +
                "?" + "$SELECTED_DATE_WORKOUT_NAV_ARG=${selectedDate.toEpochDay()}"
    )
}

internal fun NavController.navigateToEditWorkoutScreen(
    userId: String,
    workoutId: String,
) {
    Timber.d(
        "$LOG_WORKOUT_SCREEN_ROUTE/$userId/false" +
                "?" + "$WORKOUT_ID_NAV_ARG=$workoutId"
    )
    this.navigate(
        "$LOG_WORKOUT_SCREEN_ROUTE/$userId/false" +
                "?" + "$WORKOUT_ID_NAV_ARG=$workoutId"
    )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class LogWorkoutArgs(
    val userId: String,
    val isAddingWorkout: Boolean,
    val workoutId: String,
    val selectedDate: LocalDate,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[USER_ID_WORKOUT_NAV_ARG]),
                checkNotNull(savedStateHandle[IS_ADDING_WORKOUT_NAV_ARG]),
                checkNotNull(savedStateHandle[WORKOUT_ID_NAV_ARG]),
                checkNotNull(
                    LocalDate.ofEpochDay(
                        savedStateHandle[SELECTED_DATE_WORKOUT_NAV_ARG] ?: 0L
                    )
                ),
            )
}


/**
 *  The below navGraph doesn't use navigation() to create nested graph since its startDestination requires arguments which will be difficult to pass

Reference: https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
 */
@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.workoutGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {
    composable(
        route = LOG_WORKOUT_SCREEN_ROUTE +
                "/" + "{$USER_ID_WORKOUT_NAV_ARG}" +
                "/" + "{$IS_ADDING_WORKOUT_NAV_ARG}" +
                "?" + "$WORKOUT_ID_NAV_ARG={$WORKOUT_ID_NAV_ARG}" +
                "&" + "$SELECTED_DATE_WORKOUT_NAV_ARG={$SELECTED_DATE_WORKOUT_NAV_ARG}",
        arguments = listOf(
            navArgument(USER_ID_WORKOUT_NAV_ARG) { type = NavType.StringType },
            navArgument(IS_ADDING_WORKOUT_NAV_ARG) { type = NavType.BoolType },
            navArgument(WORKOUT_ID_NAV_ARG) {
                type = NavType.StringType
                defaultValue = UNDEFINED_WORKOUT_ID_NAV_ARG
            },
            navArgument(SELECTED_DATE_WORKOUT_NAV_ARG) {
                type = NavType.LongType
                defaultValue = UNDEFINED_SELECTED_DATE_NAV_ARG
            },
        )

    ) { navBackStackEntry ->
        val exercisesIdsToAdd: List<String> by navBackStackEntry
            .savedStateHandle
            .getStateFlow(SELECTED_EXERCISES_IDS_SAVED_STATE_KEY, listOf<String>())
            .collectAsStateWithLifecycle()
        Timber.d("exercisesIdsToAdd: $exercisesIdsToAdd")

        LogWorkoutRoute(
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
            })
    }
}