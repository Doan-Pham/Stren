package com.haidoan.android.stren.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.app.LocalActivity
import com.haidoan.android.stren.app.StrenAppViewModel
import com.haidoan.android.stren.app.WorkoutInProgressViewModel
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.StrenNavHost
import com.haidoan.android.stren.core.designsystem.component.BottomNavigationBar
import com.haidoan.android.stren.core.designsystem.component.SearchBar
import com.haidoan.android.stren.core.designsystem.component.StrenSmallTopAppBar
import com.haidoan.android.stren.core.designsystem.component.TEST_TAG_TOP_BAR
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.feat.auth.AUTH_GRAH_ROUTE
import com.haidoan.android.stren.feat.dashboard.DASHBOARD_GRAPH_ROUTE
import com.haidoan.android.stren.feat.training.history.navigateToStartWorkoutScreen
import timber.log.Timber

val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
fun StrenApp(
    viewModel: StrenAppViewModel = hiltViewModel(),
    workoutInProgressViewModel: WorkoutInProgressViewModel = hiltViewModel(LocalActivity.current),
    appState: StrenAppState = rememberStrenAppState(),
    onAuthStateResolved: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val userId by rememberSaveable { viewModel.userId }
    val isUserSignedIn by rememberSaveable { viewModel.isUserSignedIn }
    val shouldShowOnboarding by rememberSaveable { viewModel.shouldShowOnboarding }
    val workoutInProgressUiState by workoutInProgressViewModel.uiState.collectAsStateWithLifecycle()

    if (isUserSignedIn != null) {
        LaunchedEffect(true) {
            onAuthStateResolved()
        }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                StrenNavHost(
                    modifier = Modifier.weight(1f),
                    navController = appState.navController,
                    isUserSignedIn = isUserSignedIn ?: false,
                    appBarConfigurationChangeHandler = { newConfiguration ->
                        appState.previousAppBarConfiguration = appState.currentAppBarConfiguration
                        appState.currentAppBarConfiguration = newConfiguration
                    },
                    shouldShowOnboarding = shouldShowOnboarding,
                    userId = userId,
                    startDestination =
                    if (isUserSignedIn == true) DASHBOARD_GRAPH_ROUTE
                    else AUTH_GRAH_ROUTE
                )
                if (workoutInProgressUiState.isTraineeWorkingOut && appState.shouldShowBottomBar) {
                    WorkoutInProgressCard {
                        appState.navController.navigateToStartWorkoutScreen(
                            userId,
                            DateUtils.getCurrentDate()
                        )
                    }
                }
            }
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
                    modifier = Modifier.padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    ),
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


@Composable
private fun WorkoutInProgressCard(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .border(
                width = (1).dp,
                color = Gray90,
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
            .padding(dimensionResource(id = com.haidoan.android.stren.R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text("Workout in progress", style = MaterialTheme.typography.titleMedium)
    }
}