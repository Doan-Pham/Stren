package com.haidoan.android.stren.feat.training.programs.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.feat.training.programs.add_edit.AddEditTrainingProgramsRoute
import com.haidoan.android.stren.feat.training.routines.add_edit.*
import com.haidoan.android.stren.feat.training.routines.navigateToAddRoutineToProgram
import com.haidoan.android.stren.feat.training.trainingGraphBackStackEntry

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
        val trainingGraphEntry = remember(it) {
            navController.trainingGraphBackStackEntry
        }

        AddEditTrainingProgramsRoute(
            appBarConfigurationChangeHandler = appBarConfigurationChangeHandler,
            trainingViewModel = hiltViewModel(trainingGraphEntry),
            onNavigateToAddRoutineScreen = {
                navController.navigateToAddRoutineToProgram(it)
            }
        )
    }
}