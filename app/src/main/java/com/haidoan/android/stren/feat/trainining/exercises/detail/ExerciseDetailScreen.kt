package com.haidoan.android.stren.feat.trainining.exercises.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText
import timber.log.Timber

internal const val EXERCISE_DETAIL_SCREEN_ROUTE = "exercise_detail_screen_route"
internal const val EXERCISE_ID_ARG = "exerciseId"

@Composable
internal fun ExerciseDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: ExerciseDetailViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit
) {

    val currentExercise by viewModel.currentExercise.collectAsStateWithLifecycle()

    Timber.d("currentExercise: $currentExercise")
    //TODO: MAke app bar title - current exercise
    val exercisesAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = currentExercise.name,
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen)
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(exercisesAppBarConfiguration)
        isAppBarConfigured = true
    }
    Timber.d("isShown")
    ExerciseDetailScreen()
}

@Composable
internal fun ExerciseDetailScreen(
    modifier: Modifier = Modifier
) {
    DummyBoxWithText(modifier = Modifier.fillMaxSize(), text = "ExerciseDetailScreen")
    Timber.d("isShown")
}