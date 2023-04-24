package com.haidoan.android.stren.feat.trainining.exercises

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation

internal const val EXERCISES_SCREEN_ROUTE = "exercises_screen_route"
const val EXERCISES_LOADING_ANIMATION_TEST_TAG = "Loading-Exercises"

@Composable
internal fun ExercisesRoute() {
    ExercisesScreen(uiState = ExercisesUiState.Loading)
}

@Composable
internal fun ExercisesScreen(uiState: ExercisesUiState) {
    when (uiState) {
        is ExercisesUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation(modifier = Modifier.testTag(EXERCISES_LOADING_ANIMATION_TEST_TAG))
            }

        }
        is ExercisesUiState.LoadComplete -> {
        }
    }
}