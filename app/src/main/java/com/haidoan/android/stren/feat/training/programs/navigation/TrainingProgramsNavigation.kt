package com.haidoan.android.stren.feat.training.programs.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.training.programs.add_edit.AddEditTrainingProgramsRoute
import com.haidoan.android.stren.feat.training.routines.add_edit.*

private const val PROGRAM_ROUTE = "add_edit_training_program_route"

internal fun NavController.navigateToAddTrainingProgram(
    userId: String,
) {
    this.navigate("$PROGRAM_ROUTE/$userId")
}

// This encapsulate the SavedStateHandle access to allow ViewModel
// to easily grabs nav args. Or else, it has to know all the nav args' names
internal class AddEditTrainingProgramArgs(
    val userId: String,
) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle["userId"]) as String,
            )
}


@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.trainingProgramGraph(
    navController: NavController,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {

    composable(
        route = PROGRAM_ROUTE +
                "/{userId}",
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType },
        )
    ) {
        AddEditTrainingProgramsRoute(appBarConfigurationChangeHandler = appBarConfigurationChangeHandler)
    }
}