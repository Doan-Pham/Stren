package com.haidoan.android.stren.feat.training.exercises.create_cutom

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.app.ui.LocalSnackbarHostState
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.MuscleGroup
import kotlinx.coroutines.launch

internal const val CREATE_EXERCISE_SCREEN_ROUTE = "CREATE_EXERCISE_SCREEN_ROUTE"
internal const val EXERCISE_NAME_NAV_ARG = "EXERCISE_NAME_NAV_ARG"

@Composable
internal fun CreateCustomExerciseRoute(
    modifier: Modifier = Modifier,
    viewModel: CreateCustomExerciseViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onBackToPreviousScreen: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    if (viewModel.isCreateExerciseComplete) {
        LaunchedEffect(true) {
            coroutineScope.launch {
                onBackToPreviousScreen()
                snackbarHostState.showSnackbar(
                    message = "Custom exercise created!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    val exerciseCategories by viewModel.exerciseCategories.collectAsStateWithLifecycle()
    val muscleGroups by viewModel.muscleGroups.collectAsStateWithLifecycle()
    var isError by remember {
        mutableStateOf(false)
    }

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
                        if (isError) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please fill out exercise name and choose at least 1 trained primary muscle group",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            viewModel.createCustomExercise()
                        }
                    })
            )
        )
        appBarConfigurationChangeHandler(appBarConfiguration)
        isAppBarConfigured = true
    }

    CreateCustomExerciseScreen(
        modifier = modifier,
        isLoading = viewModel.isLoading,
        uiState = viewModel.uiState.value,
        muscleGroups = muscleGroups,
        exerciseCategories = exerciseCategories,
        onUiStateChange = viewModel::modifyUiState,
        onTogglePrimaryMuscleGroup = viewModel::toggleMuscleGroupSelection,
        onErrorStatusChange = { isError = it },
    )
}

@Composable
private fun CreateCustomExerciseScreen(
    modifier: Modifier,
    isLoading: Boolean,
    uiState: CreateCustomExerciseUiState,
    exerciseCategories: List<ExerciseCategory>,
    muscleGroups: List<MuscleGroup>,
    onUiStateChange: (CreateCustomExerciseUiState) -> Unit,
    onTogglePrimaryMuscleGroup: (muscleGroupId: String) -> Unit,
    onErrorStatusChange: (Boolean) -> Unit,
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingAnimation()
        }
        return
    }
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val startPhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    if (uri != null) {
                        onUiStateChange(uiState.copy(startImage = uri))
                    }
                }
            )
            val endPhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    if (uri != null) {
                        onUiStateChange(uiState.copy(endImage = uri))
                    }
                }
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.titleMedium
                )
                if (uiState.startImage == null) {
                    AddImageButton(modifier = Modifier.clickable {
                        startPhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    })

                } else {
                    AsyncImage(
                        model = uiState.startImage,
                        modifier = Modifier
                            .clickable {
                                startPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .size(100.dp),
                        placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
                        error = painterResource(id = R.drawable.ic_app_logo_no_padding),
                        contentDescription = "An exercise image",
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Finish",
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.endImage == null) {
                    AddImageButton(modifier = Modifier.clickable {
                        endPhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    })
                } else {
                    AsyncImage(
                        model = uiState.endImage,
                        modifier = Modifier
                            .clickable {
                                endPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .size(100.dp),
                        placeholder = painterResource(id = R.drawable.ic_app_logo_no_padding),
                        error = painterResource(id = R.drawable.ic_app_logo_no_padding),
                        contentDescription = "An exercise image",
                        contentScale = ContentScale.Fit
                    )
                }
            }
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

        StrenOutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .height(200.dp),
            text = uiState.instruction,
            onTextChange = {
                onUiStateChange(uiState.copy(instruction = it))
            },
            trailingIcon = null,
            singleLine = false,
            label = "Instructions",
            isError = false,
            errorText = "",
        )

        ExposedDropDownMenuTextField(
            modifier = Modifier.fillMaxWidth(),
            textFieldLabel = "Category",
            selectedText =
            if (uiState.exerciseCategory != ExerciseCategory.undefined) {
                uiState.exerciseCategory.name
            } else if (exerciseCategories.isNotEmpty()) {
                onUiStateChange(uiState.copy(exerciseCategory = exerciseCategories[0]))
                exerciseCategories[0].name
            } else {
                ""
            },

            menuItemsTextAndClickHandler = exerciseCategories.associate {
                it.name to { onUiStateChange(uiState.copy(exerciseCategory = it)) }
            }
        )

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_extra_large)))
        if (muscleGroups.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        } else {
            val primaryMuscleGroupFilter = FilterStandard(
                standardName = "Trained Muscle Groups",
                filterLabels = muscleGroups.map {
                    FilterLabel(
                        id = it.id,
                        name = it.name,
                        isSelected = it.id in uiState.trainedMusclesIds
                    )
                },
                onLabelSelected = {
                    onTogglePrimaryMuscleGroup(it.id)
                },
            )
            FilterRegion(filterStandard = primaryMuscleGroupFilter)
        }
    }
    val isError =
        uiState.exerciseName.isBlank() || uiState.trainedMusclesIds.isEmpty()
    onErrorStatusChange(isError)
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