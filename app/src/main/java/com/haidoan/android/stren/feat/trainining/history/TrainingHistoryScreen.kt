package com.haidoan.android.stren.feat.trainining.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.*
import timber.log.Timber

@Composable
internal fun TrainingHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: TrainingHistoryViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val currentUserId by viewModel._currentUserId.collectAsStateWithLifecycle()
//    val strenAppViewModel = viewModel<StrenAppViewModel>()
//    viewModel.setUserId(strenAppViewModel.currentUserId.value)
//    val currentUserId = viewModel._currentUserId.collectAsStateWithLifecycle()
    Timber.d("currentUserId: $currentUserId")
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

