package com.haidoan.android.stren.feat.nutrition.food

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.*
import coil.compose.AsyncImage
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.model.Food
import timber.log.Timber


@Composable
internal fun FoodRoute(
    modifier: Modifier = Modifier,
    viewModel: FoodViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {},
    onNavigateToFoodDetailScreen: (exerciseId: String) -> Unit = {},
) {
    var shouldShowFilterSheet by rememberSaveable { mutableStateOf(false) }
    val pagedFoodData = viewModel.pagedFoodData.collectAsLazyPagingItems()
//
//    val exerciseCategories by viewModel.exerciseCategories.collectAsStateWithLifecycle()
//    val exerciseCategoryFilter = FilterStandard(
//        standardName = "Category",
//        filterLabels = exerciseCategories.map {
//            FilterLabel(
//                it.category.id,
//                it.category.name,
//                it.isSelected
//            )
//        },
//        onLabelSelected = { chosenLabel -> viewModel.toggleCategorySelection(chosenLabel.id) },
//    )
//    val muscleGroups by viewModel.muscleGroups.collectAsStateWithLifecycle()
//    val muscleGroupFilter = FilterStandard(
//        standardName = "Muscle group",
//        filterLabels = muscleGroups.map {
//            FilterLabel(
//                it.muscleGroup.id,
//                it.muscleGroup.name,
//                it.isSelected
//            )
//        },
//        onLabelSelected = { chosenLabel -> viewModel.toggleMuscleGroupSelection(chosenLabel.id) },
//    )

    val exercisesAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons =
        listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "MenuItem-Search",
                clickHandler = {
//                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
//                        text = viewModel.searchBarText,
//                        placeholder = "Search food",
//                        onTextChange = {
//
//                        },
//                        onSearchClicked = { viewModel.searchFoodByName(it) })
//                    appBarConfigurationChangeHandler(searchBarConfiguration)
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

    FoodScreen(
        modifier = modifier,
        pagedFoodData = pagedFoodData,
        shouldShowFilterSheet = shouldShowFilterSheet,
        onHideFilterSheet = { shouldShowFilterSheet = false },
//        filterStandards = listOf(exerciseCategoryFilter, muscleGroupFilter),
//        onResetFilters = viewModel::resetFilters,
//        onApplyFilters = viewModel::applyFilters,
        onNavigateToFoodDetailScreen = onNavigateToFoodDetailScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FoodScreen(
    modifier: Modifier = Modifier,
    pagedFoodData: LazyPagingItems<Food>,
    shouldShowFilterSheet: Boolean = false,
    onHideFilterSheet: () -> Unit = {},
//    filterStandards: List<FilterStandard> = listOf(),
//    onResetFilters: () -> Unit,
//    onApplyFilters: () -> Unit,
    onNavigateToFoodDetailScreen: (exerciseId: String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    )
    {
        this.items(
            count = pagedFoodData.itemCount,
            key = pagedFoodData.itemKey(),
            contentType = pagedFoodData.itemContentType(
            )
        ) { index ->
            val food = pagedFoodData[index]
            food?.let {
                FoodItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                    food = food,
                    onClickHandler = onNavigateToFoodDetailScreen
                )
            }
        }
        Timber.d("itemCount : ${pagedFoodData.itemCount}")

        when (pagedFoodData.loadState.append) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                Timber.d("loadState - append: ${pagedFoodData.loadState.append}")
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                }
            }
            is LoadState.Error -> TODO()
        }

        when (pagedFoodData.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                Timber.d("loadState - refresh: ${pagedFoodData.loadState.refresh}")
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                }
            }
            is LoadState.Error -> TODO()
        }
    }

//    if (shouldShowFilterSheet) {
//        FilterModalBottomSheet(
//            onDismissRequest = onHideFilterSheet,
//            bottomSheetState = rememberModalBottomSheetState(
//                skipPartiallyExpanded = true
//            ),
//            filterStandards = filterStandards,
//            onResetFilters = onResetFilters,
//            onApplyFilters = onApplyFilters
//        )
//    }

}

@Composable
private fun FoodItem(
    modifier: Modifier = Modifier,
    food: Food,
    onClickHandler: (exerciseId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClickHandler(food.id) }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = null,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_extra_extra_large))
                .clip(
                    RoundedCornerShape(6.dp)
                ),
            placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
            error = painterResource(id = R.drawable.ic_app_logo_no_padding),
            contentDescription = "An food image",
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        Column {
            Text(text = food.name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}