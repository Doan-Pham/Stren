package com.haidoan.android.stren.feat.trainining.exercises

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.model.Exercise

internal const val EXERCISES_SCREEN_ROUTE = "exercises_screen_route"
const val EXERCISES_LOADING_ANIMATION_TEST_TAG = "Loading-Exercises"
const val EXERCISES_EXERCISE_LIST_TEST_TAG = "List-Exercises"

@Composable
internal fun ExercisesRoute(
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val exercisesAppBarConfiguration = AppBarConfiguration(
        actionIcons =
        listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_nutrition,
                description = "MenuItem-Search",
                clickHandler = {}),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_dashboard,
                description = "MenuItem-Filter",
                clickHandler = {})
        )
    )
    appBarConfigurationChangeHandler(exercisesAppBarConfiguration)
    ExercisesScreen(
        modifier = modifier, uiState = uiState
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
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .testTag(EXERCISES_EXERCISE_LIST_TEST_TAG)
            ) {
                items(uiState.exercises) { exercise ->
                    ExerciseItem(exercise = exercise)
                }
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
        AsyncImage(
            model = exercise.imageUrl,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_extra_large))
                .clip(
                    RoundedCornerShape(6.dp)
                ),
            placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
            error = painterResource(id = R.drawable.ic_app_logo_no_padding),
            contentDescription = "An exercise image",
            contentScale = ContentScale.Crop
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