package com.haidoan.android.stren.feat.training.exercises.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.model.Exercise
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
    if (!isAppBarConfigured && currentExercise.name.isNotEmpty()) {
        appBarConfigurationChangeHandler(exercisesAppBarConfiguration)
        isAppBarConfigured = true
    }

    ExerciseDetailScreen(modifier = modifier, exercise = currentExercise)
}

@Composable
internal fun ExerciseDetailScreen(
    modifier: Modifier = Modifier, exercise: Exercise
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.Start
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Start",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "Finish",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_small)
            )
        ) {
            AsyncImage(
                model = exercise.imageUrls.firstOrNull(),
                modifier = Modifier
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(6.dp)
                    ),
                placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
                error = painterResource(id = R.drawable.ic_app_logo_no_padding),
                contentDescription = "An exercise image",
                contentScale = ContentScale.Fit
            )
            AsyncImage(
                model = exercise.imageUrls.getOrNull(1),
                modifier = Modifier
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(6.dp)
                    ),
                placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
                error = painterResource(id = R.drawable.ic_app_logo_no_padding),
                contentDescription = "An exercise image",
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
        Text(
            text = "Steps",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        exercise.instructions.forEachIndexed { index, instructionStep ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(0.1f),
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    modifier = Modifier.weight(0.9f),
                    text = instructionStep,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Timber.d("isShown")
    }
}