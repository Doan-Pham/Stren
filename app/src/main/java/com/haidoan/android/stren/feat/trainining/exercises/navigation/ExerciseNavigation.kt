package com.haidoan.android.stren.feat.trainining.exercises.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.trainining.exercises.detail.EXERCISE_DETAIL_SCREEN_ROUTE
import com.haidoan.android.stren.feat.trainining.exercises.detail.EXERCISE_ID_ARG
import com.haidoan.android.stren.feat.trainining.exercises.detail.ExerciseDetailRoute
import timber.log.Timber


internal fun NavController.navigateToExerciseDetail(exerciseId: String) {
    this.navigate("$EXERCISE_DETAIL_SCREEN_ROUTE/$exerciseId")
}

@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.exerciseGraph(
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}
) {
    composable(
        route = "$EXERCISE_DETAIL_SCREEN_ROUTE/{$EXERCISE_ID_ARG}",
        arguments = listOf(
            navArgument(EXERCISE_ID_ARG) { type = NavType.StringType },
        )
    ) { backStackEntry ->
        Timber.d("Moved to detail, id: ${backStackEntry.arguments?.getString(EXERCISE_ID_ARG)}")
        ExerciseDetailRoute(appBarConfigurationChangeHandler = appBarConfigurationChangeHandler)
    }

}