package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(
    tabNamesAndScreenComposables: List<Pair<String, @Composable () -> Unit>>,
    userScrollEnabled: Boolean = true,
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabNamesAndScreenComposables.size })
    val currentTabIndex = pagerState.currentPage
    var previousTabIndex = remember { tabNamesAndScreenComposables.size }

    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            edgePadding = 0.dp,
            selectedTabIndex = currentTabIndex,
            containerColor = Color.White,
            indicator = {
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
                            pagerState.scrollToPage(index)
                        }
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = Gray90
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = userScrollEnabled,
            state = pagerState,
        ) { page ->

            // The [page] value changes much more frequently than tabIndex in order to create
            // animation. Without the below check, the composables will be unnecessarily recomposed
            // many times
            //Timber.d( "currentTabIndex: $currentTabIndex ; page: $page ")
            if (page == currentTabIndex && previousTabIndex != currentTabIndex) {
                previousTabIndex = currentTabIndex
                //Timber.d( "Shown - currentTabIndex: $currentTabIndex ; previousTabIndex: $previousTabIndex ")
                tabNamesAndScreenComposables[currentTabIndex].second()
            }
        }
    }
}