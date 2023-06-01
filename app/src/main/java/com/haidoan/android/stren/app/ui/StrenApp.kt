package com.haidoan.android.stren.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.app.StrenAppViewModel
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.StrenNavHost
import com.haidoan.android.stren.core.designsystem.component.BottomNavigationBar
import com.haidoan.android.stren.core.designsystem.component.SearchBar
import com.haidoan.android.stren.core.designsystem.component.StrenSmallTopAppBar
import com.haidoan.android.stren.core.designsystem.component.TEST_TAG_TOP_BAR
import timber.log.Timber

val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
fun StrenApp(
    viewModel: StrenAppViewModel = hiltViewModel(),
    appState: StrenAppState = rememberStrenAppState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isUserSignedIn by rememberSaveable { viewModel.isUserSignedIn }
    val shouldShowOnboarding by rememberSaveable { viewModel.shouldShowOnboarding }

    Timber.d("isUserSignedIn: $isUserSignedIn")
    Timber.d("appState.currentAppBarConfiguration: ${appState.currentAppBarConfiguration}")

    // This allows any screen in the composition to access snackbar
    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                StrenTopAppBar(
                    shouldShowTopAppBar = appState.shouldShowTopBar,
                    configuration = appState.currentAppBarConfiguration,
                    onBackClicked = {
                        appState.currentAppBarConfiguration =
                            appState.previousAppBarConfiguration
                    }
                )
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
                shouldShowOnboarding = shouldShowOnboarding,
                appBarConfigurationChangeHandler = { newConfiguration ->
                    appState.previousAppBarConfiguration = appState.currentAppBarConfiguration
                    appState.currentAppBarConfiguration = newConfiguration
                }
            )
        }
    }
}

/***
 * A separate composable for readability and (potentially) testability
 */
@Composable
private fun StrenTopAppBar(
    shouldShowTopAppBar: Boolean,
    configuration: AppBarConfiguration,
    onBackClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = shouldShowTopAppBar,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        when (configuration) {
            is AppBarConfiguration.NavigationAppBar -> {
                StrenSmallTopAppBar(
                    modifier = Modifier.testTag(TEST_TAG_TOP_BAR),
                    appBarConfiguration = configuration
                )
            }
            is AppBarConfiguration.SearchAppBar -> {
                SearchBar(
                    text = configuration.text.value,
                    placeholder = configuration.placeholder,
                    onTextChange = configuration.onTextChange,
                    shouldShowSearchIcon = configuration.shouldShowSearchIcon,
                    onBackClicked = { onBackClicked() },
                    onSearchClicked = configuration.onSearchClicked
                )
            }
        }

    }
}