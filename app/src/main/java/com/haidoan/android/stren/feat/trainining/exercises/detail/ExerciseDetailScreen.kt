package com.haidoan.android.stren.feat.trainining.exercises.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesViewModel
import timber.log.Timber

internal const val EXERCISE_DETAIL_SCREEN_ROUTE = "exercise_detail_screen_route"
internal const val EXERCISE_ID_ARG = "exerciseId"

@Composable
internal fun ExerciseDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}
) {

    val exercisesAppBarConfiguration = AppBarConfiguration.NavigationAppBar()
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