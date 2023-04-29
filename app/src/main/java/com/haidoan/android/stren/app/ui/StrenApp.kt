package com.haidoan.android.stren.app.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.StrenAppViewModel
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.StrenNavHost
import com.haidoan.android.stren.core.designsystem.component.BottomNavigationBar
import com.haidoan.android.stren.core.designsystem.component.StrenTopAppBar
import com.haidoan.android.stren.core.designsystem.component.TEST_TAG_TOP_BAR

private const val TAG = "StrenApp"
val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
fun StrenApp(
    viewModel: StrenAppViewModel = hiltViewModel(),
    appState: StrenAppState = rememberStrenAppState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isUserSignedIn by rememberSaveable { viewModel.isUserSignedIn }
    var currentTopAppBarConfiguration by remember {
        mutableStateOf(AppBarConfiguration())
    }

    Log.d(TAG, "isUserSignedIn: $isUserSignedIn")
    Log.d(TAG, "currentTopAppBarConfiguration: ${currentTopAppBarConfiguration}")
    // This allows any screen in the composition to access snackbar
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (appState.shouldShowTopBar) {
                    StrenTopAppBar(
                        modifier = Modifier.testTag(TEST_TAG_TOP_BAR),
                        title = stringResource(id = R.string.app_name),
                        appBarConfiguration = currentTopAppBarConfiguration
                    )
                }
            },
            bottomBar = {
                if (appState.shouldShowBottomBar) {
                    BottomNavigationBar(
                        destinations = appState.topLevelDestinations,
                        currentTopLevelDestination = appState.currentTopLevelDestination,
                        onNavigateToDestination = appState::navigateToTopLevelDestination
                    )
                }
            }
        ) {
            StrenNavHost(
                modifier = Modifier.padding(it),
                navController = appState.navController,
                isUserSignedIn = isUserSignedIn,
                appBarConfigurationChangeHandler = { newConfiguration ->
                    currentTopAppBarConfiguration = newConfiguration
                }
            )
        }
    }
}
