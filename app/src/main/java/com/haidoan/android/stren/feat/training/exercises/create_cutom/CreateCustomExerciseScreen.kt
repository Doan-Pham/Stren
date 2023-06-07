package com.haidoan.android.stren.feat.training.exercises.create_cutom

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.MuscleGroup

internal const val CREATE_EXERCISE_SCREEN_ROUTE = "CREATE_EXERCISE_SCREEN_ROUTE"
internal const val EXERCISE_NAME_NAV_ARG = "EXERCISE_NAME_NAV_ARG"

@Composable
internal fun CreateCustomExerciseRoute(
    modifier: Modifier = Modifier,
    viewModel: CreateCustomExerciseViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit
) {
    val exerciseCategories by viewModel.exerciseCategories.collectAsStateWithLifecycle()
    val muscleGroups by viewModel.muscleGroups.collectAsStateWithLifecycle()

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        val appBarConfiguration = AppBarConfiguration.NavigationAppBar(
            title = "Create custom exercise",
            navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
            actionIcons = listOf(
                IconButtonInfo(
                    drawableResourceId = R.drawable.ic_save,
                    description = "Menu Item Save",
                    clickHandler = {
                        //TODO: Save
                        onBackToPreviousScreen()
                    })
            )
        )
        appBarConfigurationChangeHandler(appBarConfiguration)
        isAppBarConfigured = true
    }

    CreateCustomExerciseScreen(
        modifier = modifier,
        uiState = viewModel.uiState.value,
        muscleGroups = muscleGroups,
        exerciseCategories = exerciseCategories,
        onUiStateChange = viewModel::modifyUiState,
        onTogglePrimaryMuscleGroup = viewModel::togglePrimaryMuscleGroupSelection,
        onToggleSecondaryMuscleGroup = viewModel::toggleSecondaryMuscleGroupSelection
    )
}

@Composable
private fun CreateCustomExerciseScreen(
    modifier: Modifier,
    uiState: CreateCustomExerciseUiState,
    exerciseCategories: List<ExerciseCategory>,
    muscleGroups: List<MuscleGroup>,
    onUiStateChange: (CreateCustomExerciseUiState) -> Unit,
    onTogglePrimaryMuscleGroup: (muscleGroupId: String) -> Unit,
    onToggleSecondaryMuscleGroup: (muscleGroupId: String) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.titleMedium
                )
                AddImageButton(modifier = Modifier.clickable {
                    // TODO
                })
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Finish",
                    style = MaterialTheme.typography.titleMedium
                )
                AddImageButton(modifier = Modifier.clickable {
                    // TODO
                })
            }
//            AsyncImage(
//                model = exercise.imageUrls.firstOrNull(),
//                modifier = Modifier
//                    .weight(1f)
//                    .clip(
//                        RoundedCornerShape(6.dp)
//                    ),
//                placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
//                error = painterResource(id = R.drawable.ic_app_logo_no_padding),
//                contentDescription = "An exercise image",
//                contentScale = ContentScale.Fit
//            )
//            AsyncImage(
//                model = exercise.imageUrls.getOrNull(1),
//                modifier = Modifier
//                    .weight(1f)
//                    .clip(
//                        RoundedCornerShape(6.dp)
//                    ),
//                placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
//                error = painterResource(id = R.drawable.ic_app_logo_no_padding),
//                contentDescription = "An exercise image",
//                contentScale = ContentScale.Fit
//            )
        }

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        StrenOutlinedTextField(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            text = uiState.exerciseName,
            onTextChange = {
                onUiStateChange(uiState.copy(exerciseName = it))
            },
            label = "Exercise name",
            isError = uiState.exerciseName.isBlank(),
            errorText = "Name can't be empty",
        )

        ExposedDropDownMenuTextField(
            modifier = Modifier.fillMaxWidth(),
            textFieldLabel = "Category",
            selectedText =
            if (uiState.exerciseCategory != ExerciseCategory.undefined) {
                uiState.exerciseCategory.name
            } else if (exerciseCategories.isNotEmpty()) {
                exerciseCategories[0].name
            } else {
                ""
            },

            menuItemsTextAndClickHandler = exerciseCategories.associate {
                it.name to { onUiStateChange(uiState.copy(exerciseCategory = it)) }
            }
        )

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        Text(
            text = "Trained muscles",
            style = MaterialTheme.typography.titleMedium
        )

        if (muscleGroups.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        } else {
            val primaryMuscleGroupFilter = FilterStandard(
                standardName = "Primary trained muscles",
                filterLabels = muscleGroups.map {
                    FilterLabel(
                        id = it.id,
                        name = it.name,
                        isSelected = it.id in uiState.primaryTrainedMusclesIds
                    )
                },
                onLabelSelected = {
                    // TODO
                    onTogglePrimaryMuscleGroup(it.id)
                },
            )
            FilterRegion(filterStandard = primaryMuscleGroupFilter)

            val secondaryMuscleGroupFilter = FilterStandard(
                standardName = "Secondary trained muscles",
                filterLabels = muscleGroups.map {
                    FilterLabel(
                        id = it.id,
                        name = it.name,
                        isSelected = it.id in uiState.secondaryTrainedMusclesIds
                    )
                },
                onLabelSelected = {
                    // TODO
                    onToggleSecondaryMuscleGroup(it.id)
                },
            )
            FilterRegion(filterStandard = secondaryMuscleGroupFilter)
        }
    }
}

@Composable
private fun AddImageButton(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(width = 2.dp, color = Gray60, shape = RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "Icon Camera",
                tint = Gray60
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = "Add image", color = Gray60)
    }
}