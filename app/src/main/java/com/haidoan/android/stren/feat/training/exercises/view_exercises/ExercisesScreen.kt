package com.haidoan.android.stren.feat.training.exercises.view_exercises

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.*
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
import timber.log.Timber

internal const val EXERCISES_SCREEN_ROUTE = "exercises_screen_route"
const val EXERCISES_LOADING_ANIMATION_TEST_TAG = "Loading-Exercises"
const val EXERCISES_EXERCISE_LIST_TEST_TAG = "List-Exercises"

@Composable
internal fun ExercisesRoute(
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {},
    onNavigateToExerciseDetailScreen: (exerciseId: String) -> Unit,
) {
    var shouldShowFilterSheet by rememberSaveable { mutableStateOf(false) }
    val pagedExercises = viewModel.exercises.collectAsLazyPagingItems()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    val exerciseCategories by viewModel.exerciseCategories.collectAsStateWithLifecycle()
    val exerciseCategoryFilter = FilterStandard(
        standardName = "Category",
        filterLabels = exerciseCategories.map {
            FilterLabel(
                it.category.id,
                it.category.name,
                it.isSelected
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
                it.isSelected
            )
        },
        onLabelSelected = { chosenLabel -> viewModel.toggleMuscleGroupSelection(chosenLabel.id) },
    )

    val exercisesAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons =
        listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_add,
                description = "Menu Item Add",
                clickHandler = {
                    // TODO: Navigate to Create Custom exercise
                }),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "MenuItem-Search",
                clickHandler = {
                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
                        text = viewModel.searchBarText,
                        placeholder = "Search exercise",
                        onTextChange = {
                            viewModel.searchBarText.value = it
                            viewModel.searchExerciseByName(it)
                        },
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
        isSearching = isSearching,
        pagedExercises = pagedExercises,
        shouldShowFilterSheet = shouldShowFilterSheet,
        onHideFilterSheet = { shouldShowFilterSheet = false },
        filterStandards = listOf(exerciseCategoryFilter, muscleGroupFilter),
        onResetFilters = viewModel::resetFilters,
        onApplyFilters = viewModel::applyFilters,
        onNavigateToExerciseDetailScreen = onNavigateToExerciseDetailScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExercisesScreen(
    modifier: Modifier = Modifier,
    isSearching: Boolean,
    pagedExercises: LazyPagingItems<Exercise>,
    filterStandards: List<FilterStandard> = listOf(),
    shouldShowFilterSheet: Boolean = false,
    onHideFilterSheet: () -> Unit = {},
    onResetFilters: () -> Unit,
    onApplyFilters: () -> Unit,
    onNavigateToExerciseDetailScreen: (exerciseId: String) -> Unit,
) {
    if (isSearching) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingAnimation(
                modifier = Modifier.testTag(
                    EXERCISES_LOADING_ANIMATION_TEST_TAG
                )
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag(EXERCISES_EXERCISE_LIST_TEST_TAG)
        )
        {

            items(
                count = pagedExercises.itemCount,
                key = pagedExercises.itemKey(),
                contentType = pagedExercises.itemContentType()
            ) { index ->
                val item = pagedExercises[index]
                item?.let {
                    ExerciseItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_medium)),
                        exercise = item,
                        onClickHandler = onNavigateToExerciseDetailScreen
                    )
                }
            }

            Timber.d("itemCount : ${pagedExercises.itemCount}")
            Timber.d("loadState - append: ${pagedExercises.loadState.append}")
            Timber.d("loadState - refresh: ${pagedExercises.loadState.refresh}")

            when (pagedExercises.loadState.append) {
                is LoadState.NotLoading -> Unit
                is LoadState.Loading -> {
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
                is LoadState.NotLoading -> {
                    if (pagedExercises.itemCount == 0) {
                        item {
                            EmptyScreen(modifier = Modifier
                                .clickable {
                                    //TODO : Navigate to create custom exercise screen
                                }
                                .fillParentMaxSize())
                        }
                    }
                }
                is LoadState.Loading -> {
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

    }

    if (shouldShowFilterSheet) {
        FilterModalBottomSheet(
            onDismissRequest = onHideFilterSheet,
            bottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            filterStandards = filterStandards,
            onResetFilters = onResetFilters,
            onApplyFilters = onApplyFilters
        )
    }

}


@Composable
private fun ExerciseItem(
    modifier: Modifier = Modifier,
    exercise: Exercise,
    onClickHandler: (exerciseId: String) -> Unit
) {
    Row(
        modifier = modifier.clickable { onClickHandler(exercise.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = exercise.imageUrls.first(),
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_extra_extra_large))
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

@Composable
private fun EmptyScreen(
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_whole_screen)),
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Icon edit"
            )
            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
            Text(
                text = "No exercise found",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Click to create custom exercise",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}