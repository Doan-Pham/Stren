package com.haidoan.android.stren.feat.training.programs.view_programs

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.DropDownMenuScaffold
import com.haidoan.android.stren.core.designsystem.component.LoadingAnimation
import com.haidoan.android.stren.core.designsystem.component.SimpleConfirmationDialog
import com.haidoan.android.stren.core.designsystem.component.StrenFilledButton
import com.haidoan.android.stren.core.designsystem.theme.Gray50
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.model.TrainingProgram
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.isWithin
import timber.log.Timber

@Composable
internal fun TrainingProgramsRoute(
    modifier: Modifier = Modifier,
    viewModel: TrainingProgramsViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onNavigateToAddProgramScreen: (userId: String) -> Unit,
    //TODO onNavigateToEditTrainingProgramScreen: (userId: String, trainingProgramId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val secondaryUiState by viewModel.secondaryUiState.collectAsStateWithLifecycle()

    if (secondaryUiState.shouldShowConfirmDialog) {
        SimpleConfirmationDialog(state = secondaryUiState.confirmDialogState)
    }

    LaunchedEffect(key1 = Unit, block = {
        viewModel.triggerCollection()
    })

    val trainingHistoryAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_add,
                description = "Menu Item Add",
                clickHandler = {
                    onNavigateToAddProgramScreen(viewModel.cachedUserId)
                }),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "Menu Item Search",
                clickHandler = {
                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
                        text = viewModel.searchBarText,
                        placeholder = "Search Programs",
                        onTextChange = {
                            viewModel.searchBarText.value = it
                            viewModel.searchTrainingProgramByName(it)
                        },
                        shouldShowSearchIcon = false,
                        onSearchClicked = { viewModel.searchTrainingProgramByName(it) })
                    appBarConfigurationChangeHandler(searchBarConfiguration)
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }


    TrainingProgramsScreen(
        modifier = modifier,
        uiState = uiState,
        onNavigateToAddTrainingProgramScreen = { onNavigateToAddProgramScreen(viewModel.cachedUserId) },
        onNavigateToEditTrainingProgramScreen = { trainingProgramId -> //onNavigateToEditTrainingProgramScreen(viewModel.cachedUserId, trainingProgramId)
        },
        onNavigateToAddWorkoutScreen = { trainingProgramId ->
            // onNavigateToAddWorkoutScreen(viewModel.cachedUserId, trainingProgramId)
        },
        onDeleteTrainingProgramClick = viewModel::deleteTrainingProgram
    )
}

@SuppressLint("NewApi")
@Composable
internal fun TrainingProgramsScreen(
    modifier: Modifier = Modifier,
    uiState: TrainingProgramsUiState,
    onNavigateToAddTrainingProgramScreen: () -> Unit,
    onNavigateToEditTrainingProgramScreen: (trainingProgramId: String) -> Unit,
    onNavigateToAddWorkoutScreen: (trainingProgramId: String) -> Unit,
    onDeleteTrainingProgramClick: (String) -> Unit,
) {
    when (uiState) {
        is TrainingProgramsUiState.Loading -> {
            Timber.d("Loading")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }

        is TrainingProgramsUiState.LoadComplete -> {
            Timber.d("trainingPrograms: ${uiState.trainingPrograms}")
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                items(uiState.trainingPrograms) { trainingProgram ->
                    TrainingProgramItem(
                        trainingProgram = trainingProgram,
                        onEditTrainingProgramClickHandler = onNavigateToEditTrainingProgramScreen,
                        onItemClickHandler = onNavigateToAddWorkoutScreen,
                        onDeleteTrainingProgramClick = { onDeleteTrainingProgramClick(trainingProgram.id) }
                    )
                }
            }
        }

        TrainingProgramsUiState.LoadEmpty -> {
            Timber.d("LoadEmpty")
            EmptyScreen(onCreateTrainingProgramButtonClick = onNavigateToAddTrainingProgramScreen)
        }
    }
}

@Composable
private fun TrainingProgramItem(
    trainingProgram: TrainingProgram,
    onItemClickHandler: (trainingProgramId: String) -> Unit,
    onEditTrainingProgramClickHandler: (trainingProgramId: String) -> Unit,
    onDeleteTrainingProgramClick: () -> Unit,
) {
    val currentDate = remember {
        DateUtils.getCurrentDate()
    }
    val isInProgress = currentDate.isWithin(trainingProgram.startDate, trainingProgram.endDate)
    val border = if (isInProgress) {
        BorderStroke(width = (2).dp, color = MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(width = (1.5).dp, color = Gray90)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(border = border, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
//            .clickable { onItemClickHandler(trainingProgram.id) }
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Top,
    ) {

        //region PROGRAM's NAME & 3DOT BUTTON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_extra_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = trainingProgram.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isInProgress) MaterialTheme.colorScheme.primary else Color.Black,
            )
            Spacer(Modifier.weight(1f))
            DropDownMenuScaffold(
                menuItemsTextAndClickHandler = mapOf(
                    "Edit" to { onEditTrainingProgramClickHandler(trainingProgram.id) },
                    "Delete" to { onDeleteTrainingProgramClick() })
            )
            { onExpandMenu ->
                Icon(
                    modifier = Modifier
                        .clickable {
                            onExpandMenu()
                        }
                        .size(dimensionResource(id = R.dimen.icon_size_medium)),
                    painter = painterResource(id = R.drawable.ic_more_horizontal),
                    contentDescription = "Icon more",
                    tint = if (isInProgress) MaterialTheme.colorScheme.primary else Color.Black
                )
            }

        }

        //endregion


        if (isInProgress) {
            Text(
                text = "In Progress",
                modifier = Modifier
                    .border(border = border, shape = RoundedCornerShape(15.dp))
                    .padding(
                        vertical = dimensionResource(id = R.dimen.padding_extra_small),
                        horizontal = dimensionResource(id = R.dimen.padding_small)
                    ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        //region ROUTINES

        trainingProgram.routinesByDayOffset.values.flatten().toList().take(3)
            .forEach {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray50
                )
            }

        //endregion

        //TODO
//        Button(
//            modifier = Modifier.align(Alignment.End),
//            shape = RoundedCornerShape(8.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
//            contentPadding = PaddingValues(
//                vertical = dimensionResource(id = R.dimen.padding_small),
//                horizontal = 0.dp
//            ),
//            onClick = {
//                onItemClickHandler(trainingProgram.id)
//            }) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_chevron_right),
//                contentDescription = "Icon arrow right",
//                tint = Color.White
//            )
//        }
    }
}

@Composable
private fun EmptyScreen(
    onCreateTrainingProgramButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_72),
                contentDescription = "Icon edit"
            )
            Text(
                text = "No Training Programs found",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Get started by creating a Program",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StrenFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Create Training Program",
                onClickHandler = onCreateTrainingProgramButtonClick,
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }
}