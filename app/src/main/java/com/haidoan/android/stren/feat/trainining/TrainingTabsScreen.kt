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

private const val TAG = "TrainingTabsScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainingTabsScreen(tabNamesAndScreenComposables: List<Pair<String, Unit>>) {
    // on below line we are creating variable for pager state.
    val pagerState = rememberPagerState(initialPage = 0)
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex, containerColor = Color.White, indicator = {
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(it[tabIndex]),
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
                    selected = tabIndex == index,
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
            tabNamesAndScreenComposables[page].second
        }
    }

}