package com.haidoan.android.stren.feat.trainining.routines

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.model.Workout
import timber.log.Timber


@Composable
internal fun RoutinesRoute(
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
                    //TODO: Implement "search" menu item
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RoutinesScreen(
        modifier = modifier,
        uiState = uiState
    )
}

@SuppressLint("NewApi")
@Composable
internal fun RoutinesScreen(
    modifier: Modifier = Modifier,
    uiState: RoutinesUiState
) {
    when (uiState) {
        is RoutinesUiState.Loading -> {
            Timber.d("Loading")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is RoutinesUiState.LoadComplete -> {
            Timber.d("routines: ${uiState.routines}")
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            ) {
                uiState.routines.forEach {
                    RoutineItem(routine = it)
                }
            }

        }
        RoutinesUiState.LoadEmpty -> {
            Timber.d("LoadEmpty")
            EmptyScreen()
        }
    }
}

@Composable
private fun RoutineItem(
    routine: Routine, onItemClickHandler: (Workout) -> Unit = {
        //TODO
    }, onIconMoreClickHandler: () -> Unit = {
        // TODO
    }
) {
    Column(
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.padding_large))
            .fillMaxWidth()
            .border(width = (1.5).dp, color = Gray90, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = routine.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))

            Icon(
                modifier = Modifier
                    .clickable {
                        //TODO
                    }
                    .size(dimensionResource(id = R.dimen.icon_size_medium)),
                painter = painterResource(id = R.drawable.ic_more_horizontal),
                contentDescription = "Icon more"
            )
        }

        routine.trainedExercises.subList(0, minOf(routine.trainedExercises.size, 3))
            .forEach {
                Text(
                    text = it.exercise.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray60
                )
            }

        Button(
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(
                vertical = dimensionResource(id = R.dimen.padding_small),
                horizontal = 0.dp
            ),
            onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Icon arrow right",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun EmptyScreen(
    onCreateRoutineButtonClick: () -> Unit = {
        //TODO
    },
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
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Icon edit"
            )
            Text(
                text = "No routines found",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Get started by creating routines",
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
                text = "Create routine",
                onClickHandler = { /*TODO*/ },
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }
}