package com.haidoan.android.stren.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.designsystem.component.BottomNavigationBar

private const val TAG = "StrenApp"
val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StrenApp(
    viewModel: StrenAppViewModel = hiltViewModel(),
    appState: StrenAppState = rememberStrenAppState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isUserSignedIn by rememberSaveable { viewModel.isUserSignedIn }

    // This allows any screen in the composition to access snackbar
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (appState.shouldShowBottomBar) {
                    BottomNavigationBar(appState.navController)
                }
            }
        ) {
            StrenNavHost(navController = appState.navController, isUserSignedIn = isUserSignedIn)
        }
    }
}
