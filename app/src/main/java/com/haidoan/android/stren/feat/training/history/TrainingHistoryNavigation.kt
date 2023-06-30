package com.haidoan.android.stren.feat.training.history


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.feat.training.exercises.navigateToCreateExercise
import com.haidoan.android.stren.feat.training.history.log_workout.LOG_WORKOUT_SCREEN_ROUTE
import com.haidoan.android.stren.feat.training.history.log_workout.LogWorkoutRoute
import com.haidoan.android.stren.feat.training.history.start_workout.*
import com.haidoan.android.stren.feat.training.routines.add_edit.*
import timber.log.Timber
import java.time.LocalDate

private const val UNDEFINED_WORKOUT_ID_NAV_ARG = "UNDEFINED_WORKOUT_ID_NAV_ARG"
private val UNDEFINED_SELECTED_DATE_NAV_ARG = LocalDate.of(1900, 12, 12).toEpochDay()
private const val ADD_EXERCISE_TO_WORKOUT_SCREEN_ROUTE = "ADD_EXERCISE_TO_WORKOUT_SCREEN_ROUTE"
private const val SELECTED_EXERCISES_IDS_SAVED_STATE_KEY = "SELECTED_EXERCISES_IDS_SAVED_STATE_KEY"

internal fun NavController.navigateToStartWorkoutScreen(
    userId: String,
    selectedDate: LocalDate
) {
    Timber.d(
        START_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "?" + "selectedDate=${selectedDate.toEpochDay()}"
    )
    this.navigate(
        START_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "?" + "selectedDate=${selectedDate.toEpochDay()}"
    )
}

internal fun NavController.navigateToAddWorkoutScreen(
    userId: String,
    selectedDate: LocalDate
) {
    Timber.d(
        LOG_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "/" + true +
                "?" + "selectedDate=${selectedDate.toEpochDay()}"
    )
    this.navigate(
        LOG_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "/" + true +
                "?" + "selectedDate=${selectedDate.toEpochDay()}"
    )
}

internal fun NavController.navigateToAddWorkoutWithRoutine(
    userId: String,
    routineId: String
) {
    Timber.d(
        LOG_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "/" + true +
                "?" + "selectedDate=${DateUtils.getCurrentDate().toEpochDay()}" +
                "&" + "selectedRoutineId=$routineId"
    )
    this.navigate(
        LOG_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "/" + true +
                "?" + "selectedDate=${DateUtils.getCurrentDate().toEpochDay()}" +
                "&" + "selectedRoutineId=$routineId"
    )
}


internal fun NavController.navigateToEditWorkoutScreen(
    userId: String,
    workoutId: String,
) {
    Timber.d(
        LOG_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "/" + false +
                "?" + "workoutId=$workoutId"
    )
    this.navigate(
        LOG_WORKOUT_SCREEN_ROUTE +
                "/" + userId +
                "/" + false +
                "?" + "workoutId=$workoutId"
    )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class LogWorkoutArgs(
    val userId: String,
    val isAddingWorkout: Boolean,
    val workoutId: String,
    val selectedDate: LocalDate,
    val selectedRoutineId: String
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle["userId"]),
                checkNotNull(savedStateHandle["isAddingWorkout"]),
                checkNotNull(savedStateHandle["workoutId"]),
                checkNotNull(
                    LocalDate.ofEpochDay(
                        savedStateHandle["selectedDate"]
                            ?: 0L
                    )
                ),
                checkNotNull(savedStateHandle["selectedRoutineId"]),
            )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class StartWorkoutArgs(
    val userId: String,
    val workoutId: String,
    val selectedDate: LocalDate,
    val selectedRoutineId: String
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle["userId"]),
                checkNotNull(savedStateHandle["workoutId"]),
                checkNotNull(
                    LocalDate.ofEpochDay(
                        savedStateHandle["selectedDate"]
                            ?: 0L
                    )
                ),
                checkNotNull(savedStateHandle["selectedRoutineId"]),
            )
}

