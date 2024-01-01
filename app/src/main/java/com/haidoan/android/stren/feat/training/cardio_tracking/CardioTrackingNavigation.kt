package com.haidoan.android.stren.feat.training.cardio_tracking

import android.os.Parcelable
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
import kotlinx.parcelize.Parcelize

private const val CARDIO_TRACKING_ROUTE = "cardio_tracking_route"
const val CARDIO_TRACKING_RESULT_SAVED_STATE_KEY = "CARDIO_TRACKING_RESULT_SAVED_STATE_KEY"

@Parcelize
data class CardioTrackingResult(
    val trainedExerciseId: String,
    val trainingSetId: String,
    val durationInSecs: Long,
    val distanceInKm: Float
): Parcelable

internal fun NavController.navigateToCardioTracking(
    trainedExerciseId: String,
    trainingSetId: String,
) {
    this.navigate(
        CARDIO_TRACKING_ROUTE +
                "/$trainedExerciseId" +
                "/$trainingSetId"
    )
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class CardioTrackingArgs(
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
            onNavigateBack = navController::popBackStack,
            onSaveResult = { trainedExerciseId, trainingSetId, durationInSecs, distanceInKm ->
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    CARDIO_TRACKING_RESULT_SAVED_STATE_KEY,
                    CardioTrackingResult(
                        trainedExerciseId,
                        trainingSetId,
                        durationInSecs,
                        distanceInKm
                    )
                )
                navController.popBackStack()
            }
        )
    }
}