package com.haidoan.android.stren.feat.trainining.routines

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.DummyBoxWithText

internal const val ADD_EDIT_ROUTINE_SCREEN_ROUTE = "add_edit_routine_screen_route"
internal const val ROUTINE_ID_ARG = "routine_id_arg"
internal const val IS_ADDING_ARG = "is_adding_arg"

@Composable
internal fun AddEditRoutineRoute(
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
                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
                        text = viewModel.searchBarText,
                        placeholder = "Search routine",
                        onTextChange = {
                            viewModel.searchBarText.value = it
                            viewModel.searchRoutineByName(it)
                        },
                        shouldShowSearchIcon = false,
                        onSearchClicked = { viewModel.searchRoutineByName(it) })
                    appBarConfigurationChangeHandler(searchBarConfiguration)
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AddEditRoutineScreen(
        modifier = modifier,
        uiState = uiState
    )
}

@SuppressLint("NewApi")
@Composable
internal fun AddEditRoutineScreen(
    modifier: Modifier = Modifier,
    uiState: RoutinesUiState
) {
    DummyBoxWithText(modifier = Modifier.fillMaxSize(), text = "Add edit routine")
}