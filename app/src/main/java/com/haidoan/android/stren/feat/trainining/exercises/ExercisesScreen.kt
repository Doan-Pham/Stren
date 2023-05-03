package com.haidoan.android.stren.feat.trainining.exercises

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.FilterLabel
import com.haidoan.android.stren.core.designsystem.component.FilterModalBottomSheet
import com.haidoan.android.stren.core.designsystem.component.FilterStandard
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.model.Exercise

internal const val EXERCISES_SCREEN_ROUTE = "exercises_screen_route"
const val EXERCISES_LOADING_ANIMATION_TEST_TAG = "Loading-Exercises"
const val EXERCISES_EXERCISE_LIST_TEST_TAG = "List-Exercises"
private const val TAG = "ExercisesScreen"

@Composable
internal fun ExercisesRoute(
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {}
) {
    var shouldShowFilterSheet by rememberSaveable { mutableStateOf(false) }
    val pagedExercises = viewModel.exercises.collectAsLazyPagingItems()

    val exerciseCategories by viewModel.exerciseCategories.collectAsStateWithLifecycle()
    val exerciseCategoryFilter = FilterStandard(
        standardName = "Category",
        filterLabels = exerciseCategories.map {
            FilterLabel(
                it.category.id,
                it.category.name,
                it.isChosen
            )
        },
        onLabelSelected = { chosenLabel -> viewModel.toggleCategorySelection(chosenLabel.id) },
    )
    val muscleGroups by viewModel.muscleGroups.collectAsStateWithLifecycle()
    val muscleGroupFilter = FilterStandard(
        standardName = "Muscle group",
        filterLabels = muscleGroups.map {
            FilterLabel(
                it.muscleGroup.id,
                it.muscleGroup.name,
                it.isChosen
            )
        },
        onLabelSelected = { chosenLabel -> viewModel.toggleMuscleGroupSelection(chosenLabel.id) },
    )

    val exercisesAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons =
        listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "MenuItem-Search",
                clickHandler = {
                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
                        text = viewModel.searchBarText,
                        placeholder = "Search exercise",
                        onTextChange = { viewModel.searchBarText.value = it },
                        onSearchClicked = { viewModel.searchExerciseByName(it) })
                    appBarConfigurationChangeHandler(searchBarConfiguration)
                }),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_filter,
                description = "MenuItem-Filter",
                clickHandler = { shouldShowFilterSheet = true })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(exercisesAppBarConfiguration)
        isAppBarConfigured = true
    }

    ExercisesScreen(
        modifier = modifier,
        pagedExercises = pagedExercises,
        shouldShowFilterSheet = shouldShowFilterSheet,
        onHideFilterSheet = { shouldShowFilterSheet = false },
        filterStandards = listOf(exerciseCategoryFilter, muscleGroupFilter)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExercisesScreen(
    modifier: Modifier = Modifier,
    pagedExercises: LazyPagingItems<Exercise>,
    filterStandards: List<FilterStandard> = listOf(),
    shouldShowFilterSheet: Boolean = false,
    onHideFilterSheet: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag(EXERCISES_EXERCISE_LIST_TEST_TAG)
    )
    {
        items(items = pagedExercises) { exercise ->
            exercise?.let {
                ExerciseItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                    exercise = exercise
                )
            }
        }
        Log.d(TAG, "itemCount : ${pagedExercises.itemCount}")

        when (pagedExercises.loadState.append) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                Log.d(TAG, "loadState - append: ${pagedExercises.loadState.append}")
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation(
                            modifier = Modifier.testTag(
                                EXERCISES_LOADING_ANIMATION_TEST_TAG
                            )
                        )
                    }
                }
            }
            is LoadState.Error -> TODO()
        }

        when (pagedExercises.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                Log.d(TAG, "loadState - refresh: ${pagedExercises.loadState.refresh}")
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation(
                            modifier = Modifier.testTag(
                                EXERCISES_LOADING_ANIMATION_TEST_TAG
                            )
                        )
                    }
                }
            }
            is LoadState.Error -> TODO()
        }
    }

    if (shouldShowFilterSheet) {
        FilterModalBottomSheet(
            onDismissRequest = onHideFilterSheet,
            bottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            filterStandards = filterStandards
        )
    }

}

@Composable
private fun ExerciseItem(modifier: Modifier = Modifier, exercise: Exercise) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = exercise.imageUrls.first(),
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
