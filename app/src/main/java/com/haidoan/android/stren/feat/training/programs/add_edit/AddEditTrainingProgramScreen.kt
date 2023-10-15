package com.haidoan.android.stren.feat.training.programs.add_edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.StrenOutlinedTextField
import com.haidoan.android.stren.core.designsystem.theme.Green70
import com.haidoan.android.stren.core.designsystem.theme.Red60

private val daysInWeek = listOf("M", "T", "W", "T", "F", "S", "S")


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

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    TrainingWeekGroup()
                }
                item {
                    TrainingWeekGroup()
                }
                item {
                    TrainingWeekGroup()
                }
                item {
                    TrainingWeekGroup()
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrainingWeekGroup() {
    Column(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Week 1 - 4", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.size(16.dp))

        FlowRow(
            maxItemsInEachRow = 7
        ) {
            repeat(4 * 7) {
                Day(offset = it, isSelected = false, haveWorkouts = true, onClickHandler = {})
            }
        }
    }
}


@Composable
private fun Day(
    offset: Int,
    isSelected: Boolean,
    haveWorkouts: Boolean,
    onClickHandler: (Int) -> Unit,
) {
    val backgroundColor = if (isSelected) Red60 else Color.White
    val textColor = if (isSelected) Color.White else Color.Black
    Box(
        modifier = Modifier
            .height(46.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickHandler(offset) }
            .background(backgroundColor)
            .padding(dimensionResource(id = R.dimen.padding_small)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = daysInWeek[offset % daysInWeek.size],
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            textAlign = TextAlign.Center
        )
        if (haveWorkouts) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Green70)
            )
        }
    }
}