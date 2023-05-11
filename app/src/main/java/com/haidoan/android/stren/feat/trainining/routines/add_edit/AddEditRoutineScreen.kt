package com.haidoan.android.stren.feat.trainining.routines

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.feat.trainining.routines.add_edit.AddEditRoutineUiState
import com.haidoan.android.stren.feat.trainining.routines.add_edit.AddEditRoutineViewModel
import timber.log.Timber

internal const val ADD_EDIT_ROUTINE_SCREEN_ROUTE = "add_edit_routine_screen_route"
internal const val ROUTINE_ID_NAV_ARG = "routine_id_arg"
internal const val IS_ADDING_ROUTINE_NAV_ARG = "is_adding_arg"

@Composable
internal fun AddEditRoutineRoute(
    modifier: Modifier = Modifier,
    viewModel: AddEditRoutineViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit
) {
    val addEditRoutineAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Routine",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_save,
                description = "Menu Item Save",
                clickHandler = {
                    //TODO: Implement "save" menu item
                }),
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(addEditRoutineAppBarConfiguration)
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
    uiState: AddEditRoutineUiState
) {
    when (uiState) {
        is AddEditRoutineUiState.Loading -> {
            Timber.d("Loading")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is AddEditRoutineUiState.EmptyRoutine -> {
            Timber.d("Empty")
        }
        is AddEditRoutineUiState.IsAdding -> {
            Timber.d("IsAdding")
            Timber.d("routines: ${uiState.routines}")
        }
    }
}