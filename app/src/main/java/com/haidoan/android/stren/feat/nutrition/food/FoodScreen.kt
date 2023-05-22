package com.haidoan.android.stren.feat.nutrition.food

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.model.Food
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
internal fun FoodRoute(
    modifier: Modifier = Modifier,
    viewModel: FoodViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onNavigateToFoodDetailScreen: (foodId: String) -> Unit,
) {
    val pagedFoodData = viewModel.pagedFoodData.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val foodAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons =
        listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "MenuItem-Search",
                clickHandler = {
                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
                        text = viewModel.searchBarText,
                        placeholder = "Search food",
                        onTextChange = {
                            viewModel.searchBarText.value = it
                        },
                        onSearchClicked = {
                            viewModel.searchFoodByName(it)
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(0, 0)
                            }
                        })
                    appBarConfigurationChangeHandler(searchBarConfiguration)
                }),
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(foodAppBarConfiguration)
        isAppBarConfigured = true
    }

    FoodScreen(
        modifier = modifier,
        pagedFoodData = pagedFoodData,
        onNavigateToFoodDetailScreen = onNavigateToFoodDetailScreen,
        lazyListState = lazyListState
    )
}

@Composable
internal fun FoodScreen(
    modifier: Modifier = Modifier,
    pagedFoodData: LazyPagingItems<Food>,
    onNavigateToFoodDetailScreen: (foodId: String) -> Unit,
    lazyListState: LazyListState
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = lazyListState
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
            is LoadState.NotLoading -> {
                Timber.d("loadState - append: ${pagedFoodData.loadState.append}")
            }
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
            is LoadState.NotLoading -> {
                if (pagedFoodData.itemCount == 0) {
                    item {
                        EmptyScreen(modifier = Modifier.fillParentMaxSize())
                    }
                }
                Timber.d("loadState - refresh: ${pagedFoodData.loadState.refresh}")
            }
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
}

@Composable
private fun FoodItem(
    modifier: Modifier = Modifier,
    food: Food,
    onClickHandler: (foodId: String) -> Unit
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
                painter = painterResource(id = R.drawable.ic_circle_question_mark),
                contentDescription = "Icon edit"
            )
            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
            Text(
                text = "No food found",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Please try again with a different food name",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}