package com.haidoan.android.stren.feat.training.cardio_tracking

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.training.history.*
import com.haidoan.android.stren.feat.training.history.log_workout.*

private const val CARDIO_TRACKING_ROUTE = "cardio_tracking_route"

internal fun NavController.navigateToCardioTracking(
    trainingSetId: String,
    trainedExerciseId: String,
) {
    this.navigate(
        CARDIO_TRACKING_ROUTE +
                "/$trainedExerciseId" +
                "/$trainingSetId"
    )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class AddEditCardioTrackingArgs(
    val trainedExerciseId: String,
    val trainingSetId: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle["trainedExerciseId"]) as String,
                checkNotNull(savedStateHandle["trainingSetId"]) as String,
            )
}


@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.cardioTrackingScreen(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    composable(
        route = CARDIO_TRACKING_ROUTE +
                "/" + "{trainedExerciseId}" +
                "/" + "{trainingSetId}",
        arguments = listOf(
            navArgument("trainedExerciseId") { type = NavType.StringType },
            navArgument("trainingSetId") { type = NavType.StringType }
        )
    ) {
        CardioTrackingRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            onNavigateBack = navController::navigateUp
        )
    }
}