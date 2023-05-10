package com.haidoan.android.stren.feat.trainining.routines

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*


@Composable
internal fun RoutinesRoute(
    modifier: Modifier = Modifier,
    viewModel: RoutinesViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {

    val trainingHistoryAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_add,
                description = "Menu Item Add",
                clickHandler = {
                    //TODO: Implement "add" menu item
                }),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "Menu Item Search",
                clickHandler = {
                    //TODO: Implement "search" menu item
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }

    RoutinesScreen(
        modifier = modifier
    )
}

@SuppressLint("NewApi")
@Composable
internal fun RoutinesScreen(
    modifier: Modifier = Modifier,
) {
    DummyBoxWithText(modifier = Modifier.fillMaxSize(), text = "RoutinesScreen")
}
