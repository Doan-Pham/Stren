package com.haidoan.android.stren.feat.trainining

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.haidoan.android.stren.core.designsystem.theme.Gray90

@Composable
fun TrainingTabsScreen(tabNamesAndScreenComposables: List<Pair<String, Unit>>) {
    var tabIndex by remember { mutableStateOf(0) }
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
                    onClick = { tabIndex = index },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = Gray90
                )
            }
        }
        tabNamesAndScreenComposables[tabIndex].second
    }

}