package com.haidoan.android.stren.feat.training.exercises

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.nutrition.diary.add_food.*
import com.haidoan.android.stren.feat.training.exercises.create_cutom.CREATE_EXERCISE_SCREEN_ROUTE
import com.haidoan.android.stren.feat.training.exercises.create_cutom.CreateCustomExerciseRoute
import com.haidoan.android.stren.feat.training.exercises.create_cutom.EXERCISE_NAME_NAV_ARG
import com.haidoan.android.stren.feat.training.exercises.create_cutom.UNDEFINED_EXERCISE_NAME_NAV_ARG
import com.haidoan.android.stren.feat.training.exercises.detail.EXERCISE_DETAIL_SCREEN_ROUTE
import com.haidoan.android.stren.feat.training.exercises.detail.EXERCISE_ID_ARG
import com.haidoan.android.stren.feat.training.exercises.detail.ExerciseDetailRoute
import timber.log.Timber


internal fun NavController.navigateToExerciseDetail(exerciseId: String) {
    this.navigate("$EXERCISE_DETAIL_SCREEN_ROUTE/$exerciseId")
}

internal fun NavController.navigateToCreateExercise(
    exerciseName: String = UNDEFINED_EXERCISE_NAME_NAV_ARG,
) {
    this.navigate(
        CREATE_EXERCISE_SCREEN_ROUTE +
                "?" + "$EXERCISE_NAME_NAV_ARG=${exerciseName}"
    )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class CreateExerciseArgs(
    val exerciseName: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[EXERCISE_NAME_NAV_ARG]) as String
            )
}

@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.exerciseGraph(
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit
) {
    composable(
        route = "$EXERCISE_DETAIL_SCREEN_ROUTE/{$EXERCISE_ID_ARG}",
        arguments = listOf(
            navArgument(EXERCISE_ID_ARG) { type = NavType.StringType },
        )
    ) { backStackEntry ->
        Timber.d("Moved to detail, id: ${backStackEntry.arguments?.getString(EXERCISE_ID_ARG)}")
        ExerciseDetailRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = onBackToPreviousScreen
        )
    }
    composable(
        route = CREATE_EXERCISE_SCREEN_ROUTE +
                "?" + "$EXERCISE_NAME_NAV_ARG={$EXERCISE_NAME_NAV_ARG}",
        arguments = listOf(
            navArgument(EXERCISE_NAME_NAV_ARG) {
                type = NavType.StringType
                defaultValue = UNDEFINED_EXERCISE_NAME_NAV_ARG
            },
        )
    ) {
        CreateCustomExerciseRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onBackToPreviousScreen = onBackToPreviousScreen
        )
    }
}