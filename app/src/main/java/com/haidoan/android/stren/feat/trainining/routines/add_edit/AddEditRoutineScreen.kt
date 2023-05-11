package com.haidoan.android.stren.feat.trainining.routines.add_edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.designsystem.component.StrenFilledButton
import com.haidoan.android.stren.core.designsystem.component.StrenOutlinedTextField
import timber.log.Timber

internal const val ADD_EDIT_ROUTINE_SCREEN_ROUTE = "add_edit_routine_screen_route"
internal const val ROUTINE_ID_NAV_ARG = "routine_id_arg"
internal const val IS_ADDING_ROUTINE_NAV_ARG = "is_adding_arg"

@Composable
internal fun AddEditRoutineRoute(
    modifier: Modifier = Modifier,
    exercisesIdsToAdd: List<String>,
    viewModel: AddEditRoutineViewModel = hiltViewModel(),
    onAddExercisesCompleted: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit,
    onNavigateToAddExercise: () -> Unit
) {
    if (exercisesIdsToAdd.isNotEmpty()) {
        viewModel.setExercisesIdsToAdd(exercisesIdsToAdd)
        onAddExercisesCompleted()
    }

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
        uiState = uiState,
        routineName = viewModel.routineNameTextFieldValue,
        onRoutineNameChange = { viewModel.routineNameTextFieldValue = it },
        onNavigateToAddExercise = onNavigateToAddExercise
    )
}

@SuppressLint("NewApi")
@Composable
internal fun AddEditRoutineScreen(
    modifier: Modifier = Modifier,
    uiState: AddEditRoutineUiState,
    routineName: String,
    onRoutineNameChange: (String) -> Unit,
    onNavigateToAddExercise: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        StrenOutlinedTextField(
            text = routineName,
            onTextChange = onRoutineNameChange,
            label = "Routine name",
            isError = routineName.isBlank() || routineName.isEmpty(),
            errorText = "Routine name can't be empty"
        )
        when (uiState) {
            is AddEditRoutineUiState.Loading -> {
                Timber.d("Loading")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingAnimation()
                }
            }
            is AddEditRoutineUiState.EmptyRoutine -> {
                Timber.d("Empty")
                EmptyScreen()
            }
            is AddEditRoutineUiState.IsAdding -> {
                Timber.d("IsAdding")
                Timber.d("routines: ${uiState.routines}")
            }
        }

        StrenFilledButton(
            text = "Add exercise", onClickHandler = onNavigateToAddExercise,
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }

}

@Composable
private fun ColumnScope.EmptyScreen() {

    Column(
        modifier = Modifier
            .weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = "Icon edit"
        )
        Text(
            text = "Empty routine",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Get started by adding exercises",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}