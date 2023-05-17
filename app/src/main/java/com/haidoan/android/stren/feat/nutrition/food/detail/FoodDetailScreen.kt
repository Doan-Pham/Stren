package com.haidoan.android.stren.feat.nutrition.food.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText

internal const val FOOD_DETAIL_SCREEN_ROUTE = "FOOD_DETAIL_SCREEN_ROUTE"
internal const val FOOD_ID_FOOD_DETAIL_NAV_ARG = "FOOD_ID_FOOD_DETAIL_NAV_ARG"

@Composable
internal fun FoodDetailRoute(
    modifier: Modifier = Modifier,
//    viewModel: FoodViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit = {},
) {
    val exercisesAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons =
        listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "MenuItem-Search",
                clickHandler = {
                    // TODO: Search
//                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
//                        text = viewModel.searchBarText,
//                        placeholder = "Search food",
//                        onTextChange = {
//
//                        },
//                        onSearchClicked = { viewModel.searchFoodByName(it) })
//                    appBarConfigurationChangeHandler(searchBarConfiguration)
                }),
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(exercisesAppBarConfiguration)
        isAppBarConfigured = true
    }

    FoodDetailScreen(
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FoodDetailScreen(
    modifier: Modifier = Modifier,
) {
    DummyBoxWithText(text = "Food Detail")
}