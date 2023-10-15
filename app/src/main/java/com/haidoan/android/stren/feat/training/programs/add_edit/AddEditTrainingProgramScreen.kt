package com.haidoan.android.stren.feat.training.programs.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.StrenOutlinedTextField


@Composable
internal fun AddEditTrainingProgramsRoute(
    modifier: Modifier = Modifier,
    viewModel: AddEditTrainingProgramViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {

    val addEditProgramAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Program",
        navigationIcon = IconButtonInfo.BACK_ICON,
        actionIcons = listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_save,
                description = "Menu Item Save",
                clickHandler = {
                    // TODO: AppBar
//                    if (uiState is AddEditProgramUiState.EmptyProgram) {
//                        coroutineScope.launch {
//                            snackbarHostState.showSnackbar(
//                                message = "Empty program",
//                                duration = SnackbarDuration.Short,
//                                withDismissAction = true
//                            )
//                        }
//                    } else if (uiState is AddEditProgramUiState.IsEditing) {
//                        val programName = viewModel.routineNameTextFieldValue
//                        Timber.d("Save clicked - programName: $routineName")
//
//                        if (programName.isBlank() || programName.isEmpty()) {
//                            coroutineScope.launch {
//                                snackbarHostState.showSnackbar(
//                                    message = "Please input program name",
//                                    duration = SnackbarDuration.Short,
//                                    withDismissAction = true
//                                )
//                            }
//                        } else {
//                            viewModel.addEditProgram()
//                            onBackToPreviousScreen()
//                        }
//                    }
                }
            ),
        )
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(addEditProgramAppBarConfiguration)
        isAppBarConfigured = true
    }
    AddEditTrainingProgramsScreen(
        modifier = modifier,
        programName = viewModel.programName.value,
        onProgramNameChange = viewModel::onProgramNameChange,
    )
}

@Composable
private fun AddEditTrainingProgramsScreen(
    modifier: Modifier = Modifier,
    programName: String,
    onProgramNameChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StrenOutlinedTextField(
                text = programName,
                onTextChange = onProgramNameChange,
                label = "Program name",
                isError = programName.isBlank() || programName.isEmpty(),
                errorText = "Program name can't be empty"
            )
        }
    }

}