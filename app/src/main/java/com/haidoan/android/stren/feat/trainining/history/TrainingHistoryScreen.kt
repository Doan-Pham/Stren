package com.haidoan.android.stren.feat.trainining.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*

@Composable
internal fun TrainingHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: TrainingHistoryViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val trainingHistoryAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_add,
                description = "MenuItem-Add",
                clickHandler = {
                    //TODO: Implement "add" menu item
                }),
            IconButtonInfo(drawableResourceId = R.drawable.ic_calendar,
                description = "MenuItem-Calendar",
                clickHandler = {
                    //TODO: Implement "calendar" menu item
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }

    TrainingHistoryScreen(
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TrainingHistoryScreen(
    modifier: Modifier = Modifier,
) {
    DummyBoxWithText(modifier = Modifier.fillMaxSize(), text = "Training History")
}