/**
 *  The below navGraph doesn't use navigation() to create nested graph since its startDestination requires arguments which will be difficult to pass

Reference: https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
 */
@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.trainingHistoryGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit
) {
    composable(
        route = LOG_WORKOUT_SCREEN_ROUTE +
                "/" + "{userId}" +
                "/" + "{isAddingWorkout}" +
                "?" + "workoutId={workoutId}" +
                "&" + "selectedDate={selectedDate}" +
                "&" + "selectedRoutineId={selectedRoutineId}",

        arguments = listOf(
            navArgument("userId") {
                type = NavType.StringType
            },
            navArgument("isAddingWorkout") {
                type = NavType.BoolType
            },
            navArgument("workoutId") {
                type = NavType.StringType
                defaultValue = UNDEFINED_WORKOUT_ID_NAV_ARG
            },
            navArgument("selectedDate") {
                type = NavType.LongType
                defaultValue = UNDEFINED_SELECTED_DATE_NAV_ARG
            },
            navArgument("selectedRoutineId") {
                type = NavType.StringType
                defaultValue = NO_SELECTION_ROUTINE_ID
            },
        )

    ) { navBackStackEntry ->
        val exercisesIdsToAdd: List<String> by navBackStackEntry
            .savedStateHandle
            .getStateFlow(
                SELECTED_EXERCISES_IDS_SAVED_STATE_KEY,
                listOf<String>()
            )
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
                navController.navigate(ADD_EXERCISE_TO_WORKOUT_SCREEN_ROUTE)
            }
        )
    }
    composable(
        route = START_WORKOUT_SCREEN_ROUTE +
                "/" + "{userId}" +
                "?" + "workoutId={workoutId}" +
                "&" + "selectedDate={selectedDate}" +
                "&" + "selectedRoutineId={selectedRoutineId}",
        arguments = listOf(
            navArgument("userId") {
                type = NavType.StringType
            },
            navArgument("workoutId") {
                type = NavType.StringType
                defaultValue = UNDEFINED_WORKOUT_ID_NAV_ARG
            },
            navArgument("selectedDate") {
                type = NavType.LongType
                defaultValue = UNDEFINED_SELECTED_DATE_NAV_ARG
            },
            navArgument("selectedRoutineId") {
                type = NavType.StringType
                defaultValue = NO_SELECTION_ROUTINE_ID
            },
        )
    ) { navBackStackEntry ->
        val exercisesIdsToAdd: List<String> by navBackStackEntry
            .savedStateHandle
            .getStateFlow(
                SELECTED_EXERCISES_IDS_SAVED_STATE_KEY,
                listOf<String>()
            )
            .collectAsStateWithLifecycle()
        Timber.d("exercisesIdsToAdd: $exercisesIdsToAdd")

        StartWorkoutRoute(
            exercisesIdsToAdd = exercisesIdsToAdd,
            onAddExercisesCompleted = {
                navBackStackEntry.savedStateHandle[SELECTED_EXERCISES_IDS_SAVED_STATE_KEY] =
                    listOf<String>()
            },
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() },
            onNavigateToAddExercise = {
                navController.navigate(ADD_EXERCISE_TO_WORKOUT_SCREEN_ROUTE)
            }
        )
    }

    composable(
        route = ADD_EXERCISE_TO_WORKOUT_SCREEN_ROUTE
    ) {
        AddExerciseToRoutineRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = { navController.popBackStack() },
            onAddExercisesToRoutine = { selectedExercisesIds ->
                // For some reason, app crashes if "selectedExercisesIds" is not wrapped
                // in listOf() call
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    SELECTED_EXERCISES_IDS_SAVED_STATE_KEY,
                    listOf(selectedExercisesIds).flatten()
                )
                navController.popBackStack()
            },
            onNavigateToCreateExerciseScreen = {
                navController.navigateToCreateExercise(it)
            })
    }
}