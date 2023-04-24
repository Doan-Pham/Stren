package com.haidoan.android.stren.feat.trainining.exercises

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.model.Exercise

internal const val EXERCISES_SCREEN_ROUTE = "exercises_screen_route"
const val EXERCISES_LOADING_ANIMATION_TEST_TAG = "Loading-Exercises"

@Composable
internal fun ExercisesRoute(modifier: Modifier = Modifier) {
    ExercisesScreen(
        modifier = modifier, uiState = ExercisesUiState.LoadComplete(
            listOf(
                Exercise(
                    "Bench Press",
                    trainedMuscleGroups = listOf("Chest")
                ), Exercise(
                    "Bench Press",
                    trainedMuscleGroups = listOf("Chest")
                ), Exercise(
                    "Bench Press",
                    trainedMuscleGroups = listOf("Chest")
                )
            )
        )
    )
}

@Composable
internal fun ExercisesScreen(modifier: Modifier = Modifier, uiState: ExercisesUiState) {
    when (uiState) {
        is ExercisesUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation(modifier = Modifier.testTag(EXERCISES_LOADING_ANIMATION_TEST_TAG))
            }

        }
        is ExercisesUiState.LoadComplete -> {
            Column(modifier = modifier.fillMaxSize()) {
                ExerciseItem(exercise = uiState.exercises.first())
                ExerciseItem(exercise = uiState.exercises.first())
                ExerciseItem(exercise = uiState.exercises.first())
            }
        }
    }
}

@Composable
private fun ExerciseItem(modifier: Modifier = Modifier, exercise: Exercise) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
            painter = painterResource(id = R.drawable.ic_training),
            contentDescription = "An exercise image"
        )
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        Column {
            Text(text = exercise.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = exercise.trainedMuscleGroups.first(),
                style = MaterialTheme.typography.labelLarge,
                color = Gray60
            )
        }
    }
}