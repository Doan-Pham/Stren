package com.haidoan.android.stren.feat.training.routines.add_edit

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
import com.haidoan.android.stren.app.ui.LocalSnackbarHostState
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Red90
import com.haidoan.android.stren.core.model.Exercise
import timber.log.Timber


internal const val ADD_EXERCISE_TO_ROUTINE_SCREEN_ROUTE = "add_exercise_to_routine_screen_route"
const val EXERCISES_LOADING_ANIMATION_TEST_TAG = "Loading-AddExerciseToRoutine"
const val EXERCISES_EXERCISE_LIST_TEST_TAG = "List-AddExerciseToRoutine"


@Composable
internal fun AddExerciseToRoutineRoute(
    modifier: Modifier = Modifier,
    viewModel: AddExerciseToRoutineViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {},
    onBackToPreviousScreen: () -> Unit,
    onAddExercisesToRoutine: (selectedExercisesIds: List<String>) -> Unit,
) {
    var shouldShowFilterSheet by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = LocalSnackbarHostState.current

    if (viewModel.shouldShowSnackBar) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(
                message = viewModel.snackBarErrorMessage,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            viewModel.shouldShowSnackBar = false
        }
    }

    val pagedAddExerciseToRoutine = viewModel.exercises.collectAsLazyPagingItems()

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
        title = "Add exercise",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
        actionIcons =
        listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_add,
                description = "Menu Item Add",
                clickHandler = {
                    //TODO: Add custom exercise
                }),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "Menu Item Search",
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
                description = "Menu Item Filter",
                clickHandler = { shouldShowFilterSheet = true })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(exercisesAppBarConfiguration)
        isAppBarConfigured = true
    }

    AddExerciseToRoutineScreen(
        modifier = modifier,
        pagedAddExerciseToRoutine = pagedAddExerciseToRoutine,
        shouldShowFilterSheet = shouldShowFilterSheet,
        onHideFilterSheet = { shouldShowFilterSheet = false },
        filterStandards = listOf(exerciseCategoryFilter, muscleGroupFilter),
        onResetFilters = viewModel::resetFilters,
        onApplyFilters = viewModel::applyFilters,
        selectedExercisesIds = viewModel.selectedExercisesIds,
        onSelectExercise = viewModel::toggleExerciseSelection,
        onButtonAddExerciseClick = { onAddExercisesToRoutine(viewModel.selectedExercisesIds.toList()) }
    )
}

/**
 * This composable is basically [ExercisesScreen] with minor modifications
 *
 * TODO: Probably extract some shared components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddExerciseToRoutineScreen(
    modifier: Modifier = Modifier,
    pagedAddExerciseToRoutine: LazyPagingItems<Exercise>,
    selectedExercisesIds: List<String>,
    filterStandards: List<FilterStandard> = listOf(),
    shouldShowFilterSheet: Boolean = false,
    onHideFilterSheet: () -> Unit = {},
    onResetFilters: () -> Unit,
    onApplyFilters: () -> Unit,
    onSelectExercise: (exerciseId: String) -> Unit,
    onButtonAddExerciseClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .weight(1f)
                .testTag(EXERCISES_EXERCISE_LIST_TEST_TAG),
        )
        {
            items(items = pagedAddExerciseToRoutine) { exercise ->
                exercise?.let {
                    ExerciseItem(
                        exercise = exercise,
                        onClickHandler = onSelectExercise,
                        isSelected = selectedExercisesIds.contains(exercise.id)
                    )
                }
            }
            Timber.d("itemCount : ${pagedAddExerciseToRoutine.itemCount}")

            when (pagedAddExerciseToRoutine.loadState.append) {
                is LoadState.NotLoading -> Unit
                is LoadState.Loading -> {
                    Timber.d("loadState - append: ${pagedAddExerciseToRoutine.loadState.append}")
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

            when (pagedAddExerciseToRoutine.loadState.refresh) {
                is LoadState.NotLoading -> Unit
                is LoadState.Loading -> {
                    Timber.d("loadState - refresh: ${pagedAddExerciseToRoutine.loadState.refresh}")
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

        if (selectedExercisesIds.isNotEmpty()) {
            StrenFilledButton(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                text = "Add ${selectedExercisesIds.size} " +
                        if (selectedExercisesIds.size == 1) "exercise" else "exercises",
                onClickHandler = onButtonAddExerciseClick,
                textStyle = MaterialTheme.typography.bodyMedium
            )
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
    onClickHandler: (exerciseId: String) -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) Red90 else Color.White
    Row(
        modifier = modifier
            .background(backgroundColor)
            .clickable { onClickHandler(exercise.id) }
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
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
