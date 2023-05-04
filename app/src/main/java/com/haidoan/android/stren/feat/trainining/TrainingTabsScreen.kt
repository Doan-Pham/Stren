package com.haidoan.android.stren.feat.trainining

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainingTabsScreen(
    tabNamesAndScreenComposables: List<Pair<String, @Composable () -> Unit>>
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val currentTabIndex = pagerState.currentPage
    var previousTabIndex = remember { tabNamesAndScreenComposables.size }

    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = currentTabIndex, containerColor = Color.White, indicator = {
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(it[currentTabIndex]),
                color = MaterialTheme.colorScheme.primary
            )
        }) {
            tabNamesAndScreenComposables.forEachIndexed { index, screenInfo ->
                Tab(
                    text = {
                        Text(
                            screenInfo.first,
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    selected = currentTabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = Gray90
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            pageCount = tabNamesAndScreenComposables.size
        ) { page ->

            // The [page] value changes much more frequently than tabIndex in order to create
            // animation. Without the below check, the composables will be unnecessarily recomposed
            // many times
            //Timber.d(TAG, "currentTabIndex: $currentTabIndex ; page: $page ")
            if (page == currentTabIndex && previousTabIndex != currentTabIndex) {
                previousTabIndex = currentTabIndex
                //Timber.d(TAG, "Shown - currentTabIndex: $currentTabIndex ; previousTabIndex: $previousTabIndex ")
                tabNamesAndScreenComposables[currentTabIndex].second()
            }
        }
    }
}